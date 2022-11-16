package net.fellbaum.dih.handler;

import net.fellbaum.dih.interaction.Interaction;
import net.fellbaum.dih.interaction.applicationcommand.AbstractApplicationCommand;
import net.fellbaum.dih.interaction.applicationcommand.MessageContextMenuCommand;
import net.fellbaum.dih.interaction.applicationcommand.SlashCommand;
import net.fellbaum.dih.interaction.applicationcommand.UserContextMenuCommand;
import net.fellbaum.dih.interaction.component.AbstractComponent;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.ApplicationCommand;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * The ComplexInteractionHandler class.
 */
@SuppressWarnings({"UnusedReturnValue", "unused"})
public non-sealed class ComplexInteractionHandler extends InteractionHandler<ComplexInteractionHandler> {

    /**
     * The complex server slash commands.
     */
    protected final Map<Long, Map<String, SlashCommand>> complexServerSlashCommands = new ConcurrentHashMap<>();
    /**
     * The complex server message context menu commands.
     */
    protected final Map<Long, Map<String, MessageContextMenuCommand>> complexServerMessageContextMenuCommands = new ConcurrentHashMap<>();

    /**
     * The complex server user context menu commands.
     */
    protected final Map<Long, Map<String, UserContextMenuCommand>> complexServerUserContextMenuCommands = new ConcurrentHashMap<>();

    /**
     * Creates an instance of this class.
     */
    public ComplexInteractionHandler() {
        super(ComplexityMode.COMPLEX);

        slashCommandFunction = (data) -> {
            final long serverId = data.serverId();
            final String commandName = data.commandName();
            if (complexServerSlashCommands.containsKey(data.serverId()) && complexServerSlashCommands.get(serverId).containsKey(commandName)) {
                final Map<String, SlashCommand> slashCommands = complexServerSlashCommands.get(serverId);
                if (slashCommands.containsKey(commandName)) {
                    handleCommand(data.slashCommandInteraction(), slashCommands.get(commandName));
                    return true;
                }
            }
            return false;
        };

        autocompleteCommandFunction = (data) -> {
            final long serverId = data.serverId();
            final String commandName = data.commandName();
            if (complexServerSlashCommands.containsKey(data.serverId()) && complexServerSlashCommands.get(serverId).containsKey(commandName)) {
                final Map<String, SlashCommand> slashCommands = complexServerSlashCommands.get(serverId);
                if (slashCommands.containsKey(commandName)) {
                    slashCommands.get(commandName).autocompletionHandler(data.autocompleteInteraction());
                    return true;
                }
            }
            return false;
        };

        userContextMenuFunction = (data) -> {
            final long serverId = data.serverId();
            final String commandName = data.commandName();
            if (complexServerUserContextMenuCommands.containsKey(serverId) && complexServerUserContextMenuCommands.get(serverId).containsKey(commandName)) {
                final Map<String, UserContextMenuCommand> userContextMenuCommandMap = complexServerUserContextMenuCommands.get(serverId);
                if (userContextMenuCommandMap.containsKey(commandName)) {
                    userContextMenuCommandMap.get(commandName).runCommand(data.userContextMenuInteraction());
                    return true;
                }
            }
            return false;
        };

        messageContextMenuFunction = (data) -> {
            final long serverId = data.serverId();
            final String commandName = data.commandName();
            if (complexServerMessageContextMenuCommands.containsKey(serverId) && complexServerMessageContextMenuCommands.get(serverId).containsKey(commandName)) {
                final Map<String, MessageContextMenuCommand> messageContextMenuCommandMap = complexServerMessageContextMenuCommands.get(serverId);
                if (messageContextMenuCommandMap.containsKey(commandName)) {
                    messageContextMenuCommandMap.get(commandName).runCommand(data.messageContextMenuInteraction());
                    return true;
                }
            }
            return false;
        };
    }

    /**
     * Register global interactions to the handler.
     *
     * @param interactions The interactions to register.
     * @return The current instance to chain methods.
     */
    public ComplexInteractionHandler registerGlobalInteractions(final Collection<Interaction> interactions) {
        interactions.forEach(this::registerGlobalInteraction);
        return this;
    }

    /**
     * Register a complex global interaction to the handler.
     *
     * @param interaction The interaction to register.
     * @return The current instance to chain methods.
     */
    public ComplexInteractionHandler registerGlobalInteraction(final Interaction interaction) {
        if (interaction instanceof AbstractApplicationCommand abstractApplicationCommand) {
            if (abstractApplicationCommand.isGlobal()) {
                if (abstractApplicationCommand instanceof SlashCommand s) {
                    globalSlashCommands.put(abstractApplicationCommand.getName(), s);
                } else if (abstractApplicationCommand instanceof MessageContextMenuCommand m) {
                    globalMessageContextMenuCommands.put(abstractApplicationCommand.getName(), m);
                } else if (abstractApplicationCommand instanceof UserContextMenuCommand u) {
                    globalUserContextMenuCommands.put(abstractApplicationCommand.getName(), u);
                } else {
                    throw new IllegalArgumentException("Argument is a not supported Interaction");
                }
            } else {
                throw new IllegalArgumentException("Argument is a not a global application command");
            }
        } else if (interaction instanceof AbstractComponent abstractComponent) {
            registerComponent(abstractComponent);
        } else {
            throw new IllegalArgumentException("Argument is a not supported Interaction");
        }
        return this;
    }

    /**
     * Register complex server interactions to the handler.
     *
     * @param server       The server.
     * @param interactions The interactions to register.
     * @return The current instance to chain methods.
     */
    public ComplexInteractionHandler registerServerInteraction(final Server server, final Collection<Interaction> interactions) {
        interactions.forEach(interaction -> registerServerInteraction(server.getId(), interaction));
        return this;
    }

    /**
     * Register a complex server interaction to the handler.
     *
     * @param serverId    The id of the server.
     * @param interaction The interaction to register.
     * @return The current instance to chain methods.
     */
    public ComplexInteractionHandler registerServerInteraction(final long serverId, final Interaction interaction) {
        if (interaction instanceof AbstractApplicationCommand abstractApplicationCommand) {
            if (!abstractApplicationCommand.isGlobal()) {
                if (abstractApplicationCommand instanceof SlashCommand s) {
                    complexServerSlashCommands.compute(serverId, (k, v) -> addToMap(k, v, s));
                } else if (abstractApplicationCommand instanceof MessageContextMenuCommand m) {
                    complexServerMessageContextMenuCommands.compute(serverId, (k, v) -> addToMap(k, v, m));
                } else if (abstractApplicationCommand instanceof UserContextMenuCommand u) {
                    complexServerUserContextMenuCommands.compute(serverId, (k, v) -> addToMap(k, v, u));
                } else {
                    throw new IllegalArgumentException("Argument is a not supported Interaction");
                }
            } else {
                throw new IllegalArgumentException("Argument is a not a global application command");
            }
        } else {
            throw new IllegalArgumentException("Argument is a not supported Interaction");
            //registerInteraction(interaction);
        }
        return this;
    }

    private <T extends AbstractApplicationCommand> Map<String, T> addToMap(final Long serverId, Map<String, T> stringMap, final T abstractApplicationCommand) {
        if (stringMap == null) {
            stringMap = new ConcurrentHashMap<>();
        }
        stringMap.put(abstractApplicationCommand.getName(), abstractApplicationCommand);
        return stringMap;
    }

    @Override
    public ComplexInteractionHandler attachListeners(DiscordApi api) {
        api.addServerLeaveListener(event -> {
            final long serverId = event.getServer().getId();
            complexServerSlashCommands.remove(serverId);
            complexServerMessageContextMenuCommands.remove(serverId);
            complexServerUserContextMenuCommands.remove(serverId);
        });
        return super.attachListeners(api);
    }

    /**
     * Bulk overwrite all registered guild application commands.
     *
     * @param server The server.
     * @return A future to check if the operation was successful and the registered application commands.
     */
    public CompletableFuture<Set<ApplicationCommand>> bulkOverwriteServerApplicationCommands(final Server server) {
        Set<AbstractApplicationCommand> abstractApplicationCommands = new HashSet<>();
        abstractApplicationCommands.addAll(complexServerSlashCommands.getOrDefault(server.getId(), Collections.emptyMap()).values());
        abstractApplicationCommands.addAll(complexServerMessageContextMenuCommands.getOrDefault(server.getId(), Collections.emptyMap()).values());
        abstractApplicationCommands.addAll(complexServerUserContextMenuCommands.getOrDefault(server.getId(), Collections.emptyMap()).values());

        return server.getApi().bulkOverwriteServerApplicationCommands(server, abstractApplicationCommands
                        .stream()
                        .map(AbstractApplicationCommand::getApplicationCommandBuilder)
                        .collect(Collectors.toSet()))
                .thenApply(applicationCommands -> handleBulkOverwrittenApplicationCommands(applicationCommands, "Server"));

    }

    /**
     * Unregisters all interactions from the handler.
     * Note: This will not delete the interactions from the server.
     * For that you still have to use {@link #bulkOverwriteServerApplicationCommands(Server)}.
     *
     * @param server The server.
     */
    public void unregisterAllServerInteractions(final Server server) {
        complexServerSlashCommands.remove(server.getId());
        complexServerMessageContextMenuCommands.remove(server.getId());
        complexServerUserContextMenuCommands.remove(server.getId());
    }
}
