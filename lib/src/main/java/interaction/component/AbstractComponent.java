package interaction.component;

import interaction.Interaction;

/**
 * Abstract class for all components.
 */
public abstract class AbstractComponent implements Interaction {

    /**
     * The custom id of the component.
     */
    private final String customIdPrefix;


    /**
     * Creates a new abstract component.
     *
     * @param customIdPrefix The customId as a Regex.
     */
    protected AbstractComponent(final String customIdPrefix) {
        this.customIdPrefix = customIdPrefix;
    }

    /**
     * Gets the custom id prefix of the component.
     *
     * @return The custom id prefix of the component.
     */
    public String getCustomIdPrefix() {
        return customIdPrefix;
    }

}
