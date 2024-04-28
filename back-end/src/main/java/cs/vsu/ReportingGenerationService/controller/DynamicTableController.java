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

    @GetMapping("/databases")
    public ResponseEntity<List<String>> getDatabases() throws SQLException {
        List<String> databases = new ArrayList<>();

        List<Map<String, Map<String, Map<String, List<Object>>>>> tablesDataFromReader = dynamicTableReader.getTablesData();

        for (Map<String, Map<String, Map<String, List<Object>>>> dbData : tablesDataFromReader) {
            for (String dbName : dbData.keySet()) {
                if (!databases.contains(dbName)) {
                    databases.add(dbName);
                }
            }
        }

        return ResponseEntity.ok(databases);
    }

    @GetMapping("/tables-names")
    public ResponseEntity<List<String>> getTablesNames() throws SQLException {
        List<String> tables = new ArrayList<>();

        List<Map<String, Map<String, Map<String, List<Object>>>>> tablesDataFromReader = dynamicTableReader.getTablesData();

        for (Map<String, Map<String, Map<String, List<Object>>>> dbData : tablesDataFromReader) {
            for (Map<String, Map<String, List<Object>>> tableData : dbData.values()) {
                for (String tableName : tableData.keySet()) {
                    if (!tables.contains(tableName)) {
                        tables.add(tableName);
                    }
                }
            }
        }

        return ResponseEntity.ok(tables);
    }

    @GetMapping("/fields-names")
    public ResponseEntity<List<String>> getFieldsNames() throws SQLException {
        List<String> fields = new ArrayList<>();

        List<Map<String, Map<String, Map<String, List<Object>>>>> tablesDataFromReader = dynamicTableReader.getTablesData();

        for (Map<String, Map<String, Map<String, List<Object>>>> dbData : tablesDataFromReader) {
            for (Map<String, Map<String, List<Object>>> tableData : dbData.values()) {
                for (Map<String, List<Object>> fieldData : tableData.values()) {
                    for (String fieldName : fieldData.keySet()) {
                        if (!fields.contains(fieldName)) {
                            fields.add(fieldName);
                        }
                    }
                }
            }
        }

        return ResponseEntity.ok(fields);
    }
}
