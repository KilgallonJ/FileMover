package uk.co.mhr.filemover.app;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JApplet;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.co.mhr.filemover.config.AppConfiguration;
import uk.co.mhr.filemover.gui.MainWindow;

/**
 * The main driver class for the FileMover app.
 * @author Jonathan Kilgallon
 * @since Version 0.0.1
 */
public class FileMoverApp extends JApplet {

    private static final long serialVersionUID = 1264520808607336022L;

    private static final Path CONFIG_DIR_PATH;
    private static final Path CONFIG_FILE_PATH;
    private static final Path DEFAULT_CONFIGURATION_FILE;

    static {
        final String userHome = System.getProperty("user.home");

        CONFIG_DIR_PATH = Paths.get(userHome + "/FileMover/");
        CONFIG_FILE_PATH = CONFIG_DIR_PATH.resolve("configuration.json");
        try {
            DEFAULT_CONFIGURATION_FILE = Paths
                    .get(FileMoverApp.class.getClassLoader().getResource("defaultConfiguration.json").toURI());
        } catch (URISyntaxException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private AppConfiguration config;
    private List<FileTransferRule> rules;

    public static void main(final String[] args) {
        new FileMoverApp().initialize();
    }

    private void initialize() {
        config = loadAppConfiguration();
        rules = setupRules();
        displayGUI();
    }

    private void displayGUI() {
        new MainWindow(config, rules);
    }

    /**
     * Loads the application configuration from file.
     */
    private AppConfiguration loadAppConfiguration() {
        final ObjectMapper mapper = new ObjectMapper();

        if (!CONFIG_FILE_PATH.toFile().exists()) {
            runFirstTimeSetup();
        }

        if (CONFIG_FILE_PATH.toFile().canRead()) {
            try {
                final String configJSON = new String(Files.readAllBytes(CONFIG_FILE_PATH), StandardCharsets.UTF_8);
                return mapper.readValue(configJSON, AppConfiguration.class);
            } catch (final IOException e) {
                throw new RuntimeException("Unable to read configuration file. Nested exception was: " + e.getMessage());
            }
        } else {
            throw new RuntimeException("Unable to read application configuration file.");
        }
    }

    /**
     * Creates the default configuration file in the configuration directory.
     */
    private void runFirstTimeSetup() {
        try {
            if (!CONFIG_DIR_PATH.toFile().exists()) {
                Files.createDirectory(CONFIG_DIR_PATH);
            }
            Files.copy(DEFAULT_CONFIGURATION_FILE, CONFIG_FILE_PATH, StandardCopyOption.REPLACE_EXISTING);
        } catch (final IOException e) {
            throw new RuntimeException("Unable to create configuration file: " + e.getMessage());
        }
    }

    private List<FileTransferRule> setupRules() {
        return config.getTransferRules().stream()
                .map(xr -> new FileTransferRule(Paths.get(xr.getSourceDirectory()), Paths.get(xr.getDestinationDirectory()),
                        xr.getDisplayName(), xr.getExtensionFilter()))
                .collect(Collectors.toList());
    }

}
