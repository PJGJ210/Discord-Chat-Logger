package com.discordchatlogger;

import com.discordchatlogger.domain.WebhookBody;
import com.discordchatlogger.helpers.DiscordHelper;
import com.discordchatlogger.helpers.FriendsChatHelper;
import com.discordchatlogger.utils.ChatMessageUtils;
import com.google.inject.Provides;

import net.runelite.api.*;

import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import net.runelite.client.util.Text;


@Slf4j
@PluginDescriptor(
        name = "Discord Chat Logger"
)
public class DiscordChatLoggerPlugin extends Plugin {
    @Inject
    private DiscordChatLoggerConfig config;
    @Inject
    private Client client;
    @Inject
    private DiscordHelper discordHelper;
    @Inject
    private FriendsChatHelper friendsChatHelper;

    @Provides
    DiscordChatLoggerConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(DiscordChatLoggerConfig.class);
    }

    @Subscribe
    public void onChatMessage(ChatMessage chatMessage) {
        if (chatMessage.getType() == ChatMessageType.GAMEMESSAGE || chatMessage.getType() == ChatMessageType.SPAM) {
            return;
        }
        String sender = ChatMessageUtils.getSanitizedAuthor(chatMessage);
        String receiver;
        String inputMessage = chatMessage.getMessage();
        String outputMessage = Text.removeTags(inputMessage);
        if(chatMessage.getType() == ChatMessageType.PRIVATECHATOUT || chatMessage.getType() == ChatMessageType.PRIVATECHAT) {
            if (config.logPrivateChat()) {
                if (chatMessage.getType() == ChatMessageType.PRIVATECHATOUT && config.logSelf()){
                    receiver = sender;
                    sender = getPlayerName();
                    processPrivate(outputMessage,sender,receiver, chatMessage.getType());
                }
                if (chatMessage.getType() == ChatMessageType.PRIVATECHAT && config.logOthers()){
                    receiver = getPlayerName();
                    processPrivate(outputMessage,sender,receiver, chatMessage.getType());
                }
            }
        }
        if(chatMessage.getType() == ChatMessageType.CLAN_GIM_CHAT){
            String groupName = ChatMessageUtils.getSanitizedSender(chatMessage);
            if (config.logGroupChat()){
                if((sender.equals(getPlayerName()) && config.logSelf()) || (!sender.equals(getPlayerName()) && config.logOthers())) {
                    processGroup(outputMessage, sender, groupName, chatMessage.getType());
                }
            }
        }
        if(chatMessage.getType() == ChatMessageType.FRIENDSCHAT){
            friendsChatHelper.handleChatMessage(chatMessage);
        }
    }

    private String getPlayerName()
    {
        return client.getLocalPlayer().getName();
    }

    private void processPrivate(String outputText,String senderName, String receiverName, ChatMessageType chatMessageType){
        WebhookBody webhookBody = new WebhookBody();
        StringBuilder stringBuilder = new StringBuilder();
        if(config.includeOtherUsername()) {
            if (senderName.equals(getPlayerName())) {
                stringBuilder.append("To **").append(receiverName).append("**").append(" : ");
            }
            if (receiverName.equals(getPlayerName())) {
                stringBuilder.append("From **").append(senderName).append("**").append(" : ");
            }
        }
        stringBuilder.append(outputText);
        webhookBody.setContent(stringBuilder.toString());
        discordHelper.sendWebhookBody(webhookBody, chatMessageType);
    }

    private void processGroup(String outputText,String senderName, String groupName, ChatMessageType chatMessageType){
        WebhookBody webhookBody = new WebhookBody();
        StringBuilder stringBuilder = new StringBuilder();
        if (config.includeGroupName())
        {
            stringBuilder.append("**[").append(groupName).append("]** ");
        }
        if ((senderName.equals(getPlayerName()) && config.includeUsername()) || (!senderName.equals(getPlayerName()) && config.includeOtherUsername()))
        {
            stringBuilder.append("**").append(senderName).append("**").append(" : ");
        }
        stringBuilder.append(outputText);
        webhookBody.setContent(stringBuilder.toString());
        discordHelper.sendWebhookBody(webhookBody, chatMessageType);
    }
}