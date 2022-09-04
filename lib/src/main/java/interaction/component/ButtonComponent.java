package interaction.component;

import org.javacord.api.interaction.ButtonInteraction;

/**
 * Abstract class for all buttons.
 */
public abstract class ButtonComponent extends AbstractComponent {

    /**
     * @param customIdPrefix The customId as a Regex.
     */
    protected ButtonComponent(final String customIdPrefix) {
        super(customIdPrefix);
    }

    /**
     * Runs the button interaction.
     *
     * @param interaction The interaction.
     */
    public abstract void runButtonComponent(final ButtonInteraction interaction);
}
