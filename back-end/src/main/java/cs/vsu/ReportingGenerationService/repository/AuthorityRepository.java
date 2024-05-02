package cs.vsu.ReportingGenerationService.repository;

import cs.vsu.ReportingGenerationService.model.Authority;
import cs.vsu.ReportingGenerationService.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AuthorityRepository  extends JpaRepository<Authority, Integer> {

    Optional<Authority> findFirstByUserId(Long user_id);

    Optional<Authority> deleteFirstByUserId(Long user_id);

}
