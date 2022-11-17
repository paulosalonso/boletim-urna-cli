package com.github.paulosalonso.election.output.http.client.webhook;

public class WebHookClientException extends RuntimeException {

    public WebHookClientException(Throwable cause) {
        super(cause);
    }

    public WebHookClientException(String message) {
        super(message);
    }
}
