package akros.employee.manager.service.impl;

import akros.employee.manager.domain.AkrosUser;
import akros.employee.manager.domain.Employee;
import akros.employee.manager.domain.enumeration.Role;
import akros.employee.manager.domain.plaisibility.EmployeeValidation;
import akros.employee.manager.domain.plaisibility.EmployeeValidator;
import akros.employee.manager.dto.EmployeeRequestDto;
import akros.employee.manager.dto.EmployeeResponseDto;
import akros.employee.manager.dto.LoginRequestDto;
import akros.employee.manager.domain.mapper.AkrosUserMapper;
import akros.employee.manager.repository.AkrosUserRepository;
import akros.employee.manager.service.AkrosUserService;
import akros.employee.manager.utility.ServiceUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

import static akros.employee.manager.constant.AppConstant.*;
import static akros.employee.manager.domain.enumeration.Role.ROLE_USER;
import static akros.employee.manager.domain.mapper.AkrosUserMapper.INSTANCE;
import static akros.employee.manager.domain.plaisibility.EmployeeValidation.VALID;
import static akros.employee.manager.domain.plaisibility.EmployeeValidator.*;
import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AkrosUserServiceImpl implements AkrosUserService {
    private static final ServiceUtility SERVICE_UTILITY = ServiceUtility.getInstance();

    private final AkrosUserRepository repository;
    private static final AkrosUserMapper MAPPER = INSTANCE;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public EmployeeResponseDto register(LoginRequestDto loginRequestDto) {
        String path = AKROS_USER_API_PATH+"register";
        String startedSaveEmployee = "Started register";
        EmployeeResponseDto employeeResponseDto = validateDto(loginRequestDto, path, startedSaveEmployee);
        if(employeeResponseDto.getStatus() != OK) {
            return employeeResponseDto;
        }

        var akrosUser = MAPPER.mapToAkrosUser(loginRequestDto);
        akrosUser.setId(UUID.randomUUID().toString());
        akrosUser.setJoinDate(new Date().toString());
        akrosUser.setRole(ROLE_USER);
        akrosUser.setUsername(loginRequestDto.getUsername().toLowerCase());
        akrosUser.setPassword(passwordEncoder.encode(loginRequestDto.getPassword()));

        repository.save(akrosUser);
        var jwtToken = jwtService.generateToken(akrosUser);
        log.info(CREATED.getReasonPhrase());
        log.info(startedSaveEmployee);
        return SERVICE_UTILITY.employeeResponseDto(MAPPER.mapToLoginRequestDto(akrosUser), CREATED,
                "User Successfully registered", null, path, jwtToken);
    }

    public EmployeeResponseDto authenticate(LoginRequestDto loginRequestDto) {
        String username = loginRequestDto.getUsername();
        var userOptional = repository.findByUsername(username);
        if (userOptional.isPresent()) {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, loginRequestDto.getPassword()));
            setLastLoginDate(userOptional.get());
            var jwtToken = jwtService.generateToken(userOptional.get());
            log.info(OK.getReasonPhrase());


            return SERVICE_UTILITY.employeeResponseDto(MAPPER.mapToLoginRequestDto(userOptional.get()), OK,
                    "User Successfully logged in", null, AKROS_USER_API_PATH + "authenticate", jwtToken);
        } else {
            return authenticateByEmail(loginRequestDto);
        }
    }

    public EmployeeResponseDto active(String username) {
        var akrosUserOptional = repository.findByUsername(username);
        var path = AKROS_USER_API_PATH + "active/" + username;
        if (akrosUserOptional.isPresent()) {
            AkrosUser akrosUser = akrosUserOptional.get();
            akrosUser.setNotLocked(true);
            akrosUser.setActive(true);
            repository.save(akrosUser);
            var jwtToken = jwtService.generateToken(akrosUser);
            log.info(OK.getReasonPhrase());

            return SERVICE_UTILITY.employeeResponseDto(MAPPER.mapToLoginRequestDto(akrosUser), OK,
                    "User successfully activated: ", null,
                    path, jwtToken);
        }

        log.error("User not found by username {}", username);
        log.info(NOT_FOUND.getReasonPhrase());
        return employeeNotFoundByUsername(username, path, NOT_FOUND_BY_USERNAME, NOT_FOUND_BY_USERNAME, NOT_FOUND);
    }

    public void deleteAllUsers() {
        log.info("Starting deleteAllUsers()");
        repository.deleteAll();
        log.info("Started deleteAllUsers()");
    }

    @Override
    public EmployeeResponseDto changePassword(String username, String oldPassword, String newPassword) {
        var userOptional = repository.findByUsername(username);
        var path = AKROS_USER_API_PATH + "change-Password/" + username;
        if (userOptional.isPresent()) {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, oldPassword));
            var akrosUser = userOptional.get();
            akrosUser.setPassword(passwordEncoder.encode(newPassword));
            AkrosUser savedAkrosUser = repository.save(akrosUser);

            var jwtToken = jwtService.generateToken(savedAkrosUser);
            log.info(OK.getReasonPhrase());

            return SERVICE_UTILITY.employeeResponseDto(MAPPER.mapToLoginRequestDto(savedAkrosUser), OK,
                    "User Password changed successfully", null, path, jwtToken);
        } else {
            return employeeNotFoundByUsername(username, path, NOT_FOUND_BY_USERNAME, NOT_FOUND_BY_USERNAME, NOT_FOUND);
        }
    }

    @Override
    public EmployeeResponseDto resetPassword(String adminUsername, LoginRequestDto loginRequestDto) {
        var path = AKROS_USER_API_PATH + "reset-password/" + adminUsername;
        var akrosUserOptional = repository.findByUsername(adminUsername);
        if (akrosUserOptional.isPresent()) {
            if (isAdmin(akrosUserOptional.get())) {
                return employeeNotFoundByUsername(adminUsername, path, NOT_ENOUGH_PERMISSION, NOT_ENOUGH_PERMISSION, FORBIDDEN);
            }
            var username = loginRequestDto.getUsername();
            var akrosUser = repository.findByUsername(username);
            if (akrosUser.isPresent()) {
                akrosUser.get().setPassword(passwordEncoder.encode(loginRequestDto.getPassword()));
                var akrosUserToBeSaved = repository.save(akrosUser.get());
                var jwtToken = jwtService.generateToken(akrosUserToBeSaved);
                log.info(OK.getReasonPhrase());
                return SERVICE_UTILITY.employeeResponseDto(MAPPER.mapToLoginRequestDto(akrosUserToBeSaved), OK,
                        "User found by username ", null,
                        path, jwtToken);
            }
            return employeeNotFoundByUsername(username, AKROS_USER_API_PATH + "reset-password/" + username, NOT_FOUND_BY_USERNAME, NOT_FOUND_BY_USERNAME, NOT_FOUND);
        }
        return employeeNotFoundByUsername(adminUsername, path, NOT_FOUND_BY_USERNAME, NOT_FOUND_BY_USERNAME, NOT_FOUND);
    }

    /**
     * @param loginRequestDto authenticateByEmail
     * @return EmployeeResponseDto
     */
    private EmployeeResponseDto authenticateByEmail(LoginRequestDto loginRequestDto) {
        String email = loginRequestDto.getEmail();
        var akrosUserOptional = repository.findByEmail(email);
        if (akrosUserOptional.isPresent()) {
            AkrosUser akrosUser = akrosUserOptional.get();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(akrosUser.getUsername(), loginRequestDto.getPassword()));
            setLastLoginDate(akrosUserOptional.get());
            var jwtToken = jwtService.generateToken(akrosUser);
            log.info(OK.getReasonPhrase());

            return SERVICE_UTILITY.employeeResponseDto(MAPPER.mapToLoginRequestDto(akrosUser), OK,
                    "User found by username", null,
                    AKROS_USER_API_PATH + email, jwtToken);
        }
        log.error("User not found by email: {}", email);
        log.info(NOT_FOUND.getReasonPhrase());
        return SERVICE_UTILITY.employeeResponseDto(new LoginRequestDto(), NOT_FOUND,
                "User not found by email: " + email, "User not found by email: " + email,
                AKROS_USER_API_PATH + email, null);
    }

    private static EmployeeResponseDto employeeNotFoundByUsername(String username, String path, String description, String error, HttpStatus httpStatus) {
        return SERVICE_UTILITY.employeeResponseDto(new LoginRequestDto(), httpStatus,
                description + username, error + username,
                path, null);
    }

    private boolean isAdmin(AkrosUser akrosUser) {
        Role role = akrosUser.getRole();
        return role == Role.ROLE_ADMIN || role == Role.ROLE_SUPER_ADMIN;
    }

    private void setLastLoginDate(AkrosUser akrosUser) {
        akrosUser.setLastLoginDate(new Date().toString());
        repository.save(akrosUser);
    }

    private EmployeeResponseDto validateDto(LoginRequestDto loginRequestDto, String path, String startedSaveEmployee) {
        EmployeeRequestDto employeeRequestDto = MAPPER.mapToEmployeeRequestDto(MAPPER.mapToLoginRequestDto(findAkrosUserByUsername(loginRequestDto.getUsername())));

        HttpStatus httpStatus = OK;
        var validation = EmployeeValidator
                .isEmployeeValid()
                .apply(employeeRequestDto);
        log.info("ValidationResult: {}", validation);
        if(validation == VALID) {
            String username = loginRequestDto.getUsername();
            log.error("Username already in user: {}", username);
            log.info(startedSaveEmployee);
            validation = employeeUsernameAlreadyInUser(username).apply(employeeRequestDto);

            httpStatus = NOT_FOUND;
            return SERVICE_UTILITY.employeeResponseDto(employeeRequestDto, httpStatus,
                    validation.getDescription(), httpStatus.toString(), path);

        }

        employeeRequestDto = MAPPER.mapToEmployeeRequestDto(MAPPER.mapToLoginRequestDto(findAkrosUserByEmail(loginRequestDto.getEmail())));
        validation = EmployeeValidator
                .isEmployeeValid()
                .apply(employeeRequestDto);
        if(validation == VALID) {
            validation = employeeEmailAlreadyInUser(loginRequestDto.getEmail()).apply(employeeRequestDto);
            log.error("Email already in user: {}", loginRequestDto.getEmail());
            log.info(startedSaveEmployee);
            httpStatus = NOT_ACCEPTABLE;
            return SERVICE_UTILITY.employeeResponseDto(employeeRequestDto, httpStatus,
                    validation.getDescription(), httpStatus.toString(), path);
        }

        employeeRequestDto = MAPPER.mapToEmployeeRequestDto(loginRequestDto);
        validation = isEmployeeEmailValid().and(isEmployeeUsernameValid()).and(isEmployeePasswordValid()).apply(employeeRequestDto);
        if(validation != VALID) {
            log.error(validation.getDescription());
            log.info(startedSaveEmployee);
            httpStatus = BAD_REQUEST;
            return SERVICE_UTILITY.employeeResponseDto(employeeRequestDto, httpStatus,
                    validation.getDescription(), httpStatus.toString(), EMPLOYEE_API_PATH);

        }

        return SERVICE_UTILITY.employeeResponseDto(employeeRequestDto, httpStatus,
                validation.getDescription(), httpStatus.toString(), path);
    }

    private AkrosUser findAkrosUserByEmail(String email) {
        var employeeOptional = repository.findByEmail(email);
        return employeeOptional.orElse(null);

    }

    private AkrosUser findAkrosUserByUsername(String username) {
        return repository.findByUsername(username.toLowerCase()).orElse(null);
    }
}
