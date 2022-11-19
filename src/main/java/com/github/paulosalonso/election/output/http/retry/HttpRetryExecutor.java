package com.github.paulosalonso.election.output.http.retry;

import com.github.paulosalonso.election.configuration.Configuration;
import com.github.paulosalonso.election.output.http.HttpResponseBodyMapper;
import com.github.paulosalonso.election.output.http.HttpResponseStatusValidator;
import com.github.paulosalonso.election.tools.text.MessageFormatter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.util.concurrent.TimeUnit;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public class HttpRetryExecutor {

    private static final String ATTEMPT = "attempt";
    private static final String ERROR = "error";
    private static final String ERROR_MESSAGE = "Error performing HTTP request attempt ${attempt}. Error message: ${error}";

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    public static HttpResponse<InputStream> execute(HttpRequest request, BodyHandler<InputStream> bodyHandler) {
        var attempt = 1;
        Exception exception = null;

        while(attempt <= Configuration.getRetryMaxAttempts()) {
            try {
                var response = HTTP_CLIENT.send(request, bodyHandler);

                if (HttpResponseStatusValidator.is2xx(response)) {
                    return response;
                } else {
                    final var errorMessage = HttpResponseBodyMapper.toString(response);

                    log.warn(MessageFormatter.format(
                            ERROR_MESSAGE, ATTEMPT, Integer.toString(attempt), ERROR, errorMessage));

                    if (attempt < Configuration.getRetryMaxAttempts()) {
                        try {
                            TimeUnit.SECONDS.sleep(Configuration.getRetryIntervalInSeconds());
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
