package cs.vsu.ReportingGenerationService.controller;

import cs.vsu.ReportingGenerationService.dao.DynamicTableReader;
import cs.vsu.ReportingGenerationService.dto.ReportRequestDTO;
import cs.vsu.ReportingGenerationService.dto.ReportResponseDTO;
import cs.vsu.ReportingGenerationService.model.Report;
import cs.vsu.ReportingGenerationService.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

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
