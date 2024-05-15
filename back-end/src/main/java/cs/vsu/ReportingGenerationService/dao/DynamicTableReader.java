package cs.vsu.ReportingGenerationService.dao;

import cs.vsu.ReportingGenerationService.dto.ReportRequestDTO;
import cs.vsu.ReportingGenerationService.dto.ReportResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class DynamicTableReader {

    public ReportResponseDTO getReportData(ReportRequestDTO request) {
        return new ReportResponseDTO();
    }
}

