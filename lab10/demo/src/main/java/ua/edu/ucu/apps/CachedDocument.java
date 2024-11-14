package ua.edu.ucu.apps;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class CachedDocument extends AbstractDecorator {

    private static final String DB_URL = "jdbc:sqlite:cache.db";
    private final String gcsPath;

    public CachedDocument(Document document, String gcsPath) {
        super(document);
        this.gcsPath = gcsPath;
        initializeCache();
    }

    private void initializeCache() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS Cache (Path TEXT PRIMARY KEY, Content TEXT)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Optional<String> getCachedContent() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement("SELECT Content FROM Cache WHERE Path = ?")) {
            pstmt.setString(1, gcsPath);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(rs.getString("Content"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private void cacheContent(String content) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement("REPLACE INTO Cache (Path, Content) VALUES (?, ?)")) {
            pstmt.setString(1, gcsPath);
            pstmt.setString(2, content);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String parse() {
        return getCachedContent().orElseGet(() -> {
            String result = super.parse();
            cacheContent(result);
            return result;
        });
    }
}

