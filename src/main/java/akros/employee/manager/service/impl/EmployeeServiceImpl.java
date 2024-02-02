package akros.employee.manager.service.impl;

import akros.employee.manager.domain.Employee;
import akros.employee.manager.dto.EmployeeRequestDto;
import akros.employee.manager.dto.EmployeeResponseDto;
import akros.employee.manager.domain.mapper.EmployeeMapper;
import akros.employee.manager.domain.plaisibility.EmployeeValidator;
import akros.employee.manager.repository.EmployeeRepository;
import akros.employee.manager.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static akros.employee.manager.constant.AppConstant.EMPLOYEE_API_PATH;
import static akros.employee.manager.constant.AppConstant.EMPLOYEE;
import static akros.employee.manager.domain.mapper.EmployeeMapper.INSTANCE;
import static akros.employee.manager.domain.plaisibility.EmployeeValidation.VALID;
import static akros.employee.manager.domain.plaisibility.EmployeeValidator.isEmployeeUsernameValid;
import static org.springframework.http.HttpStatus.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository repository;

    private final PasswordEncoder passwordEncoder;
    private static final EmployeeMapper EMPLOYEE_MAPPER = INSTANCE;

    public EmployeeResponseDto saveEmployee(EmployeeRequestDto dto) {
        log.info("Starting saveEmployee");
        var findEmployee = findEmployee(dto.getEmail());
        var validation = EmployeeValidator
                .isEmployeeValid()
                .apply(findEmployee);
        log.info("ValidationResult: {}", validation);

        if( validation == VALID) {
            String email = dto.getEmail();
            validation = EmployeeValidator.employeeEmailAlreadyInUser(email).apply(findEmployee);
            log.error("Employee email already in user: {}", email);
            log.info("Started saveEmployee");

            return employeeResponseDto(dto, NOT_ACCEPTABLE,
                    validation.getDescription(), NOT_ACCEPTABLE.toString(), EMPLOYEE_API_PATH);
        }

        var employee = EMPLOYEE_MAPPER.mapToEmployee(dto);
        validation = EmployeeValidator
                .isEmployeeEmailValid()
                .and(isEmployeeUsernameValid())
                .and(EmployeeValidator.isEmployeePasswordValid(employee.getPassword()))
                .apply(employee);
        if(validation != VALID) {
            log.error(validation.getDescription());
            log.info("Started saveEmployee");
            return employeeResponseDto(dto, BAD_REQUEST,
                    validation.getDescription(), BAD_REQUEST.toString(), EMPLOYEE_API_PATH);

        }

        dto.setEmployeeId(UUID.randomUUID().toString());
        var employeeToSave = EMPLOYEE_MAPPER.mapToEmployee(dto);
        employeeToSave.setPassword(passwordEncoder.encode(dto.getPassword()));
        employeeToSave.setUsername(dto.getUsername().toLowerCase());
        repository.save(employeeToSave);
        log.info("Started saveEmployee");

        return employeeResponseDto(dto, CREATED,
                "Employee Successfully Added", null, EMPLOYEE_API_PATH);
    }

    public EmployeeResponseDto findEmployeeByEmail(String email) {
        log.info("Starting findEmployeeByEmail");
        var path = EMPLOYEE_API_PATH +email;
        var employeeRequestDto = EMPLOYEE_MAPPER.mapToEmployeeRequestDto(findEmployee(email));
        var validation = EmployeeValidator.findEmployeeByEmail(email).apply(findEmployee(email));
        if(validation != VALID) {
            log.error(validation.getDescription());
            log.info("Started findEmployeeByEmail");
            return employeeResponseDto(new EmployeeRequestDto(), NOT_FOUND,
                    "Employee not found by email: "+email, NOT_FOUND.toString(), path);

        }

        log.info("Started findEmployeeByEmail");
        return employeeResponseDto(employeeRequestDto, OK,
                "Employee found by email: "+email, OK.toString(), path);
    }

    public List<EmployeeRequestDto> findAllEmployees() {
        return repository.findAll()
                .stream()
                .map(EMPLOYEE_MAPPER::mapToEmployeeRequestDto)
                .toList();
    }

    public EmployeeResponseDto deleteEmployeeByEmail(String email) {
        log.info("Starting deleteEmployeeByEmail");
        var path = EMPLOYEE_API_PATH +email;
        var employeeOptional = findEmployee(email);
        if(employeeOptional != null) {
            log.info("Employee found by email: {}", email);
            repository.delete(employeeOptional);
            log.info("Started deleteEmployeeByEmail");
            return employeeResponseDto(EMPLOYEE_MAPPER.mapToEmployeeRequestDto(employeeOptional), OK, "Employee with the email: "+email+" has been deleted.", OK.toString(), path);
        }

        log.error("Employee not found by email.");
        log.info("Started deleteEmployeeByEmail");
        return employeeResponseDto(new EmployeeRequestDto(), NOT_FOUND, "Employee not found by email.", NOT_FOUND.toString(), path);
    }

    @Override
    public void deleteAllEmployees() {
        repository.deleteAll();
    }

    private Employee findEmployee(String email) {
        var employeeOptional = repository.findByEmail(email);
        return employeeOptional.orElse(null);

    }

    private EmployeeResponseDto employeeResponseDto(EmployeeRequestDto requestDto, HttpStatus status, String description, String error, String path) {
        return EmployeeResponseDto.builder()
                .timestamp(Instant.now().toString())
                .status(status)
                .statusCode(status.value())
                .error(error)
                .data(Map.of(EMPLOYEE, requestDto))
                .message(description)
                .path(path)
                .build();
    }
}
