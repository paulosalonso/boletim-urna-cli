package com.github.paulosalonso.election.service;

import com.github.paulosalonso.election.model.OutputType;
import com.github.paulosalonso.election.model.Scope;
import com.github.paulosalonso.election.output.file.FileCreator;
import com.github.paulosalonso.election.output.http.client.tse.TseHttpClient;
import com.github.paulosalonso.election.output.http.client.tse.model.PollingPlace;
import com.github.paulosalonso.election.service.mapper.BulletinMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.nio.file.Path;

import static com.github.paulosalonso.election.tools.text.MessageFormatter.format;

@Slf4j
public class BulletinToFileService {

    private static final String PATH = "path";
    private static final String FILE_SAVED_LOG = "File ${path} saved successfully";

    public static void main(String[] args) {
        final var init = System.currentTimeMillis();

        FileCreator.setRootPath(Path.of("/home/paulo/Desktop/Boletins_Urna"));

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

        for (var pollingPlace : pollingPlaces) {
            saveBulletins(pollingPlace, outputType);
        }
    }

    public static void saveByCity(String stateCode, String cityCode, OutputType outputType) {
        final var pollingPlaces = PollingPlaceService.getPollingPlace(stateCode, cityCode);

        for (var pollingPlace : pollingPlaces) {
            saveBulletins(pollingPlace, outputType);
        }
    }

    public static void saveByZone(String stateCode, String cityCode, String zone, OutputType outputType) {
        final var pollingPlaces = PollingPlaceService.getPollingPlace(stateCode, cityCode, zone);

        for (var pollingPlace : pollingPlaces) {
            saveBulletins(pollingPlace, outputType);
        }
    }

    public static void saveBySection(String stateCode, String cityCode, String zone, String section, OutputType outputType) {
        final var pollingPlace = PollingPlaceService.getPollingPlace(stateCode, cityCode, zone, section);
        saveBulletins(pollingPlace, outputType);
    }

    public static void keepOnSaving(String stateCode, String cityCode, String zone, String section, Scope scope, OutputType outputType) {
        final var pollingPlaces = PollingPlaceService.getPollingPlace(stateCode, cityCode, zone, section, scope);

        for (var pollingPlace : pollingPlaces) {
            saveBulletins(pollingPlace, outputType);
        }
    }

    private static void saveBulletins(PollingPlace pollingPlace, OutputType outputType) {
        final var bulletins = TseHttpClient.getBulletins(pollingPlace);

        for (var i = 0; i < bulletins.size(); i++) {
            var fileNameSuffix = i > 0 ? "(" + i + ")" : "";
            saveBulletin(bulletins.get(i), pollingPlace, fileNameSuffix, outputType);
        }
    }

    private static void saveBulletin(InputStream bulletin, PollingPlace pollingPlace, String fileNameSuffix, OutputType outputType) {
        Path savedPath;

        if (outputType.equals(OutputType.JSON)) {
            final var entidadeBoletimUrna = BulletinMapper.toAsn1Object(bulletin);
            final var boletimUrnaModel = BulletinMapper.toModel(entidadeBoletimUrna);
            final var json = BulletinMapper.toJson(boletimUrnaModel);
            savedPath = FileCreator.saveAsJsonFile(pollingPlace, json, fileNameSuffix);
        } else {
            savedPath = FileCreator.saveAsBuFile(pollingPlace, bulletin, fileNameSuffix);
        }

        log.info(format(FILE_SAVED_LOG, PATH, savedPath.toString()));
    }
}
