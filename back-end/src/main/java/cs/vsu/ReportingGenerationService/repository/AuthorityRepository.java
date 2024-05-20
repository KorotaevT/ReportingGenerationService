package cs.vsu.ReportingGenerationService.repository;

import cs.vsu.ReportingGenerationService.model.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthorityRepository  extends JpaRepository<Authority, Integer> {

    Optional<Authority> findFirstByUserId(Long user_id);

    void deleteFirstByUserId(Long user_id);

}