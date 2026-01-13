package com.discordchatlogger.helpers.clan;

import com.discordchatlogger.DiscordChatLoggerConfig;
import com.discordchatlogger.domain.WebhookBody;
import com.discordchatlogger.helpers.ChatHelper;
import com.discordchatlogger.utils.ChatMessageUtils;
import net.runelite.api.Client;
import net.runelite.api.events.ChatMessage;

public class ClanChatHelper extends ChatHelper {

    public ClanChatHelper(Client client, DiscordChatLoggerConfig config, ChatMessage chatMessage) {
        super(client, config, chatMessage);
    }

    public WebhookBody handleChatMessage(ChatMessage chatMessage) {
        if (!config.logClanChat())
            return null;
        return super.handleChatMessage(chatMessage);
    }

    @Override
    protected  String buildPrefix() {
        StringBuilder stringBuilder = new StringBuilder();
        if (config.includeClanChatName()) {
            String clanChatName = ChatMessageUtils.getSanitizedSender(chatMessage);
            stringBuilder.append("**[").append(clanChatName).append("]** ");
        }
        stringBuilder.append(super.buildPrefix());
        return stringBuilder.toString();
    }
}