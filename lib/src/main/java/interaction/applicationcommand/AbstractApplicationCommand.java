package interaction.applicationcommand;

import interaction.Interaction;
import org.javacord.api.interaction.ApplicationCommandBuilder;

/**
 * Abstract class for all application commands.
 */
public abstract sealed class AbstractApplicationCommand implements Interaction permits MessageContextMenuCommand, SlashCommand, UserContextMenuCommand {

    /**
     * The name of the command.
     */
    private final String name;

    /**
     * Whether the command is global.
     */
    private final boolean global;

    /**
     * Creates a new abstract application command.
     *
     * @param name   The name of the command.
     * @param global Whether the command is global.
     */
    protected AbstractApplicationCommand(final String name, final boolean global) {
        this.name = name;
        this.global = global;
    }

    /**
     * Gets the application command builder.
     *
     * @return The application command builder.
     */
    public abstract ApplicationCommandBuilder<?, ?, ?> getApplicationCommandBuilder();

    /**
     * Gets whether the command is global.
     *
     * @return Whether the command is global.
     */
    public boolean isGlobal() {
        return global;
    }

    /**
     * Gets the name of the command.
     *
     * @return The name of the command.
     */
    public String getName() {
        return name;
    }
}