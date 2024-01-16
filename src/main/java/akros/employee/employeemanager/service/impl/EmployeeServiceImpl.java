package akros.employee.employeemanager.service.impl;

import akros.employee.employeemanager.domain.Employee;
import akros.employee.employeemanager.domain.dto.EmployeeRequestDto;
import akros.employee.employeemanager.domain.dto.HttpResponseDto;
import akros.employee.employeemanager.domain.mapper.EmployeeMapper;
import akros.employee.employeemanager.domain.plaisibility.EmployeeValidator;
import akros.employee.employeemanager.repository.EmployeeRepository;
import akros.employee.employeemanager.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static akros.employee.employeemanager.constant.AppConstant.EMPLOYEE_API_PATH;
import static akros.employee.employeemanager.constant.AppConstant.EMPLOYEE;
import static akros.employee.employeemanager.domain.mapper.EmployeeMapper.INSTANCE;
import static akros.employee.employeemanager.domain.plaisibility.EmployeeValidation.VALID;
import static org.springframework.http.HttpStatus.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository repository;
    private static final EmployeeMapper EMPLOYEE_MAPPER = INSTANCE;

    public HttpResponseDto saveEmployee(EmployeeRequestDto dto) {
        var findEmployee = findEmployee(dto.getEmail());
        var validation = EmployeeValidator
                .isEmployeeValid()
                .apply(findEmployee);
        log.info("ValidationResult: {}", validation);

        if( validation == VALID) {
            validation = EmployeeValidator.employeeEmailAlreadyInUser(dto.getEmail()).apply(findEmployee);

            return HttpResponseDto.builder()
                    .timestamp(Instant.now().toString())
                    .status(NOT_ACCEPTABLE)
                    .statusCode(NOT_ACCEPTABLE.value())
                    .error(NOT_ACCEPTABLE.toString())
                    .data(Map.of(EMPLOYEE, new EmployeeRequestDto()))
                    .message(validation.getDescription())
                    .path(EMPLOYEE_API_PATH)
                    .build();
        }

        var employee = EMPLOYEE_MAPPER.mapToEmployee(dto);
        validation = EmployeeValidator
                .isEmployeeEmailValid()
                .and(EmployeeValidator.isEmployeePasswordValid(employee.getPassword()))
                .apply(employee);
        if(validation != VALID) {
            return HttpResponseDto.builder()
                    .timestamp(Instant.now().toString())
                    .status(BAD_REQUEST)
                    .statusCode(BAD_REQUEST.value())
                    .error(BAD_REQUEST.toString())
                    .message(validation.getDescription())
                    .data(Map.of(BAD_REQUEST, dto))
                    .path(EMPLOYEE_API_PATH)
                    .build();

        }

        dto.setEmployeeId(UUID.randomUUID().toString());
        var employeeToSave = EMPLOYEE_MAPPER.mapToEmployee(dto);
        repository.save(employeeToSave);

        return HttpResponseDto.builder()
                .timestamp(Instant.now().toString())
                .status(CREATED)
                .statusCode(CREATED.value())
                .message("Employee Successfully Added")
                .data(Map.of(EMPLOYEE, dto))
                .path(EMPLOYEE_API_PATH)
                .build();
    }

    public HttpResponseDto findEmployeeByEmail(String email) {
        var path = EMPLOYEE_API_PATH +email;
        var employeeRequestDto = EMPLOYEE_MAPPER.mapToEmployeeRequestDto(findEmployee(email));
        var validation = EmployeeValidator.findEmployeeByEmail(email).apply(findEmployee(email));
        if(validation != VALID) {
            return HttpResponseDto.builder()
                    .timestamp(Instant.now().toString())
                    .message("Employee not found by email: "+email)
                    .path(path)
                    .status(NOT_FOUND)
                    .statusCode(NOT_FOUND.value())
                    .error("Not found")
                    .build();

        }

        return HttpResponseDto.builder()
                .timestamp(Instant.now().toString())
                .message("Employee found by email: "+email)
                .path(path)
                .status(OK)
                .statusCode(OK.value())
                .data(Map.of(EMPLOYEE, employeeRequestDto))
                .data(Map.of("Employee Deleted", employeeRequestDto))
                .build();
    }

    public List<EmployeeRequestDto> findAllEmployees() {
        return repository.findAll()
                .stream()
                .map(EMPLOYEE_MAPPER::mapToEmployeeRequestDto)
                .toList();
    }

    public HttpResponseDto deleteEmployeeByEmail(String email) {
        var path = EMPLOYEE_API_PATH +email;
        var employeeOptional = findEmployee(email);
        if(employeeOptional != null) {
            repository.delete(employeeOptional);
            return HttpResponseDto.builder()
                    .timestamp(Instant.now().toString())
                    .message("Employee with the email: "+email+" has been deleted.")
                    .path(path)
                    .status(OK)
                    .statusCode(OK.value())
                    .data(Map.of(EMPLOYEE, employeeOptional))
                    .build();
        }

        return  HttpResponseDto.builder()
                .timestamp(Instant.now().toString())
                .error(NOT_FOUND.toString())
                .message("Employee with the email: "+email+" has been deleted.")
                .status(NOT_FOUND)
                .statusCode(NOT_FOUND.value())
                .path(path)
                .build();
    }

    @Override
    public void deleteAllEmployees() {
        repository.deleteAll();
    }

    private Employee findEmployee(String email) {
        var employeeOptional = repository.findByEmail(email);
        return employeeOptional.orElse(null);

    }
}
