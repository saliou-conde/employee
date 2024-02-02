package akros.employee.manager.service.impl;

import akros.employee.manager.domain.AkrosUser;
import akros.employee.manager.dto.EmployeeResponseDto;
import akros.employee.manager.dto.LoginRequestDto;
import akros.employee.manager.domain.mapper.AkrosUserMapper;
import akros.employee.manager.repository.AkrosUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import static akros.employee.manager.constant.AppConstant.AKROS_USER_API_PATH;
import static akros.employee.manager.constant.AppConstant.AKROS_USER;
import static akros.employee.manager.domain.enumeration.Role.ROLE_USER;
import static akros.employee.manager.domain.mapper.AkrosUserMapper.INSTANCE;
import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AkrosUserService {

    private final AkrosUserRepository repository;
    private static final AkrosUserMapper MAPPER = INSTANCE;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    public EmployeeResponseDto register(LoginRequestDto loginRequestDto) {
        var akrosUser = MAPPER.mapToAkrosUser(loginRequestDto);
        akrosUser.setId(UUID.randomUUID().toString());
        akrosUser.setJoinDate(new Date().toString());
        akrosUser.setRole(ROLE_USER);
        akrosUser.setPassword(passwordEncoder.encode(loginRequestDto.getPassword()));
        repository.save(akrosUser);
        var jwtToken = jwtService.generateToken(akrosUser);

        log.info(CREATED.getReasonPhrase());
        return EmployeeResponseDto.builder()
                .timestamp(Instant.now().toString())
                .status(CREATED)
                .statusCode(CREATED.value())
                .message("User Successfully registered")
                .data(Map.of(AKROS_USER, loginRequestDto))
                .path(AKROS_USER_API_PATH +"register")
                .token(jwtToken)
                .build();
    }

    public EmployeeResponseDto authenticate(LoginRequestDto loginRequestDto) {
        String username = loginRequestDto.getUsername();
        var userOptional = repository.findByUsername(username);
        if(userOptional.isPresent()) {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, loginRequestDto.getPassword()));
            var jwtToken = jwtService.generateToken(userOptional.get());
            log.info(OK.getReasonPhrase());

            return EmployeeResponseDto.builder()
                    .timestamp(Instant.now().toString())
                    .status(OK)
                    .statusCode(OK.value())
                    .message("User Successfully logged in")
                    .data(Map.of(AKROS_USER, userOptional.get()))
                    .path(AKROS_USER_API_PATH +"authenticate")
                    .token(jwtToken)
                    .build();

        }

        log.error("User not found by username: {}", username);
        log.error(FORBIDDEN.toString());
        return EmployeeResponseDto.builder()
                .timestamp(Instant.now().toString())
                .status(FORBIDDEN)
                .statusCode(FORBIDDEN.value())
                .message(FORBIDDEN.toString())
                .data(Map.of(AKROS_USER, loginRequestDto))
                .path(AKROS_USER_API_PATH)
                .build();
    }

    public EmployeeResponseDto active(String username) {
        var akrosUserOptional = repository.findByUsername(username);
        if(akrosUserOptional.isPresent()) {
            AkrosUser akrosUser = akrosUserOptional.get();
            akrosUser.setNotLocked(true);
            akrosUser.setActive(true);
            repository.save(akrosUser);
            var jwtToken = jwtService.generateToken(akrosUser);
            log.info(OK.getReasonPhrase());

            return EmployeeResponseDto.builder()
                    .timestamp(Instant.now().toString())
                    .status(OK)
                    .statusCode(OK.value())
                    .message("User found by username")
                    .data(Map.of(AKROS_USER, MAPPER.mapToLoginRequestDto(akrosUser)))
                    .path(AKROS_USER_API_PATH +"active/"+username)
                    .token(jwtToken)
                    .build();
        }

        log.error("User not found by username: {}", username);
        log.info(NOT_FOUND.getReasonPhrase());
        return EmployeeResponseDto.builder()
                .timestamp(Instant.now().toString())
                .status(NOT_FOUND)
                .statusCode(NOT_FOUND.value())
                .message("User not found by username: "+username)
                .path(AKROS_USER_API_PATH +"active/"+username)
                .build();
    }

    public void deleteAllUsers() {
        log.info("Starting deleteAllUsers()");
        repository.deleteAll();
        log.info("Started deleteAllUsers()");
    }
}
