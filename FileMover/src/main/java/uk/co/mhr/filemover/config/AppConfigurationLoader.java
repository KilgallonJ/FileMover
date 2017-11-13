package uk.co.mhr.filemover.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.co.mhr.filemover.app.FileTransferRule;

public final class AppConfigurationLoader {

    private static final Path CONFIG_DIR_PATH;
    private static final Path CONFIG_FILE_PATH;
    private static final String DEFAULT_CONFIGURATION_FILE;

    private static AppConfiguration currentConfig;
    private static List<FileTransferRule> currentRules;

    static {
        final String userHome = System.getProperty("user.home");

        CONFIG_DIR_PATH = Paths.get(userHome + "/FileMover/");
        CONFIG_FILE_PATH = CONFIG_DIR_PATH.resolve("configuration.json");
        try {
            try (final BufferedReader configFile = new BufferedReader(
                    new InputStreamReader(ClassLoader.getSystemClassLoader().getResourceAsStream("defaultConfiguration.json")))) {

                final StringBuilder contents = new StringBuilder();

                configFile.lines().forEach(s -> contents.append(s).append(System.lineSeparator()));
                DEFAULT_CONFIGURATION_FILE = contents.toString();
            }
        } catch (final IOException e) {
            e.printStackTrace();
            throw new ExceptionInInitializerError(e.getMessage());
        }
    }

    /**
     * Loads the application configuration from file.
     */
    public static AppConfiguration loadAppConfiguration() {
        final ObjectMapper mapper = new ObjectMapper();

        if (!CONFIG_FILE_PATH.toFile().exists()) {
            runFirstTimeSetup();
        }

        if (CONFIG_FILE_PATH.toFile().canRead()) {
            try {
                final String configJSON = new String(Files.readAllBytes(CONFIG_FILE_PATH), StandardCharsets.UTF_8);
                currentConfig = mapper.readValue(configJSON, AppConfiguration.class);
                currentRules = setupRules();
                return currentConfig;
            } catch (final IOException e) {
                throw new RuntimeException("Unable to read configuration file. Nested exception was: " + e.getMessage());
            }
        } else {
            throw new RuntimeException("Unable to read application configuration file.");
        }
    }

    public static AppConfiguration reloadConfigurationFromFile() {
        final ObjectMapper mapper = new ObjectMapper();

        if (CONFIG_FILE_PATH.toFile().canRead()) {
            try {
                final String configJSON = new String(Files.readAllBytes(CONFIG_FILE_PATH), StandardCharsets.UTF_8);
                currentConfig = mapper.readValue(configJSON, AppConfiguration.class);
                currentRules = setupRules();
            } catch (final IOException e) {
                throw new RuntimeException("Unable to read configuration file. Nested exception was: " + e.getMessage());
            }
        } else {
            throw new RuntimeException("Unable to read application configuration file.");
        }

        return currentConfig;
    }

    public static List<FileTransferRule> getTransferRules() {
        return currentRules;
    }

    /**
     * Creates the default configuration file in the configuration directory.
     */
    private static void runFirstTimeSetup() {
        try {
            if (!CONFIG_DIR_PATH.toFile().exists()) {
                Files.createDirectory(CONFIG_DIR_PATH);
            }

            final Path configPath = Files.createFile(CONFIG_FILE_PATH);

            Files.write(configPath, DEFAULT_CONFIGURATION_FILE.getBytes(StandardCharsets.UTF_8), StandardOpenOption.TRUNCATE_EXISTING);
        } catch (final IOException e) {
            throw new RuntimeException("Unable to create configuration file: " + e.getMessage());
        }
    }

    private static List<FileTransferRule> setupRules() {
        return currentConfig.getTransferRules().stream().map(xr -> new FileTransferRule(Paths.get(xr.getSourceDirectory()),
                Paths.get(xr.getDestinationDirectory()), xr.getDisplayName(), xr.getExtensionFilter())).collect(Collectors.toList());
    }

    private AppConfigurationLoader() {
        throw new UnsupportedOperationException("Don't instantiate me.");
    }
}
