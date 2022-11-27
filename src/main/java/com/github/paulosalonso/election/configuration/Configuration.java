package com.github.paulosalonso.election.configuration;

import lombok.NoArgsConstructor;

import java.nio.file.Files;
import java.nio.file.Path;

import static com.github.paulosalonso.election.tools.text.MessageFormatter.format;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class Configuration {

    private static final String PATH = "path";
    private static final String ROOT_DIRECTORY_DOES_NOT_EXISTS = "Root directory does not exists [${path}]";

    private static String rootPath = Path.of(System.getProperty("java.io.tmpdir"), "eleicoes_2022").toString();
    private static String webHookUri;
    private static Integer retryMaxAttempts = 3;
    private static Integer retryIntervalInSeconds = 10;
    private static Integer requestIntervalInMillis = 250;
    private static Integer tseTimeout = 10;
    private static Integer webHookTimeout = 10;

    public static String getRootPath() {
        return rootPath;
    }

    public static void setRootDirectory(String rootPath) {
        if (!Files.exists(Path.of(rootPath))) {
            throw new IllegalArgumentException(
                    format(ROOT_DIRECTORY_DOES_NOT_EXISTS, PATH, rootPath));
        }

        Configuration.rootPath = rootPath;
    }

    public static String getWebHookUri() {
        return webHookUri;
    }

    public static void setWebHookUri(String uri) {
        webHookUri = uri;
    }

    public static int getRetryMaxAttempts() {
        return retryMaxAttempts;
    }

    public static void setRetryMaxAttempts(int retryMaxAttempts) {
        Configuration.retryMaxAttempts = retryMaxAttempts;
    }

    public static int getRetryIntervalInSeconds() {
        return retryIntervalInSeconds;
    }

    public static void setRetryIntervalInSeconds(int retryIntervalInSeconds) {
        Configuration.retryIntervalInSeconds = retryIntervalInSeconds;
    }

    public static int getRequestIntervalInMillis() {
        return requestIntervalInMillis;
    }

    public static void setRequestIntervalInMillis(int requestIntervalInMillis) {
        Configuration.requestIntervalInMillis = requestIntervalInMillis;
    }

    public static int getTseTimeout() {
        return tseTimeout;
    }

    public static void setTseTimeout(int tseTimeout) {
        Configuration.tseTimeout = tseTimeout;
    }

    public static int getWebHookTimeout() {
        return webHookTimeout;
    }

    public static void setWebHookTimeout(int webHookTimeout) {
        Configuration.webHookTimeout = webHookTimeout;
    }
}
