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

    private static ServiceUtility instance;

    private ServiceUtility() {
    }

    public static synchronized ServiceUtility getInstance() {
        if(instance == null) {
            instance = new ServiceUtility();
            return instance;
        }
        return instance;
    }

    public EmployeeResponseDto employeeResponseDto(EmployeeRequestDto requestDto, HttpStatus status, String description, String error, String path) {
        requestDto.setPassword("***********");
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
        requestDto.setPassword("***********");
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
