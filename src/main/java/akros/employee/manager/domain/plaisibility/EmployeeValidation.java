package akros.employee.manager.domain.plaisibility;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EmployeeValidation {
    VALID("Employee Valid"),
    EMPLOYEE_NOT_EXISTS("Employee not found"),
    EMPLOYEE_NOT_FOUND_BY_ID("Employee not found by ID"),
    EMPLOYEE_NOT_FOUND_BY_EMAIL("Employee not found by email"),
    EMAIL_NOT_VALID("Employee Email not Valid"),
    USER_NOT_VALID("Employee Username not Valid"),
    EMPLOYEE_NOT_ACTIVE("Employee not active"),
    PASSWORD_NOT_VALID("Employee Password not correct"),
    EMPLOYEE_EMAIL_ALREADY_IN_USE("Employee Email already in use");
    private final String description;
}
