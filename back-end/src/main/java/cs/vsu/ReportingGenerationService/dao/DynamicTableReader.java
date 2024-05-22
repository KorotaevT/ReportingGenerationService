package cs.vsu.ReportingGenerationService.dao;

import cs.vsu.ReportingGenerationService.dto.FieldSelectionDTO;
import cs.vsu.ReportingGenerationService.dto.ReportRequestDTO;
import cs.vsu.ReportingGenerationService.model.Report;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;

@Component
public class DynamicTableReader {

    public List<Map<String, Object>> getReportData(ReportRequestDTO request, Optional<Report> report) {

        List<Map<String, Object>> data = new ArrayList<>();

        if (report.isPresent()) {
            String query = report.get().getQuery();
            if (isValidQuery(query)) {
                try (Connection connection = DriverManager.getConnection(report.get().getUrl(), report.get().getUsername(), report.get().getPassword())) {
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(query);

                    while (resultSet.next()) {
                        Map<String, Object> row = new LinkedHashMap<>();

                        for (Map.Entry<String, List<FieldSelectionDTO>> entry : request.getFields().entrySet()) {
                            if ("variable".equals(entry.getKey())) {
                                for (FieldSelectionDTO fieldSelectionDTO : entry.getValue()) {
                                    row.put(fieldSelectionDTO.getFieldName(), resultSet.getObject(fieldSelectionDTO.getFieldName()));
                                }
                            }
                        }

                        data.add(row);
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                throw new IllegalArgumentException("Invalid SQL query.");
            }
        }

        return data;
    }

    private boolean isValidQuery(String query) {

        String trimmedQuery = query.trim().toUpperCase();

        int semicolonCount = 0;
        for (int i = 0; i < trimmedQuery.length(); i++) {
            if (trimmedQuery.charAt(i) == ';') {
                semicolonCount++;
            }
        }

        if (semicolonCount > 1) {
            return false;
        }

        return trimmedQuery.startsWith("SELECT");
    }

}