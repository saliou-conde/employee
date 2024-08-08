package akros.employee.manager.testdata;

import akros.employee.manager.dto.EmployeeRequestDto;
import org.instancio.Instancio;

import java.util.UUID;

public class EmployeeTestDataBuilder {
    protected <T extends EmployeeRequestDto> T build(T employeeRequestDto) {
        if (employeeRequestDto.getEmployeeId() == null) {
            employeeRequestDto.setEmployeeId(UUID.randomUUID().toString());

        }
        return employeeRequestDto;
    }

    public EmployeeRequestDtoBuilder anEmployeeRequestDto() {
        return new EmployeeRequestDtoBuilder().anEmployeeRequestDto();
    }

    public EmployeeRequestDto anyEmployeeRequestDto() {
        return anEmployeeRequestDto().build();
    }


    public class EmployeeRequestDtoBuilder {
        private EmployeeRequestDto employeeRequestDto;

        public EmployeeRequestDtoBuilder anEmployeeRequestDto() {
            employeeRequestDto = Instancio.of(EmployeeRequestDto.class)
                    .create();
            return this;
        }

        public EmployeeRequestDtoBuilder withEmployeeId(final String employeeId) {
            employeeRequestDto.setEmployeeId(employeeId);
            return this;
        }

        public EmployeeRequestDtoBuilder withEmployeeUsername(final String username) {
            employeeRequestDto.setUsername(username);
            return this;
        }

        public EmployeeRequestDtoBuilder withEmployeeEmail(final String email) {
            employeeRequestDto.setEmail(email);
            return this;
        }

        public EmployeeRequestDtoBuilder withEmployeePassword(final String password) {
            employeeRequestDto.setPassword(password);
            return this;
        }

        public EmployeeRequestDtoBuilder withEmployeeFirstname(final String firstname) {
            employeeRequestDto.setLastname(firstname);
            return this;
        }

        public EmployeeRequestDtoBuilder withEmployeeLastname(final String lastname) {
            employeeRequestDto.setLastname(lastname);
            return this;
        }

        public EmployeeRequestDto build() {
            return EmployeeTestDataBuilder.this.build(employeeRequestDto);
        }
    }
}
