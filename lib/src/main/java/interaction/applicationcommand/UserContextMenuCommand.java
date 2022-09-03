package interaction.applicationcommand;

import org.javacord.api.interaction.UserContextMenu;
import org.javacord.api.interaction.UserContextMenuBuilder;
import org.javacord.api.interaction.UserContextMenuInteraction;

public abstract non-sealed class UserContextMenuCommand extends AbstractApplicationCommand {

    final UserContextMenuBuilder userContextMenuBuilder;

    protected UserContextMenuCommand(final String name, final boolean global) {
        super(name, global);
        this.userContextMenuBuilder = UserContextMenu.with(name, null);
    }

    @Override
    public final UserContextMenuBuilder getApplicationCommandBuilder() {
        return userContextMenuBuilder;
    }

    public abstract void runCommand(UserContextMenuInteraction interaction);
}