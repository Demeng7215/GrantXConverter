package dev.demeng.grantx.converter.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.AccessLevel;
import lombok.Getter;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

public class MySQL {

  private HikariDataSource source;

  @Getter(AccessLevel.PROTECTED)
  private Connection connection;

  private final boolean h2;
  private String host;
  private int port;
  private final String database;
  private final String username;
  private final String password;

  protected MySQL(String database, String username, String password) throws SQLException {
    this.h2 = true;
    this.database = database;
    this.username = username;
    this.password = password;

    init();
  }

  protected MySQL(String host, int port, String database, String username, String password)
      throws SQLException {
    this.h2 = false;
    this.host = host;
    this.port = port;
    this.database = database;
    this.username = username;
    this.password = password;

    init();
  }

  private void init() throws SQLException {

    final HikariConfig config = new HikariConfig();
    config.setMaximumPoolSize(10);

    if (h2) {
      config.setDriverClassName("org.h2.Driver");
      config.setJdbcUrl("jdbc:h2:" + new File(database).getAbsolutePath() + ";mode=MySQL");
    } else {
      config.setDriverClassName("com.mysql.jdbc.Driver");
      config.setJdbcUrl(
          "jdbc:mysql://"
              + host
              + ":"
              + port
              + "/"
              + database
              + "?autoReconnect=true&useSSL=false");
    }

    config.setUsername(username);
    config.setPassword(password);

    this.source = new HikariDataSource(config);
    this.connection = source.getConnection();

    connection
        .createStatement()
        .executeUpdate(
            "CREATE TABLE IF NOT EXISTS grantx_grants "
                + "(id INTEGER AUTO_INCREMENT, status VARCHAR(255), time BIGINT, "
                + "target VARCHAR(255), issuer VARCHAR(255), revoker VARCHAR(255), "
                + "rank VARCHAR(255), server VARCHAR(255), duration VARCHAR(255), reason VARCHAR(255), "
                + "revoke_time BIGINT, last_login BIGINT, PRIMARY KEY (id));");

    final TimerTask timerTask =
        new TimerTask() {
          @Override
          public void run() {
            try {

              if (connection != null && !connection.isClosed()) {
                connection.createStatement().execute("SELECT 1");
              } else {
                connection = getNewConnection();
              }

            } catch (SQLException ex) {
              connection = getNewConnection();
            }
          }
        };

    final Timer timer = new Timer();
    timer.schedule(timerTask, 60000, 60000);
  }

  protected void executeUpdate(String sql, Object... placeholders) throws SQLException {

    final PreparedStatement statement = connection.prepareStatement(sql);
    for (int i = 0; i < placeholders.length; i++) {
      statement.setObject(i + 1, placeholders[i]);
    }

    statement.executeUpdate();
  }

  protected ResultSet executeQuery(String sql, Object... placeholders) throws SQLException {

    final PreparedStatement statement = connection.prepareStatement(sql);
    for (int i = 0; i < placeholders.length; i++) {
      statement.setObject(i + 1, placeholders[i]);
    }

    return statement.executeQuery();
  }

  protected void close() throws SQLException {
    if (connection != null) connection.close();
    if (source != null) source.close();
  }

  private Connection getNewConnection() {

    try {
      return source.getConnection();
    } catch (SQLException ex) {
      Common.report(ex, "Failed to maintain database connection.", true);
    }

    return null;
  }
}
