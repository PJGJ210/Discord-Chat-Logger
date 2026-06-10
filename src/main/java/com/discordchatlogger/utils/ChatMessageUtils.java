package com.discordchatlogger.utils;

import net.runelite.api.events.ChatMessage;
import net.runelite.client.util.Text;

public class ChatMessageUtils {
    public static String getSanitizedAuthor(ChatMessage chatMessage) {
        return Text
            .removeTags(chatMessage.getName())
            .replaceAll("CA_ID:\\d+\\|", "")
            .replaceAll("[^0-9a-zA-Z ]+", " ");
    }

    public static String getSanitizedSender(ChatMessage chatMessage) {
        return Text
            .removeTags(chatMessage.getSender())
            .replaceAll("CA_ID:\\d+\\|", "")
            .replaceAll("[^0-9a-zA-Z ]+", " ");
    }

    public static String getSanitizedChatContent(ChatMessage chatMessage) {
        String messageContent = Text.removeTags(chatMessage.getMessage());
        return messageContent
                .replaceAll("@everyone", "@ everyone")
                .replaceAll("@here", "@ here")
                .replaceAll("~", "\\~")
                .replaceAll("`", "\\`");
    }
}
