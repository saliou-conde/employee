package akros.employee.manager.domain.mapper;

import akros.employee.manager.domain.Employee;
import akros.employee.manager.dto.EmployeeRequestDto;
import org.junit.jupiter.api.Test;

import static akros.employee.manager.domain.mapper.EmployeeMapper.INSTANCE;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;

class EmployeeMapperTest {

    @Test
    void mapToEmployee() {
        //Given
        var requestDto = new EmployeeRequestDto();
        requestDto.setEmployeeId(randomUUID().toString());
        requestDto.setEmail("saliou-conde@gmx.de");
        requestDto.setFirstname("Saliou");
        requestDto.setLastname("Condé");
        requestDto.setPassword("19A12iou#");
        requestDto.setJobCode(randomUUID().toString());

        //When
        var employee = INSTANCE.mapToEmployee(requestDto);

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
        var employee = new Employee(
                randomUUID().toString(),
                "Saliou",
                "Condé",
                "saliou-conde@gmx.de",
                randomUUID().toString(),
                "123456");

        //When
        var httpRequestDto = INSTANCE.mapToEmployeeRequestDto(employee);

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