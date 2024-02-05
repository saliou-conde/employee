package akros.employee.manager.utility;

import akros.employee.manager.dto.EmployeeRequestDto;
import akros.employee.manager.dto.EmployeeResponseDto;
import akros.employee.manager.dto.LoginRequestDto;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Map;

import static akros.employee.manager.constant.AppConstant.AKROS_USER;
import static akros.employee.manager.constant.AppConstant.EMPLOYEE;

public final class ServiceUtility {

    private ServiceUtility() {

    }
    private static ServiceUtility utility;

    public static ServiceUtility getUtility() {
        if(utility == null) {
            utility = new ServiceUtility();
            return utility;
        }
        return utility;
    }

    public EmployeeResponseDto employeeResponseDto(EmployeeRequestDto requestDto, HttpStatus status, String description, String error, String path) {
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
    public EmployeeResponseDto employeeResponseDto(LoginRequestDto requestDto, HttpStatus status, String description, String error, String path, String token) {
        return EmployeeResponseDto.builder()
                .timestamp(Instant.now().toString())
                .status(status)
                .statusCode(status.value())
                .error(error)
                .data(Map.of(AKROS_USER, requestDto))
                .message(description)
                .path(path)
                .token(token)
                .build();
    }
}
