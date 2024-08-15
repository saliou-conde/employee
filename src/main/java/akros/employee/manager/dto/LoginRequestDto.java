package akros.employee.manager.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LoginRequestDto {
    private String id;
    private String firstname;
    private String lastname;
    private String email;
    private String username;
    private String password;
    private String profileImageUrl;
}
