package cs.vsu.ReportingGenerationService.controller;

import cs.vsu.ReportingGenerationService.dao.DynamicTableReader;
import cs.vsu.ReportingGenerationService.dto.ReportRequestDTO;
import cs.vsu.ReportingGenerationService.dto.ReportResponseDTO;
import cs.vsu.ReportingGenerationService.model.Report;
import cs.vsu.ReportingGenerationService.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/data")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/getReports")
    public ResponseEntity<List<Report>> getReports() {
        List<Report> reportList = reportService.getReports().orElseThrow();
        return ResponseEntity.ok(reportList);
    }

    @GetMapping("/getReportById/{id}")
    public ResponseEntity<Report> getReportById(@PathVariable Long id) {
        Report report = reportService.getReportByIdWithNullSensitiveFields(id);
        if (report != null) {
            return ResponseEntity.ok(report);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/getReportDataById")
    public ResponseEntity<ReportResponseDTO> getReportData(@RequestBody ReportRequestDTO request) {
        ReportResponseDTO responseDTO = reportService.getReportDataById(request);
        return ResponseEntity.ok(responseDTO);
    }

}
