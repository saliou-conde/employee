package akros.employee.employeemanager.domain.mapper;

import akros.employee.employeemanager.domain.Employee;
import akros.employee.employeemanager.domain.dto.HttpRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EmployeeMapper {
    EmployeeMapper INSTANCE = Mappers.getMapper(EmployeeMapper.class);
    @Mapping(source = "employeeId", target = "id")
    Employee mapToEmployee(HttpRequestDto httpRequestDto);
    @Mapping(source = "id", target = "employeeId")
    HttpRequestDto mapToEmployeeRequestDto(Employee employee);

}
