package com.github.paulosalonso.election.output.http.client.webhook;

import com.github.paulosalonso.election.output.http.HttpResponseBodyMapper;
import com.github.paulosalonso.election.output.http.HttpResponseStatusValidator;
import com.github.paulosalonso.election.tools.text.MessageFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class WebHookClient {

    private static final String URI = "uri";
    private static final String ERROR = "error";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String JSON = "application/json";

    private static final String SENDING_MESSAGE = "Sending content to webhook in URI ${uri}";
    private static final String SUCCESS_MESSAGE = "Content was sent to webhook";
    private static final String POST_ERROR_MESSAGE = "Error requesting webhook. Original message: ${error}";
    private static final String URI_NOT_SET_ERROR = "Webhook URI was not set";

    private final String url;
    private final int timeout;
    private final HttpClient httpClient;

    public void post(String json, Map<String, String> variables) {
        if (url == null) {
            throw new WebHookClientException(URI_NOT_SET_ERROR);
        }

        final var uri = java.net.URI.create(MessageFormatter.format(this.url, variables));

        log.info(MessageFormatter.format(SENDING_MESSAGE, URI, uri.toString()));

        final var httpRequest = HttpRequest.newBuilder()
                .POST(BodyPublishers.ofString(json))
                .uri(uri)
                .header(CONTENT_TYPE, JSON)
                .timeout(Duration.ofSeconds(timeout))
                .build();

        try {
            final var response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofInputStream());

            if (!HttpResponseStatusValidator.is2xx(response)) {
                final var errorMessage = HttpResponseBodyMapper.toString(response);
                final var message = MessageFormatter.format(POST_ERROR_MESSAGE, ERROR, errorMessage);

                throw new WebHookClientException(message);
            }
        } catch (Exception e) {
            throw new WebHookClientException(e);
        }

        log.info(SUCCESS_MESSAGE);
    }
}
