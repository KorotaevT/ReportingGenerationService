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
public class ReportRequestDTO {

    private Long id;
    private Map<String, List<FieldSelectionDTO>> fields;

}