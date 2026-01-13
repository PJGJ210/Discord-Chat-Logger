package com.discordchatlogger.helpers;

import com.discordchatlogger.DiscordChatLoggerConfig;
import com.discordchatlogger.domain.WebhookBody;
import com.discordchatlogger.utils.ChatMessageUtils;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.events.ChatMessage;

public abstract class ChatHelper {
    protected Client client;
    protected DiscordChatLoggerConfig config;
    protected final ChatMessage chatMessage;
    protected final ChatMessageType chatMessageType;
    protected final String playerName;
    protected final String messageAuthor;

    public ChatHelper(Client client, DiscordChatLoggerConfig config, ChatMessage chatMessage) {
        this.client = client;
        this.config = config;
        this.chatMessage = chatMessage;
        this.chatMessageType = chatMessage.getType();
        this.playerName = client.getLocalPlayer().getName();
        this.messageAuthor = ChatMessageUtils.getSanitizedAuthor(chatMessage);
    }

    public WebhookBody handleChatMessage(ChatMessage chatMessage) {
        if (shouldMessageBeLogged()) {
            String stringBuilder = buildPrefix() + buildSuffix();
            return new WebhookBody(stringBuilder);
        } else {
            return null;
        }
    }

    protected boolean shouldMessageBeLogged() {
        if (playerName.equals(messageAuthor) && config.logSelf()) {
            return true;
        } else if (!playerName.equals(messageAuthor) && config.logOthers()) {
            return true;
        }
        return false;
    }

    protected String buildPrefix() {
        StringBuilder stringBuilder = new StringBuilder();
        if (playerName.equals(messageAuthor) && config.includeUsername()
                || !playerName.equals(messageAuthor) && config.includeOtherUsername())
            stringBuilder.append("**").append(messageAuthor).append("**").append(" : ");

        return stringBuilder.toString();
    }

    protected String buildSuffix() {
        return ChatMessageUtils.getSanitizedChatContent(chatMessage);
    }
}
