package dev.demeng.grantx.converter;

import dev.demeng.grantx.converter.data.OutputDatabase;
import dev.demeng.grantx.converter.data.input.InputDatabase;
import dev.demeng.grantx.converter.data.input.MySQLInputDatabase;
import dev.demeng.grantx.converter.data.input.YamlInputDatabase;
import dev.demeng.grantx.converter.util.Common;
import lombok.Getter;
import org.simpleyaml.configuration.ConfigurationSection;
import org.simpleyaml.configuration.file.YamlFile;
import org.simpleyaml.exceptions.InvalidConfigurationException;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class Main {

  @Getter private static YamlFile config;

  @Getter private static InputDatabase input;
  @Getter private static OutputDatabase output;

  public static void main(String[] args) {

    System.out.println("Loading config...");

    if (!loadConfig()) {
      return;
    }

    System.out.println("Connecting to input storage...");
    input = loadInputStorage();
    if (input == null) {
      return;
    }

    System.out.println("Connecting to output storage...");
    output = loadOutputStorage();
    if (output == null) {
      return;
    }

    // TODO Conversion

    System.out.println("Conversion completed.");
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  private static boolean loadConfig() {

    try {

      final File file = new File("config.yml");
      boolean fresh = false;

      if (!file.exists()) {
        file.createNewFile();
        fresh = true;
      }

      config = new YamlFile(file);
      config.load();

      if (fresh) {

        final ConfigurationSection inputStorage = config.createSection("input-storage");
        inputStorage.set("yaml", true);
        inputStorage.set("host", "localhost");
        inputStorage.set("port", 3306);
        inputStorage.set("username", "root");
        inputStorage.set("password", "root");

        final ConfigurationSection outputStorage = config.createSection("output-storage");
        outputStorage.set("h2", true);
        outputStorage.set("host", "localhost");
        outputStorage.set("port", 3306);
        outputStorage.set("username", "root");
        outputStorage.set("password", "root");

        config.save();
      }

    } catch (IOException | InvalidConfigurationException ex) {
      Common.report(ex, "Failed to load config.", true);
      return false;
    }

    return true;
  }

  private static InputDatabase loadInputStorage() {

    try {
      final ConfigurationSection section =
          Main.getConfig().getConfigurationSection("input-storage");
      Objects.requireNonNull(section);

      if (section.getBoolean("yaml")) {
        return new YamlInputDatabase();

      } else {
        return new MySQLInputDatabase(
            section.getString("host"),
            section.getInt("port"),
            section.getString("database"),
            section.getString("username"),
            section.getString("password"));
      }

    } catch (Throwable ex) {
      Common.report(ex, "Failed to connect to input storage.", true);
      return null;
    }
  }

  private static OutputDatabase loadOutputStorage() {

    try {
      final ConfigurationSection section =
          Main.getConfig().getConfigurationSection("output-storage");
      Objects.requireNonNull(section);

      if (section.getBoolean("h2")) {
        return new OutputDatabase("grantx");

      } else {
        return new OutputDatabase(
            section.getString("host"),
            section.getInt("port"),
            section.getString("database"),
            section.getString("username"),
            section.getString("password"));
      }

    } catch (Throwable ex) {
      Common.report(ex, "Failed to connect to output storage.", true);
      return null;
    }
  }
}
