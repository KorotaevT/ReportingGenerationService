package cs.vsu.ReportingGenerationService.repository;

import cs.vsu.ReportingGenerationService.model.Tables;
import cs.vsu.ReportingGenerationService.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TablesRepository  extends JpaRepository<Tables, Integer> {
}
