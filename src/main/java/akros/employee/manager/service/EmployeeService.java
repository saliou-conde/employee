package akros.employee.manager.service;

import akros.employee.manager.dto.EmployeeRequestDto;
import akros.employee.manager.dto.EmployeeResponseDto;

import java.util.List;


public interface EmployeeService {
    EmployeeResponseDto saveEmployee(EmployeeRequestDto dto);
    EmployeeResponseDto findEmployeeByEmail(String email);
    List<EmployeeRequestDto> findAllEmployees() ;
    EmployeeResponseDto deleteEmployeeByEmail(String email);
    void deleteAllEmployees();
}
