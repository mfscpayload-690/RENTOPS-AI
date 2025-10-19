package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

/**
 * Seeds the database with cars from add_new_cars.sql if they are not already
 * present. It parses the INSERT statement, extracts license_plate values, and
 * performs INSERT IGNORE per row.
 */
public class DatabaseSeeder {

    // Look for SQL seed script under sql/seeds for better repo hygiene
    private static final String SCRIPT_FILE = "sql/seeds/add_new_cars.sql";

    public static void seedCarsIfNeeded() {
        File f = new File(SCRIPT_FILE);
        if (!f.exists()) {
            System.out.println("DatabaseSeeder: Script not found, skipping seeding: " + SCRIPT_FILE);
            return;
        }
        try (Connection conn = DatabaseConnection.getConnection()) {
            ensureUniqueIndex(conn);
            if (existingCarCount(conn) >= 40) { // heuristic: already seeded
                System.out.println("DatabaseSeeder: Cars table already populated (>=40). Skipping.");
                return;
            }
            String content = readFile(f);
            int inserted = processMultiInsert(content, conn);
            System.out.println("DatabaseSeeder: Inserted " + inserted + " car rows (ignore duplicates).");
        } catch (Exception e) {
            System.err.println("DatabaseSeeder: Error during seeding: " + e.getMessage());
        }
    }

    private static void ensureUniqueIndex(Connection conn) {
        try (Statement st = conn.createStatement()) {
            // Check if index already exists
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet indexes = meta.getIndexInfo(null, null, "cars", true, false);
            boolean indexExists = false;
            while (indexes.next()) {
                if ("ux_cars_license_plate".equals(indexes.getString("INDEX_NAME"))) {
                    indexExists = true;
                    break;
                }
            }
            if (!indexExists) {
                st.execute("ALTER TABLE cars ADD UNIQUE INDEX ux_cars_license_plate (license_plate)");
            }
        } catch (SQLException e) {
            if (!e.getMessage().toLowerCase().contains("duplicate")) {
                System.out.println("DatabaseSeeder: Index creation warning: " + e.getMessage());
            }
        }
    }

    private static int existingCarCount(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM cars")) {
            rs.next();
            return rs.getInt(1);
        }
    }

    private static String readFile(File f) throws Exception {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append('\n');
            }
        }
        return sb.toString();
    }

    private static int processMultiInsert(String sqlScript, Connection conn) throws Exception {
        // Find the VALUES (...) blocks
        int valuesIdx = sqlScript.toUpperCase().indexOf("VALUES");
        if (valuesIdx < 0) {
            return 0;
        }
        String afterValues = sqlScript.substring(valuesIdx + 6); // skip 'VALUES'
        // Split on '),'; keep ) inside
        String[] blocks = afterValues.split("\\),");
        int inserted = 0;
        Set<String> seenPlates = new HashSet<>();
        for (String rawBlock : blocks) {
            String block = rawBlock.trim();
            if (block.startsWith("(")) {
                block = block.substring(1);
            }
            if (block.endsWith(");")) {
                block = block.substring(0, block.length() - 2);
            }
            // Now block is comma-separated fields.
            String[] parts = splitTopLevel(block);
            if (parts.length < 8) {
                continue; // sanity

            }
            String licensePlate = stripQuotes(parts[3].trim());
            if (seenPlates.contains(licensePlate)) {
                continue;
            }
            seenPlates.add(licensePlate);
            // Reconstruct single-row insert IGNORE
            String single = "INSERT IGNORE INTO cars (make, model, year, license_plate, status, specs, price_per_day, total_km_driven) VALUES ("
                    + parts[0].trim() + "," + parts[1].trim() + "," + parts[2].trim() + "," + parts[3].trim() + ","
                    + parts[4].trim() + "," + parts[5].trim() + "," + parts[6].trim() + "," + parts[7].trim() + ")";
            try (Statement st = conn.createStatement()) {
                int uc = st.executeUpdate(single);
                inserted += uc; // 1 if inserted, 0 if duplicate
            }
        }
        return inserted;
    }

    private static String stripQuotes(String s) {
        s = s.trim();
        if (s.startsWith("'") && s.endsWith("'")) {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }

    private static String[] splitTopLevel(String block) {
        // Splits on commas not inside quotes
        java.util.List<String> out = new java.util.ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuote = false;
        for (int i = 0; i < block.length(); i++) {
            char c = block.charAt(i);
            if (c == '\\' && i + 1 < block.length()) { // escape seq
                cur.append(c).append(block.charAt(i + 1));
                i++;
                continue;
            }
            if (c == '\'') {
                inQuote = !inQuote;
                cur.append(c);
                continue;
            }
            if (c == ',' && !inQuote) {
                out.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        if (cur.length() > 0) {
            out.add(cur.toString());
        }
        return out.toArray(new String[0]);
    }
}
