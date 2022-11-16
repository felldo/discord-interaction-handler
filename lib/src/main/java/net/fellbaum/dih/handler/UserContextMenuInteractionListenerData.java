package net.fellbaum.dih.handler;

import org.javacord.api.interaction.UserContextMenuInteraction;

record UserContextMenuInteractionListenerData(UserContextMenuInteraction userContextMenuInteraction, Long serverId, String commandName) {

}
