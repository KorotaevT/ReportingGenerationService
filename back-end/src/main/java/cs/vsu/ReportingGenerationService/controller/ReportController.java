package cs.vsu.ReportingGenerationService.controller;

import cs.vsu.ReportingGenerationService.dto.ReportRequestDTO;
import cs.vsu.ReportingGenerationService.dto.ReportResponseDTO;
import cs.vsu.ReportingGenerationService.model.Report;
import cs.vsu.ReportingGenerationService.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/data")
@Tag(name = "Контроллер обработки отчётов", description = "API для обработки отчётов")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/getReports")
    @Operation(summary = "Получить все отчеты", description = "Возвращает список всех отчетов")
    public ResponseEntity<List<Report>> getReports() {
        List<Report> reportList = reportService.getReports().orElseGet(ArrayList::new);
        return ResponseEntity.ok(reportList);
    }

    @GetMapping("/getReportById/{id}")
   @Operation(summary = "Получить отчет по ID", description = "Возвращает отчет по заданному ID")
   public ResponseEntity<Report> getReportById(@PathVariable Long id) {
        Report report = reportService.getReportByIdWithNullSensitiveFields(id);
        if (report != null) {
            return ResponseEntity.ok(report);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/getReportDataById")
    @Operation(summary = "Получить данные отчета", description = "Возвращает данные отчета по заданному запросу")
    public ResponseEntity<ReportResponseDTO> getReportData(@RequestBody ReportRequestDTO request, @RequestHeader("Authorization") String token) {
        ReportResponseDTO responseDTO = reportService.getReportDataById(request, token);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/downloadExcel")
    @Operation(summary = "Скачать отчет в формате Excel", description = "Возвращает отчет в формате Excel")
    public ResponseEntity<byte[]> downloadExcel(@RequestBody ReportRequestDTO request, @RequestHeader("Authorization") String token) throws IOException {

        byte[] excelBytes = reportService.downloadExcel(request, token);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "report.xlsx");

        return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
    }

}