package cs.vsu.ReportingGenerationService.repository;

import cs.vsu.ReportingGenerationService.model.Database;
import cs.vsu.ReportingGenerationService.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DatabaseRepository  extends JpaRepository<Database, Integer> {
}
