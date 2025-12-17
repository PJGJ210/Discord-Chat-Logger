package com.discordchatlogger.domain;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class WebhookBody
{
    private String content;
    private List<Embed> embeds = new ArrayList<>();

    public WebhookBody() {}

    public WebhookBody(String content) {
        this.content = content;
    }

    @Data
    static class Embed
    {
        final UrlEmbed image;
    }

    @Data
    static class UrlEmbed
    {
        final String url;
    }
}
