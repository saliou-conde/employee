package akros.employee.manager.domain.mapper;

import akros.employee.manager.domain.Employee;
import akros.employee.manager.dto.EmployeeRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EmployeeMapper {
    EmployeeMapper INSTANCE = Mappers.getMapper(EmployeeMapper.class);
    @Mapping(source = "employeeId", target = "id")
    Employee mapToEmployee(EmployeeRequestDto employeeRequestDto);
    @Mapping(source = "id", target = "employeeId")
    EmployeeRequestDto mapToEmployeeRequestDto(Employee employee);

}
