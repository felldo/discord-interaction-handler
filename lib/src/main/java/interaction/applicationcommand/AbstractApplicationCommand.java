package interaction.applicationcommand;

import de.bettertickets.bot.commands.commandframework.interaction.Interaction;
import org.javacord.api.interaction.ApplicationCommandBuilder;

public abstract sealed class AbstractApplicationCommand implements Interaction permits MessageContextMenuCommand, SlashCommand, UserContextMenuCommand {

    private final boolean global;
    private final String name;

    protected AbstractApplicationCommand(final String name, final boolean global) {
        this.name = name;
        this.global = global;
    }

    public abstract ApplicationCommandBuilder<?, ?, ?> getApplicationCommandBuilder();

    public boolean isGlobal() {
        return global;
    }

    public String getName() {
        return name;
    }
}