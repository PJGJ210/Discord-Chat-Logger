package com.discordchatlogger;

import com.discordchatlogger.domain.WebhookBody;
import com.discordchatlogger.helpers.*;
import com.discordchatlogger.helpers.clan.ClanChatHelper;
import com.discordchatlogger.helpers.group.GroupChatHelper;
import com.discordchatlogger.helpers.group.GroupMessageHelper;
import com.google.inject.Provides;
import net.runelite.api.*;

import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;


@Slf4j
@PluginDescriptor(
        name = "Discord Chat Logger"
)
public class DiscordChatLoggerPlugin extends Plugin {
    @Provides
    DiscordChatLoggerConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(DiscordChatLoggerConfig.class);
    }
    @Inject private Client client;
    @Inject private DiscordChatLoggerConfig config;
    @Inject private DiscordHelper discordHelper;

    @Subscribe
    public void onChatMessage(ChatMessage chatMessage) {
        ChatMessageType chatMessageType = chatMessage.getType();
        if (chatMessageType == ChatMessageType.GAMEMESSAGE || chatMessageType == ChatMessageType.SPAM) {
            return;
        }
         ChatHelper chatHelper = null;
        switch (chatMessageType) {
            case FRIENDSCHAT:
                chatHelper = new FriendsChatHelper(client, config, chatMessage);
                break;
            case PRIVATECHAT:
            case PRIVATECHATOUT:
                chatHelper = new PrivateChatHelper(client, config, chatMessage);
                break;
            case CLAN_GIM_CHAT:
                chatHelper = new GroupChatHelper(client, config, chatMessage);
                break;
            case CLAN_GIM_MESSAGE:
                chatHelper = new GroupMessageHelper(client, config, chatMessage);
                break;
            case CLAN_CHAT:
                chatHelper = new ClanChatHelper(client, config, chatMessage);
                break;
        }
        if (chatHelper == null) {
            return;
        }

        WebhookBody webhookBody = chatHelper.handleChatMessage(chatMessage);
        if (webhookBody == null) {
            return;
        }
        discordHelper.sendWebhookBody(webhookBody, chatMessageType);
    }
}