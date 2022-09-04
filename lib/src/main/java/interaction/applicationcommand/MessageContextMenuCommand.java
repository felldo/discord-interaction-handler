package interaction.applicationcommand;

import org.javacord.api.interaction.MessageContextMenu;
import org.javacord.api.interaction.MessageContextMenuBuilder;
import org.javacord.api.interaction.MessageContextMenuInteraction;

/**
 * A message context menu command.
 */
public abstract non-sealed class MessageContextMenuCommand extends AbstractApplicationCommand {

    /**
     * The message context menu builder.
     */
    private final MessageContextMenuBuilder messageContextMenuBuilder;

    /**
     * Creates a new message context menu command.
     *
     * @param name The name of the command.
     * @param global Whether the command should be global or not.
     */
    protected MessageContextMenuCommand(final String name, final boolean global) {
        super(name, global);
        this.messageContextMenuBuilder = MessageContextMenu.with(name);
    }

    @Override
    public final MessageContextMenuBuilder getApplicationCommandBuilder() {
        return messageContextMenuBuilder;
    }

    /**
     * Runs the command.
     *
     * @param interaction The interaction that triggered this command.
     */
    public abstract void runCommand(MessageContextMenuInteraction interaction);
}