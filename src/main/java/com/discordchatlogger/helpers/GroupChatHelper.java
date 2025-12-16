package com.discordchatlogger.helpers;

import com.discordchatlogger.DiscordChatLoggerConfig;
import com.discordchatlogger.domain.WebhookBody;
import com.discordchatlogger.utils.ChatMessageUtils;
import net.runelite.api.Client;
import net.runelite.api.events.ChatMessage;

import javax.inject.Inject;

public class GroupChatHelper {
    @Inject
    private DiscordChatLoggerConfig config;
    @Inject
    private DiscordHelper discordHelper;
    @Inject
    private Client client;
    public void handleChatMessage(ChatMessage chatMessage) {
        if (!config.logGroupChat())
            return;

        String playerName = client.getLocalPlayer().getName();
        String messageAuthor = ChatMessageUtils.getSanitizedAuthor(chatMessage);
        String groupChatName = ChatMessageUtils.getSanitizedSender(chatMessage);

        boolean logMessage = playerName.equals(messageAuthor)  && config.logSelf()
                || !playerName.equals(messageAuthor) && config.logOthers();

        if (logMessage) {
            StringBuilder stringBuilder = new StringBuilder();

            if (config.includeGroupName())
                stringBuilder.append("**[").append(groupChatName).append("]** ");

            if (playerName.equals(messageAuthor) && config.includeUsername()
                    || !playerName.equals(messageAuthor) && config.includeOtherUsername())
                stringBuilder.append("**").append(messageAuthor).append("**").append(" : ");

            stringBuilder.append(ChatMessageUtils.getSanitizedChatContent(chatMessage));
            WebhookBody webhookBody = new WebhookBody(stringBuilder.toString());
            discordHelper.sendWebhookBody(webhookBody, chatMessage.getType());
        }
    }
}
