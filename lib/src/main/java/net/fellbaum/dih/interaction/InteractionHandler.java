package net.fellbaum.dih.interaction;

import net.fellbaum.dih.interaction.applicationcommand.MessageContextMenuCommand;
import net.fellbaum.dih.interaction.applicationcommand.SlashCommand;
import net.fellbaum.dih.interaction.applicationcommand.UserContextMenuCommand;
import net.fellbaum.dih.interaction.component.ModalComponent;
import net.fellbaum.dih.interaction.component.SelectMenuComponent;
import net.fellbaum.dih.interaction.applicationcommand.AbstractApplicationCommand;
import net.fellbaum.dih.interaction.component.ButtonComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.ApplicationCommand;
import org.javacord.api.interaction.AutocompleteInteraction;
import org.javacord.api.interaction.MessageContextMenuInteraction;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.UserContextMenuInteraction;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * This class is responsible for handling all interactions.
 */
@SuppressWarnings("unused")
public class InteractionHandler {

    private static final Logger LOGGER = LogManager.getLogger(InteractionHandler.class);

    private final Map<Long, ApplicationCommand> globalApplicationCommands = new ConcurrentHashMap<>();
    private final Map<Long, ApplicationCommand> serverApplicationCommands = new ConcurrentHashMap<>();


    private final Map<String, SlashCommand> globalSlashCommands = new ConcurrentHashMap<>();
    private final Map<String, SlashCommand> serverSlashCommands = new ConcurrentHashMap<>();

    private final Map<String, MessageContextMenuCommand> globalMessageContextMenuCommands = new ConcurrentHashMap<>();
    private final Map<String, MessageContextMenuCommand> serverMessageContextMenuCommands = new ConcurrentHashMap<>();
    private final Map<String, UserContextMenuCommand> globalUserContextMenuCommands = new ConcurrentHashMap<>();
    private final Map<String, UserContextMenuCommand> serverUserContextMenuCommands = new ConcurrentHashMap<>();

    //COMPONENTS
    private final Map<Pattern, ButtonComponent> buttonComponents = new ConcurrentHashMap<>();
    private final Map<Pattern, SelectMenuComponent> selectMenuComponents = new ConcurrentHashMap<>();
    private final Map<Pattern, ModalComponent> modalComponents = new ConcurrentHashMap<>();

    /**
     * Register an interaction to the handler.
     *
     * @param interaction The interaction to register.
     * @return The current instance to chain methods.
     */
    public InteractionHandler registerInteraction(Interaction interaction) {
        if (interaction instanceof ButtonComponent comp) {
            buttonComponents.put(Pattern.compile(comp.getCustomIdPrefix()), comp);
        } else if (interaction instanceof SelectMenuComponent comp) {
            selectMenuComponents.put(Pattern.compile(comp.getCustomIdPrefix()), comp);
        } else if (interaction instanceof ModalComponent comp) {
            modalComponents.put(Pattern.compile(comp.getCustomIdPrefix()), comp);
        } else if (interaction instanceof AbstractApplicationCommand abstractApplicationCommand) {
            if (abstractApplicationCommand instanceof SlashCommand s) {
                (abstractApplicationCommand.isGlobal()
                        ? globalSlashCommands
                        : serverSlashCommands).put(abstractApplicationCommand.getName(), s);
            } else if (abstractApplicationCommand instanceof MessageContextMenuCommand m) {
                (abstractApplicationCommand.isGlobal()
                        ? globalMessageContextMenuCommands
                        : serverMessageContextMenuCommands).put(abstractApplicationCommand.getName(), m);
            } else if (abstractApplicationCommand instanceof UserContextMenuCommand u) {
                (abstractApplicationCommand.isGlobal()
                        ? globalUserContextMenuCommands
                        : serverUserContextMenuCommands).put(abstractApplicationCommand.getName(), u);
            } else {
                throw new IllegalArgumentException("Argument is a not supported Interaction");
            }

        } else {
            throw new IllegalArgumentException("Argument is a not supported Interaction");
        }
        return this;
    }

