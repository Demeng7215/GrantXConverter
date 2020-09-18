package dev.demeng.grantx.converter;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.demeng.grantx.converter.util.ErrorReporter;
import org.simpleyaml.configuration.ConfigurationSection;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

public class ConverterDatabase {

  private final HikariDataSource source;
  private Connection connection;

  public ConverterDatabase(String sectionPath) throws Exception {

    final ConfigurationSection section = Main.getConfig().getConfigurationSection(sectionPath);

    final String host = Objects.requireNonNull(section).getString("host");
    final int port = section.getInt("port", -1);
    final String database = section.getString("database");
    final String user = section.getString("username");
    final String password = section.getString("password");

    if (host == null || port == -1 || database == null || user == null || password == null) {
      ErrorReporter.report(null, "Incorrect database credentials.", true);
      throw new DatabaseException();
    }

    final HikariConfig config = new HikariConfig();
    config.setMaximumPoolSize(10);

    switch (Objects.requireNonNull(section.getString("method")).toLowerCase()) {
      case "h2":
        config.setDriverClassName("org.h2.Driver");
        config.setJdbcUrl("jdbc:h2:." + new File("grantx").getAbsolutePath() + ";mode=MySQL");
        break;

      case "mysql":
        config.setDriverClassName("com.mysql.jdbc.Driver");
        config.setJdbcUrl(
            "jdbc:mysql://"
                + host
                + ":"
                + port
                + "/"
                + database
                + section.getString("additional-options"));
        break;

      default:
        ErrorReporter.report(null, "Invalid storage method.", true);
        throw new DatabaseException();
    }

    config.setUsername(user);
    config.setPassword(password);

    this.source = new HikariDataSource(config);
    this.connection = source.getConnection();
    init();
  }

  private void init() throws SQLException {

    connection
        .createStatement()
        .executeUpdate(
            "CREATE TABLE IF NOT EXISTS grantx_grants "
                + "(id INTEGER AUTO_INCREMENT, status VARCHAR(255), time BIGINT, "
                + "target VARCHAR(255), issuer VARCHAR(255), revoker VARCHAR(255), "
                + "rank VARCHAR(255), server VARCHAR(255), duration VARCHAR(255), reason VARCHAR(255), "
                + "revoke_time BIGINT, last_login BIGINT, PRIMARY KEY (id));");

    final Runnable runnable =
        () -> {
          while (true) {
            try {

              if (connection != null && !connection.isClosed()) {
                connection.createStatement().execute("SELECT 1");
              } else {
                connection = getNewConnection();
              }

            } catch (SQLException ex) {
              connection = getNewConnection();
            }

            try {
              Thread.sleep(60000);
            } catch (InterruptedException ex) {
              ErrorReporter.report(ex, "Failed to maintain database connection.", true);
            }
          }
        };

    new Thread(runnable).start();
  }

  private Connection getNewConnection() {

    try {
      return source.getConnection();
    } catch (SQLException ex) {
      ErrorReporter.report(ex, "Failed to maintain database connection.", true);
    }

    return null;
  }

  public void close() throws SQLException {
    if (connection != null) connection.close();
    if (source != null) source.close();
  }

  public static class DatabaseException extends RuntimeException {}
}
