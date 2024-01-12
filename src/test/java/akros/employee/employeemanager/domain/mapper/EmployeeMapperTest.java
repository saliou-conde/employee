package akros.employee.employeemanager.domain.mapper;

import akros.employee.employeemanager.domain.Employee;
import akros.employee.employeemanager.domain.dto.HttpRequestDto;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EmployeeMapperTest {

    @Test
    void mapToEmployee() {
        //Given
        HttpRequestDto requestDto = new HttpRequestDto();
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
        assertThat(employee.getEmail()).isEqualTo(requestDto.getEmail());
        assertThat(employee.getPassword()).isEqualTo(requestDto.getPassword());
        assertThat(employee.getId()).isEqualTo(requestDto.getEmployeeId());
        assertThat(employee.getFirstname()).isEqualTo(requestDto.getFirstname());
        assertThat(employee.getLastname()).isEqualTo(requestDto.getLastname());
        assertThat(employee.getJobCode()).isEqualTo(requestDto.getJobCode());

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
        HttpRequestDto httpRequestDto = EmployeeMapper.INSTANCE.mapToEmployeeRequestDto(employee);

        //Then
        assertThat(httpRequestDto).isNotNull();
        assertThat(employee.getEmail()).isEqualTo(httpRequestDto.getEmail());
        assertThat(employee.getPassword()).isEqualTo(httpRequestDto.getPassword());
        assertThat(employee.getId()).isEqualTo(httpRequestDto.getEmployeeId());
        assertThat(employee.getFirstname()).isEqualTo(httpRequestDto.getFirstname());
        assertThat(employee.getLastname()).isEqualTo(httpRequestDto.getLastname());
        assertThat(employee.getJobCode()).isEqualTo(httpRequestDto.getJobCode());
    }
}