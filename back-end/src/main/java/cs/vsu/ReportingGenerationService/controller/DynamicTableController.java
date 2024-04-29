package cs.vsu.ReportingGenerationService.controller;

import cs.vsu.ReportingGenerationService.dao.DynamicTableReader;
import cs.vsu.ReportingGenerationService.dto.TableDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/data")
public class DynamicTableController {

    private final DynamicTableReader dynamicTableReader;

    @Autowired
    public DynamicTableController(DynamicTableReader dynamicTableReader) {
        this.dynamicTableReader = dynamicTableReader;
    }

    @GetMapping("/all")
    public ResponseEntity<List<TableDTO>> getTables() throws SQLException {
        List<TableDTO> tablesData = new ArrayList<>();

        List<Map<String, Map<String, Map<String, List<Object>>>>> tablesDataFromReader = dynamicTableReader.getTablesData();

        for (Map<String, Map<String, Map<String, List<Object>>>> dbData : tablesDataFromReader) {
            for (Map.Entry<String, Map<String, Map<String, List<Object>>>> dbEntry : dbData.entrySet()) {
                String dbName = dbEntry.getKey();
                for (Map.Entry<String, Map<String, List<Object>>> tableEntry : dbEntry.getValue().entrySet()) {
                    TableDTO table = new TableDTO();
                    table.setDbName(dbName);
                    table.setTableName(tableEntry.getKey());
                    table.setData(tableEntry.getValue());

                    tablesData.add(table);
                }
            }
        }

        return ResponseEntity.ok(tablesData);
    }

    @GetMapping("/structure")
    public ResponseEntity<List<TableDTO>> getDbStructure() {
        List<TableDTO> tablesData = new ArrayList<>();

        List<Map<String, Map<String, Map<String, List<Object>>>>> tablesDataFromReader = dynamicTableReader.getDbStructure();

        for (Map<String, Map<String, Map<String, List<Object>>>> dbData : tablesDataFromReader) {
            for (Map.Entry<String, Map<String, Map<String, List<Object>>>> dbEntry : dbData.entrySet()) {
                String dbName = dbEntry.getKey();
                for (Map.Entry<String, Map<String, List<Object>>> tableEntry : dbEntry.getValue().entrySet()) {
                    TableDTO table = new TableDTO();
                    table.setDbName(dbName);
                    table.setTableName(tableEntry.getKey());
                    table.setData(tableEntry.getValue());

                    tablesData.add(table);
                }
            }
        }

        return ResponseEntity.ok(tablesData);
    }
}
