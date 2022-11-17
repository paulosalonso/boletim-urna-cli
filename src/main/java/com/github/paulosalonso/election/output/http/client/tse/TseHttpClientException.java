package com.github.paulosalonso.election.output.http.client.tse;

public class TseHttpClientException extends RuntimeException {

    public TseHttpClientException(Throwable cause) {
        super(cause);
    }

    public TseHttpClientException(String message) {
        super(message);
    }
}
