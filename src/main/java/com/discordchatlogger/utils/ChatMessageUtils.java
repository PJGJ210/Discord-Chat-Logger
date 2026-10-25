package com.discordchatlogger.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.util.Text;

public class ChatMessageUtils {
    public static String getSanitizedAuthor(ChatMessage chatMessage) {
        return Text
            .removeTags(chatMessage.getName())
            .replaceAll("[^0-9a-zA-Z ]+", " ");
    }

    public static String getSanitizedSender(ChatMessage chatMessage) {
        return Text
            .removeTags(chatMessage.getSender())
            .replaceAll("[^0-9a-zA-Z ]+", " ");
    }

    public static String getSanitizedChatContent(ChatMessage chatMessage) {
        String messageContent = chatMessage.getMessage();

		Pattern STRING_TEMPLATE_PATTERN = Pattern.compile("<(?<name>str_\\w+)=(?<value>[^>]*)>");
		Matcher matcher = STRING_TEMPLATE_PATTERN.matcher(messageContent);

		// Correctly handle quest tag
		if (matcher.find()) {
			String tagName = matcher.group(1);
			String tagValue = matcher.group(2);
			messageContent = matcher.replaceFirst("");
			messageContent = messageContent.replace("<" + tagName + ">", tagValue);
		}

		// Remove any remaining tags from the message content
		messageContent = Text.removeTags(messageContent);

        return messageContent
                .replaceAll("CA_ID:\\d+\\|", "")
                .replaceAll("@everyone", "@ everyone")
                .replaceAll("@here", "@ here")
                .replaceAll("~", "\\~")
                .replaceAll("`", "\\`");
    }
}
