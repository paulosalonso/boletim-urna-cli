package com.github.paulosalonso.election.output.http.retry;

public class HttpRetryException extends RuntimeException {
    public HttpRetryException(String message) {
        super(message);
    }

    public HttpRetryException(Throwable cause) {
        super(cause);
    }
}
