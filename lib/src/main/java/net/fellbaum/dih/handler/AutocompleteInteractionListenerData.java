package net.fellbaum.dih.handler;

import org.javacord.api.interaction.AutocompleteInteraction;

record AutocompleteInteractionListenerData(AutocompleteInteraction autocompleteInteraction, Long serverId, String commandName) {

}
