package com.discordchatlogger;

import com.discordchatlogger.domain.WebhookBody;
import com.discordchatlogger.helpers.DiscordHelper;
import com.discordchatlogger.helpers.FriendsChatHelper;
import com.discordchatlogger.helpers.GroupChatHelper;
import com.discordchatlogger.helpers.PrivateChatHelper;
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
    private FriendsChatHelper friendsChatHelper;
    @Inject
    private PrivateChatHelper privateChatHelper;
    @Inject
    private GroupChatHelper groupChatHelper;

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

        if(chatMessage.getType() == ChatMessageType.PRIVATECHATOUT || chatMessage.getType() == ChatMessageType.PRIVATECHAT)
            privateChatHelper.handleChatMessage(chatMessage);

        if(chatMessage.getType() == ChatMessageType.CLAN_GIM_CHAT)
            groupChatHelper.handleChatMessage(chatMessage);

        if(chatMessage.getType() == ChatMessageType.FRIENDSCHAT)
            friendsChatHelper.handleChatMessage(chatMessage);
    }
}