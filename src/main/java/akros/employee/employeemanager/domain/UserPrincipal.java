package akros.employee.employeemanager.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@RequiredArgsConstructor
public class UserPrincipal implements UserDetails {
    private final AkrosUser akrosUser;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return akrosUser.getAuthorities();
    }

    @Override
    public String getPassword() {
        return akrosUser.getPassword();
    }

    @Override
    public String getUsername() {
        return akrosUser.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return akrosUser.isNotLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return akrosUser.isActive();
    }
}
