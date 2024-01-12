package akros.employee.employeemanager.service;

import java.util.List;


public interface EmployeeService<T, U> {
    U saveEmployee(T dto);
    U findEmployeeByEmail(String email);
    List<T> findAllEmployees() ;
    U deleteEmployeeByEmail(String email);
    void deleteAllEmployees();
}
