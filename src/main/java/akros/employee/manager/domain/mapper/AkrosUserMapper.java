package akros.employee.manager.domain.mapper;

import akros.employee.manager.domain.AkrosUser;
import akros.employee.manager.dto.LoginRequestDto;
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
    //@Mapping(source = "id", target = "id")
    LoginRequestDto mapToLoginRequestDto(AkrosUser dto);
}
