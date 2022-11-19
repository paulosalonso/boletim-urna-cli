package com.github.paulosalonso.election.configuration;

import lombok.NoArgsConstructor;

import java.net.URI;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class Configuration {

    private static URI webHookUri;
    private static int maxAttempts = 3;
    private static int intervalInSeconds = 10;
    private static int tseTimeout = 10;
    private static int webhookTimeout = 10;

    public static URI getWebHookUri() {
        return webHookUri;
    }

    public static void setWebHookUri(String uri) {
        webHookUri = URI.create(uri);
    }

    public static int getMaxAttempts() {
        return maxAttempts;
    }

    public static void setMaxAttempts(int maxAttempts) {
        Configuration.maxAttempts = maxAttempts;
    }

    public static int getIntervalInSeconds() {
        return intervalInSeconds;
    }

    public static void setIntervalInSeconds(int intervalInSeconds) {
        Configuration.intervalInSeconds = intervalInSeconds;
    }

    public static int getTseTimeout() {
        return tseTimeout;
    }

    public static void setTseTimeout(int tseTimeout) {
        Configuration.tseTimeout = tseTimeout;
    }

    public static int getWebhookTimeout() {
        return webhookTimeout;
    }

    public static void setWebhookTimeout(int webhookTimeout) {
        Configuration.webhookTimeout = webhookTimeout;
    }
}
