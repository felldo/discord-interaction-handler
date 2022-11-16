package net.fellbaum.dih.handler;

import net.fellbaum.dih.interaction.Interaction;
import net.fellbaum.dih.interaction.applicationcommand.MessageContextMenuCommand;
import net.fellbaum.dih.interaction.applicationcommand.SlashCommand;
import net.fellbaum.dih.interaction.applicationcommand.UserContextMenuCommand;
import net.fellbaum.dih.interaction.component.AbstractComponent;
import net.fellbaum.dih.interaction.component.ModalComponent;
import net.fellbaum.dih.interaction.component.SelectMenuComponent;
import net.fellbaum.dih.interaction.applicationcommand.AbstractApplicationCommand;
import net.fellbaum.dih.interaction.component.ButtonComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.ApplicationCommand;
import org.javacord.api.interaction.AutocompleteInteraction;
import org.javacord.api.interaction.MessageContextMenuInteraction;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.UserContextMenuInteraction;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * This class is responsible for handling all interactions.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public abstract sealed class InteractionHandler<T extends InteractionHandler<?>> permits SimpleInteractionHandler, ComplexInteractionHandler {

    private static final Logger LOGGER = LogManager.getLogger(InteractionHandler.class);

    /**
     * The global slash commands.
     */
    protected final Map<String, SlashCommand> globalSlashCommands = new ConcurrentHashMap<>();


    /**
     * The global message context menu commands.
     */
    protected final Map<String, MessageContextMenuCommand> globalMessageContextMenuCommands = new ConcurrentHashMap<>();


    /**
     * The global user context menu commands.
     */
    protected final Map<String, UserContextMenuCommand> globalUserContextMenuCommands = new ConcurrentHashMap<>();


    //COMPONENTS
    private final Map<Pattern, ButtonComponent> buttonComponents = new ConcurrentHashMap<>();
    private final Map<Pattern, SelectMenuComponent> selectMenuComponents = new ConcurrentHashMap<>();
    private final Map<Pattern, ModalComponent> modalComponents = new ConcurrentHashMap<>();

    /**
     * The slash command function.
     */
    protected Function<SlashCommandInteractionListenerData, Boolean> slashCommandFunction;

    /**
     * The autocomplete command function.
     */
    protected Function<AutocompleteInteractionListenerData, Boolean> autocompleteCommandFunction;

    /**
     * The message context menu command function.
     */
    protected Function<UserContextMenuInteractionListenerData, Boolean> userContextMenuFunction;

    /**
     * The user context menu command function.
     */
    protected Function<MessageContextMenuInteractionListenerData, Boolean> messageContextMenuFunction;

    /**
     * The complexity mode.
     */
    protected final ComplexityMode complexityMode;

    /**
     * This class should not be instantiated by any other class, only by it's extending ones.
     */
    private InteractionHandler() {
        throw new UnsupportedOperationException("This class is not meant to be instantiated.");
    }

    /**
     * Creates a new interaction handler.
     *
     * @param complexityMode The complexity mode.
     */
    protected InteractionHandler(final ComplexityMode complexityMode) {
        this.complexityMode = complexityMode;
    }

    /**
     * Register the component.
     *
     * @param abstractComponent The component.
     * @return The interaction handler.
     */
    protected T registerComponent(final AbstractComponent abstractComponent) {
        if (abstractComponent instanceof ButtonComponent comp) {
            buttonComponents.put(Pattern.compile(comp.getCustomIdPrefix()), comp);
        } else if (abstractComponent instanceof SelectMenuComponent comp) {
            selectMenuComponents.put(Pattern.compile(comp.getCustomIdPrefix()), comp);
        } else if (abstractComponent instanceof ModalComponent comp) {
            modalComponents.put(Pattern.compile(comp.getCustomIdPrefix()), comp);
        } else {
            throw new IllegalArgumentException("Argument is a not supported Interaction");
        }

        return (T) this;
    }

    /**
     * Attach the interaction listeners to the {@link DiscordApi}.
     *
     * @param api The {@link DiscordApi} to attach the listeners to.
     * @return The current instance to chain methods.
     */
    public T attachListeners(final DiscordApi api) {

        api.addSlashCommandCreateListener(event -> {
            final SlashCommandInteraction interaction = event.getSlashCommandInteraction();
            final String commandName = interaction.getCommandName();
            final boolean isServerCommand = interaction.getRegisteredCommandServerId().isPresent();

            if (!isServerCommand && null != globalSlashCommands.get(commandName)) {
                handleCommand(interaction, globalSlashCommands.get(commandName));
                return;
            } else if (isServerCommand) {
                if (slashCommandFunction.apply(new SlashCommandInteractionListenerData(interaction, interaction.getRegisteredCommandServerId().orElseThrow(), commandName))) {
                    return;
                }
            }
            LOGGER.info("Received a slash command interaction for a not registered command");
        });

        api.addAutocompleteCreateListener(event -> {
            final AutocompleteInteraction interaction = event.getAutocompleteInteraction();
            final boolean isServerCommand = interaction.getRegisteredCommandServerId().isPresent();
            final String commandName = interaction.getCommandName();

            if (!isServerCommand && globalSlashCommands.containsKey(commandName)) {
                globalSlashCommands.get(commandName).autocompletionHandler(interaction);
                return;
            } else if (isServerCommand) {
                if (autocompleteCommandFunction.apply(new AutocompleteInteractionListenerData(interaction, interaction.getRegisteredCommandServerId().orElseThrow(), commandName))) {
                    return;
                }
            }
            LOGGER.info("Autocompletion interaction is not from a created slash command in this application");
        });

        api.addUserContextMenuCommandListener(event -> {
            final UserContextMenuInteraction interaction = event.getUserContextMenuInteraction();
            final String commandName = interaction.getCommandName();
            final boolean isServerCommand = interaction.getRegisteredCommandServerId().isPresent();

            if (!isServerCommand && null != globalUserContextMenuCommands.get(commandName)) {
                globalUserContextMenuCommands.get(commandName).runCommand(interaction);
            } else if (isServerCommand) {

                if (userContextMenuFunction.apply(new UserContextMenuInteractionListenerData(interaction, interaction.getRegisteredCommandServerId().orElseThrow(), commandName))) {
                    return;
                }

            }
            LOGGER.info("Received a user context menu command interaction for a not registered command");
        });

        api.addMessageContextMenuCommandListener(event -> {
            final MessageContextMenuInteraction interaction = event.getMessageContextMenuInteraction();
            final String commandName = interaction.getCommandName();
            final boolean isServerCommand = interaction.getRegisteredCommandServerId().isPresent();

            if (!isServerCommand && null != globalMessageContextMenuCommands.get(commandName)) {
                globalMessageContextMenuCommands.get(commandName).runCommand(interaction);
            } else if (isServerCommand) {
                if (messageContextMenuFunction.apply(new MessageContextMenuInteractionListenerData(interaction, interaction.getRegisteredCommandServerId().orElseThrow(), commandName))) {
                    return;
                }
            }
            LOGGER.info("Received a message context menu command interaction for a not registered command");

        });

        api.addButtonClickListener(event -> {
            final String customId = event.getButtonInteraction().getCustomId();
            final Set<Map.Entry<Pattern, ButtonComponent>> entrySet = buttonComponents.entrySet()
                    .stream()
                    .filter(stringComponentEntry -> stringComponentEntry.getKey().matcher(customId).matches())
                    .collect(Collectors.toSet());

            switch (entrySet.size()) {
                case 0 -> LOGGER.debug("No matching Pattern found for received component interaction: {}", customId);
                case 1 -> entrySet.stream().findFirst()
                        .ifPresent(patternButtonComponentEntry -> patternButtonComponentEntry.getValue()
                                .runButtonComponent(event.getButtonInteraction()));
                default ->
                        LOGGER.info(entrySet.stream().map(patternButtonComponentEntry -> patternButtonComponentEntry.getKey()
                                        .pattern() + " -> " + patternButtonComponentEntry.getValue().getClass().getSimpleName())
                                .collect(Collectors.joining(",", "Found multiple pattern patching the received component interaction: {", "}")));
            }
        });

        api.addSelectMenuChooseListener(event -> {
            final String customId = event.getSelectMenuInteraction().getCustomId();
            final Set<Map.Entry<Pattern, SelectMenuComponent>> entrySet = selectMenuComponents.entrySet()
                    .stream()
                    .filter(stringComponentEntry -> stringComponentEntry.getKey().matcher(customId).matches())
                    .collect(Collectors.toSet());

            switch (entrySet.size()) {
                case 0 -> LOGGER.debug("No matching Pattern found for received component interaction: {}", customId);
                case 1 -> entrySet.stream().findFirst()
                        .ifPresent(patternSelectMenuComponentEntry -> patternSelectMenuComponentEntry.getValue()
                                .runSelectMenuComponent(event.getSelectMenuInteraction()));
                default ->
                        LOGGER.info(entrySet.stream().map(patternSelectMenuComponentEntry -> patternSelectMenuComponentEntry.getKey()
                                        .pattern() + " -> " + patternSelectMenuComponentEntry.getValue().getClass().getSimpleName())
                                .collect(Collectors.joining(",", "Found multiple pattern patching the received component interaction: {", "}")));
            }
        });

        api.addModalSubmitListener(event -> {
            final String customId = event.getModalInteraction().getCustomId();
            final Set<Map.Entry<Pattern, ModalComponent>> entrySet = modalComponents.entrySet()
                    .stream()
                    .filter(stringComponentEntry -> stringComponentEntry.getKey().matcher(customId).matches())
                    .collect(Collectors.toSet());

            switch (entrySet.size()) {
                case 0 -> LOGGER.debug("No matching Pattern found for received component interaction: {}", customId);
                case 1 -> entrySet.stream().findFirst()
                        .ifPresent(patternSelectMenuComponentEntry -> patternSelectMenuComponentEntry.getValue()
                                .runModalComponent(event.getModalInteraction()));
                default ->
                        LOGGER.info(entrySet.stream().map(patternSelectMenuComponentEntry -> patternSelectMenuComponentEntry.getKey()
                                        .pattern() + " -> " + patternSelectMenuComponentEntry.getValue().getClass().getSimpleName())
                                .collect(Collectors.joining(",", "Found multiple pattern patching the received component interaction: {", "}")));
            }
        });
        return (T) this;
    }

    /**
     * Handle the bulk overwritten application commands.
     *
     * @param applicationCommands  The application commands.
     * @param globalOrServerString The global or server string.
     * @return The application commands.
     */
    protected Set<ApplicationCommand> handleBulkOverwrittenApplicationCommands(final Set<ApplicationCommand> applicationCommands, final String globalOrServerString) {
        applicationCommands.stream()
                .sorted(Comparator.comparing(o -> o.getClass().getInterfaces()[0].getSimpleName()))
                .forEach(applicationCommand -> {
                    LOGGER.debug("Registered {} {} command with ID <{}> and name <{}>",
                            globalOrServerString,
                            applicationCommand.getClass().getInterfaces()[0].getSimpleName(),
                            applicationCommand.getId(), applicationCommand.getName());

                });
        return applicationCommands;
    }

    /**
     * Handles receiving a slash command.
     *
     * @param interaction The event.
     * @param command     The command.
     */
    protected void handleCommand(final SlashCommandInteraction interaction, final SlashCommand command) {

        LOGGER.trace("Received slash command: {} ({})",
                interaction.getFullCommandName(),
                getLogArguments(interaction.getArguments()));

        command.runCommand(interaction);
    }

    /**
     * Bulk overwrite all global application commands.
     *
     * @param api The api instance.
     * @return A future to check if the operation was successful and the registered application commands.
     */
    public CompletableFuture<Set<ApplicationCommand>> bulkOverwriteGlobalApplicationCommands(final DiscordApi api) {
        return bulkOverwriteGlobalApplicationCommands(api, s -> true);
    }

    /**
     * Bulk overwrite all global application commands with the given names.
     *
     * @param api          The api instance.
     * @param commandNames The names of the commands to overwrite.
     * @return A future to check if the operation was successful and the registered application commands.
     */
    public CompletableFuture<Set<ApplicationCommand>> bulkOverwriteGlobalApplicationCommands(final DiscordApi api, final String... commandNames) {
        return bulkOverwriteGlobalApplicationCommands(api, Arrays.asList(commandNames));
    }

    /**
     * Bulk overwrite all global application commands with the given names.
     *
     * @param api          The api instance.
     * @param commandNames The names of the commands to overwrite.
     * @return A future to check if the operation was successful and the registered application commands.
     */
    public CompletableFuture<Set<ApplicationCommand>> bulkOverwriteGlobalApplicationCommands(final DiscordApi api, final Collection<String> commandNames) {
        return bulkOverwriteGlobalApplicationCommands(api, abstractApplicationCommand -> commandNames.contains(abstractApplicationCommand.getName()));
    }

    /**
     * Bulk overwrite all global application commands with the given names.
     *
     * @param api                  The api instance.
     * @param commandNamePredicate The predicate to check if the command should be overwritten.
     * @return A future to check if the operation was successful and the registered application commands.
     */
    public CompletableFuture<Set<ApplicationCommand>> bulkOverwriteGlobalApplicationCommands(final DiscordApi api, final Predicate<AbstractApplicationCommand> commandNamePredicate) {
        final Set<AbstractApplicationCommand> abstractApplicationCommands = new HashSet<>();
        abstractApplicationCommands.addAll(globalSlashCommands.values());
        abstractApplicationCommands.addAll(globalMessageContextMenuCommands.values());
        abstractApplicationCommands.addAll(globalUserContextMenuCommands.values());

        return api.bulkOverwriteGlobalApplicationCommands(abstractApplicationCommands
                        .stream()
                        .filter(commandNamePredicate)
                        .map(AbstractApplicationCommand::getApplicationCommandBuilder)
                        .collect(Collectors.toSet()))
                .thenApply(applicationCommands -> handleBulkOverwrittenApplicationCommands(applicationCommands, "Global"));
    }

    /**
     * Log the received slash command with its arguments.
     *
     * @param slashCommandInteractionOptions The slash command interaction options.
     * @return The log arguments.
     */
    private String getLogArguments(final List<SlashCommandInteractionOption> slashCommandInteractionOptions) {
        return slashCommandInteractionOptions.isEmpty()
                ? ""
                : getSlashCommandArgumentString(slashCommandInteractionOptions);
    }

    private String getSlashCommandArgumentString(final List<SlashCommandInteractionOption> options) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < options.size(); i++) {
            final SlashCommandInteractionOption option = options.get(i);
            stringBuilder
                    .append(option.getName())
                    .append(": ")
                    .append(option.getStringRepresentationValue().orElse(""));
            if (i != (options.size() - 1)) {
                stringBuilder.append(", ");
            }
        }
        return stringBuilder.toString();
    }
}
