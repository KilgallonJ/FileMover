package uk.co.mhr.filemover.app;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * This class is responsible for actually copying a file from the source directory into the destination directory.
 * @author Jonathan Kilgallon
 * @since Version 0.0.1
 */
public class FileTransferRule {

    private final Path source;
    private final Path destination;
    private final String displayName;

    /**
     * Instantiate this rule.
     * @param source The source directory.
     * @param destination The destination directory.
     * @param displayName The displayable name of this rule.
     */
    FileTransferRule(final Path source, final Path destination, final String displayName) {
        this.source = source;
        this.destination = destination;
        this.displayName = displayName;
    }

    /**
     * Returns the name of this rule as shown to the user.
     * @return The name of this rule.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Invokes this transfer rule to move the detected file.
     * @param event The WatchEvent that triggered this invocation.
     */
    public void apply(final List<Path> files) {
        copySelectedFiles(files);
    }

    /**
     * Copy a file from the source folder to the destination folder.
     */
    private void copySelectedFiles(final List<Path> files) {
        files.stream().forEach(this::copyFile);
    }

    /**
     * Copies a file to the destination folder.
     * @param original The file being copied.
     */
    private void copyFile(final Path file) {
        final Path fileName = file.getFileName();
        try {
            Files.copy(file, destination.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
        } catch (final IOException e) {
            throw new RuntimeException("Unable to copy source file: " + e.getMessage());
        }
    }

    @Override
    public String toString() {
        return displayName + ": " + source + " -> " + destination;
    }
}
