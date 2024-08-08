package akros.employee.manager.service;

import akros.employee.manager.dto.EmployeeResponseDto;
import akros.employee.manager.dto.LoginRequestDto;

public interface AkrosUserService {
    EmployeeResponseDto register(LoginRequestDto loginRequestDto);
    EmployeeResponseDto authenticate(LoginRequestDto loginRequestDto);
    EmployeeResponseDto active(String username);
    void deleteAllUsers();
    EmployeeResponseDto changePassword(String username, String oldPassword, String newPassword);
    EmployeeResponseDto resetPassword(String adminUsername, LoginRequestDto loginRequestDto);
}
