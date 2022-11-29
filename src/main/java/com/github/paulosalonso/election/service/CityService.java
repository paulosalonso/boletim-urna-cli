package com.github.paulosalonso.election.service;

import com.github.paulosalonso.election.output.http.client.tse.TseHttpClient;
import com.github.paulosalonso.election.output.http.client.tse.model.City;
import com.github.paulosalonso.election.output.http.client.tse.model.State;
import com.github.paulosalonso.election.output.http.client.webhook.WebHookClient;
import com.github.paulosalonso.election.service.mapper.CityMapper;
import com.github.paulosalonso.election.tools.text.MessageFormatter;
import com.github.paulosalonso.election.tools.text.StringNormalizer;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import static java.util.stream.Collectors.*;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class CityService {

    private static final String STATE = "state";
    private static final String CODE = "code";
    private static final String NAME = "name";
    private static final String SEARCH = "search";
    private static final String PRINT_PATTERN = "[${state}][${code}] ${name}";
    private static final String NOT_FOUND_MESSAGE = "No city was found for search '${search}'";

    public static void printCities(String search) {
        processCities(search, CityService::printCityOnConsole);
    }

    public static void sendCitiesToWebHook(String search) {
        processCities(search, CityService::sendCityToWebHook);
    }

    private static void processCities(String search, BiConsumer<String, City> consumer) {
        final var cities = TseHttpClient.getCities();
        final var citiesByState = filter(cities, search);

        if (citiesByState.isEmpty()) {
            System.out.println(MessageFormatter.format(NOT_FOUND_MESSAGE, SEARCH, search));
        } else {
            processCities(citiesByState, consumer);
        }
    }

    private static void processCities(Map<String, List<City>> cities, BiConsumer<String, City> consumer) {
        final var states = cities.keySet().stream().sorted().toList();

        for (final var state : states) {
            for (final var city : cities.get(state)) {
                consumer.accept(state, city);
            }
        }
    }

    private static Map<String, List<City>> filter(List<State> source, String search) {
        final var citiesByState = groupCitiesByState(source);

        if (search == null || search.isBlank()) {
            return citiesByState;
        }

        final var result = new HashMap<String, List<City>>();

        for (final var state : citiesByState.keySet()) {
            final var cities = citiesByState.get(state).stream()
                    .filter(city -> filter(city, search))
                    .toList();

            if (!cities.isEmpty()) {
                result.put(state, cities);
            }
        }

        return Map.copyOf(result);
    }

    private static Map<String, List<City>> groupCitiesByState(List<State> source) {
        return source.stream().collect(groupingBy(
                State::getCode, flatMapping(state -> state.getCities().stream(), toList())));
    }

    private static boolean filter(City city, String search) {
        final var normalizedSearch = StringNormalizer.removeAccents(search).toLowerCase();
        final var normalizedCityName = StringNormalizer.removeAccents(city.getName()).toLowerCase();
        return normalizedCityName.contains(normalizedSearch);
    }

    private static void printCityOnConsole(String state, City city) {
        System.out.println(MessageFormatter.format(
                PRINT_PATTERN, CODE, city.getCode(), NAME, city.getName(), STATE, state));
    }

    private static void sendCityToWebHook(String state, City city) {
        final var json = CityMapper.toJson(city);
        final var variables = Map.of("estado", state);
        WebHookClient.post(json, variables);
    }
}
