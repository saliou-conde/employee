package akros.employee.manager.domain.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static akros.employee.manager.domain.Authority.*;

@Getter
@RequiredArgsConstructor
public enum Role {
    ROLE_USER(USER_AUTHORITIES),
    ROLE_HR(HR_AUTHORITIES),
    ROLE_MANAGER(MANAGER_AUTHORITIES),
    ROLE_ADMIN(ADMIN_AUTHORITIES),
    ROLE_SUPER_ADMIN(SUPER_ADMIN_AUTHORITIES);
    private final String[] authorities;

}
