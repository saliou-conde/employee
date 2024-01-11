package akros.employee.employeemanager.domain.mapper;

import akros.employee.employeemanager.domain.Employee;
import akros.employee.employeemanager.domain.dto.EmployeeRequestDto;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EmployeeMapperTest {

    @Test
    void mapToEmployee() {
        //Given
        EmployeeRequestDto requestDto = new EmployeeRequestDto();
        requestDto.setEmployeeId(UUID.randomUUID().toString());
        requestDto.setEmail("saliou-conde@gmx.de");
        requestDto.setFirstname("Saliou");
        requestDto.setLastname("Condé");
        requestDto.setPassword("19A12iou#");
        requestDto.setJobCode(UUID.randomUUID().toString());

        //When
        Employee employee = EmployeeMapper.INSTANCE.mapToEmployee(requestDto);

        //Then
        assertThat(employee).isNotNull();
        assertThat(employee.getPassword()).isEqualTo("19A12iou#");

    }

    @Test
    void mapToEmployeeRequestDto() {
        //Given
        Employee employee = new Employee();
        employee.setId(UUID.randomUUID().toString());
        employee.setEmail("saliou-conde@gmx.de");
        employee.setFirstname("Saliou");
        employee.setLastname("Condé");
        employee.setJobCode(UUID.randomUUID().toString());

        //When
        EmployeeRequestDto employeeRequestDto = EmployeeMapper.INSTANCE.mapToEmployeeRequestDto(employee);

        //Then
        assertThat(employeeRequestDto).isNotNull();
    }
}