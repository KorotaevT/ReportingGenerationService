package cs.vsu.ReportingGenerationService.dto;

import lombok.*;

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
