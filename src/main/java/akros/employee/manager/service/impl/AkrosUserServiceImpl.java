package akros.employee.manager.service.impl;

import akros.employee.manager.domain.AkrosUser;
import akros.employee.manager.dto.EmployeeResponseDto;
import akros.employee.manager.dto.LoginRequestDto;
import akros.employee.manager.domain.mapper.AkrosUserMapper;
import akros.employee.manager.repository.AkrosUserRepository;
import akros.employee.manager.service.AkrosUserService;
import akros.employee.manager.utility.ServiceUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

import static akros.employee.manager.constant.AppConstant.AKROS_USER_API_PATH;
import static akros.employee.manager.domain.enumeration.Role.ROLE_USER;
import static akros.employee.manager.domain.mapper.AkrosUserMapper.INSTANCE;
import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AkrosUserServiceImpl implements AkrosUserService {

    private final AkrosUserRepository repository;
    private static final AkrosUserMapper MAPPER = INSTANCE;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private static final ServiceUtility SERVICE_UTILITY = ServiceUtility.getInstance();

    public EmployeeResponseDto register(LoginRequestDto loginRequestDto) {
        var akrosUser = MAPPER.mapToAkrosUser(loginRequestDto);
        akrosUser.setId(UUID.randomUUID().toString());
        akrosUser.setJoinDate(new Date().toString());
        akrosUser.setRole(ROLE_USER);
        akrosUser.setPassword(passwordEncoder.encode(loginRequestDto.getPassword()));
        repository.save(akrosUser);
        var jwtToken = jwtService.generateToken(akrosUser);

        log.info(CREATED.getReasonPhrase());
        return SERVICE_UTILITY.employeeResponseDto(MAPPER.mapToLoginRequestDto(akrosUser), CREATED,
                        "User Successfully registered", null, AKROS_USER_API_PATH, jwtToken);
    }

    public EmployeeResponseDto authenticate(LoginRequestDto loginRequestDto) {
        String username = loginRequestDto.getUsername();
        var userOptional = repository.findByUsername(username);
        if(userOptional.isPresent()) {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, loginRequestDto.getPassword()));
            var jwtToken = jwtService.generateToken(userOptional.get());
            log.info(OK.getReasonPhrase());

            return SERVICE_UTILITY.employeeResponseDto(MAPPER.mapToLoginRequestDto(userOptional.get()), OK,
                    "User Successfully logged in", null, AKROS_USER_API_PATH +"authenticate", jwtToken);
        }
        else {
            return authenticateByEmail(loginRequestDto);
        }
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

            return SERVICE_UTILITY.employeeResponseDto(MAPPER.mapToLoginRequestDto(akrosUser), OK,
                            "User found by username", null,
                            AKROS_USER_API_PATH +"active/"+username, jwtToken);
        }

        log.error("User not found by username {}", username);
        log.info(NOT_FOUND.getReasonPhrase());
        return SERVICE_UTILITY.employeeResponseDto(new LoginRequestDto(), NOT_FOUND,
                        "User not found by username: "+username, "User not found by username: "+username,
                        AKROS_USER_API_PATH +"active/"+username, null);
    }

    public void deleteAllUsers() {
        log.info("Starting deleteAllUsers()");
        repository.deleteAll();
        log.info("Started deleteAllUsers()");
    }

    /**
     *
     * @param loginRequestDto authenticateByEmail
     * @return EmployeeResponseDto
     */
    private EmployeeResponseDto authenticateByEmail(LoginRequestDto loginRequestDto) {
        String email = loginRequestDto.getEmail();
        var akrosUserOptional = repository.findByEmail(email);
        if(akrosUserOptional.isPresent()) {
            AkrosUser akrosUser = akrosUserOptional.get();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(akrosUser.getUsername(), loginRequestDto.getPassword()));
            var jwtToken = jwtService.generateToken(akrosUser);
            log.info(OK.getReasonPhrase());

            return SERVICE_UTILITY.employeeResponseDto(MAPPER.mapToLoginRequestDto(akrosUser), OK,
                    "User found by username", null,
                    AKROS_USER_API_PATH +email, jwtToken);
        }
        log.error("User not found by email: {}", email);
        log.info(NOT_FOUND.getReasonPhrase());
        return SERVICE_UTILITY.employeeResponseDto(new LoginRequestDto(), NOT_FOUND,
                "User not found by email: "+email, "User not found by email: "+email,
                AKROS_USER_API_PATH +email, null);
    }
}
