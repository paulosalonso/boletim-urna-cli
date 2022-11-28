package com.github.paulosalonso.election.service;

import com.github.paulosalonso.election.output.http.client.tse.TseHttpClient;
import com.github.paulosalonso.election.output.http.client.tse.model.City;
import com.github.paulosalonso.election.tools.text.MessageFormatter;
import com.github.paulosalonso.election.tools.text.StringNormalizer;
import lombok.NoArgsConstructor;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class CityToConsoleService {

    private static final String CODE = "code";
    private static final String NAME = "name";
    private static final String SEARCH = "search";
    private static final String PRINT_PATTERN = "${code} - ${name}";
    private static final String NOT_FOUND_MESSAGE = "No city was found for search '${search}'";

    public static void printCities(String search) {
        final var cities = TseHttpClient.getCities();

        if (search == null || search.isBlank()) {
            printCities(cities);
        } else {
            final var filteredCities = cities.stream()
                    .filter(city -> filter(city, search))
                    .toList();

            printCities(filteredCities);

            if (filteredCities.isEmpty()) {
                System.out.println(MessageFormatter.format(NOT_FOUND_MESSAGE, SEARCH, search));
            }
        }

    }

    private static void printCities(List<City> cities) {
        cities.forEach(city -> System.out.println(
                MessageFormatter.format(PRINT_PATTERN, CODE, city.getCode(), NAME, city.getName())));
    }

    private static boolean filter(City city, String search) {
        final var normalizedSearch = StringNormalizer.removeAccents(search).toLowerCase();
        final var normalizedCityName = StringNormalizer.removeAccents(city.getName()).toLowerCase();
        return normalizedCityName.contains(normalizedSearch);
    }
}
