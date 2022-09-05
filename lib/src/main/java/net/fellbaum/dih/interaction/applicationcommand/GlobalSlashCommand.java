package net.fellbaum.dih.interaction.applicationcommand;

/**
 * Abstract class for all application commands.
 */
public abstract class GlobalSlashCommand extends SlashCommand {

    /**
     * Creates a new application command.
     *
     * @param name The name of the command.
     * @param description The description of the command.
     */
    public GlobalSlashCommand(String name, String description) {
        super(name, description, true);
    }
}
