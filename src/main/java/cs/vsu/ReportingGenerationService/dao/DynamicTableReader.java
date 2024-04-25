package cs.vsu.ReportingGenerationService.dao;

import cs.vsu.ReportingGenerationService.controller.DynamicTableController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
public class DynamicTableReader {

    @Autowired
    private Environment env;

    public List<Map<String, Map<String, List<Object>>>> getTablesData() throws SQLException {
        List<String> tableDefinitions = readTableDefinitionLinesFromFileResource("tables.txt");

        String url = env.getProperty("spring.datasource.url");
        String user = env.getProperty("spring.datasource.username");
        String password = env.getProperty("spring.datasource.password");
        assert url != null;
        Connection connection = DriverManager.getConnection(url, user, password);

        DatabaseMetaData metaData = connection.getMetaData();

        List<Map<String, Map<String, List<Object>>>> tablesData = new ArrayList<>();

        for (String tableDefinition : tableDefinitions) {
            String tableName = getTableNameFromDefinition(tableDefinition);
            List<String> columnNames = getColumnNamesFromDefinition(tableDefinition);

            if (!tableExists(metaData, tableName)) {
                System.out.println("Таблица " + tableName + " не существует.");
                continue;
            }

            if (!columnsExist(metaData, tableName, columnNames)) {
                System.out.println("Не все поля существуют в таблице " + tableName + ".");
                continue;
            }

            List<List<Object>> rows = executeQuery(connection, tableName, columnNames);
            Map<String, List<Object>> tableData = createTableDataWithColumnNames(columnNames, rows);
            Map<String, Map<String, List<Object>>> tableMap = new HashMap<>();
            tableMap.put(tableName, tableData);
            tablesData.add(tableMap);
        }


        return tablesData;
    }

    private static Map<String, List<Object>> createTableDataWithColumnNames(List<String> columnNames, List<List<Object>> rows) {
        Map<String, List<Object>> tableData = new HashMap<>();

        for (String columnName : columnNames) {
            List<Object> columnData = new ArrayList<>();

            for (List<Object> row : rows) {
                columnData.add(row.get(columnNames.indexOf(columnName)));
            }

            tableData.put(columnName, columnData);
        }

        return tableData;
    }


    private static List<String> readTableDefinitionLinesFromFileResource(String fileName) {
        List<String> tableDefinitionLines = new ArrayList<>();
        ClassLoader classLoader = DynamicTableController.class.getClassLoader();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(classLoader.getResourceAsStream(fileName))))) {
            String line;
            while ((line = reader.readLine()) != null) {
                tableDefinitionLines.add(line);
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла " + fileName + ": " + e.getMessage());
        }
        return tableDefinitionLines;
    }

    private static String getTableNameFromDefinition(String tableDefinition) {
        int startIndex = 0;
        int endIndex = tableDefinition.indexOf('(');
        return tableDefinition.substring(startIndex, endIndex).trim();
    }

    private static List<String> getColumnNamesFromDefinition(String tableDefinition) {
        int startIndex = tableDefinition.indexOf('(') + 1;
        int endIndex = tableDefinition.indexOf(')');
        String columnList = tableDefinition.substring(startIndex, endIndex).trim();
        String[] columnArray = columnList.split(", ");
        return new ArrayList<>(Arrays.asList(columnArray));
    }


    private static boolean tableExists(DatabaseMetaData metaData, String tableName) throws SQLException {
        ResultSet resultSet = metaData.getTables(null, null, tableName, new String[]{"TABLE"});
        return resultSet.next();
    }

    private static boolean columnsExist(DatabaseMetaData metaData, String tableName, List<String> columnNames) throws SQLException {
        ResultSet resultSet = metaData.getColumns(null, null, tableName, null);
        HashSet<String> columnSet = new HashSet<>();

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
