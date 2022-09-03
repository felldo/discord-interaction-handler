package interaction.applicationcommand;

public abstract class ServerSlashCommand extends SlashCommand {

    public ServerSlashCommand(String name, String description) {
        super(name, description, false);
    }
}
