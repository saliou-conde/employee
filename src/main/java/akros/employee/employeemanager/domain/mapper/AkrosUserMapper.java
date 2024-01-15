package akros.employee.employeemanager.domain.mapper;

import akros.employee.employeemanager.domain.AkrosUser;
import akros.employee.employeemanager.domain.dto.LoginRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AkrosUserMapper {
    AkrosUserMapper INSTANCE = Mappers.getMapper(AkrosUserMapper.class);

    @Mapping(target = "lastLoginDate", ignore = true)
    @Mapping(target = "joinDate", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "notLocked", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    AkrosUser mapToAkrosUser(LoginRequestDto dto);
    @Mapping(source = "id", target = "id")
    LoginRequestDto mapToLoginRequestDto(AkrosUser dto);
}
