package akros.employee.employeemanager.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeRequestDto {

    private String employeeId;
    private String firstname;
    private String lastname;
    private String email;
    private String jobCode;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
}
