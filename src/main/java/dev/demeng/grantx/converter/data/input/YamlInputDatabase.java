package dev.demeng.grantx.converter.data.input;

import dev.demeng.grantx.converter.model.Grant;
import org.simpleyaml.configuration.ConfigurationSection;
import org.simpleyaml.configuration.file.YamlFile;
import org.simpleyaml.exceptions.InvalidConfigurationException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class YamlInputDatabase implements InputDatabase {

  private final YamlFile data;

  public YamlInputDatabase()
      throws NullPointerException, InvalidConfigurationException, IOException {

    final File file = new File("data.yml");

    if (!file.exists()) {
      throw new NullPointerException("Data.yml not found");
    }

    data = new YamlFile(file);
    data.load();
  }

  @Override
  public List<Grant> getGrants() {

    final List<Grant> grants = new ArrayList<>();

    final ConfigurationSection section = data.getConfigurationSection("grants");
    Objects.requireNonNull(section);

    for (String key : section.getKeys(false)) {

      final int id = Integer.parseInt(key);
      final Grant.Status status = Grant.Status.valueOf(section.getString(key + ".status"));
      final long time = section.getLong(key + ".time");
      final String target = section.getString(key + ".target");
      final String staff = section.getString(key + ".staff");
      final String rank = section.getString(key + ".rank");
      final String duration = section.getString(key + ".duration");
      final String reason = section.getString(key + ".reason");
      final String revoker = section.getString(key + ".revoker");

      grants.add(
          new Grant(
              id,
              status,
              time,
              UUID.fromString(target),
              staff.equals("00000000-0000-0000-0000-000000000000") ? null : UUID.fromString(staff),
              revoker.equals("none") ? null : UUID.fromString(revoker),
              rank,
              "global",
              duration.replace("custom-duration;", "custom:"),
              reason.replace("custom-reason;", "custom:"),
              status == Grant.Status.REVOKED ? 0 : -1));
    }

    return grants;
  }
}
