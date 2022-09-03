package interaction.applicationcommand;

import org.javacord.api.interaction.MessageContextMenu;
import org.javacord.api.interaction.MessageContextMenuBuilder;
import org.javacord.api.interaction.MessageContextMenuInteraction;

public abstract non-sealed class MessageContextMenuCommand extends AbstractApplicationCommand {

    final MessageContextMenuBuilder messageContextMenuBuilder;

    protected MessageContextMenuCommand(final String name, final boolean global) {
        super(name, global);
        this.messageContextMenuBuilder = MessageContextMenu.with(name, null);
    }

    @Override
    public final MessageContextMenuBuilder getApplicationCommandBuilder() {
        return messageContextMenuBuilder;
    }

    public abstract void runCommand(MessageContextMenuInteraction interaction);
}