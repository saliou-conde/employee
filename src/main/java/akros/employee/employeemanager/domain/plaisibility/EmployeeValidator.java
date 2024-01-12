package akros.employee.employeemanager.domain.plaisibility;

import akros.employee.employeemanager.domain.Employee;

import java.util.function.Function;

import static akros.employee.employeemanager.domain.plaisibility.EmployeeValidation.*;

public interface EmployeeValidator extends Function<Employee, EmployeeValidation> {


    static EmployeeValidator findEmployeeByEmail(String email) {
        return employee -> employee != null && employee.getEmail().equals(email) ? VALID : EMPLOYEE_NOT_FOUND_BY_EMAIL;
    }
    static EmployeeValidator isEmployeeValid() {
        return employee -> employee != null &&
                employee.getEmail() != null &&
                employee.getPassword() != null &&
                employee.getJobCode() != null &&
                employee.getId() != null ? VALID : EMPLOYEE_NOT_EXISTS;
    }

    static EmployeeValidator isEmployeeEmailValid() {
        return employee -> employee.getEmail().contains("@") ? VALID : EMAIL_NOT_VALID;
    }

    static EmployeeValidator isEmployeePasswordValid(String password) {
        return employee -> employee.getPassword() != null &&
                employee.getPassword().equals(password)  ? VALID : PASSWORD_NOT_VALID;
    }

    static EmployeeValidator employeeEmailAlreadyInUser(String email) {
        return employee -> employee != null && !employee.getEmail().equalsIgnoreCase(email) ?
                VALID : EMPLOYEE_EMAIL_ALREADY_IN_USE;
    }

    default EmployeeValidator and(EmployeeValidator other) {
        return employee -> {
            EmployeeValidation result = this.apply(employee);
            return result.equals(EmployeeValidation.VALID) ? other.apply(employee) : result;
        };
    }
}
