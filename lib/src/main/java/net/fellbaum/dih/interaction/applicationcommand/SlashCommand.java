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
     * Log when receiving an auto-completion interaction is received but its method is not overridden.
     *
     * @param interaction The interaction that triggered this command.
     */
    private void logNotOverriddenAutocompletionHandler(AutocompleteInteraction interaction) {
        LOGGER.info("Autocompletion interaction [/{}\t<{}>]\ttriggered a not overridden autocompletion handler. Please check the argument position of this autocompletable option and ensure to override the correct method.",
                interaction.getCommandName(), interaction.getFocusedOption().getName());
    }

    /**
     * Autocompletion handler for the first option of this slash command.
     *
     * @param interaction The interaction that triggered this autocompletion handler.
     */
    public void autocompletionHandler_0(AutocompleteInteraction interaction) {
        logNotOverriddenAutocompletionHandler(interaction);
    }

    /**
     * Autocompletion handler for the second option of this slash command.
     *
     * @param interaction The interaction that triggered this autocompletion handler.
     */
    public void autocompletionHandler_1(AutocompleteInteraction interaction) {
        logNotOverriddenAutocompletionHandler(interaction);
    }

    /**
     * Autocompletion handler for the third option of this slash command.
     *
     * @param interaction The interaction that triggered this autocompletion handler.
     */
    public void autocompletionHandler_2(AutocompleteInteraction interaction) {
        logNotOverriddenAutocompletionHandler(interaction);
    }

    /**
     * Autocompletion handler for the fourth option of this slash command.
     *
     * @param interaction The interaction that triggered this autocompletion handler.
     */
    public void autocompletionHandler_3(AutocompleteInteraction interaction) {
        logNotOverriddenAutocompletionHandler(interaction);
    }

    /**
     * Autocompletion handler for the fifth option of this slash command.
     *
     * @param interaction The interaction that triggered this autocompletion handler.
     */
    public void autocompletionHandler_4(AutocompleteInteraction interaction) {
        logNotOverriddenAutocompletionHandler(interaction);
    }

    /**
     * Autocompletion handler for the sixth option of this slash command.
     *
     * @param interaction The interaction that triggered this autocompletion handler.
     */
    public void autocompletionHandler_5(AutocompleteInteraction interaction) {
        logNotOverriddenAutocompletionHandler(interaction);
    }

    /**
     * Autocompletion handler for the seventh option of this slash command.
     *
     * @param interaction The interaction that triggered this autocompletion handler.
     */
    public void autocompletionHandler_6(AutocompleteInteraction interaction) {
        logNotOverriddenAutocompletionHandler(interaction);
    }

    /**
     * Autocompletion handler for the eighth option of this slash command.
     *
     * @param interaction The interaction that triggered this autocompletion handler.
     */
    public void autocompletionHandler_7(AutocompleteInteraction interaction) {
        logNotOverriddenAutocompletionHandler(interaction);
    }

    /**
     * Autocompletion handler for the ninth option of this slash command.
     *
     * @param interaction The interaction that triggered this autocompletion handler.
     */
    public void autocompletionHandler_8(AutocompleteInteraction interaction) {
        logNotOverriddenAutocompletionHandler(interaction);
    }

    /**
     * Autocompletion handler for the tenth option of this slash command.
     *
     * @param interaction The interaction that triggered this autocompletion handler.
     */
    public void autocompletionHandler_9(AutocompleteInteraction interaction) {
        logNotOverriddenAutocompletionHandler(interaction);
    }

    /**
     * Autocompletion handler for the eleventh option of this slash command.
     *
     * @param interaction The interaction that triggered this autocompletion handler.
     */
    public void autocompletionHandler_10(AutocompleteInteraction interaction) {
        logNotOverriddenAutocompletionHandler(interaction);
    }

    /**
     * Autocompletion handler for the twelfth option of this slash command.
     *
     * @param interaction The interaction that triggered this autocompletion handler.
     */
    public void autocompletionHandler_11(AutocompleteInteraction interaction) {
        logNotOverriddenAutocompletionHandler(interaction);
    }

    /**
     * Autocompletion handler for the thirteenth option of this slash command.
     *
     * @param interaction The interaction that triggered this autocompletion handler.
     */
    public void autocompletionHandler_12(AutocompleteInteraction interaction) {
        logNotOverriddenAutocompletionHandler(interaction);
    }

    /**
     * Autocompletion handler for the fourteenth option of this slash command.
     *
     * @param interaction The interaction that triggered this autocompletion handler.
     */
    public void autocompletionHandler_13(AutocompleteInteraction interaction) {
        logNotOverriddenAutocompletionHandler(interaction);
    }

    /**
     * Autocompletion handler for the fifteenth option of this slash command.
     *
     * @param interaction The interaction that triggered this autocompletion handler.
     */
    public void autocompletionHandler_14(AutocompleteInteraction interaction) {
        logNotOverriddenAutocompletionHandler(interaction);
    }

    /**
     * Autocompletion handler for the sixteenth option of this slash command.
     *
     * @param interaction The interaction that triggered this autocompletion handler.
     */
    public void autocompletionHandler_15(AutocompleteInteraction interaction) {
        logNotOverriddenAutocompletionHandler(interaction);
    }

    /**
     * Autocompletion handler for the seventeenth option of this slash command.
     *
     * @param interaction The interaction that triggered this autocompletion handler.
     */
    public void autocompletionHandler_16(AutocompleteInteraction interaction) {
        logNotOverriddenAutocompletionHandler(interaction);
    }

    /**
     * Autocompletion handler for the eighteenth option of this slash command.
     *
     * @param interaction The interaction that triggered this autocompletion handler.
     */
    public void autocompletionHandler_17(AutocompleteInteraction interaction) {
        logNotOverriddenAutocompletionHandler(interaction);
    }

    /**
     * Autocompletion handler for the nineteenth option of this slash command.
     *
     * @param interaction The interaction that triggered this autocompletion handler.
     */
    public void autocompletionHandler_18(AutocompleteInteraction interaction) {
        logNotOverriddenAutocompletionHandler(interaction);
    }

    /**
     * Autocompletion handler for the twentieth option of this slash command.
     *
     * @param interaction The interaction that triggered this autocompletion handler.
     */
    public void autocompletionHandler_19(AutocompleteInteraction interaction) {
        logNotOverriddenAutocompletionHandler(interaction);
    }

    /**
     * Autocompletion handler for the twenty-first option of this slash command.
     *
     * @param interaction The interaction that triggered this autocompletion handler.
     */
    public void autocompletionHandler_20(AutocompleteInteraction interaction) {
        logNotOverriddenAutocompletionHandler(interaction);
    }

    /**
     * Autocompletion handler for the twenty-second option of this slash command.
     *
     * @param interaction The interaction that triggered this autocompletion handler.
     */
    public void autocompletionHandler_21(AutocompleteInteraction interaction) {
        logNotOverriddenAutocompletionHandler(interaction);
    }

    /**
     * Autocompletion handler for the twenty-third option of this slash command.
     *
     * @param interaction The interaction that triggered this autocompletion handler.
     */
    public void autocompletionHandler_22(AutocompleteInteraction interaction) {
        logNotOverriddenAutocompletionHandler(interaction);
    }

    /**
     * Autocompletion handler for the twenty-fourth option of this slash command.
     *
     * @param interaction The interaction that triggered this autocompletion handler.
     */
    public void autocompletionHandler_23(AutocompleteInteraction interaction) {
        logNotOverriddenAutocompletionHandler(interaction);
    }

    /**
     * Autocompletion handler for the twenty-fifth option of this slash command.
     *
     * @param interaction The interaction that triggered this autocompletion handler.
     */
    public void autocompletionHandler_24(AutocompleteInteraction interaction) {
        logNotOverriddenAutocompletionHandler(interaction);
    }

}