package akros.employee.manager.domain.plaisibility;

import akros.employee.manager.domain.Employee;
import akros.employee.manager.dto.EmployeeRequestDto;

import java.util.function.Function;

import static akros.employee.manager.domain.plaisibility.EmployeeValidation.*;

public interface EmployeeValidator extends Function<EmployeeRequestDto, EmployeeValidation> {

    static EmployeeValidator findEmployeeByEmail(String email) {
        return employee -> employee != null && employee.getEmail().equals(email) ? VALID : EMPLOYEE_NOT_FOUND_BY_EMAIL;
    }
    static EmployeeValidator isEmployeeValid() {
        return employee -> employee != null ? VALID : EMPLOYEE_NOT_EXISTS;
    }

    static EmployeeValidator isEmployeeEmailValid() {
        return employee -> employee.getEmail().contains("@") ? VALID : EMAIL_NOT_VALID;
    }

    /**
     * 
     * @return check if the username is not null or empty
     */
    static EmployeeValidator isEmployeeUsernameValid() {
        return employee -> employee.getUsername() != null ? VALID : USER_NOT_VALID;
    }

    /**
     *
     * @return check if the employees password exists (please later check the following password > 7 && password.matches(".*[regex])
     */
    static EmployeeValidator isEmployeePasswordValid() {
        return employee -> employee.getPassword() != null ? VALID : PASSWORD_NOT_VALID;
    }

    static EmployeeValidator employeeEmailAlreadyInUser(String email) {
        return employee -> employee != null && !employee.getEmail().equalsIgnoreCase(email) ?
                VALID : EMPLOYEE_EMAIL_ALREADY_IN_USE;
    }

    static EmployeeValidator employeeUsernameAlreadyInUser(String username) {
        return employee -> employee != null && employee.getUsername().equalsIgnoreCase(username) ? EMPLOYEE_USERNAME_ALREADY_IN_USE : VALID;
    }

    /**
     *
     * @param other combine validation check
     * @return the combined validation(s)
     */
    default EmployeeValidator and(EmployeeValidator other) {
        return employee -> {
            var result = this.apply(employee);
            return result.equals(VALID) ? other.apply(employee) : result;
        };
    }
}
