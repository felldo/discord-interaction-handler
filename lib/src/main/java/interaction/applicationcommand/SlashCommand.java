package interaction.applicationcommand;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.interaction.*;

public abstract non-sealed class SlashCommand extends AbstractApplicationCommand {

    private static final Logger LOGGER = LogManager.getLogger(SlashCommand.class);

    final SlashCommandBuilder slashCommandBuilder;

    protected SlashCommand(final String name, final String description, final boolean global) {
        super(name, global);
        this.slashCommandBuilder = org.javacord.api.interaction.SlashCommand.with(name, description);
    }

    public SlashCommandBuilder getApplicationCommandBuilder() {
        return slashCommandBuilder;
    }

    public abstract void runCommand(SlashCommandInteraction interaction);

    private void logNotOverriddenAutocompletionHandler(AutocompleteInteraction interaction) {
        LOGGER.info("Autocompletion interaction [/{}\t<{}>]\ttriggered a not overridden autocompletion handler. Please check the argument position of this autocompletable option and ensure to override the correct method.",
                interaction.getCommandName(), interaction.getFocusedOption().getName());
    }

    public void autocompletionHandler_0(AutocompleteInteraction interaction) {
        logNotOverriddenAutocompletionHandler(interaction);
    }

    public void autocompletionHandler_1(AutocompleteInteraction interaction) {
        logNotOverriddenAutocompletionHandler(interaction);
    }

    public void autocompletionHandler_2(AutocompleteInteraction interaction) {
        logNotOverriddenAutocompletionHandler(interaction);
    }

    public void autocompletionHandler_3(AutocompleteInteraction interaction) {
        logNotOverriddenAutocompletionHandler(interaction);
    }

    public void autocompletionHandler_4(AutocompleteInteraction interaction) {
        logNotOverriddenAutocompletionHandler(interaction);
    }

    public void autocompletionHandler_5(AutocompleteInteraction interaction) {
        logNotOverriddenAutocompletionHandler(interaction);
    }

    public void autocompletionHandler_6(AutocompleteInteraction interaction) {
        logNotOverriddenAutocompletionHandler(interaction);
    }

    public void autocompletionHandler_7(AutocompleteInteraction interaction) {
        logNotOverriddenAutocompletionHandler(interaction);
    }

    public void autocompletionHandler_8(AutocompleteInteraction interaction) {
        logNotOverriddenAutocompletionHandler(interaction);
    }

    public void autocompletionHandler_9(AutocompleteInteraction interaction) {
        logNotOverriddenAutocompletionHandler(interaction);
    }

    public void autocompletionHandler_10(AutocompleteInteraction interaction) {
        logNotOverriddenAutocompletionHandler(interaction);
    }

    public void autocompletionHandler_11(AutocompleteInteraction interaction) {
        logNotOverriddenAutocompletionHandler(interaction);
    }

    public void autocompletionHandler_12(AutocompleteInteraction interaction) {
        logNotOverriddenAutocompletionHandler(interaction);
    }

    public void autocompletionHandler_13(AutocompleteInteraction interaction) {
        logNotOverriddenAutocompletionHandler(interaction);
    }

    public void autocompletionHandler_14(AutocompleteInteraction interaction) {
        logNotOverriddenAutocompletionHandler(interaction);
    }

    public void autocompletionHandler_15(AutocompleteInteraction interaction) {
        logNotOverriddenAutocompletionHandler(interaction);
    }

    public void autocompletionHandler_16(AutocompleteInteraction interaction) {
        logNotOverriddenAutocompletionHandler(interaction);
    }

    public void autocompletionHandler_17(AutocompleteInteraction interaction) {
        logNotOverriddenAutocompletionHandler(interaction);
    }

    public void autocompletionHandler_18(AutocompleteInteraction interaction) {
        logNotOverriddenAutocompletionHandler(interaction);
    }

    public void autocompletionHandler_19(AutocompleteInteraction interaction) {
        logNotOverriddenAutocompletionHandler(interaction);
    }

    public void autocompletionHandler_20(AutocompleteInteraction interaction) {
        logNotOverriddenAutocompletionHandler(interaction);
    }

    public void autocompletionHandler_21(AutocompleteInteraction interaction) {
        logNotOverriddenAutocompletionHandler(interaction);
    }

    public void autocompletionHandler_22(AutocompleteInteraction interaction) {
        logNotOverriddenAutocompletionHandler(interaction);
    }

    public void autocompletionHandler_23(AutocompleteInteraction interaction) {
        logNotOverriddenAutocompletionHandler(interaction);
    }

    public void autocompletionHandler_24(AutocompleteInteraction interaction) {
        logNotOverriddenAutocompletionHandler(interaction);
    }

}