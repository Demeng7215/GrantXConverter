package dev.demeng.grantx.converter.data.input;

import dev.demeng.grantx.converter.model.Grant;
import org.simpleyaml.configuration.file.YamlFile;
import org.simpleyaml.exceptions.InvalidConfigurationException;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
    return null;
  }
}
