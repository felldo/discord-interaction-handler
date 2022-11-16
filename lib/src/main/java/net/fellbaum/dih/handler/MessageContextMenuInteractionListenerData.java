package net.fellbaum.dih.handler;

import org.javacord.api.interaction.MessageContextMenuInteraction;

record MessageContextMenuInteractionListenerData(MessageContextMenuInteraction messageContextMenuInteraction, Long serverId, String commandName) {

}
