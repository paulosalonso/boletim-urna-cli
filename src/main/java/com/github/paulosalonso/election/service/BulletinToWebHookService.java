package com.github.paulosalonso.election.service;

import com.github.paulosalonso.election.model.Scope;
import com.github.paulosalonso.election.output.http.client.tse.TseHttpClient;
import com.github.paulosalonso.election.output.http.client.tse.model.PollingPlace;
import com.github.paulosalonso.election.output.http.client.webhook.WebHookClient;
import com.github.paulosalonso.election.service.mapper.BulletinMapper;
import lombok.extern.slf4j.Slf4j;

import static java.util.Collections.singletonList;

@Slf4j
public class BulletinToWebHookService {

    public static void sendToWebHookByState(String stateCode) {
        final var pollingPlaces = PollingPlaceService.getPollingPlace(stateCode);
        ProgressMonitorService.runMonitoringProgress(pollingPlaces, BulletinToWebHookService::sendToWebHook);
    }

    public static void sendToWebHookByCity(String stateCode, String cityCode) {
        final var pollingPlaces = PollingPlaceService.getPollingPlace(stateCode, cityCode);
        ProgressMonitorService.runMonitoringProgress(pollingPlaces, BulletinToWebHookService::sendToWebHook);
    }

    public static void sendToWebHookByZone(String stateCode, String cityCode, String zone) {
        final var pollingPlaces = PollingPlaceService.getPollingPlace(stateCode, cityCode, zone);
        ProgressMonitorService.runMonitoringProgress(pollingPlaces, BulletinToWebHookService::sendToWebHook);
    }

    public static void sendToWebHookBySection(String stateCode, String cityCode, String zone, String section) {
        final var pollingPlace = PollingPlaceService.getPollingPlace(stateCode, cityCode, zone, section);
        ProgressMonitorService.runMonitoringProgress(singletonList(pollingPlace), BulletinToWebHookService::sendToWebHook);
    }

    public static void keepOnSendingToWebHook(String stateCode, String cityCode, String zone, String section, Scope scope) {
        final var pollingPlaces = PollingPlaceService.getPollingPlace(stateCode, cityCode, zone, section, scope);
        ProgressMonitorService.runMonitoringProgress(pollingPlaces, BulletinToWebHookService::sendToWebHook);
    }

    private static void sendToWebHook(PollingPlace pollingPlace) {
        final var bulletins = TseHttpClient.getBulletins(pollingPlace);

        for (var bulletin : bulletins) {
            final var entidadeBoletimUrna = BulletinMapper.toAsn1Object(bulletin);
            final var boletimUrnaModel = BulletinMapper.toModel(entidadeBoletimUrna);
            final var json = BulletinMapper.toJson(boletimUrnaModel);

            WebHookClient.post(pollingPlace, json);
        }
    }
}
