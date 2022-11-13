package net.fellbaum.dih.interaction.applicationcommand;

import org.javacord.api.interaction.UserContextMenu;
import org.javacord.api.interaction.UserContextMenuBuilder;
import org.javacord.api.interaction.UserContextMenuInteraction;

/**
 * A user context menu command.
 */
public abstract non-sealed class UserContextMenuCommand extends AbstractApplicationCommand {

    /**
     * The user context menu builder.
     */
    private final UserContextMenuBuilder userContextMenuBuilder;

    /**
     * The user context menu command.
     * @param name The name of the command.
     * @param global Whether the command is global.
     */
    protected UserContextMenuCommand(final String name, final boolean global) {
        super(name, global);
        this.userContextMenuBuilder = UserContextMenu.with(name);
    }

    @Override
    public final UserContextMenuBuilder getApplicationCommandBuilder() {
        return userContextMenuBuilder;
    }

    /**
     * Runs the command.
     *
     * @param interaction The interaction that triggered this command.
     */
    public abstract void runCommand(UserContextMenuInteraction interaction);
}