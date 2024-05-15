package cs.vsu.ReportingGenerationService.service;

import cs.vsu.ReportingGenerationService.dao.DynamicTableReader;
import cs.vsu.ReportingGenerationService.dto.FieldSelectionDTO;
import cs.vsu.ReportingGenerationService.dto.ReportRequestDTO;
import cs.vsu.ReportingGenerationService.dto.ReportResponseDTO;
import cs.vsu.ReportingGenerationService.model.Report;
import cs.vsu.ReportingGenerationService.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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
        Optional<Report> report = reportRepository.findById(request.getId());

        List<Map<String,Object>> data = dynamicTableReader.getReportData(request, report);

        boolean hasFieldNames = false;
        String reportName = null;
        for (FieldSelectionDTO fieldSelectionDTO : request.getFields().getOrDefault("fixed", Collections.emptyList())) {
            switch (fieldSelectionDTO.getFieldName()) {
                case "Название отчета" -> reportName = report.map(Report::getName).orElse(null);
                case "Названия полей" -> {
                    ReportResponseDTO.builder().fieldNames(true);
                    hasFieldNames = true;
                }
                case "Предоставитель отчета" -> ReportResponseDTO.builder().reportProvider(null);
            }
        }

        return ReportResponseDTO.builder()
                .data(data)
                .reportName(reportName)
                .fieldNames(hasFieldNames)
                .reportProvider(null)
                .build();
    }



    private void nullifySensitiveFields(Report report) {
        report.setPassword(null);
        report.setUsername(null);
        report.setUrl(null);
        report.setQuery(null);
    }
}

