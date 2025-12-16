package com.discordchatlogger.helpers;

import com.discordchatlogger.DiscordChatLoggerConfig;
import com.discordchatlogger.domain.WebhookBody;
import com.discordchatlogger.utils.ChatMessageUtils;
import com.google.common.base.Strings;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.events.ChatMessage;

import javax.inject.Inject;

public class PrivateChatHelper {
    @Inject
    private DiscordChatLoggerConfig config;
    @Inject
    private DiscordHelper discordHelper;
    @Inject
    private Client client;

    public void handleChatMessage(ChatMessage chatMessage) {
        if (!config.logPrivateChat())
            return;

        String messageAuthor = null;
        String messageReceiver = null;
        String playerName = client.getLocalPlayer().getName();

        if (chatMessage.getType() == ChatMessageType.PRIVATECHAT && config.logOthers()) {
            messageAuthor = ChatMessageUtils.getSanitizedSender(chatMessage);
            messageReceiver = playerName;
        } else if (chatMessage.getType() == ChatMessageType.PRIVATECHATOUT && config.logSelf()) {
            messageAuthor = ChatMessageUtils.getSanitizedAuthor(chatMessage);
            messageReceiver = ChatMessageUtils.getSanitizedSender(chatMessage);
        }

        if (Strings.isNullOrEmpty(messageAuthor) || Strings.isNullOrEmpty(messageReceiver))
            return;

        StringBuilder stringBuilder = new StringBuilder();

        if(config.includeOtherUsername()) {
            if (messageAuthor.equals(playerName)) {
                stringBuilder.append("To **").append(messageReceiver).append("**").append(" : ");
            }
            if (messageReceiver.equals(playerName)) {
                stringBuilder.append("From **").append(messageAuthor).append("**").append(" : ");
            }
        }

        stringBuilder.append(ChatMessageUtils.getSanitizedChatContent(chatMessage));
        WebhookBody webhookBody = new WebhookBody(stringBuilder.toString());
        discordHelper.sendWebhookBody(webhookBody, chatMessage.getType());
    }
}
