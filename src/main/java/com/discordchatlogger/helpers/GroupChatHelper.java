package com.discordchatlogger.helpers;

import com.discordchatlogger.DiscordChatLoggerConfig;
import com.discordchatlogger.domain.WebhookBody;
import com.discordchatlogger.utils.ChatMessageUtils;
import net.runelite.api.Client;
import net.runelite.api.events.ChatMessage;

import javax.inject.Inject;

public class GroupChatHelper extends ChatHelper {

    public GroupChatHelper(Client client, DiscordChatLoggerConfig config, ChatMessage chatMessage) {
        super(client, config, chatMessage);
    }

    public WebhookBody handleChatMessage(ChatMessage chatMessage) {
        if (!config.logGroupChat())
            return null;
        return super.handleChatMessage(chatMessage);
    }

    @Override
    protected  String buildPrefix() {
        StringBuilder stringBuilder = new StringBuilder();
        if (config.includeGroupName()) {
            String groupChatName = ChatMessageUtils.getSanitizedSender(chatMessage);
            stringBuilder.append("**[").append(groupChatName).append("]** ");
        }
        stringBuilder.append(super.buildPrefix());
        return stringBuilder.toString();
    }
}
