package akros.employee.employeemanager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoginRequestDto {
    private String id;
    private String firstname;
    private String lastname;
    private String email;
    private String username;
    private String password;
    private String profileImageUrl;
}
