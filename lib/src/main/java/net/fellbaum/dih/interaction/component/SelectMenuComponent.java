package net.fellbaum.dih.interaction.component;

import org.javacord.api.interaction.SelectMenuInteraction;

/**
 * A select menu component.
 */
public abstract class SelectMenuComponent extends AbstractComponent {

    /**
     * @param customIdPrefix The customId as a Regex.
     */
    protected SelectMenuComponent(final String customIdPrefix) {
        super(customIdPrefix);
    }

    /**
     * Runs the component.
     * @param interaction The interaction that triggered this component.
     */
    public abstract void runSelectMenuComponent(SelectMenuInteraction interaction);
}
