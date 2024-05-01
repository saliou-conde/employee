package akros.employee.manager.service.impl;

import akros.employee.manager.domain.Employee;
import akros.employee.manager.dto.EmployeeRequestDto;
import akros.employee.manager.dto.EmployeeResponseDto;
import akros.employee.manager.domain.mapper.EmployeeMapper;
import akros.employee.manager.domain.plaisibility.EmployeeValidator;
import akros.employee.manager.repository.EmployeeRepository;
import akros.employee.manager.service.EmployeeService;
import akros.employee.manager.utility.ServiceUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static akros.employee.manager.constant.AppConstant.EMPLOYEE_API_PATH;
import static akros.employee.manager.domain.mapper.EmployeeMapper.INSTANCE;
import static akros.employee.manager.domain.plaisibility.EmployeeValidation.VALID;
import static akros.employee.manager.domain.plaisibility.EmployeeValidator.employeeUsernameAlreadyInUser;
import static akros.employee.manager.domain.plaisibility.EmployeeValidator.isEmployeeUsernameValid;
import static org.springframework.http.HttpStatus.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository repository;

    private final PasswordEncoder passwordEncoder;
    private static final EmployeeMapper EMPLOYEE_MAPPER = INSTANCE;
    private static final ServiceUtility SERVICE_UTILITY = ServiceUtility.getInstance();

    public EmployeeResponseDto saveEmployee(EmployeeRequestDto dto) {
        log.info("Starting saveEmployee");
        var findEmployee = findEmployee(dto.getEmail());
        var validation = EmployeeValidator
                .isEmployeeValid()
                .apply(findEmployee);
        log.info("ValidationResult: {}", validation);

        String startedSaveEmployee = "Started saveEmployee";
        if( validation == VALID) {
            String email = dto.getEmail();
            validation = EmployeeValidator.employeeEmailAlreadyInUser(email).apply(findEmployee);
            log.error("Employee email already in user: {}", email);
            log.info(startedSaveEmployee);

            return SERVICE_UTILITY.employeeResponseDto(dto, NOT_ACCEPTABLE,
                    validation.getDescription(), NOT_ACCEPTABLE.toString(), EMPLOYEE_API_PATH);
        }

        var findByUsername = findEmployeeByUsername(dto.getUsername());
        validation = employeeUsernameAlreadyInUser(dto.getUsername()).apply(findByUsername);
        if(validation != VALID) {
            log.error(validation.getDescription());
            log.info(startedSaveEmployee);
            return SERVICE_UTILITY.employeeResponseDto(dto, NOT_ACCEPTABLE,
                    validation.getDescription(), NOT_ACCEPTABLE.toString(), EMPLOYEE_API_PATH);

        }

        var employee = EMPLOYEE_MAPPER.mapToEmployee(dto);
        validation = EmployeeValidator
                .isEmployeeEmailValid()
                .and(isEmployeeUsernameValid())
                .and(EmployeeValidator.isEmployeePasswordValid())
                .apply(employee);
        if(validation != VALID) {
            log.error(validation.getDescription());
            log.info(startedSaveEmployee);
            return SERVICE_UTILITY.employeeResponseDto(dto, BAD_REQUEST,
                    validation.getDescription(), BAD_REQUEST.toString(), EMPLOYEE_API_PATH);

        }


        dto.setEmployeeId(UUID.randomUUID().toString());
        var employeeToSave = EMPLOYEE_MAPPER.mapToEmployee(dto);

        employeeToSave.setPassword(passwordEncoder.encode(dto.getPassword()));
        employeeToSave.setUsername(dto.getUsername().toLowerCase());
        repository.save(employeeToSave);
        log.info(startedSaveEmployee);

        return SERVICE_UTILITY.employeeResponseDto(dto, CREATED,
                "Employee Successfully Added", null, EMPLOYEE_API_PATH);
    }

    @Override
    public EmployeeResponseDto updateEmployee(EmployeeRequestDto dto, String email) {
        var findEmployee = findEmployee(email);
        if(findEmployee != null) {
            findEmployee.setUsername(dto.getUsername() != null? dto.getUsername() : findEmployee.getUsername());
            findEmployee.setEmail(dto.getEmail() != null? dto.getEmail() : findEmployee.getEmail());
            findEmployee.setPassword(dto.getPassword() != null? dto.getPassword() : findEmployee.getPassword());
            findEmployee.setFirstname(dto.getFirstname() != null? dto.getFirstname() : findEmployee.getFirstname());
            findEmployee.setLastname(dto.getLastname() != null? dto.getLastname() : findEmployee.getLastname());
            repository.save(findEmployee);
            return SERVICE_UTILITY.employeeResponseDto(dto, OK,
                    "Employee Successfully Updated", null, EMPLOYEE_API_PATH);
        }
        return SERVICE_UTILITY.employeeResponseDto(dto, NOT_FOUND,
                "Employee Not Found by Email: "+email, NOT_FOUND.toString(), EMPLOYEE_API_PATH);
    }

    public EmployeeResponseDto findEmployeeByEmail(String email) {
        log.info("Starting findEmployeeByEmail");
        var path = EMPLOYEE_API_PATH +email;
        var employeeRequestDto = EMPLOYEE_MAPPER.mapToEmployeeRequestDto(findEmployee(email));
        var validation = EmployeeValidator.findEmployeeByEmail(email).apply(findEmployee(email));
        if(validation != VALID) {
            log.error(validation.getDescription());
            log.info("Started findEmployeeByEmail");
            return SERVICE_UTILITY.employeeResponseDto(new EmployeeRequestDto(), NOT_FOUND,
                    "Employee not found by email: "+email, NOT_FOUND.toString(), path);

        }

        log.info("Started findEmployeeByEmail");
        return SERVICE_UTILITY.employeeResponseDto(employeeRequestDto, OK,
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
            return SERVICE_UTILITY.employeeResponseDto(EMPLOYEE_MAPPER.mapToEmployeeRequestDto(employeeOptional), OK, "Employee with the email: "+email+" has been deleted.", OK.toString(), path);
        }

        log.error("Employee not found by email.");
        log.info("Started deleteEmployeeByEmail");
        return SERVICE_UTILITY.employeeResponseDto(new EmployeeRequestDto(), NOT_FOUND, "Employee not found by email.", NOT_FOUND.toString(), path);
    }

    @Override
    public void deleteAllEmployees() {
        repository.deleteAll();
    }

    private Employee findEmployee(String email) {
        var employeeOptional = repository.findByEmail(email);
        return employeeOptional.orElse(null);

    }

    private Employee findEmployeeByUsername(String username) {
        return repository.findByUsername(username.toLowerCase()).orElse(null);

    }
}
