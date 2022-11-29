package com.github.paulosalonso.election.input.cli.subcommands;

import com.github.paulosalonso.election.configuration.Configuration;
import com.github.paulosalonso.election.input.cli.converter.CityOutputTypeConverter;
import com.github.paulosalonso.election.model.CityOutputType;
import com.github.paulosalonso.election.service.CityService;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "municipio", description = "Busca de cidades na API do TSE", resourceBundle = "i18n")
public class CityCLI implements Runnable {

    private static final String WEBHOOK_URL_NOT_PASSED_MESSAGE = "To send a city to a webhook it is mandatory to pass the parameter --url";

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
        if (CityOutputType.WEBHOOK.equals(outputType)) {
            if (url == null) {
                throw new IllegalArgumentException(WEBHOOK_URL_NOT_PASSED_MESSAGE);
            }

            Configuration.setWebHookUri(url);
            Configuration.setWebHookTimeout(webhookTimeoutInSeconds);
        }

        if (outputType.equals(CityOutputType.WEBHOOK)) {
            CityService.sendCitiesToWebHook(search);
        } else {
            CityService.printCities(search);
        }
    }
}
