package cs.vsu.ReportingGenerationService.repository;

import cs.vsu.ReportingGenerationService.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReportRepository  extends JpaRepository<Report, Integer> {

    List<Report> findAll();

    Optional<Report> findById(Long id);

    void deleteById(Long id);
}
