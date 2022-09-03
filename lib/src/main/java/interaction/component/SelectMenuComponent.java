package interaction.component;

import org.javacord.api.interaction.SelectMenuInteraction;

public abstract class SelectMenuComponent extends AbstractComponent {

    /**
     * @param customIdPrefix The customId as a Regex.
     */
    protected SelectMenuComponent(final String customIdPrefix) {
        super(customIdPrefix);
    }

    public abstract void runSelectMenuComponent(SelectMenuInteraction interaction);
}
