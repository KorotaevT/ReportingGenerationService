package cs.vsu.ReportingGenerationService.service;

import cs.vsu.ReportingGenerationService.dao.DynamicTableReader;
import cs.vsu.ReportingGenerationService.dto.FieldSelectionDTO;
import cs.vsu.ReportingGenerationService.dto.ReportRequestDTO;
import cs.vsu.ReportingGenerationService.dto.ReportResponseDTO;
import cs.vsu.ReportingGenerationService.model.Report;
import cs.vsu.ReportingGenerationService.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;

    private final DynamicTableReader dynamicTableReader;

    private final JwtService jwtService;

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

    public ReportResponseDTO getReportDataById(ReportRequestDTO request, String token) {
        Optional<Report> report = reportRepository.findById(request.getId());

        List<Map<String, Object>> data = dynamicTableReader.getReportData(request, report);

        boolean hasFieldNames = false;
        String reportName = null;
        String reportProvider = null;
        Date reportDate = null;
        int recordCount = -1;

        for (FieldSelectionDTO fieldSelectionDTO : request.getFields().getOrDefault("fixed", Collections.emptyList())) {
            switch (fieldSelectionDTO.getFieldName()) {
                case "Название отчета" -> reportName = report.map(Report::getName).orElse(null);
                case "Названия полей" -> hasFieldNames = true;
                case "Предоставитель отчета" -> {
                    String curJwt = token.substring(7);
                    reportProvider = jwtService.extractUsername(curJwt);
                }
                case "Дата" -> reportDate = new Date();
                case "Количество" -> recordCount = data.size();
            }
        }

        return ReportResponseDTO.builder()
                .data(data)
                .reportName(reportName)
                .fieldNames(hasFieldNames)
                .reportProvider(reportProvider)
                .reportDate(reportDate)
                .recordCount(recordCount)
                .build();
    }

    public byte[] downloadExcel(ReportRequestDTO request, String token) throws IOException {

        ReportResponseDTO report = getReportDataById(request, token);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Отчёт");

        int rowIndex = 0;

        if (report.getReportName() != null) {
            Row reportNameRow = sheet.createRow(rowIndex++);
            reportNameRow.createCell(0).setCellValue("Название отчета:");
            reportNameRow.createCell(1).setCellValue(report.getReportName());
        }

        if (report.getReportProvider() != null) {
            Row reportProviderRow = sheet.createRow(rowIndex++);
            reportProviderRow.createCell(0).setCellValue("Предоставитель отчета:");
            reportProviderRow.createCell(1).setCellValue(report.getReportProvider());
        }

        CellStyle dateCellStyle = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd.MM.yyyy"));

        if (report.getReportDate() != null) {
            Row reportDateRow = sheet.createRow(rowIndex++);
            reportDateRow.createCell(0).setCellValue("Дата отчета:");
            Cell dateCell = reportDateRow.createCell(1);
            dateCell.setCellValue(report.getReportDate());
            dateCell.setCellStyle(dateCellStyle);
        }

        if (report.getRecordCount() != -1) {
            Row reportProviderRow = sheet.createRow(rowIndex++);
            reportProviderRow.createCell(0).setCellValue("Количество записей:");
            reportProviderRow.createCell(1).setCellValue(report.getRecordCount());
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

        return outputStream.toByteArray();
    }



    private void nullifySensitiveFields(Report report) {
        report.setPassword(null);
        report.setUsername(null);
        report.setUrl(null);
        report.setQuery(null);
    }
}

