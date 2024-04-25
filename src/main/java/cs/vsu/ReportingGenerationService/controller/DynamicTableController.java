package cs.vsu.ReportingGenerationService.controller;

import cs.vsu.ReportingGenerationService.dao.DynamicTableReader;
import cs.vsu.ReportingGenerationService.dto.TableDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class DynamicTableController {

    private final DynamicTableReader dynamicTableReader;

    @Autowired
    public DynamicTableController(DynamicTableReader dynamicTableReader) {
        this.dynamicTableReader = dynamicTableReader;
    }

    @GetMapping("/tables")
    public ResponseEntity<List<TableDTO>> getTables() throws SQLException {
        List<TableDTO> tablesData = new ArrayList<>();

        List<Map<String, Map<String, List<Object>>>> tablesDataFromReader = dynamicTableReader.getTablesData();

        for (Map<String, Map<String, List<Object>>> tableData : tablesDataFromReader) {
            for (Map.Entry<String, Map<String, List<Object>>> entry : tableData.entrySet()) {
                TableDTO table = new TableDTO();
                table.setTableName(entry.getKey());
                table.setData(entry.getValue());

                tablesData.add(table);
            }
        }

        return ResponseEntity.ok(tablesData);
    }



}
