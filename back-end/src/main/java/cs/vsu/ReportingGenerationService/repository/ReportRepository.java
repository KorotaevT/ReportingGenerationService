package cs.vsu.ReportingGenerationService.repository;

import cs.vsu.ReportingGenerationService.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository  extends JpaRepository<Report, Integer> {
}
