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
    public ResponseEntity<ReportResponseDTO> getReportData(@RequestBody ReportRequestDTO request) {
        ReportResponseDTO responseDTO = reportService.getReportDataById(request);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/downloadExcel")
    public ResponseEntity<byte[]> downloadExcel(@RequestBody ReportRequestDTO request) throws IOException {

        ReportResponseDTO report = reportService.getReportDataById(request);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Report Data");

        int rowIndex = 0;

        if (report.getReportName() != null) {
            Row reportNameRow = sheet.createRow(rowIndex++);
            reportNameRow.createCell(0).setCellValue("Название отчета");
            reportNameRow.createCell(1).setCellValue(report.getReportName());
        }

        if (report.getReportProvider() != null) {
            Row reportProviderRow = sheet.createRow(rowIndex++);
            reportProviderRow.createCell(0).setCellValue("Предоставитель отчета");
            reportProviderRow.createCell(1).setCellValue(report.getReportProvider());
        }

        if (report.isFieldNames() && report.getData() != null && !report.getData().isEmpty()) {
            Row fieldNamesRow = sheet.createRow(rowIndex++);
            int cellIndex = 0;
            for (String fieldName : report.getData().get(0).keySet()) {
                fieldNamesRow.createCell(cellIndex++).setCellValue(fieldName);
            }
        }

        if (report.getData() != null) {
            for (Map<String, Object> rowData : report.getData()) {
                Row row = sheet.createRow(rowIndex++);
                int cellIndex = 0;
                for (Object value : rowData.values()) {
                    Cell cell = row.createCell(cellIndex++);
                    if (value instanceof String) {
                        cell.setCellValue((String) value);
                    } else if (value instanceof Integer) {
                        cell.setCellValue((Integer) value);
                    } else if (value instanceof Double) {
                        cell.setCellValue((Double) value);
                    } else if (value instanceof Boolean) {
                        cell.setCellValue((Boolean) value);
                    }
                }
            }
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            workbook.write(outputStream);
        } finally {
            workbook.close();
        }

        byte[] excelBytes = outputStream.toByteArray();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "report.xlsx");

        return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
    }




}
