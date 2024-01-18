package akros.employee.manager.repository;

import akros.employee.manager.domain.AkrosUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AkrosUserRepository extends JpaRepository<AkrosUser, String> {
    Optional<AkrosUser> findByUsername(String username);
}
