package akros.employee.employeemanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AkrosUser extends JpaRepository<AkrosUser, String> {
    Optional<AkrosUser> findAkrosUserByUsername(String username);
}
