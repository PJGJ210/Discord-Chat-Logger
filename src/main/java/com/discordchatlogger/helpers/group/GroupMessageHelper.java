package com.discordchatlogger.helpers.group;

import com.discordchatlogger.DiscordChatLoggerConfig;
import com.discordchatlogger.domain.WebhookBody;
import com.discordchatlogger.helpers.ChatHelper;
import com.discordchatlogger.utils.ChatMessageUtils;
import net.runelite.api.Client;
import net.runelite.api.events.ChatMessage;

public class GroupMessageHelper extends ChatHelper {

    public GroupMessageHelper(Client client, DiscordChatLoggerConfig config, ChatMessage chatMessage) {
        super(client, config, chatMessage);
    }

    public WebhookBody handleChatMessage(ChatMessage chatMessage) {
        if (!config.logGroupChat())
            return null;
        return super.handleChatMessage(chatMessage);
    }

    @Override
    protected boolean shouldMessageBeLogged() {
        String sanitizedMessage = chatMessage.getMessage().replaceAll("\\u00a0", " ");
        if (sanitizedMessage.contains(playerName) && config.logSelf()) {
            return true;
        } else if (!sanitizedMessage.contains(playerName) && config.logOthers()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected  String buildPrefix() {
        StringBuilder stringBuilder = new StringBuilder();
        if (config.includeGroupName()) {
            String groupChatName = ChatMessageUtils.getSanitizedSender(chatMessage);
            stringBuilder.append("**[").append(groupChatName).append("]** ");
        }
        return stringBuilder.toString();
    }
}
