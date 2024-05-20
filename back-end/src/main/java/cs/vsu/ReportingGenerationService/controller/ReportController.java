package cs.vsu.ReportingGenerationService.controller;

import cs.vsu.ReportingGenerationService.dto.ReportRequestDTO;
import cs.vsu.ReportingGenerationService.dto.ReportResponseDTO;
import cs.vsu.ReportingGenerationService.model.Report;
import cs.vsu.ReportingGenerationService.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
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
    public ResponseEntity<ReportResponseDTO> getReportData(@RequestBody ReportRequestDTO request, @RequestHeader("Authorization") String token) {
        ReportResponseDTO responseDTO = reportService.getReportDataById(request, token);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/downloadExcel")
    public ResponseEntity<byte[]> downloadExcel(@RequestBody ReportRequestDTO request, @RequestHeader("Authorization") String token) throws IOException {

        byte[] excelBytes = reportService.downloadExcel(request, token);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "report.xlsx");

        return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
    }

}