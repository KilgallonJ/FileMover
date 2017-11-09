package uk.co.mhr.filemover.config;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * This class serves as the JSON wrapper for the entire configuration of the app.
 * @author Jonathan Kilgallon
 * @since Version 0.0.1
 */
@JsonRootName("configuration")
public final class AppConfiguration {

    @JsonProperty("title")
    private String title;

    @JsonProperty("transferRule")
    private List<TransferRuleConfiguration> transferRules;

    /**
     * Retrieves the string that should be used as the window title for the app.
     * @return The title of the app window.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Retrieves the list of configured transfer rules.
     * @return The transfer rules.
     */
    public List<TransferRuleConfiguration> getTransferRules() {
        return transferRules;
    }
}
