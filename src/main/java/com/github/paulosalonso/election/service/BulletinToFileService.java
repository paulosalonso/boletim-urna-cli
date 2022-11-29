package com.github.paulosalonso.election.service;

import com.github.paulosalonso.election.model.BulletinOutputType;
import com.github.paulosalonso.election.model.Scope;
import com.github.paulosalonso.election.output.file.FileCreator;
import com.github.paulosalonso.election.output.http.client.tse.TseHttpClient;
import com.github.paulosalonso.election.output.http.client.tse.model.PollingPlace;
import com.github.paulosalonso.election.service.mapper.BulletinMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.List;

import static com.github.paulosalonso.election.tools.text.MessageFormatter.format;
import static java.util.Collections.singletonList;

@Slf4j
@RequiredArgsConstructor
public class BulletinToFileService {

    private static final String PATH = "path";
    private static final String FILE_SAVED_LOG = "File ${path} saved successfully";

    private final TseHttpClient tseHttpClient;
    private final PollingPlaceService pollingPlaceService;
    private final FileCreator fileCreator;

    public void save(String state, String city, String zone, String section, Scope scopeToContinue, BulletinOutputType outputType) {
        if (state != null && city != null && zone != null && section != null) {
            if (scopeToContinue != null) {
                keepOnSaving(state, city, zone, section, scopeToContinue, outputType);
            } else {
                saveBySection(state, city, zone, section, outputType);
            }
        } else if (state != null && city != null && zone != null) {
            saveByZone(state, city, zone, outputType);
        } else if (state != null && city != null) {
            saveByCity(state, city, outputType);
        } else if (state != null) {
            saveByState(state, outputType);
        }
    }

    private void saveByState(String stateCode, BulletinOutputType outputType) {
        final var pollingPlaces = pollingPlaceService.getPollingPlace(stateCode);
        saveBulletins(pollingPlaces, outputType);
    }

    private void saveByCity(String stateCode, String cityCode, BulletinOutputType outputType) {
        final var pollingPlaces = pollingPlaceService.getPollingPlace(stateCode, cityCode);
        saveBulletins(pollingPlaces, outputType);
    }

    private void saveByZone(String stateCode, String cityCode, String zone, BulletinOutputType outputType) {
        final var pollingPlaces = pollingPlaceService.getPollingPlace(stateCode, cityCode, zone);
        saveBulletins(pollingPlaces, outputType);
    }

    private void saveBySection(String stateCode, String cityCode, String zone, String section, BulletinOutputType outputType) {
        final var pollingPlace = pollingPlaceService.getPollingPlace(stateCode, cityCode, zone, section);
        saveBulletins(singletonList(pollingPlace), outputType);
    }

    private void keepOnSaving(String stateCode, String cityCode, String zone, String section, Scope scope, BulletinOutputType outputType) {
        final var pollingPlaces = pollingPlaceService.getPollingPlace(stateCode, cityCode, zone, section, scope);
        saveBulletins(pollingPlaces, outputType);
    }

    private void saveBulletins(List<PollingPlace> pollingPlaces, BulletinOutputType outputType) {
        if (BulletinOutputType.JSON.equals(outputType)) {
            ProgressMonitorService.runMonitoringProgress(pollingPlaces, this::saveBulletinsAsJson);
        } else {
            ProgressMonitorService.runMonitoringProgress(pollingPlaces, this::saveBulletinsAsBU);
        }
    }

    private void saveBulletinsAsJson(PollingPlace pollingPlace) {
        final var bulletins = tseHttpClient.getBulletins(pollingPlace);

        for (var i = 0; i < bulletins.size(); i++) {
            var fileNameSuffix = i > 0 ? "(" + i + ")" : "";
            saveBulletinAsJson(bulletins.get(i), pollingPlace, fileNameSuffix);
        }
    }

    private void saveBulletinAsJson(InputStream bulletin, PollingPlace pollingPlace, String fileNameSuffix) {
        final var entidadeBoletimUrna = BulletinMapper.toAsn1Object(bulletin);
        final var boletimUrnaModel = BulletinMapper.toModel(entidadeBoletimUrna);
        final var json = BulletinMapper.toJson(boletimUrnaModel);
        final var savedPath = fileCreator.saveAsJsonFile(pollingPlace, json, fileNameSuffix);

        log.info(format(FILE_SAVED_LOG, PATH, savedPath.toString()));
    }

    private void saveBulletinsAsBU(PollingPlace pollingPlace) {
        final var bulletins = tseHttpClient.getBulletins(pollingPlace);

        for (var i = 0; i < bulletins.size(); i++) {
            var fileNameSuffix = i > 0 ? "(" + i + ")" : "";
            saveBulletinAsBU(bulletins.get(i), pollingPlace, fileNameSuffix);
        }
    }

    private void saveBulletinAsBU(InputStream bulletin, PollingPlace pollingPlace, String fileNameSuffix) {
        final var savedPath = fileCreator.saveAsBuFile(pollingPlace, bulletin, fileNameSuffix);
        log.info(format(FILE_SAVED_LOG, PATH, savedPath.toString()));
    }
}
