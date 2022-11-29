package com.github.paulosalonso.election.service.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.paulosalonso.election.output.http.client.tse.model.City;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class CityMapper {

    private static final String ERROR_MESSAGE = "Error mapping city to JSON";

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static String toJson(City city) {
        try {
            return MAPPER.writeValueAsString(city);
        } catch (JsonProcessingException e) {
            throw new MappingException(ERROR_MESSAGE, e);
        }
    }
}
