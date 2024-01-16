package akros.employee.employeemanager.service;

import akros.employee.employeemanager.domain.dto.EmployeeRequestDto;
import akros.employee.employeemanager.domain.dto.HttpResponseDto;

import java.util.List;


public interface EmployeeService {
    HttpResponseDto saveEmployee(EmployeeRequestDto dto);
    HttpResponseDto findEmployeeByEmail(String email);
    List<EmployeeRequestDto> findAllEmployees() ;
    HttpResponseDto deleteEmployeeByEmail(String email);
    void deleteAllEmployees();
}
