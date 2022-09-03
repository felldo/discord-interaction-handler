package interaction.component;

import org.javacord.api.interaction.ModalInteraction;

public abstract class ModalComponent extends AbstractComponent {

    /**
     * @param customIdPrefix The customId as a Regex.
     */
    protected ModalComponent(final String customIdPrefix) {
        super(customIdPrefix);
    }

    public abstract void runModalComponent(final ModalInteraction interaction);
}
