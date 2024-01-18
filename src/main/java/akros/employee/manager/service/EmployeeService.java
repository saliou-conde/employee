package akros.employee.manager.service;

import akros.employee.manager.dto.EmployeeRequestDto;
import akros.employee.manager.dto.HttpResponseDto;

import java.util.List;


public interface EmployeeService {
    HttpResponseDto saveEmployee(EmployeeRequestDto dto);
    HttpResponseDto findEmployeeByEmail(String email);
    List<EmployeeRequestDto> findAllEmployees() ;
    HttpResponseDto deleteEmployeeByEmail(String email);
    void deleteAllEmployees();
}
