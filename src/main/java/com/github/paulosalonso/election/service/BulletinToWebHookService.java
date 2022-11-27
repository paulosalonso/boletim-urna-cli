package com.github.paulosalonso.election.service;

import com.github.paulosalonso.election.model.Scope;
import com.github.paulosalonso.election.output.http.client.tse.TseHttpClient;
import com.github.paulosalonso.election.output.http.client.tse.model.PollingPlace;
import com.github.paulosalonso.election.output.http.client.webhook.WebHookClient;
import com.github.paulosalonso.election.service.mapper.BulletinMapper;
import com.github.paulosalonso.election.tools.text.MessageFormatter;
import lombok.extern.slf4j.Slf4j;

import static java.util.Collections.singletonList;

@Slf4j
public class BulletinToWebHookService {

    private static final String STATE = "state";
    private static final String CITY = "city";
    private static final String ZONE = "zone";
    private static final String SECTION = "section";

    private static final String BULLETINS_NOT_FOUND = "There are not bulletins for path /${state}/${city}/${zone}/${section}";

    public static void sendToWebHook(String state, String city, String zone, String section, Scope scopeToContinue) {
        if (state != null && city != null && zone != null && section != null) {
            if (scopeToContinue != null) {
                keepOnSendingToWebHook(state, city, zone, section, scopeToContinue);
            } else {
                sendToWebHookBySection(state, city, zone, section);
            }
        } else if (state != null && city != null && zone != null) {
            sendToWebHookByZone(state, city, zone);
        } else if (state != null && city != null) {
            sendToWebHookByCity(state, city);
        } else if (state != null) {
            sendToWebHookByState(state);
        }
    }

    private static void sendToWebHookByState(String stateCode) {
        final var pollingPlaces = PollingPlaceService.getPollingPlace(stateCode);
        ProgressMonitorService.runMonitoringProgress(pollingPlaces, BulletinToWebHookService::sendToWebHook);
    }

    private static void sendToWebHookByCity(String stateCode, String cityCode) {
        final var pollingPlaces = PollingPlaceService.getPollingPlace(stateCode, cityCode);
        ProgressMonitorService.runMonitoringProgress(pollingPlaces, BulletinToWebHookService::sendToWebHook);
    }

    private static void sendToWebHookByZone(String stateCode, String cityCode, String zone) {
        final var pollingPlaces = PollingPlaceService.getPollingPlace(stateCode, cityCode, zone);
        ProgressMonitorService.runMonitoringProgress(pollingPlaces, BulletinToWebHookService::sendToWebHook);
    }

    private static void sendToWebHookBySection(String stateCode, String cityCode, String zone, String section) {
        final var pollingPlace = PollingPlaceService.getPollingPlace(stateCode, cityCode, zone, section);
        ProgressMonitorService.runMonitoringProgress(singletonList(pollingPlace), BulletinToWebHookService::sendToWebHook);
    }

    private static void keepOnSendingToWebHook(String stateCode, String cityCode, String zone, String section, Scope scope) {
        final var pollingPlaces = PollingPlaceService.getPollingPlace(stateCode, cityCode, zone, section, scope);
        ProgressMonitorService.runMonitoringProgress(pollingPlaces, BulletinToWebHookService::sendToWebHook);
    }

    private static void sendToWebHook(PollingPlace pollingPlace) {
        final var bulletins = TseHttpClient.getBulletins(pollingPlace);

        if (bulletins.isEmpty()) {
            log.warn(MessageFormatter.format(BULLETINS_NOT_FOUND,
                    STATE, pollingPlace.getState(),
                    CITY, pollingPlace.getCityCode(),
                    ZONE, pollingPlace.getZone(),
                    SECTION, pollingPlace.getSection()));
        }

        for (var bulletin : bulletins) {
            final var entidadeBoletimUrna = BulletinMapper.toAsn1Object(bulletin);
            final var boletimUrnaModel = BulletinMapper.toModel(entidadeBoletimUrna);
            final var json = BulletinMapper.toJson(boletimUrnaModel);

            WebHookClient.post(pollingPlace, json);
        }
    }
}
