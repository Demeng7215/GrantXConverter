package dev.demeng.grantx.converter;

import dev.demeng.grantx.converter.util.ErrorReporter;
import lombok.Getter;
import org.simpleyaml.configuration.ConfigurationSection;
import org.simpleyaml.configuration.file.YamlFile;
import org.simpleyaml.exceptions.InvalidConfigurationException;

import java.io.File;
import java.io.IOException;

public class Main {

  @Getter private static YamlFile config;

  public static void main(String[] args) {

    System.out.println("Loading config...");

    if (!loadConfig()) {
      return;
    }

    // TODO Connect/load input

    System.out.println("Connecting to output storage...");
    try {
      new ConverterDatabase("output-storage");
    } catch (ConverterDatabase.DatabaseException ex) {
      return;
    } catch (Exception ex) {
      ErrorReporter.report(ex, "Failed to connect to output storage.", true);
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
        inputStorage.set("method", "yml");
        inputStorage.set("host", "localhost");
        inputStorage.set("port", 3306);
        inputStorage.set("username", "root");
        inputStorage.set("password", "root");

        final ConfigurationSection outputStorage = config.createSection("output-storage");
        outputStorage.set("method", "h2");
        outputStorage.set("host", "localhost");
        outputStorage.set("port", 3306);
        outputStorage.set("username", "root");
        outputStorage.set("password", "root");

        config.save();
      }

    } catch (IOException | InvalidConfigurationException ex) {
      ErrorReporter.report(ex, "Failed to load config.", true);
      return false;
    }

    return true;
  }
}
