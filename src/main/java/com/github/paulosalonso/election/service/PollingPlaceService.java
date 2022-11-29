package com.github.paulosalonso.election.service;

import com.github.paulosalonso.election.model.Scope;
import com.github.paulosalonso.election.output.http.client.tse.TseHttpClient;
import com.github.paulosalonso.election.output.http.client.tse.model.PollingPlace;
import com.github.paulosalonso.election.service.mapper.PollingPlaceMapper;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class PollingPlaceService {

    private final TseHttpClient tseHttpClient;

    public List<PollingPlace> getPollingPlace(String state) {
        final var sectionsByState = tseHttpClient.getSectionsByState(state);
        return PollingPlaceMapper.toPollingPlace(sectionsByState);
    }

    public List<PollingPlace> getPollingPlace(String state, String cityCode) {
        final var sectionsByState = tseHttpClient.getSectionsByState(state);
        return PollingPlaceMapper.toPollingPlace(sectionsByState, cityCode);
    }

    public List<PollingPlace> getPollingPlace(String state, String cityCode, String zone) {
        final var sectionsByState = tseHttpClient.getSectionsByState(state);
        return PollingPlaceMapper.toPollingPlace(sectionsByState, cityCode, zone);
    }

    public PollingPlace getPollingPlace(String state, String cityCode, String zone, String section) {
        final var sectionsByState = tseHttpClient.getSectionsByState(state);
        return PollingPlaceMapper.toPollingPlace(sectionsByState, cityCode, zone, section);
    }

    public List<PollingPlace> getPollingPlace(String state, String cityCode, String zoneCode, String sectionNumber, Scope scope) {
        final var sectionsByState = tseHttpClient.getSectionsByState(state);

        var cityFound = false;
        var zoneFound = false;
        var sectionFound = false;

        final var result = new ArrayList<PollingPlace>();

        for (var city : sectionsByState.getCities()) {
            if (Scope.CITY.equals(scope)) {
                if (city.getCode().equals(cityCode)) {
                    cityFound = true;
                } else {
                    continue;
                }
            }

            for (var zone : city.getZones()) {
                if (Scope.ZONE.equals(scope)) {
                    if (city.getCode().equals(cityCode) && zone.getCode().equals(zoneCode)) {
                        zoneFound = true;
                    } else {
                        continue;
                    }
                }

                for (var section : zone.getSections()) {
                    if (city.getCode().equals(cityCode) && zone.getCode().equals(zoneCode) && section.getNumber().equals(sectionNumber)) {
                        sectionFound = true;
                    }

                    if (sectionFound) {
                        result.add(PollingPlaceMapper.toPollingPlace(
                                sectionsByState, city.getCode(), zone.getCode(), section.getNumber()));
                    }
                }

                if (Scope.ZONE.equals(scope) && zoneFound) {
                    break;
                }
            }

            if ((Scope.CITY.equals(scope) && cityFound) || (Scope.ZONE.equals(scope) && zoneFound)) {
                break;
            }
        }

        return Collections.unmodifiableList(result);
    }
}
