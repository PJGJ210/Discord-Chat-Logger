package com.discordchatlogger.helpers;

import com.discordchatlogger.DiscordChatLoggerConfig;
import com.discordchatlogger.utils.ChatMessageUtils;
import com.discordchatlogger.domain.WebhookBody;
import net.runelite.api.Client;
import net.runelite.api.events.ChatMessage;

import javax.inject.Inject;

public class FriendsChatHelper extends ChatHelper {

    public FriendsChatHelper(Client client, DiscordChatLoggerConfig config, ChatMessage chatMessage) {
        super(client, config, chatMessage);
    }

    public WebhookBody handleChatMessage(ChatMessage chatMessage) {
        if (!config.logFriendsChat())
            return null;
        return super.handleChatMessage(chatMessage);
    }

    @Override
    protected  String buildPrefix() {
        StringBuilder stringBuilder = new StringBuilder();
        if (config.includeFriendsChatName()) {
            String friendsChatName = ChatMessageUtils.getSanitizedSender(chatMessage);
            stringBuilder.append("**[").append(friendsChatName).append("]** ");
        }
        stringBuilder.append(super.buildPrefix());
        return stringBuilder.toString();
    }
}
