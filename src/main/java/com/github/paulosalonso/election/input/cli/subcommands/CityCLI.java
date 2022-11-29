package com.github.paulosalonso.election.input.cli.subcommands;

import com.github.paulosalonso.election.input.cli.ElectionsCLI;
import com.github.paulosalonso.election.input.cli.converter.CityOutputTypeConverter;
import com.github.paulosalonso.election.model.CityOutputType;
import com.github.paulosalonso.election.output.http.client.tse.Semaphore;
import com.github.paulosalonso.election.output.http.client.tse.TseHttpClient;
import com.github.paulosalonso.election.output.http.client.webhook.WebHookClient;
import com.github.paulosalonso.election.output.http.retry.HttpRetryExecutor;
import com.github.paulosalonso.election.service.CityService;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

import java.net.http.HttpClient;

@Command(name = "municipio", description = "Busca de cidades na API do TSE", resourceBundle = "i18n")
public class CityCLI implements Runnable {

    private static final String WEBHOOK_URL_NOT_PASSED_MESSAGE = "To send a city to a webhook it is mandatory to pass the parameter --url";

    @ParentCommand
    private ElectionsCLI parent;

    @Option(names = "--busca")
    private String search;

    @Option(names = "--saida", defaultValue = "console", converter = CityOutputTypeConverter.class)
    private CityOutputType outputType;

    @Option(names = "--url")
    private String url;

    @Option(names = "--timeout", defaultValue = "10")
    private Integer webhookTimeoutInSeconds = 10;

    @Override
    public void run() {
        final var httpRetryExecutor = new HttpRetryExecutor(
                parent.getRetryMaxAttempts(), parent.getRetryIntervalInSeconds(), HttpClient.newHttpClient());
        final var tseHttpClient = new TseHttpClient(parent.getTseTimeoutInSeconds(),
                parent.getRequestIntervalInMillis(), new Semaphore(), httpRetryExecutor);

        WebHookClient webHookClient = null;

        if (CityOutputType.WEBHOOK.equals(outputType)) {
            if (url == null) {
                throw new IllegalArgumentException(WEBHOOK_URL_NOT_PASSED_MESSAGE);
            }

            webHookClient = new WebHookClient(url, webhookTimeoutInSeconds, HttpClient.newHttpClient());
        }

        final var cityService = new CityService(tseHttpClient, webHookClient);

        if (outputType.equals(CityOutputType.WEBHOOK)) {
            cityService.sendCitiesToWebHook(search);
        } else {
            cityService.printCities(search);
        }
    }
}
