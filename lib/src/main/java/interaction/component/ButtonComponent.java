package interaction.component;

import org.javacord.api.interaction.ButtonInteraction;

public abstract class ButtonComponent extends AbstractComponent {

    /**
     * @param customIdPrefix The customId as a Regex.
     */
    protected ButtonComponent(final String customIdPrefix) {
        super(customIdPrefix);
    }

    public abstract void runButtonComponent(final ButtonInteraction interaction);
}
