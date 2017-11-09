package uk.co.mhr.filemover.config;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class defines the configuration of a specific transfer rule.
 * @author Jonathan Kilgallon
 * @since Version 0.0.1
 */
public class TransferRuleConfiguration {

    @JsonProperty("sourceDirectory")
    private String sourceDirectory;

    @JsonProperty("destinationDirectory")
    private String destinationDirectory;

    @JsonProperty("displayName")
    private String displayName;

    /**
     * Returns the string used to display this rule in the UI.
     * @return The displayable name for this rule.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the path representing the directory that files are sourced from.
     * @return The source directory path.
     */
    public String getSourceDirectory() {
        return sourceDirectory;
    }

    /**
     * Returns the path representing the directory that files are moved to.
     * @return The destination directory path.
     */
    public String getDestinationDirectory() {
        return destinationDirectory;
    }

    @Override
    public String toString() {
        return displayName + ": " + sourceDirectory + " -> " + destinationDirectory;
    }
}
