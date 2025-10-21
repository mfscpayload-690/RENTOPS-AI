package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility to execute SQL scripts from file
 */
public class SqlScriptRunner {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: SqlScriptRunner <script_file_path>");
            return;
        }

        String scriptPath = args[0];
        System.out.println("Executing script: " + scriptPath);

        try {
            List<String> statements = readSqlStatementsFromFile(scriptPath);

            if (statements.isEmpty()) {
                System.out.println("No SQL statements found in the file.");
                return;
            }

            System.out.println("Found " + statements.size() + " SQL statements to execute.");

            try (Connection conn = DatabaseConnection.getConnection()) {
                try (Statement stmt = conn.createStatement()) {
                    for (String sql : statements) {
                        System.out.println("Executing: " + sql);
                        boolean isResultSet = stmt.execute(sql);
                        if (!isResultSet) {
                            int updateCount = stmt.getUpdateCount();
                            System.out.println("Update count: " + updateCount);
                        }
                    }
                    System.out.println("Script executed successfully!");
                }
            }
        } catch (Exception e) {
            System.err.println("Error executing script: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static List<String> readSqlStatementsFromFile(String filePath) throws Exception {
        List<String> statements = new ArrayList<>();
        StringBuilder currentStatement = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Skip empty lines and comments
                line = line.trim();
                if (line.isEmpty() || line.startsWith("--") || line.startsWith("/*")) {
                    continue;
                }

                currentStatement.append(line).append(" ");

                if (line.endsWith(";")) {
                    statements.add(currentStatement.toString());
                    currentStatement = new StringBuilder();
                }
            }

            // Add the last statement if it doesn't end with a semicolon
            String lastStatement = currentStatement.toString().trim();
            if (!lastStatement.isEmpty()) {
                statements.add(lastStatement);
            }
        }

        return statements;
    }
}
