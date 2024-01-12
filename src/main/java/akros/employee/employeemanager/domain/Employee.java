package akros.employee.employeemanager.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity
public class Employee {

    @Id
    private String id;
    @Column(nullable = false)
    private String firstname;
    @Column(nullable = false)
    private String lastname;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false, unique = true)
    private String jobCode;
    @Column(nullable = false)
    //@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    public Employee(String id, String firstname, String lastname, String email, String jobCode, String password) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.jobCode = jobCode;
        this.password = password;
    }

    public Employee() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getJobCode() {
        return jobCode;
    }

    public void setJobCode(String jobCode) {
        this.jobCode = jobCode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(id, employee.id) && Objects.equals(firstname, employee.firstname)
                && Objects.equals(lastname, employee.lastname)
                && Objects.equals(email, employee.email)
                && Objects.equals(jobCode, employee.jobCode)
                && Objects.equals(password, employee.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstname, lastname, email, jobCode, password);
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id='" + id + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", email='" + email + '\'' +
                ", jobCode='" + jobCode + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
