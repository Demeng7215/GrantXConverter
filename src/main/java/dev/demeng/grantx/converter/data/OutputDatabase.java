package dev.demeng.grantx.converter.data;

import dev.demeng.grantx.converter.model.Grant;
import dev.demeng.grantx.converter.util.MySQL;

import java.sql.SQLException;
import java.util.List;

public class OutputDatabase extends MySQL {

  public OutputDatabase(String host, int port, String database, String username, String password)
      throws SQLException {
    super(host, port, database, username, password);
    init();
  }

  public OutputDatabase(String database) throws SQLException {
    super(database);
    init();
  }

  private void init() throws SQLException {
    executeUpdate(
        "CREATE TABLE IF NOT EXISTS grantx_grants "
            + "(id INTEGER AUTO_INCREMENT, status VARCHAR(255), time BIGINT, "
            + "target VARCHAR(255), issuer VARCHAR(255), revoker VARCHAR(255), "
            + "rank VARCHAR(255), server VARCHAR(255), duration VARCHAR(255), reason VARCHAR(255), "
            + "revoke_time BIGINT, last_login BIGINT, PRIMARY KEY (id));");
  }

  public void convert(List<Grant> oldGrants) throws SQLException {

    for (Grant grant : oldGrants) {
      executeUpdate(
          "INSERT INTO grantx_grants VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);",
          grant.getId(),
          grant.getStatus().name(),
          grant.getTime(),
          grant.getTarget().toString(),
          grant.getIssuer() == null ? null : grant.getIssuer().toString(),
          grant.getRevoker() == null ? null : grant.getRevoker().toString(),
          grant.getRank(),
          grant.getServer(),
          grant.getDuration(),
          grant.getReason(),
          grant.getRevokeTime(),
          System.currentTimeMillis());
    }
  }
}
