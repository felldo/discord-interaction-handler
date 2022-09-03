package interaction.component;

import de.bettertickets.bot.commands.commandframework.interaction.Interaction;

public abstract class AbstractComponent implements Interaction {

    private final String customIdPrefix;

    protected AbstractComponent(final String customIdPrefix) {
        this.customIdPrefix = customIdPrefix;
    }

    public String getCustomIdPrefix() {
        return customIdPrefix;
    }

}
