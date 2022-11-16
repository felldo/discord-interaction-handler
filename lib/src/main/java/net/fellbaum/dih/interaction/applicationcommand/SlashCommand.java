package net.fellbaum.dih.interaction.applicationcommand;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.interaction.*;

/**
 * A slash command.
 */
public abstract sealed class SlashCommand extends AbstractApplicationCommand permits GlobalSlashCommand, ServerSlashCommand {

    private static final Logger LOGGER = LogManager.getLogger(SlashCommand.class);

    private final SlashCommandBuilder slashCommandBuilder;

    /**
     * Creates a new slash command.
     *
     * @param name        The name of the command.
     * @param description The description of the command.
     * @param global      Whether the command is global or not.
     */
    protected SlashCommand(final String name, final String description, final boolean global) {
        super(name, global);
        this.slashCommandBuilder = org.javacord.api.interaction.SlashCommand.with(name, description);
    }

    @Override
    public SlashCommandBuilder getApplicationCommandBuilder() {
        return slashCommandBuilder;
    }

    /**
     * Runs the command.
     *
     * @param interaction The interaction that triggered this command.
     */
    public abstract void runCommand(SlashCommandInteraction interaction);

    /**
     * Autocompletion handler for the first option of this slash command.
     *
     * @param interaction The interaction that triggered this autocompletion handler.
     */
    public void autocompletionHandler(AutocompleteInteraction interaction) {
        LOGGER.info("Autocompletion interaction [/{}\t<{}>]\ttriggered a not overridden autocompletion handler.",
                interaction.getCommandName(), interaction.getFocusedOption().getName());
    }

}