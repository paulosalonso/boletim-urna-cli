package com.github.paulosalonso.election.input.cli;

import com.github.paulosalonso.election.configuration.Configuration;
import com.github.paulosalonso.election.input.cli.converter.ScopeConverter;
import com.github.paulosalonso.election.input.cli.subcommands.BulletinToFile;
import com.github.paulosalonso.election.input.cli.subcommands.BulletinToWebHook;
import com.github.paulosalonso.election.input.cli.subcommands.CityToConsole;
import com.github.paulosalonso.election.model.Scope;
import lombok.Getter;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParseResult;
import picocli.CommandLine.RunLast;

@Getter
@Command(name = "eleicao",
        version = "1.0.0",
        description = "Obtém informações sobre as eleições presidenciais 2022 diretamente da api do TSE",
        resourceBundle = "i18n",
        subcommands = { BulletinToFile.class, BulletinToWebHook.class, CityToConsole.class})
public class ElectionsCLI {

    @Option(names = "--estado")
    private String state;

    @Option(names = "--municipio")
    private String city;

    @Option(names = "--zona")
    private String zone;

    @Option(names = "--secao")
    private String section;

    @Option(names = "--continuar", converter = ScopeConverter.class)
    private Scope scopeToContinue;

    @Option(names = "--tse-timeout", defaultValue = "10")
    private Integer tseTimeoutInSeconds = 10;

    @Option(names = "--tse-tentativas", defaultValue = "3")
    private Integer retryMaxAttempts = 3;

    @Option(names = "--tse-intervalo-retentativa", defaultValue = "10")
    private Integer retryIntervalInSeconds = 10;

    @Option(names = "--tse-intervalo-requisicao", defaultValue = "250")
    private Integer requestIntervalInMillis = 250;

    public static void main(String[] args) {
        var cli = new ElectionsCLI();
        new CommandLine(cli)
                .setExecutionStrategy(cli::setUp)
                .execute(args);
    }

    private int setUp(ParseResult parseResult) {
        final var tseTimeout = parseResult.matchedOptionValue("tse-timeout", this.tseTimeoutInSeconds);
        final var retryMaxAttempts = parseResult.matchedOptionValue("tentativas", this.retryMaxAttempts);
        final var retryInterval = parseResult.matchedOptionValue("intervalo-retentativa", this.retryIntervalInSeconds);
        final var requestInterval = parseResult.matchedOptionValue("intervalo-requisicao", this.requestIntervalInMillis);

        Configuration.setTseTimeout(tseTimeout);
        Configuration.setRetryMaxAttempts(retryMaxAttempts);
        Configuration.setRetryIntervalInSeconds(retryInterval);
        Configuration.setRequestIntervalInMillis(requestInterval);

        return new RunLast().execute(parseResult);
    }
}
