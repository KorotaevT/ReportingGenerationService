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
public class ReportResponseDTO {
    private List<Map<String, Object>> data;
    private String reportName;
    private boolean fieldNames;
    private String reportProvider;
}
