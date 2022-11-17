package com.github.paulosalonso.election.output.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpResponse;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class HttpResponseBodyMapper {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static String toString(HttpResponse<InputStream> response) {
        try {
            return new String(response.body().readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T toObject(HttpResponse<InputStream> response, Class<T> type) {
        try {
            return MAPPER.readValue(response.body(), type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
