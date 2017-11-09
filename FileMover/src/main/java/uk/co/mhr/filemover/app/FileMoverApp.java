package uk.co.mhr.filemover.app;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.application.Application;
import javafx.stage.Stage;
import uk.co.mhr.filemover.config.AppConfiguration;

/**
 * The main driver class for the FileMover app.
 * @author Jonathan Kilgallon
 * @since Version 0.0.1
 */
public class FileMoverApp extends Application {

    private static final Path CONFIG_FILE_PATH;
    private static final Path DEFAULT_CONFIGURATION_FILE = Paths.get("/defaultConfiguration.json");

    static {
        final String userHome = System.getProperty("user.home");
        final String pathSep = System.getProperty("path.separator");

        CONFIG_FILE_PATH = Paths.get(userHome + pathSep + "FileMover" + pathSep + "configuration.json");
    }

    private AppConfiguration config;

    @Override
    public void start(final Stage primaryStage) throws Exception {
        loadAppConfiguration();
    }

    /**
     * Loads the application configuration from file.
     */
    private void loadAppConfiguration() {
        final ObjectMapper mapper = new ObjectMapper();

        if (!CONFIG_FILE_PATH.toFile().exists()) {
            runFirstTimeSetup();
        }

        if (CONFIG_FILE_PATH.toFile().canRead()) {
            try {
                final String configJSON = new String(Files.readAllBytes(CONFIG_FILE_PATH), StandardCharsets.UTF_8);
                config = mapper.readValue(configJSON, AppConfiguration.class);
            } catch (final IOException e) {
                throw new RuntimeException("Unable to read configuration file. Nested exception was: " + e.getMessage());
            }
        } else {
            throw new RuntimeException("Unable to read application configuration file.");
        }
    }

    /**
     * Creates an empty configuration file in the configuration directory.
     */
    private void runFirstTimeSetup() {
        try {
            Files.copy(DEFAULT_CONFIGURATION_FILE, CONFIG_FILE_PATH, StandardCopyOption.COPY_ATTRIBUTES);
        } catch (final IOException e) {
            throw new RuntimeException("Unable to create configuration file: " + e.getMessage());
        }
    }

}
