package net.fellbaum.dih.interaction.component;

import org.javacord.api.interaction.ModalInteraction;

/**
 * A modal component.
 */
public abstract class ModalComponent extends AbstractComponent {

    /**
     * @param customIdPrefix The customId as a Regex.
     */
    protected ModalComponent(final String customIdPrefix) {
        super(customIdPrefix);
    }

    /**
     * Runs the component.
     *
     * @param interaction The interaction that triggered this component.
     */
    public abstract void runModalComponent(final ModalInteraction interaction);
}
