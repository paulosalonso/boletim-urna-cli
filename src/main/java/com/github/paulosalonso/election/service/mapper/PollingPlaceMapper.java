package com.github.paulosalonso.election.service.mapper;

import com.github.paulosalonso.election.output.http.client.tse.model.PollingPlace;
import com.github.paulosalonso.election.output.http.client.tse.model.State;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.github.paulosalonso.election.tools.text.MessageFormatter.format;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final  class PollingPlaceMapper {

    public static List<PollingPlace> toPollingPlace(State state) {
        final var result = new ArrayList<PollingPlace>();

        for (var city : state.getCities()) {
            for (var zone : city.getZones()) {
                for (var section : zone.getSections()) {
                    result.add(PollingPlace.builder()
                            .state(state.getCode())
                            .cityCode(city.getCode())
                            .cityName(city.getName())
                            .zone(zone.getCode())
                            .section(section.getNumber())
                            .build());
                }
            }
        }

        return Collections.unmodifiableList(result);
    }

    public static List<PollingPlace> toPollingPlace(State state, String cityCode) {
        final var result = new ArrayList<PollingPlace>();

        for (var city : state.getCities()) {
            if (city.getCode().equals(cityCode)) {
                for (var zone : city.getZones()) {
                    for (var section : zone.getSections()) {
                        result.add(PollingPlace.builder()
                                .state(state.getCode())
                                .cityCode(city.getCode())
                                .cityName(city.getName())
                                .zone(zone.getCode())
                                .section(section.getNumber())
                                .build());
                    }
                }
            }
        }

        return Collections.unmodifiableList(result);
    }

    public static List<PollingPlace> toPollingPlace(State state, String cityCode, String zoneCode) {
        final var result = new ArrayList<PollingPlace>();

        for (var city : state.getCities()) {
            if (city.getCode().equals(cityCode)) {
                for (var zone : city.getZones()) {
                    if (zone.getCode().equals(zoneCode)) {
                        for (var section : zone.getSections()) {
                            result.add(PollingPlace.builder()
                                    .state(state.getCode())
                                    .cityCode(city.getCode())
                                    .cityName(city.getName())
                                    .zone(zone.getCode())
                                    .section(section.getNumber())
                                    .build());
                        }
                    }
                }
            }
        }

        return Collections.unmodifiableList(result);
    }

    public static PollingPlace toPollingPlace(State state, String cityCode, String zoneCode, String section) {
        for (var city : state.getCities()) {
            if (city.getCode().equals(cityCode)) {
                return PollingPlace.builder()
                        .state(state.getCode())
                        .cityCode(city.getCode())
                        .cityName(city.getName())
                        .zone(zoneCode)
                        .section(section)
                        .build();
            }
        }

        throw new IllegalArgumentException(format("City with code ${cityCode} was not found", "cityCode", cityCode));
    }
}
