package cs.vsu.ReportingGenerationService.repository;

import cs.vsu.ReportingGenerationService.model.Report;
import cs.vsu.ReportingGenerationService.model.ReportRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRequestRepository extends JpaRepository<ReportRequest, Integer> {

    List<ReportRequest> findAllByOrderByRequestTimeDesc();

}