package cs.vsu.ReportingGenerationService.repository;

import cs.vsu.ReportingGenerationService.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository  extends JpaRepository<Report, Integer> {

    List<Report> findAll();

    Report findById(Long id);

    void deleteById(Long id);
}
