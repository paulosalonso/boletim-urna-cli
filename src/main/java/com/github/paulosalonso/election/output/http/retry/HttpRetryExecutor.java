package com.github.paulosalonso.election.output.http.retry;

import com.github.paulosalonso.election.output.http.HttpResponseBodyMapper;
import com.github.paulosalonso.election.output.http.HttpResponseStatusValidator;
import com.github.paulosalonso.election.tools.text.MessageFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class HttpRetryExecutor {

    private static final String ATTEMPT = "attempt";
    private static final String MAX_ATTEMPTS = "maxAttempts";
    private static final String ERROR = "error";
    private static final String COUNT_MESSAGE = "Performing attempt ${attempt} of ${maxAttempts}";
    private static final String ERROR_MESSAGE = "Error performing HTTP request attempt ${attempt}. Error message: ${error}";

    private final Integer maxAttempts;
    private final Integer interval;
    private final HttpClient httpClient;

    public HttpResponse<InputStream> execute(HttpRequest request, BodyHandler<InputStream> bodyHandler) {
        var attempt = Integer.valueOf(1);
        Exception exception = null;

        while(attempt <= maxAttempts) {
            log.info(MessageFormatter.format(COUNT_MESSAGE,
                    ATTEMPT, attempt.toString(), MAX_ATTEMPTS, maxAttempts.toString()));

            try {
                var response = httpClient.send(request, bodyHandler);

                if (HttpResponseStatusValidator.is2xx(response)) {
                    return response;
                } else {
                    final var errorMessage = HttpResponseBodyMapper.toString(response);

                    log.warn(MessageFormatter.format(
                            ERROR_MESSAGE, ATTEMPT, Integer.toString(attempt), ERROR, errorMessage));

                    if (attempt < maxAttempts) {
                        try {
                            TimeUnit.SECONDS.sleep(interval);
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                    } else {
                        throw new HttpRetryException(errorMessage);
                    }
                }
            } catch (Exception e) {
                exception = e;
            } finally {
                attempt++;
            }
        }

        throw new HttpRetryException(exception);
    }
}
