package net.fellbaum.dih.handler;

import org.javacord.api.interaction.SlashCommandInteraction;

record SlashCommandInteractionListenerData(SlashCommandInteraction slashCommandInteraction, Long serverId, String commandName) {

}
