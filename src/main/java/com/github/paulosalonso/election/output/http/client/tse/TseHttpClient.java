package com.github.paulosalonso.election.output.http.client.tse;

import com.github.paulosalonso.election.output.http.HttpResponseBodyMapper;
import com.github.paulosalonso.election.output.http.HttpResponseStatusValidator;
import com.github.paulosalonso.election.output.http.client.tse.model.PollingPlace;
import com.github.paulosalonso.election.output.http.client.tse.model.State;
import com.github.paulosalonso.election.output.http.client.tse.model.TseDefaultApiResponse;
import com.github.paulosalonso.election.output.http.client.tse.model.UnrInfo;
import com.github.paulosalonso.election.output.http.retry.HttpRetryExecutor;
import com.github.paulosalonso.election.tools.text.MessageFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.github.paulosalonso.election.tools.text.MessageFormatter.format;

@Slf4j
@RequiredArgsConstructor
public class TseHttpClient {

    private static final String STATE = "state";
    private static final String CITY = "city";
    private static final String ZONE = "zone";
    private static final String SECTION = "section";
    private static final String ERROR = "error";
    private static final String HASH = "hash";
    private static final String FILE_NAME = "fileName";
    private static final String SPENT_TIME = "spentTime";
    private static final String STATUS = "status";

    private static final String TSE_BASE_URL = "https://resultados.tse.jus.br/oficial/ele2022";

    private static final String GET_CITIES_PATH_PATTERN = TSE_BASE_URL + "/545/config/mun-e000545-cm.json";
    private static final String GET_SECTIONS_BY_STATE_PATH_PATTERN = TSE_BASE_URL + "/arquivo-urna/407/config/${state}/${state}-p000407-cs.json";
    private static final String GET_URN_INFO_PATH_PATTERN = TSE_BASE_URL + "/arquivo-urna/407/dados/${state}/${city}/${zone}/${section}/${fileName}";
    private static final String GET_BULLETIN_PATH_PATTERN = TSE_BASE_URL + "/arquivo-urna/407/dados/${state}/${city}/${zone}/${section}/${hash}/${fileName}";

    private static final String URN_INFO_FILE_NAME_PATTERN = "p000407-${state}-m${city}-z${zone}-s${section}-aux.json";

    private static final String GETTING_CITIES_MESSAGE = "Getting cities";
    private static final String GETTING_CITIES_ERROR_MESSAGE = "Error getting cities. Original message: ${error}";
    private static final String GETTING_SECTIONS_MESSAGE = "Getting sections from state of ${state}";
    private static final String GETTING_FILE_MESSAGE = "Getting file from path /${state}/${city}/${zone}/${section}/${hash}/${fileName}";
    private static final String FILE_GOT_MESSAGE = "File was gotten in ${spentTime} millis";
    private static final String SECTION_ERROR_MESSAGE = "Error getting sections from state of ${state}. Original message: ${error}";
    private static final String SECTIONS_NOT_FOUND_MESSAGE = "Sections of state of ${state} were not found";
    private static final String URN_INFO_ERROR_MESSAGE = "Error getting Unr Info of section ${state}/${city}/${zone}/${section}. Original message: ${error}";
    private static final String BULLETIN_ERROR_MESSAGE = "Error getting Urn Bulletin of section ${state}/${city}/${zone}/${section}. Original message: ${error}";
    private static final String HASH_IGNORED_MESSAGE = "Hash for /${state}/${city}/${zone}/${section} ignored because status is not 'TOTALIZADO'";

    private static final String NOT_INSTALLED_URN = "N??o instalada";
    private static final String TOTALIZADO = "Totalizado";

    private final int timeout;
    private final int requestInterval;
    private final Semaphore semaphore;
    private final HttpRetryExecutor httpRetryExecutor;

