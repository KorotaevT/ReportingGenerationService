package cs.vsu.ReportingGenerationService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class TableDTO {

    private String dbName;
    private String tableName;
    private Map<String, List<Object>> data;

}