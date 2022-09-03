package interaction.applicationcommand;

public abstract class GlobalSlashCommand extends SlashCommand {

    public GlobalSlashCommand(String name, String description) {
        super(name, description, true);
    }
}
