package cs.vsu.ReportingGenerationService.dao;

import cs.vsu.ReportingGenerationService.dto.FieldSelectionDTO;
import cs.vsu.ReportingGenerationService.dto.ReportRequestDTO;
import cs.vsu.ReportingGenerationService.dto.ReportResponseDTO;
import cs.vsu.ReportingGenerationService.model.Report;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

@Component
public class DynamicTableReader {

    public List<Map<String,Object>> getReportData(ReportRequestDTO request, Optional<Report> report) {

        List<Map<String, Object>> data = new ArrayList<>();

        if (report.isPresent()) {
            try (Connection connection = DriverManager.getConnection(report.get().getUrl(), report.get().getUsername(), report.get().getPassword())) {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(report.get().getQuery());

                while (resultSet.next()) {
                    Map<String, Object> row = new HashMap<>();

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
        }

        return data;
    }
}