    /**
     * Attach the interaction listeners to the {@link DiscordApi}.
     *
     * @param api The {@link DiscordApi} to attach the listeners to.
     */
    public void attachListeners(DiscordApi api) {
        //Check if a server leaves and cleanup server application commands
        api.addServerLeaveListener(event -> {
            serverApplicationCommands.remove(event.getServer().getId());
        });

        api.addSlashCommandCreateListener(event -> {
            final SlashCommandInteraction interaction = event.getSlashCommandInteraction();
            final long commandId = interaction.getCommandId();
            final String commandName = interaction.getCommandName();

            if (globalApplicationCommands.containsKey(commandId) && null != globalSlashCommands.get(commandName)) {
                handleCommand(event, globalSlashCommands.get(commandName));
            } else if (serverApplicationCommands.containsKey(commandId) && null != serverSlashCommands.get(commandName)) {
                handleCommand(event, serverSlashCommands.get(commandName));
            } else {
                //Should be a server command. This should happen in case on startup the server commands
                //were not overwritten for this server so the commands are unknown.
                //TODO: Find a solution to avoid this problem without having to override the commands each startup.
                //Maybe do not cache the commands by it's id but with the name once they are registered in the InteractionHandler
                interaction.getServer().ifPresent(server -> interaction.getApi().bulkOverwriteServerApplicationCommands(server, Collections.emptySet()));
                LOGGER.info("Received a slash command interaction for a not registered command");
            }
        });

        api.addAutocompleteCreateListener(event -> {
            final AutocompleteInteraction autocompleteInteraction = event.getAutocompleteInteraction();

            if (globalSlashCommands.containsKey(autocompleteInteraction.getCommandName())) {
                executeSlashCommandAutocompletionInteractionHandler(globalSlashCommands, autocompleteInteraction);
            } else if (serverSlashCommands.containsKey(autocompleteInteraction.getCommandName())) {
                executeSlashCommandAutocompletionInteractionHandler(serverSlashCommands, autocompleteInteraction);
            } else {
                LOGGER.info("Autocompletion interaction is not from a created slash command in this application");
            }
        });

        api.addUserContextMenuCommandListener(event -> {
            final UserContextMenuInteraction interaction = event.getUserContextMenuInteraction();
            final long commandId = interaction.getCommandId();
            final String commandName = interaction.getCommandName();

            if (globalApplicationCommands.containsKey(commandId) && null != globalUserContextMenuCommands.get(commandName)) {
                globalUserContextMenuCommands.get(commandName).runCommand(interaction);
            } else if (serverApplicationCommands.containsKey(commandId) && null != serverUserContextMenuCommands.get(commandName)) {
                serverUserContextMenuCommands.get(commandName).runCommand(interaction);
            } else {
                //TODO: Same here
                interaction.getServer().ifPresent(server -> interaction.getApi().bulkOverwriteServerApplicationCommands(server, Collections.emptySet()));
                LOGGER.info("Received a user context menu command interaction for a not registered command");
            }
        });

        api.addMessageContextMenuCommandListener(event -> {
            final MessageContextMenuInteraction interaction = event.getMessageContextMenuInteraction();
            final long commandId = interaction.getCommandId();
            final String commandName = interaction.getCommandName();

            if (globalApplicationCommands.containsKey(commandId) && null != globalMessageContextMenuCommands.get(commandName)) {
                globalMessageContextMenuCommands.get(commandName).runCommand(interaction);
            } else if (serverApplicationCommands.containsKey(commandId) && null != serverMessageContextMenuCommands.get(commandName)) {
                serverMessageContextMenuCommands.get(commandName).runCommand(interaction);
            } else {
                //TODO: Same here
                interaction.getServer().ifPresent(server -> interaction.getApi().bulkOverwriteServerApplicationCommands(server, Collections.emptySet()));
                LOGGER.info("Received a message context menu command interaction for a not registered command");
            }
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
    }

    private void executeSlashCommandAutocompletionInteractionHandler(final Map<String, SlashCommand> applicationCommands, final AutocompleteInteraction autocompleteInteraction) {
        org.javacord.api.interaction.SlashCommand realSlashCommand = (org.javacord.api.interaction.SlashCommand)
                autocompleteInteraction.getServer()
                        .map(server -> serverApplicationCommands)
                        .orElse(globalApplicationCommands)
                        .get(autocompleteInteraction.getCommandId());

        String[] splitCommandName = autocompleteInteraction.getFullCommandName().split(" ");
        List<SlashCommandOption> slashCommandOptions = realSlashCommand.getOptions();
        if (splitCommandName.length > 1) {
            for (int i = 1; i < splitCommandName.length; i++) {
                String s = splitCommandName[i];
                slashCommandOptions = slashCommandOptions.stream()
                        .filter(slashCommandOption -> slashCommandOption.getName().equals(s))
                        .findFirst()
                        .map(SlashCommandOption::getOptions)
                        .orElse(Collections.emptyList());
            }
        }
        int index = 0;
        for (int i = 0; i < slashCommandOptions.size(); i++) {
            SlashCommandOption slashCommandOption = slashCommandOptions.get(i);
            if (slashCommandOption.getName().equals(autocompleteInteraction.getFocusedOption().getName())) {
                index = i;
                break;
            }
        }

        final SlashCommand slashCommand = applicationCommands.get(autocompleteInteraction.getCommandName());

        switch (index) {
            case 0 -> slashCommand.autocompletionHandler_0(autocompleteInteraction);
            case 1 -> slashCommand.autocompletionHandler_1(autocompleteInteraction);
            case 2 -> slashCommand.autocompletionHandler_2(autocompleteInteraction);
            case 3 -> slashCommand.autocompletionHandler_3(autocompleteInteraction);
            case 4 -> slashCommand.autocompletionHandler_4(autocompleteInteraction);
            case 5 -> slashCommand.autocompletionHandler_5(autocompleteInteraction);
            case 6 -> slashCommand.autocompletionHandler_6(autocompleteInteraction);
            case 7 -> slashCommand.autocompletionHandler_7(autocompleteInteraction);
            case 8 -> slashCommand.autocompletionHandler_8(autocompleteInteraction);
            case 9 -> slashCommand.autocompletionHandler_9(autocompleteInteraction);
            case 10 -> slashCommand.autocompletionHandler_10(autocompleteInteraction);
            case 11 -> slashCommand.autocompletionHandler_11(autocompleteInteraction);
            case 12 -> slashCommand.autocompletionHandler_12(autocompleteInteraction);
            case 13 -> slashCommand.autocompletionHandler_13(autocompleteInteraction);
            case 14 -> slashCommand.autocompletionHandler_14(autocompleteInteraction);
            case 15 -> slashCommand.autocompletionHandler_15(autocompleteInteraction);
            case 16 -> slashCommand.autocompletionHandler_16(autocompleteInteraction);
            case 17 -> slashCommand.autocompletionHandler_17(autocompleteInteraction);
            case 18 -> slashCommand.autocompletionHandler_18(autocompleteInteraction);
            case 19 -> slashCommand.autocompletionHandler_19(autocompleteInteraction);
            case 20 -> slashCommand.autocompletionHandler_20(autocompleteInteraction);
            case 21 -> slashCommand.autocompletionHandler_21(autocompleteInteraction);
            case 22 -> slashCommand.autocompletionHandler_22(autocompleteInteraction);
            case 23 -> slashCommand.autocompletionHandler_23(autocompleteInteraction);
            case 24 -> slashCommand.autocompletionHandler_24(autocompleteInteraction);
            default -> LOGGER.info("Autocompletion index {} out of range. No method was executed", index);
        }
    }

    //
    // Application command methods
    //

    /**
     * Bulk overwrite all global application commands.
     *
     * @param api The api instance.
     * @return A future to check if the operation was successful and the registered application commands.
     */
    public CompletableFuture<Set<ApplicationCommand>> bulkOverwriteGlobalApplicationCommands(DiscordApi api) {
        return bulkOverwriteGlobalApplicationCommands(api, s -> true);
    }

    /**
     * Bulk overwrite all global application commands with the given names.
     *
     * @param api          The api instance.
     * @param commandNames The names of the commands to overwrite.
     * @return A future to check if the operation was successful and the registered application commands.
     */
    public CompletableFuture<Set<ApplicationCommand>> bulkOverwriteGlobalApplicationCommands(DiscordApi api, String... commandNames) {
        return bulkOverwriteGlobalApplicationCommands(api, Arrays.asList(commandNames));
    }

    /**
     * Bulk overwrite all global application commands with the given names.
     *
     * @param api          The api instance.
     * @param commandNames The names of the commands to overwrite.
     * @return A future to check if the operation was successful and the registered application commands.
     */
    public CompletableFuture<Set<ApplicationCommand>> bulkOverwriteGlobalApplicationCommands(DiscordApi api, Collection<String> commandNames) {
        return bulkOverwriteGlobalApplicationCommands(api, abstractApplicationCommand -> commandNames.contains(abstractApplicationCommand.getName()));
    }

    /**
     * Bulk overwrite all global application commands with the given names.
     *
     * @param api                  The api instance.
     * @param commandNamePredicate The predicate to check if the command should be overwritten.
     * @return A future to check if the operation was successful and the registered application commands.
     */
    public CompletableFuture<Set<ApplicationCommand>> bulkOverwriteGlobalApplicationCommands(DiscordApi api, Predicate<AbstractApplicationCommand> commandNamePredicate) {
        Set<AbstractApplicationCommand> abstractApplicationCommands = new HashSet<>();
        abstractApplicationCommands.addAll(globalSlashCommands.values());
        abstractApplicationCommands.addAll(globalMessageContextMenuCommands.values());
        abstractApplicationCommands.addAll(globalUserContextMenuCommands.values());

        return api.bulkOverwriteGlobalApplicationCommands(abstractApplicationCommands
                        .stream()
                        .filter(commandNamePredicate)
                        .map(AbstractApplicationCommand::getApplicationCommandBuilder)
                        .collect(Collectors.toSet()))
                .thenApply(applicationCommands -> handleBulkOverwrittenApplicationCommands(applicationCommands, globalApplicationCommands, "Global"));
    }

    /**
     * Bulk overwrite all registered guild application commands.
     *
     * @param server The server.
     * @return A future to check if the operation was successful and the registered application commands.
     */
    public CompletableFuture<Set<ApplicationCommand>> bulkOverwriteServerApplicationCommands(Server server) {
        return bulkOverwriteServerApplicationCommands(server, s -> true);
    }

    /**
     * Bulk overwrite all guild application commands with the given names.
     *
     * @param server       The server.
     * @param commandNames The names of the commands to overwrite.
     * @return A future to check if the operation was successful and the registered application commands.
     */
    public CompletableFuture<Set<ApplicationCommand>> bulkOverwriteServerApplicationCommands(Server server, String... commandNames) {
        return bulkOverwriteServerApplicationCommands(server, Arrays.asList(commandNames));
    }

    /**
     * Bulk overwrite all guild application commands with the given names.
     *
     * @param server       The server.
     * @param commandNames The names of the commands to overwrite.
     * @return A future to check if the operation was successful and the registered application commands.
     */
    public CompletableFuture<Set<ApplicationCommand>> bulkOverwriteServerApplicationCommands(Server server, Collection<String> commandNames) {
        return bulkOverwriteServerApplicationCommands(server, abstractApplicationCommand -> commandNames.contains(abstractApplicationCommand.getName()));
    }

    /**
     * Bulk overwrite all guild application commands with the given names.
     *
     * @param server               The server.
     * @param commandNamePredicate The predicate to check if the command should be overwritten.
     * @return A future to check if the operation was successful and the registered application commands.
     */
    public CompletableFuture<Set<ApplicationCommand>> bulkOverwriteServerApplicationCommands(Server server, Predicate<AbstractApplicationCommand> commandNamePredicate) {
        Set<AbstractApplicationCommand> abstractApplicationCommands = new HashSet<>();
        abstractApplicationCommands.addAll(serverSlashCommands.values());
        abstractApplicationCommands.addAll(serverMessageContextMenuCommands.values());
        abstractApplicationCommands.addAll(serverUserContextMenuCommands.values());

        return server.getApi().bulkOverwriteServerApplicationCommands(server, abstractApplicationCommands
                        .stream()
                        .filter(commandNamePredicate)
                        .map(AbstractApplicationCommand::getApplicationCommandBuilder)
                        .collect(Collectors.toSet()))
                .thenApply(applicationCommands -> handleBulkOverwrittenApplicationCommands(applicationCommands, serverApplicationCommands, "Server"));
    }

    /**
     * Handle the bulk overwritten application commands.
     *
     * @param applicationCommands   The application commands.
     * @param applicationCommandMap The application command map.
     * @param globalOrServerString  The global or server string.
     * @return The application commands.
     */
    private Set<ApplicationCommand> handleBulkOverwrittenApplicationCommands(final Set<ApplicationCommand> applicationCommands, Map<Long, ApplicationCommand> applicationCommandMap, String globalOrServerString) {
        applicationCommands.stream()
                .sorted(Comparator.comparing(o -> o.getClass().getInterfaces()[0].getSimpleName()))
                .forEach(applicationCommand -> {
                    applicationCommandMap.put(applicationCommand.getId(), applicationCommand);
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
     * @param event   The event.
     * @param command The command.
     */
    private void handleCommand(final SlashCommandCreateEvent event, final SlashCommand command) {
        SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();

        LOGGER.trace("Received slash command: {} ({})",
                slashCommandInteraction.getFullCommandName(),
                getLogArguments(slashCommandInteraction.getArguments()));

        command.runCommand(slashCommandInteraction);
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

    private String getSlashCommandArgumentString(List<SlashCommandInteractionOption> options) {
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