    public List<State> getCities() {
        waitForSemaphoreToOpen();

        log.info(GETTING_CITIES_MESSAGE);

        try {
            final var httpRequest = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(GET_CITIES_PATH_PATTERN))
                    .timeout(Duration.ofSeconds(timeout))
                    .build();

            final var response = httpRetryExecutor.execute(httpRequest, BodyHandlers.ofInputStream());

            if (!HttpResponseStatusValidator.is2xx(response)) {
                final var errorMessage = HttpResponseBodyMapper.toString(response);
                final var message = MessageFormatter.format(GETTING_CITIES_ERROR_MESSAGE, ERROR, errorMessage);

                throw new TseHttpClientException(message);
            }

            final var body = HttpResponseBodyMapper.toObject(response, TseDefaultApiResponse.class);

            return body.getStates();
        } finally {
            closeSemaphore();
        }
    }

    public State getSectionsByState(String state) {
        waitForSemaphoreToOpen();

        log.info(format(GETTING_SECTIONS_MESSAGE, STATE, state));

        final var uri = format(GET_SECTIONS_BY_STATE_PATH_PATTERN, STATE, state.toLowerCase());
        final var httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri))
                .timeout(Duration.ofSeconds(timeout))
                .build();

        try {
            final var response = httpRetryExecutor.execute(httpRequest, BodyHandlers.ofInputStream());

            if (!HttpResponseStatusValidator.is2xx(response)) {
                final var errorMessage = HttpResponseBodyMapper.toString(response);
                final var message = MessageFormatter.format(SECTION_ERROR_MESSAGE, STATE, state, ERROR, errorMessage);

                throw new TseHttpClientException(message);
            }

            final var body = HttpResponseBodyMapper.toObject(response, TseDefaultApiResponse.class);

            return body.getStates().stream()
                    .findFirst()
                    .orElseThrow(() -> new TseHttpClientException(format(SECTIONS_NOT_FOUND_MESSAGE, STATE, state)));
        } catch (Exception e) {
            throw new TseHttpClientException(e);
        } finally {
            closeSemaphore();
        }
    }

    public List<InputStream> getBulletins(PollingPlace pollingPlace) {
        final var init = System.currentTimeMillis();

        final var urnInfo = getUrnInfo(pollingPlace);

        if (urnInfo.getStatus().equals(NOT_INSTALLED_URN)) {
            return Collections.emptyList();
        }

        final var bulletins = new ArrayList<InputStream>();

        for (var hash : urnInfo.getHashes()) {
            if (!hash.getStatus().equals(TOTALIZADO)) {
                log.warn(format(
                        HASH_IGNORED_MESSAGE,
                        STATE, pollingPlace.getState(),
                        CITY, pollingPlace.getCityCode(),
                        ZONE, pollingPlace.getZone(),
                        SECTION, pollingPlace.getSection(),
                        STATUS, hash.getStatus()));
                continue;
            }

            waitForSemaphoreToOpen();

            log.info(format(GETTING_FILE_MESSAGE,
                    STATE, pollingPlace.getState().toLowerCase(),
                    CITY, pollingPlace.getCityCode(),
                    ZONE, pollingPlace.getZone(),
                    SECTION, pollingPlace.getSection(),
                    HASH, hash.getHash(),
                    FILE_NAME, hash.getBulletinFileName()));

            var uri = getBulletinUri(pollingPlace, hash.getHash(), hash.getBulletinFileName());

            final var httpRequest = HttpRequest.newBuilder()
                    .GET()
                    .uri(uri)
                    .timeout(Duration.ofSeconds(timeout))
                    .build();

            try {
                final var response = httpRetryExecutor.execute(httpRequest, BodyHandlers.ofInputStream());

                if (!HttpResponseStatusValidator.is2xx(response)) {
                    final var errorMessage = HttpResponseBodyMapper.toString(response);

                    final var message = format(BULLETIN_ERROR_MESSAGE,
                            STATE, pollingPlace.getState(),
                            CITY, pollingPlace.getCityCode(),
                            ZONE, pollingPlace.getZone(),
                            SECTION, pollingPlace.getSection(),
                            ERROR, errorMessage);

                    throw new TseHttpClientException(message);
                }

                bulletins.add(response.body());
            } catch (Exception e) {
                throw new TseHttpClientException(e);
            } finally {
                closeSemaphore();
            }
        }

        Long spentTime = System.currentTimeMillis() - init;

        log.info(format(FILE_GOT_MESSAGE, SPENT_TIME, spentTime.toString()));

        return bulletins;
    }

    private UnrInfo getUrnInfo(PollingPlace pollingPlace) {
        waitForSemaphoreToOpen();

        final var uri = getUrnInfoUri(pollingPlace);
        final var httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .timeout(Duration.ofSeconds(timeout))
                .build();

        try {
            final var response = httpRetryExecutor.execute(httpRequest, BodyHandlers.ofInputStream());

            if (!HttpResponseStatusValidator.is2xx(response)) {
                final var errorMessage = HttpResponseBodyMapper.toString(response);

                final var message = format(URN_INFO_ERROR_MESSAGE,
                        STATE, pollingPlace.getState(),
                        CITY, pollingPlace.getCityCode(),
                        ZONE, pollingPlace.getZone(),
                        SECTION, pollingPlace.getSection(),
                        ERROR, errorMessage);

                throw new TseHttpClientException(message);
            }

            return HttpResponseBodyMapper.toObject(response, UnrInfo.class);
        } catch (Exception e) {
            throw new TseHttpClientException(e);
        } finally {
            closeSemaphore();
        }
    }

    private URI getUrnInfoUri(PollingPlace pollingPlace) {
        final var uri =  format(GET_URN_INFO_PATH_PATTERN,
                STATE, pollingPlace.getState().toLowerCase(),
                CITY, pollingPlace.getCityCode(),
                ZONE, pollingPlace.getZone(),
                SECTION, pollingPlace.getSection(),
                FILE_NAME, getUrnInfoFileName(pollingPlace));

        return URI.create(uri);
    }

    private String getUrnInfoFileName(PollingPlace pollingPlace) {
        return format(URN_INFO_FILE_NAME_PATTERN,
                STATE, pollingPlace.getState().toLowerCase(),
                CITY, pollingPlace.getCityCode(),
                ZONE, pollingPlace.getZone(),
                SECTION, pollingPlace.getSection());
    }

    private URI getBulletinUri(PollingPlace pollingPlace, String hash, String fileName) {
        final var uri = format(GET_BULLETIN_PATH_PATTERN,
                STATE, pollingPlace.getState().toLowerCase(),
                CITY, pollingPlace.getCityCode(),
                ZONE, pollingPlace.getZone(),
                SECTION, pollingPlace.getSection(),
                HASH, hash,
                FILE_NAME, fileName);

        return URI.create(uri);
    }

    private void waitForSemaphoreToOpen() {
        while(semaphore.isClosed()) {
            log.debug("Waiting for semaphore");
        }
    }

    private void closeSemaphore() {
        semaphore.close(requestInterval);
    }
}
