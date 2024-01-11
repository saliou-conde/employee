package akros.employee.employeemanager.service.impl;

import akros.employee.employeemanager.domain.Employee;
import akros.employee.employeemanager.domain.dto.EmployeeRequestDto;
import akros.employee.employeemanager.domain.dto.HttpResponseDto;
import akros.employee.employeemanager.domain.mapper.EmployeeMapper;
import akros.employee.employeemanager.repository.EmployeeRepository;
import akros.employee.employeemanager.service.EmployeeService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@Service
public class EmployeeServiceImpl implements EmployeeService<EmployeeRequestDto, HttpResponseDto> {

    private final EmployeeRepository repository;
    private static final EmployeeMapper mapper = EmployeeMapper.INSTANCE;

    public EmployeeServiceImpl(EmployeeRepository repository) {
        this.repository = repository;
    }

    public EmployeeRequestDto saveEmployee(EmployeeRequestDto dto) {

        dto.setEmployeeId(UUID.randomUUID().toString());
        Employee employeeToSave = mapper.mapToEmployee(dto);
        Employee employee = repository.save(employeeToSave);

        return mapper.mapToEmployeeRequestDto(employee);
    }
    public HttpResponseDto findEmployeeByEmail(String email) {
        String path = "/api/v1/employees/"+email;
        var employeeRequestDto = mapper.mapToEmployeeRequestDto(findEmployee(email));
        if(employeeRequestDto == null) {
            return HttpResponseDto
                    .builder()
                    .message("Employee not found by email: "+email)
                    .path(path)
                    .status(NOT_FOUND)
                    .statusCode(NOT_FOUND.value())
                    .error("Not found")
                    .build();

        }
        return HttpResponseDto
                .builder()
                .message("Employee found by email: "+email)
                .path(path)
                .status(OK)
                .statusCode(OK.value())
                .data(Map.of("Employee Deleted", employeeRequestDto))
                .build();
    }

    public List<EmployeeRequestDto> findAllEmployees() {
        List<Employee> employees = repository.findAll();

        return employees
                .stream()
                .map(mapper::mapToEmployeeRequestDto)
                .toList();
    }
    public HttpResponseDto deleteEmployeeByEmail(String email) {
        String path = "/api/v1/employees/"+email;

        Employee employeeOptional = findEmployee(email);
        if(employeeOptional != null) {
            repository.delete(employeeOptional);
            return HttpResponseDto
                    .builder()
                    .message("Employee with the email: "+email+" has been deleted.")
                    .path(path)
                    .status(OK)
                    .statusCode(200)
                    .data(Map.of("Employee Deleted", employeeOptional))
                    .build();
        }

        return  HttpResponseDto
                .builder()
                .error("Not found")
                .message("Employee with the email: "+email+" has been deleted.")
                .status(NOT_FOUND)
                .statusCode(404)
                .path(path)
                .build();
    }

    @Override
    public void deleteAllEmployees() {
        repository.deleteAll();
    }

    private Employee findEmployee(String email) {
        Optional<Employee> employeeOptional = repository.findByEmail(email);
        return employeeOptional.orElse(null);

    }
}
