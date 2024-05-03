package cs.vsu.ReportingGenerationService.repository;

import cs.vsu.ReportingGenerationService.enums.Role;
import cs.vsu.ReportingGenerationService.model.Authority;
import cs.vsu.ReportingGenerationService.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);

    Optional<User> findById(Long id);

    void deleteById(Long id);

    Optional<List<User>> findAllByAuthoritiesRole(Role role);

    Optional<List<User>> findAllByAuthoritiesRoleNot(Role role);
}
