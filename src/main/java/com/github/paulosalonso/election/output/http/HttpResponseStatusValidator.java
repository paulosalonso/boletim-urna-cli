package com.github.paulosalonso.election.output.http;

import lombok.NoArgsConstructor;

import java.net.http.HttpResponse;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class HttpResponseStatusValidator {

    public static boolean is2xx(HttpResponse response) {
        final var status = response.statusCode();
        return status >= 200 && status < 300;
    }
}
