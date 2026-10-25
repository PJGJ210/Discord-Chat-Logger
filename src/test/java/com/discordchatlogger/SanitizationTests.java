package com.discordchatlogger;

import com.discordchatlogger.utils.ChatMessageUtils;
import net.runelite.api.ChatMessageType;
import net.runelite.api.events.ChatMessage;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class SanitizationTests
{
	private ChatMessage initChatMessage(String message) {
		ChatMessage chatMessage = new ChatMessage();
		chatMessage.setType(ChatMessageType.GAMEMESSAGE);
		chatMessage.setMessage(message);
		return chatMessage;
	}
	@Test
	public void testSanitize_quest() {
		String input = "<str_quest_name_0=King's Ransom>Neig has completed a quest: <str_quest_name_0>";
		ChatMessage chatMessage = initChatMessage(input);

		String result = ChatMessageUtils.getSanitizedChatContent(chatMessage);

		String expected = "Neig has completed a quest: King's Ransom";
		assertEquals(expected, result);
	}

	@Test
	public void testSanitize_CA_ID() {
		String input = "CA_ID:25|Neig has completed a medium combat task: Barrows Champion.";
		ChatMessage chatMessage = initChatMessage(input);

		String result = ChatMessageUtils.getSanitizedChatContent(chatMessage);

		String expected = "Neig has completed a medium combat task: Barrows Champion.";
		assertEquals(expected, result);
	}
}
