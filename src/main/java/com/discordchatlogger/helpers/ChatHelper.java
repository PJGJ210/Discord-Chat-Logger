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

    public ChatHelper(Client client, DiscordChatLoggerConfig config, ChatMessage chatMessage) {
        this.client = client;
        this.config = config;
        this.chatMessage = chatMessage;
    }

    public WebhookBody handleChatMessage(ChatMessage chatMessage) {
        String stringBuilder = buildPrefix() + buildSuffix();
        WebhookBody webhookBody = new WebhookBody(stringBuilder);
        return webhookBody;
    }

    protected String buildPrefix() {
        StringBuilder stringBuilder = new StringBuilder();
        String playerName = client.getLocalPlayer().getName();
        String messageAuthor = ChatMessageUtils.getSanitizedAuthor(chatMessage);

        if (playerName.equals(messageAuthor) && config.includeUsername()
                || !playerName.equals(messageAuthor) && config.includeOtherUsername())
            stringBuilder.append("**").append(messageAuthor).append("**").append(" : ");

        return stringBuilder.toString();
    }

    protected String buildSuffix() {
        return ChatMessageUtils.getSanitizedChatContent(chatMessage);
    }
}
