package com.discordchatlogger.helpers;

import com.discordchatlogger.DiscordChatLoggerConfig;
import com.discordchatlogger.domain.WebhookBody;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import okhttp3.*;

import static net.runelite.http.api.RuneLiteAPI.GSON;
import javax.inject.Inject;
import java.io.IOException;
import com.google.common.base.Strings;

@Slf4j
public class DiscordHelper {
    @Inject
    private OkHttpClient okHttpClient;

    @Inject
    private DiscordChatLoggerConfig config;

    public void sendWebhookBody(WebhookBody webhookBody, ChatMessageType chatMessageType) {
        String configUrl = getWebhookUrl(chatMessageType);
        if (Strings.isNullOrEmpty(configUrl))
                return;

        HttpUrl url = HttpUrl.parse(configUrl);
        MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("payload_json", GSON.toJson(webhookBody));

        buildRequestAndSend(url, requestBodyBuilder);
    }

    private String getWebhookUrl(ChatMessageType chatMessageType) {
        if (chatMessageType == ChatMessageType.PRIVATECHAT || chatMessageType == ChatMessageType.PRIVATECHATOUT) {
            return config.webhookPrivate();
        } else if (chatMessageType == ChatMessageType.FRIENDSCHAT) {
            return config.webhookFriendsChat();
        } else if (chatMessageType == ChatMessageType.CLAN_GIM_CHAT || chatMessageType == ChatMessageType.CLAN_GIM_MESSAGE) {
            return config.webhookGroup();
        } else if (chatMessageType == ChatMessageType.CLAN_CHAT) {
            return config.webhookClanChat();
        } else {
            return null;
        }
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
