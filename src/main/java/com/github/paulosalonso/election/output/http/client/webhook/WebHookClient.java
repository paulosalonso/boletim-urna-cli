package com.github.paulosalonso.election.output.http.client.webhook;

import com.github.paulosalonso.election.output.http.HttpResponseBodyMapper;
import com.github.paulosalonso.election.output.http.HttpResponseStatusValidator;
import com.github.paulosalonso.election.tools.text.MessageFormatter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.time.Duration;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public class WebHookClient {

    private static final String ERROR = "error";

    private static final String POST_ERROR_MESSAGE = "Error requesting webhook. Original message: ${error}";
    private static final String URI_NOT_SET_ERROR = "Webhook URI was not set";

    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    private static URI webHookUri;

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    public static void setWebHookUri(String uri) {
        webHookUri = URI.create(uri);
    }

    public static void post(String json) {
        if (webHookUri == null) {
            throw new WebHookClientException(URI_NOT_SET_ERROR);
        }

        final var httpRequest = HttpRequest.newBuilder()
                .POST(BodyPublishers.ofString(json))
                .uri(webHookUri)
                .timeout(TIMEOUT)
                .build();

        try {
            final var response = HTTP_CLIENT.send(httpRequest, HttpResponse.BodyHandlers.ofInputStream());

            if (!HttpResponseStatusValidator.is2xx(response)) {
                final var errorMessage = HttpResponseBodyMapper.toString(response);
                final var message = MessageFormatter.format(POST_ERROR_MESSAGE, ERROR, errorMessage);

                throw new WebHookClientException(message);
            }
        } catch (Exception e) {
            throw new WebHookClientException(e);
        }
    }
}
