package com.discordchatlogger;

import com.google.common.base.Strings;
import com.google.inject.Provides;

import java.io.IOException;

import net.runelite.api.*;

import java.util.regex.Pattern;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.DrawManager;

import static net.runelite.http.api.RuneLiteAPI.GSON;

import net.runelite.client.util.Text;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


@Slf4j
@PluginDescriptor(
        name = "Discord Chat Logger"
)
public class DiscordChatLoggerPlugin extends Plugin {
    @Inject
    private DiscordChatLoggerConfig config;
    @Inject
    private ItemManager itemManager;
    @Inject
    private OkHttpClient okHttpClient;

    @Inject
    private Client client;


    @Override
    protected void startUp()
    {
    }

    @Override
    protected void shutDown()
    {
    }

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
        String sender = chatMessage.getName().replaceAll("\\<.*?>", "").replaceAll("[^0-9a-zA-Z ]+", " ");
        String receiver;
        String inputMessage = chatMessage.getMessage();
        String outputMessage = Text.removeTags(inputMessage);
        if(chatMessage.getType() == ChatMessageType.PRIVATECHATOUT || chatMessage.getType() == ChatMessageType.PRIVATECHAT) {
            if (config.usePrivate()) {
                if (chatMessage.getType() == ChatMessageType.PRIVATECHATOUT && config.logSelf()){
                    receiver = sender;
                    sender = getPlayerName();
                    processPrivate(outputMessage,sender,receiver);
                }
                if (chatMessage.getType() == ChatMessageType.PRIVATECHAT && config.logOthers()){
                    receiver = getPlayerName();
                    processPrivate(outputMessage,sender,receiver);
                }
            }
        }
        if(chatMessage.getType() == ChatMessageType.CLAN_GIM_CHAT){
            String groupName = chatMessage.getSender().replaceAll("\\<.*?>", "").replaceAll("[^0-9a-zA-Z ]+", " ");
            if (config.useGroup()){
                if((sender.equals(getPlayerName()) && config.logSelf()) || (!sender.equals(getPlayerName()) && config.logOthers())) {
                    processGroup(outputMessage, sender, groupName);
                }
            }
        }
        if(chatMessage.getType() == ChatMessageType.FRIENDSCHAT){
            String friendsName = chatMessage.getSender().replaceAll("\\<.*?>", "").replaceAll("[^0-9a-zA-Z ]+", " ");
            if (config.usefriendsChat()){
                if((sender.equals(getPlayerName()) && config.logSelf()) || (!sender.equals(getPlayerName()) && config.logOthers())) {
                    processFriendsChat(outputMessage, sender, friendsName);
                }
            }
        }
    }

    private String getPlayerName()
    {
        return client.getLocalPlayer().getName();
    }

    private void processPrivate(String outputText,String senderName, String receiverName){
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
        sendWebhookPrivate(webhookBody);
    }

    private void sendWebhookPrivate(WebhookBody webhookBody)
    {
        String configUrl = config.webhookPrivate();
        if (Strings.isNullOrEmpty(configUrl))
        {
            return;
        }

        HttpUrl url = HttpUrl.parse(configUrl);
        MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("payload_json", GSON.toJson(webhookBody));

        buildRequestAndSend(url, requestBodyBuilder);
    }

    private void processGroup(String outputText,String senderName, String groupName){
        WebhookBody webhookBody = new WebhookBody();
        StringBuilder stringBuilder = new StringBuilder();
        if (config.useGroupName())
        {
            stringBuilder.append("**[").append(groupName).append("]** ");
        }
        if ((senderName.equals(getPlayerName()) && config.includeUsername()) || (!senderName.equals(getPlayerName()) && config.includeOtherUsername()))
        {
            stringBuilder.append("**").append(senderName).append("**").append(" : ");
        }
        stringBuilder.append(outputText);
        webhookBody.setContent(stringBuilder.toString());
        sendWebhookGroup(webhookBody);
    }

    private void sendWebhookGroup(WebhookBody webhookBody)
    {
        String configUrl = config.webhookGroup();
        if (Strings.isNullOrEmpty(configUrl))
        {
            return;
        }

        HttpUrl url = HttpUrl.parse(configUrl);
        MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("payload_json", GSON.toJson(webhookBody));

        buildRequestAndSend(url, requestBodyBuilder);
    }

    private void processFriendsChat(String outputText,String senderName, String friendsChatName){
        WebhookBody webhookBody = new WebhookBody();
        StringBuilder stringBuilder = new StringBuilder();
        if (config.usefriendsChat())
        {
            stringBuilder.append("**[").append(friendsChatName).append("]** ");
        }
        if ((senderName.equals(getPlayerName()) && config.includeUsername()) || (!senderName.equals(getPlayerName()) && config.includeOtherUsername()))
        {
            stringBuilder.append("**").append(senderName).append("**").append(" : ");
        }
        stringBuilder.append(outputText);
        webhookBody.setContent(stringBuilder.toString());
        sendWebhookFriends(webhookBody);
    }

    private void sendWebhookFriends(WebhookBody webhookBody)
    {
        String configUrl = config.webhookFriendsChat();
        if (Strings.isNullOrEmpty(configUrl))
        {
            return;
        }

        HttpUrl url = HttpUrl.parse(configUrl);
        MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("payload_json", GSON.toJson(webhookBody));

        buildRequestAndSend(url, requestBodyBuilder);
    }

    private void buildRequestAndSend(HttpUrl url, MultipartBody.Builder requestBodyBuilder)
    {
        RequestBody requestBody = requestBodyBuilder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        sendRequest(request);
    }

    private void sendRequest(Request request)
    {
        okHttpClient.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                log.debug("Error submitting webhook", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                response.close();
            }
        });
    }
}