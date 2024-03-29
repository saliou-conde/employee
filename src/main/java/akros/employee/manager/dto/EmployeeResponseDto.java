package akros.employee.manager.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
@Setter
@Builder
public class EmployeeResponseDto {
    private String message;
    private String error;
    private String path;
    HttpStatus status;
    private Integer statusCode;
    private String timestamp;
    private Map<?,?> data;
    private String token;
}
