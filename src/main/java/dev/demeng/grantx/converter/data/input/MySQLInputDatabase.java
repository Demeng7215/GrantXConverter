package dev.demeng.grantx.converter.data.input;

import dev.demeng.grantx.converter.model.Grant;
import dev.demeng.grantx.converter.util.MySQL;

import java.sql.SQLException;
import java.util.List;

public class MySQLInputDatabase extends MySQL implements InputDatabase {

  public MySQLInputDatabase(
      String host, int port, String database, String username, String password)
      throws SQLException {
    super(host, port, database, username, password);
    init();
  }

  private void init() throws SQLException {}

  @Override
  public List<Grant> getGrants() {
    return null;
  }
}
