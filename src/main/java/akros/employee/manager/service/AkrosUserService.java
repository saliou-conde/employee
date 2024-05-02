package akros.employee.manager.service;

import akros.employee.manager.dto.EmployeeResponseDto;
import akros.employee.manager.dto.LoginRequestDto;

public interface AkrosUserService {
    EmployeeResponseDto register(LoginRequestDto loginRequestDto);
    EmployeeResponseDto authenticate(LoginRequestDto loginRequestDto);
    EmployeeResponseDto active(String username);
    void deleteAllUsers();
    default EmployeeResponseDto findByEmail(String email) {
        return null;
    }
}
