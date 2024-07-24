package com.discordchatlogger;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup(DiscordChatLoggerConfig.GROUP)
public interface DiscordChatLoggerConfig extends Config{
    String GROUP = "discordchatlogger";

    @ConfigSection(
            name = "Private",
            description = "Options for private message logging",
            position = 100
    )
    String privateOptions = "privateOptions";

    @ConfigItem(
            keyName = "useprivate",
            name = "Send Private Messages",
            description = "Send private messages to discord webhook",
            position = 1,
            section = privateOptions
    )
    default boolean usePrivate()
    {
        return false;
    }

    @ConfigItem(
            keyName = "webhookprivate",
            name = "Webhook URL",
            description = "The Discord Webhook URL for private messages",
            position = 2,
            section = privateOptions
    )
    String webhookPrivate();

    @ConfigSection(
            name = "Group",
            description = "Options for group message logging",
            position = 300
    )
    String groupOptions = "groupOptions";

    @ConfigItem(
            keyName = "usegroup",
            name = "Send Group Messages",
            description = "Send group messages to discord webhook",
            position = 1,
            section = groupOptions
    )
    default boolean useGroup()
    {
        return false;
    }

    @ConfigItem(
            keyName = "webhookgroup",
            name = "Webhook URL",
            description = "Send group messages to discord webhook",
            position = 2,
            section = groupOptions
    )

    String webhookGroup();    @ConfigItem(
            keyName = "usegroupname",
            name = "Include Group Name",
            description = "Include group name in discord message",
            position = 3,
            section = groupOptions
    )
    default boolean useGroupName()
    {
        return true;
    }

    @ConfigSection(
            name = "Friends Chat",
            description = "Options for friends chat message logging",
            position = 200
    )
    String friendsChatOptions = "friendsChatOptions";

    @ConfigItem(
            keyName = "usefriendsChat",
            name = "Send friends chat Messages",
            description = "Send friends chat messages to discord webhook",
            position = 1,
            section = friendsChatOptions
    )
    default boolean usefriendsChat()
    {
        return false;
    }

    @ConfigItem(
            keyName = "webhookfriendsChat",
            name = "Webhook URL",
            description = "Send friends chat messages to discord webhook",
            position = 2,
            section = friendsChatOptions
    )

    String webhookFriendsChat();

    @ConfigSection(
            name = "Logging",
            description = "General options for logging",
            position = 400
    )
    String loggingOptions = "logginOptions";

    @ConfigItem(
            keyName = "logself",
            name = "Log Self",
            description = "Include messages sent",
            position = 1,
            section = loggingOptions
    )
    default boolean logSelf()
    {
        return true;
    }

    @ConfigItem(
            keyName = "logOthers",
            name = "Log Others",
            description = "Include messages received",
            position = 2,
            section = loggingOptions
    )
    default boolean logOthers()
    {
        return true;
    }

    @ConfigItem(
            keyName = "includeusername",
            name = "Include Username",
            description = "Include own RSN in the post",
            position = 3,
            section = loggingOptions
    )
    default boolean includeUsername()
    {
        return true;
    }

    @ConfigItem(
            keyName = "includeotherusername",
            name = "Include Other's Username",
            description = "Include Other's RSN in the post",
            position = 4,
            section = loggingOptions
    )
    default boolean includeOtherUsername()
    {
        return true;
    }
}