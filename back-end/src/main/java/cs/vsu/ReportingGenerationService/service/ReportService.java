package cs.vsu.ReportingGenerationService.service;

import cs.vsu.ReportingGenerationService.dao.DynamicTableReader;
import cs.vsu.ReportingGenerationService.dto.ReportRequestDTO;
import cs.vsu.ReportingGenerationService.dto.ReportResponseDTO;
import cs.vsu.ReportingGenerationService.model.Report;
import cs.vsu.ReportingGenerationService.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;

    private final DynamicTableReader dynamicTableReader;

    public Optional<List<Report>> getReports() {
        List<Report> reports = reportRepository.findAll();
        if (reports != null) {
            reports.forEach(this::nullifySensitiveFields);
        }
        return Optional.ofNullable(reports);
    }

    public Report getReportByIdWithNullSensitiveFields(Long id) {
        Report report = reportRepository.findById(id).orElse(null);
        if (report != null) {
            nullifySensitiveFields(report);
        }
        return report;
    }

    public ReportResponseDTO getReportDataById(ReportRequestDTO request) {
        ReportResponseDTO responseDTO = dynamicTableReader.getReportData(request);
        return responseDTO;
    }

    private void nullifySensitiveFields(Report report) {
        report.setPassword(null);
        report.setUsername(null);
        report.setUrl(null);
        report.setQuery(null);
    }
}

