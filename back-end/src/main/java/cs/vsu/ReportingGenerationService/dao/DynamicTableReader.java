package cs.vsu.ReportingGenerationService.dao;

import cs.vsu.ReportingGenerationService.model.Database;
import cs.vsu.ReportingGenerationService.model.Tables;
import cs.vsu.ReportingGenerationService.repository.DatabaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.*;

@Component
public class DynamicTableReader {

    @Autowired
    private DatabaseRepository databaseRepository;

    public List<Map<String, Map<String, Map<String, List<Object>>>>> getTablesData() throws SQLException {

        List<Database> databases = databaseRepository.findAll();
        List<Map<String, Map<String, Map<String, List<Object>>>>> tablesData = new ArrayList<>();

        for (Database database : databases) {
            String url = database.getUrl();
            String user = database.getUsername();
            String password = database.getPassword();
            assert url != null;
            Connection connection = DriverManager.getConnection(url, user, password);

            DatabaseMetaData metaData = connection.getMetaData();

            List<Tables> tables = database.getTables();

            Map<String, Map<String, Map<String, List<Object>>>> dbTablesData = new LinkedHashMap<>();
            Map<String, Map<String, List<Object>>> tableMap = new LinkedHashMap<>();

            for (Tables table : tables) {
                List<String> columnNames = getColumnNamesFromDefinition(table.getFields());
                if (!tableExists(metaData, table.getName())) {
                    System.out.println("Таблица " + table.getName() + " не существует.");
                    continue;
                }

                if (!columnsExist(metaData, table.getName(), columnNames)) {
                    System.out.println("Не все поля существуют в таблице " + table.getName() + ".");
                    continue;
                }

                List<List<Object>> rows = executeQuery(connection, table.getName(), columnNames);
                Map<String, List<Object>> tableData = createTableDataWithColumnNames(columnNames, rows);
                tableMap.put(table.getName(), tableData);
            }

            dbTablesData.put(database.getName(), tableMap);
            tablesData.add(dbTablesData);
        }

        return tablesData;
    }

    private static Map<String, List<Object>> createTableDataWithColumnNames(List<String> columnNames, List<List<Object>> rows) {
        Map<String, List<Object>> tableData = new LinkedHashMap<>();

        for (String columnName : columnNames) {
            List<Object> columnData = new ArrayList<>();

            for (List<Object> row : rows) {
                columnData.add(row.get(columnNames.indexOf(columnName)));
            }

            tableData.put(columnName, columnData);
        }

        return tableData;
    }

    private static List<String> getColumnNamesFromDefinition(String tableDefinition) {
        String[] columnArray = tableDefinition.split(", ");
        return new ArrayList<>(Arrays.asList(columnArray));
    }

    private static boolean tableExists(DatabaseMetaData metaData, String tableName) throws SQLException {
        ResultSet resultSet = metaData.getTables(null, null, tableName, new String[]{"TABLE"});
        return resultSet.next();
    }

    private static boolean columnsExist(DatabaseMetaData metaData, String tableName, List<String> columnNames) throws SQLException {
        ResultSet resultSet = metaData.getColumns(null, null, tableName, null);
        LinkedHashSet<String> columnSet = new LinkedHashSet<>();

        while (resultSet.next()) {
            String columnName = resultSet.getString("COLUMN_NAME");
            columnSet.add(columnName);
        }

        for (String requiredColumn : columnNames) {
            if (!columnSet.contains(requiredColumn)) {
                return false;
            }
        }

        return true;
    }

    private static List<List<Object>> executeQuery(Connection connection, String tableName, List<String> columnNames) throws SQLException {
        String query = "SELECT " + String.join(", ", columnNames) + " FROM " + tableName;
        try (java.sql.Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            List<List<Object>> rows = new ArrayList<>();
            while (resultSet.next()) {
                List<Object> row = new ArrayList<>();
                for (String columnName : columnNames) {
                    row.add(resultSet.getObject(columnName));
                }

                rows.add(row);
            }
            return rows;
        }
    }
}
