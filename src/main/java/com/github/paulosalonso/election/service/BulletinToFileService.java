package com.github.paulosalonso.election.service;

import com.github.paulosalonso.election.configuration.Configuration;
import com.github.paulosalonso.election.model.OutputType;
import com.github.paulosalonso.election.model.Scope;
import com.github.paulosalonso.election.output.file.FileCreator;
import com.github.paulosalonso.election.output.http.client.tse.TseHttpClient;
import com.github.paulosalonso.election.output.http.client.tse.model.PollingPlace;
import com.github.paulosalonso.election.service.mapper.BulletinMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.List;

import static com.github.paulosalonso.election.tools.text.MessageFormatter.format;
import static java.util.Collections.singletonList;

@Slf4j
public class BulletinToFileService {

    private static final String PATH = "path";
    private static final String FILE_SAVED_LOG = "File ${path} saved successfully";

    public static void main(String[] args) {
        final var init = System.currentTimeMillis();

        Configuration.setRootPath("/home/paulo/Desktop/Boletins_Urna");

        saveByState("PI", OutputType.BU);

        log.info("Process finished in {} millis", System.currentTimeMillis() - init);

        /*
         * REGIÃO NORTE
         * AC OK
         * AP OK
         * AM OK
         * PA OK
         * RO OK
         * RR OK
         * TO OK
         *
         * REGIÃO NORDESTE
         * AL OK
         * BA OK
         * CE OK
         * MA OK
         * PB OK
         * PE OK
         * PI
         * RN
         * SE OK
         *
         * DISTRITO FEDERAL
         * DF
         *
         * REGIÃO CENTRO-OESTE
         * GO
         * MT
         * MS
         *
         * REGIÃO SUDESTE
         * ES
         * MG
         * RJ
         * SP OK
         *
         * REGIÃO SUL
         * PR
         * SC OK
         * RS
         *
         * EXTERIOR
         * ZZ
         */
    }

    public static void saveByState(String stateCode, OutputType outputType) {
        final var pollingPlaces = PollingPlaceService.getPollingPlace(stateCode);
        saveBulletins(pollingPlaces, outputType);
    }

    public static void saveByCity(String stateCode, String cityCode, OutputType outputType) {
        final var pollingPlaces = PollingPlaceService.getPollingPlace(stateCode, cityCode);
        saveBulletins(pollingPlaces, outputType);
    }

    public static void saveByZone(String stateCode, String cityCode, String zone, OutputType outputType) {
        final var pollingPlaces = PollingPlaceService.getPollingPlace(stateCode, cityCode, zone);
        saveBulletins(pollingPlaces, outputType);
    }

    public static void saveBySection(String stateCode, String cityCode, String zone, String section, OutputType outputType) {
        final var pollingPlace = PollingPlaceService.getPollingPlace(stateCode, cityCode, zone, section);
        saveBulletins(singletonList(pollingPlace), outputType);
    }

    public static void keepOnSaving(String stateCode, String cityCode, String zone, String section, Scope scope, OutputType outputType) {
        final var pollingPlaces = PollingPlaceService.getPollingPlace(stateCode, cityCode, zone, section, scope);
        saveBulletins(pollingPlaces, outputType);
    }

    private static void saveBulletins(List<PollingPlace> pollingPlaces, OutputType outputType) {
        if (OutputType.JSON.equals(outputType)) {
            ProgressMonitorService.runMonitoringProgress(pollingPlaces, BulletinToFileService::saveBulletinsAsJson);
        } else {
            ProgressMonitorService.runMonitoringProgress(pollingPlaces, BulletinToFileService::saveBulletinsAsBU);
        }
    }

    private static void saveBulletinsAsJson(PollingPlace pollingPlace) {
        final var bulletins = TseHttpClient.getBulletins(pollingPlace);

        for (var i = 0; i < bulletins.size(); i++) {
            var fileNameSuffix = i > 0 ? "(" + i + ")" : "";
            saveBulletinAsJson(bulletins.get(i), pollingPlace, fileNameSuffix);
        }
    }

    private static void saveBulletinAsJson(InputStream bulletin, PollingPlace pollingPlace, String fileNameSuffix) {
        final var entidadeBoletimUrna = BulletinMapper.toAsn1Object(bulletin);
        final var boletimUrnaModel = BulletinMapper.toModel(entidadeBoletimUrna);
        final var json = BulletinMapper.toJson(boletimUrnaModel);
        final var savedPath = FileCreator.saveAsJsonFile(pollingPlace, json, fileNameSuffix);

        log.info(format(FILE_SAVED_LOG, PATH, savedPath.toString()));
    }

    private static void saveBulletinsAsBU(PollingPlace pollingPlace) {
        final var bulletins = TseHttpClient.getBulletins(pollingPlace);

        for (var i = 0; i < bulletins.size(); i++) {
            var fileNameSuffix = i > 0 ? "(" + i + ")" : "";
            saveBulletinAsBU(bulletins.get(i), pollingPlace, fileNameSuffix);
        }
    }

    private static void saveBulletinAsBU(InputStream bulletin, PollingPlace pollingPlace, String fileNameSuffix) {
        final var savedPath = FileCreator.saveAsBuFile(pollingPlace, bulletin, fileNameSuffix);
        log.info(format(FILE_SAVED_LOG, PATH, savedPath.toString()));
    }
}
