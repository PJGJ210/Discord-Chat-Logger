package com.discordchatlogger.helpers.clan;

import com.discordchatlogger.DiscordChatLoggerConfig;
import com.discordchatlogger.domain.WebhookBody;
import com.discordchatlogger.helpers.ChatHelper;
import com.discordchatlogger.utils.ChatMessageUtils;
import net.runelite.api.Client;
import net.runelite.api.events.ChatMessage;

public class GuestClanChatHelper extends ChatHelper {

    public GuestClanChatHelper(Client client, DiscordChatLoggerConfig config, ChatMessage chatMessage) {
        super(client, config, chatMessage);
    }

    public WebhookBody handleChatMessage(ChatMessage chatMessage) {
        if (!config.logGuestClanChat())
            return null;
        return super.handleChatMessage(chatMessage);
    }

    @Override
    protected  String buildPrefix() {
        StringBuilder stringBuilder = new StringBuilder();
        if (config.includeGuestClanChatName()) {
            String clanChatName = ChatMessageUtils.getSanitizedSender(chatMessage);
            stringBuilder.append("**[").append(clanChatName).append("]** ");
        }
        stringBuilder.append(super.buildPrefix());
        return stringBuilder.toString();
    }
}