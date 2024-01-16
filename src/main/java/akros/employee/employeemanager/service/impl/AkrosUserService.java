package akros.employee.employeemanager.service.impl;

import akros.employee.employeemanager.domain.AkrosUser;
import akros.employee.employeemanager.dto.HttpResponseDto;
import akros.employee.employeemanager.dto.LoginRequestDto;
import akros.employee.employeemanager.domain.mapper.AkrosUserMapper;
import akros.employee.employeemanager.repository.AkrosUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import static akros.employee.employeemanager.constant.AppConstant.AKROS_USER_API_PATH;
import static akros.employee.employeemanager.constant.AppConstant.AKROS_USER;
import static akros.employee.employeemanager.domain.enumeration.Role.ROLE_USER;
import static akros.employee.employeemanager.domain.mapper.AkrosUserMapper.INSTANCE;
import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class AkrosUserService {

    private final AkrosUserRepository repository;
    private static final AkrosUserMapper MAPPER = INSTANCE;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    public HttpResponseDto register(LoginRequestDto loginRequestDto) {
        var akrosUser = MAPPER.mapToAkrosUser(loginRequestDto);
        akrosUser.setId(UUID.randomUUID().toString());
        akrosUser.setJoinDate(new Date().toString());
        akrosUser.setRole(ROLE_USER);
        akrosUser.setPassword(passwordEncoder.encode(loginRequestDto.getPassword()));
        repository.save(akrosUser);
        var jwtToken = jwtService.generateToken(akrosUser);

        return HttpResponseDto.builder()
                .timestamp(Instant.now().toString())
                .status(CREATED)
                .statusCode(CREATED.value())
                .message("User Successfully registered")
                .data(Map.of(AKROS_USER, loginRequestDto))
                .path(AKROS_USER_API_PATH +"register")
                .token(jwtToken)
                .build();
    }

    public HttpResponseDto authenticate(LoginRequestDto loginRequestDto) {
        var userOptional = repository.findByUsername(loginRequestDto.getUsername());
        if(userOptional.isPresent()) {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword()));
            var jwtToken = jwtService.generateToken(userOptional.get());

            return HttpResponseDto.builder()
                    .timestamp(Instant.now().toString())
                    .status(OK)
                    .statusCode(OK.value())
                    .message("User Successfully logged in")
                    .data(Map.of(AKROS_USER, userOptional.get()))
                    .path(AKROS_USER_API_PATH +"authenticate")
                    .token(jwtToken)
                    .build();

        }

        return HttpResponseDto.builder()
                .timestamp(Instant.now().toString())
                .status(FORBIDDEN)
                .statusCode(FORBIDDEN.value())
                .message(FORBIDDEN.toString())
                .data(Map.of(AKROS_USER, loginRequestDto))
                .path(AKROS_USER_API_PATH)
                .build();
    }

    public HttpResponseDto active(String username) {
        var akrosUserOptional = repository.findByUsername(username);
        if(akrosUserOptional.isPresent()) {
            AkrosUser akrosUser = akrosUserOptional.get();
            akrosUser.setNotLocked(true);
            akrosUser.setActive(true);
            repository.save(akrosUser);
            var jwtToken = jwtService.generateToken(akrosUser);

            return HttpResponseDto.builder()
                    .timestamp(Instant.now().toString())
                    .status(OK)
                    .statusCode(OK.value())
                    .message("User Successfully Added")
                    .data(Map.of(AKROS_USER, MAPPER.mapToLoginRequestDto(akrosUser)))
                    .path(AKROS_USER_API_PATH +"register")
                    .token(jwtToken)
                    .build();
        }

        return HttpResponseDto.builder()
                .timestamp(Instant.now().toString())
                .status(NOT_FOUND)
                .statusCode(NOT_FOUND.value())
                .message("User Successfully Added")
                .path(AKROS_USER_API_PATH +"register")
                .build();
    }

    public void deleteAllUsers() {
        repository.deleteAll();
    }
}
