package akros.employee.employeemanager.service;

import akros.employee.employeemanager.domain.dto.HttpRequestDto;
import akros.employee.employeemanager.domain.dto.HttpResponseDto;

import java.util.List;


public interface EmployeeService {
    HttpResponseDto saveEmployee(HttpRequestDto dto);
    HttpResponseDto findEmployeeByEmail(String email);
    List<HttpRequestDto> findAllEmployees() ;
    HttpResponseDto deleteEmployeeByEmail(String email);
    void deleteAllEmployees();
}
