package com.discordchatlogger.helpers;

import com.discordchatlogger.DiscordChatLoggerConfig;
import com.discordchatlogger.domain.WebhookBody;
import com.discordchatlogger.utils.ChatMessageUtils;
import com.google.common.base.Strings;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.events.ChatMessage;

import javax.inject.Inject;

public class PrivateChatHelper extends ChatHelper {
    public PrivateChatHelper(Client client, DiscordChatLoggerConfig config, ChatMessage chatMessage) {
        super(client, config, chatMessage);
    }

    public WebhookBody handleChatMessage(ChatMessage chatMessage) {
        if (!config.logPrivateChat())
            return null;
        return super.handleChatMessage(chatMessage);
    }

    @Override
    protected  String buildPrefix() {
        StringBuilder stringBuilder = new StringBuilder();
        String messageAuthor = null;
        String messageReceiver = null;
        String playerName = client.getLocalPlayer().getName();

        if (chatMessage.getType() == ChatMessageType.PRIVATECHAT && config.logOthers()) {
            messageAuthor = ChatMessageUtils.getSanitizedAuthor(chatMessage);
            messageReceiver = playerName;
        } else if (chatMessage.getType() == ChatMessageType.PRIVATECHATOUT && config.logSelf()) {
            messageAuthor = playerName;
            messageReceiver = ChatMessageUtils.getSanitizedAuthor(chatMessage);
        }

        if (Strings.isNullOrEmpty(messageAuthor) || Strings.isNullOrEmpty(messageReceiver))
            return null;


        if(config.includeOtherUsername()) {
            if (messageAuthor.equals(playerName)) {
                stringBuilder.append("To **").append(messageReceiver).append("**").append(" : ");
            }
            if (messageReceiver.equals(playerName)) {
                stringBuilder.append("From **").append(messageAuthor).append("**").append(" : ");
            }
        }
        return stringBuilder.toString();
    }
}
