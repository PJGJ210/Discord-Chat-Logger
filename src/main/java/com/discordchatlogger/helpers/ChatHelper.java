package com.discordchatlogger.helpers;

import com.discordchatlogger.DiscordChatLoggerConfig;
import com.discordchatlogger.domain.WebhookBody;
import com.discordchatlogger.utils.ChatMessageUtils;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.events.ChatMessage;

import javax.inject.Inject;

public abstract class ChatHelper {
    protected Client client;
    protected DiscordChatLoggerConfig config;
    protected final ChatMessage chatMessage;
    protected final String playerName;
    protected final String messageAuthor;

    public ChatHelper(Client client, DiscordChatLoggerConfig config, ChatMessage chatMessage) {
        this.client = client;
        this.config = config;
        this.chatMessage = chatMessage;
        this.playerName = client.getLocalPlayer().getName();
        this.messageAuthor = ChatMessageUtils.getSanitizedAuthor(chatMessage);
    }

    public WebhookBody handleChatMessage(ChatMessage chatMessage) {
        boolean logMessage = false;
        if (playerName.equals(messageAuthor) && config.logSelf()) {
            logMessage = true;
        } else if (!playerName.equals(messageAuthor) && config.logOthers()) {
            logMessage = true;
        }

        if (logMessage) {
            String stringBuilder = buildPrefix() + buildSuffix();
            WebhookBody webhookBody = new WebhookBody(stringBuilder);
            return webhookBody;
        } else {
            return null;
        }
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
