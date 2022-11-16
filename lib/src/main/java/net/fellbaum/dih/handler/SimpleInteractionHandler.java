package net.fellbaum.dih.handler;

import net.fellbaum.dih.interaction.Interaction;
import net.fellbaum.dih.interaction.applicationcommand.AbstractApplicationCommand;
import net.fellbaum.dih.interaction.applicationcommand.MessageContextMenuCommand;
import net.fellbaum.dih.interaction.applicationcommand.SlashCommand;
import net.fellbaum.dih.interaction.applicationcommand.UserContextMenuCommand;
import net.fellbaum.dih.interaction.component.AbstractComponent;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.ApplicationCommand;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * The SimpleInteractionHandler class.
 */
@SuppressWarnings({"UnusedReturnValue", "unused"})
public non-sealed class SimpleInteractionHandler extends InteractionHandler<SimpleInteractionHandler> {

    /**
     * The simple server slash commands.
     */
    protected final Map<String, SlashCommand> serverSlashCommands = new ConcurrentHashMap<>();
    /**
     * The simple server message context menu commands.
     */
    protected final Map<String, MessageContextMenuCommand> serverMessageContextMenuCommands = new ConcurrentHashMap<>();
    /**
     * The simple server user context menu commands.
     */
    protected final Map<String, UserContextMenuCommand> serverUserContextMenuCommands = new ConcurrentHashMap<>();

    /**
     * Creates an instance of this class.
     */
    public SimpleInteractionHandler() {
        super(ComplexityMode.SIMPLE);

        slashCommandFunction = (data) -> {
            final String commandName = data.commandName();
            if (serverSlashCommands.containsKey(commandName)) {
                handleCommand(data.slashCommandInteraction(), serverSlashCommands.get(commandName));
                return true;
            }
            return false;
        };

        autocompleteCommandFunction = (data) -> {
            final String commandName = data.commandName();
            if (serverSlashCommands.containsKey(commandName)) {
                serverSlashCommands.get(commandName).autocompletionHandler(data.autocompleteInteraction());
                return true;
            }
            return false;
        };

        userContextMenuFunction = (data) -> {
            final String commandName = data.commandName();
            if (serverUserContextMenuCommands.containsKey(commandName)) {
                serverUserContextMenuCommands.get(commandName).runCommand(data.userContextMenuInteraction());
                return true;
            }
            return false;
        };

        messageContextMenuFunction = (data) -> {
            final String commandName = data.commandName();
            if (serverMessageContextMenuCommands.containsKey(commandName)) {
                serverMessageContextMenuCommands.get(commandName).runCommand(data.messageContextMenuInteraction());
                return true;
            }
            return false;
        };

    }

    /**
     * Register an interaction to the handler.
     *
     * @param interactions The interactions to register.
     * @return The current instance to chain methods.
     */
    public SimpleInteractionHandler registerInteractions(final Collection<Interaction> interactions) {
        interactions.forEach(this::registerInteraction);
        return this;
    }

    /**
     * Register an interaction to the handler.
     *
     * @param interaction The interaction to register.
     * @return The current instance to chain methods.
     */
    public SimpleInteractionHandler registerInteraction(final Interaction interaction) {
        if (interaction instanceof AbstractComponent comp) {
            registerComponent(comp);
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
     * Bulk overwrite all registered guild application commands.
     *
     * @param server The server.
     * @return A future to check if the operation was successful and the registered application commands.
     */
    public CompletableFuture<Set<ApplicationCommand>> bulkOverwriteServerApplicationCommands(final Server server) {
        return bulkOverwriteServerApplicationCommands(server, s -> true);
    }

    /**
     * Bulk overwrite all guild application commands with the given names.
     *
     * @param server       The server.
     * @param commandNames The names of the commands to overwrite.
     * @return A future to check if the operation was successful and the registered application commands.
     */
    public CompletableFuture<Set<ApplicationCommand>> bulkOverwriteServerApplicationCommands(final Server server, final String... commandNames) {
        return bulkOverwriteServerApplicationCommands(server, Arrays.asList(commandNames));
    }

    /**
     * Bulk overwrite all guild application commands with the given names.
     *
     * @param server       The server.
     * @param commandNames The names of the commands to overwrite.
     * @return A future to check if the operation was successful and the registered application commands.
     */
    public CompletableFuture<Set<ApplicationCommand>> bulkOverwriteServerApplicationCommands(final Server server, final Collection<String> commandNames) {
        return bulkOverwriteServerApplicationCommands(server, abstractApplicationCommand -> commandNames.contains(abstractApplicationCommand.getName()));
    }

    /**
     * Bulk overwrite all guild application commands with the given names.
     *
     * @param server               The server.
     * @param commandNamePredicate The predicate to check if the command should be overwritten.
     * @return A future to check if the operation was successful and the registered application commands.
     */
    public CompletableFuture<Set<ApplicationCommand>> bulkOverwriteServerApplicationCommands(final Server server, final Predicate<AbstractApplicationCommand> commandNamePredicate) {
        Set<AbstractApplicationCommand> abstractApplicationCommands = new HashSet<>();
        abstractApplicationCommands.addAll(serverSlashCommands.values());
        abstractApplicationCommands.addAll(serverMessageContextMenuCommands.values());
        abstractApplicationCommands.addAll(serverUserContextMenuCommands.values());

        return server.getApi().bulkOverwriteServerApplicationCommands(server, abstractApplicationCommands
                        .stream()
                        .filter(commandNamePredicate)
                        .map(AbstractApplicationCommand::getApplicationCommandBuilder)
                        .collect(Collectors.toSet()))
                .thenApply(applicationCommands -> handleBulkOverwrittenApplicationCommands(applicationCommands, "Server"));
    }

}
