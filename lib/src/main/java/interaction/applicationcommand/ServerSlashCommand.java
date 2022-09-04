package interaction.applicationcommand;

/**
 * A server slash command.
 */
public abstract class ServerSlashCommand extends SlashCommand {

    /**
     * The server slash command builder.
     *
     * @param name        The name of the command.
     * @param description The description of the command.
     */
    public ServerSlashCommand(String name, String description) {
        super(name, description, false);
    }
}
