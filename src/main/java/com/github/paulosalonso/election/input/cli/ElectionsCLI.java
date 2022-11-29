package com.github.paulosalonso.election.input.cli;

import com.github.paulosalonso.election.input.cli.converter.ScopeConverter;
import com.github.paulosalonso.election.input.cli.subcommands.BulletinToFileCLI;
import com.github.paulosalonso.election.input.cli.subcommands.BulletinToWebHookCLI;
import com.github.paulosalonso.election.input.cli.subcommands.CityCLI;
import com.github.paulosalonso.election.model.Scope;
import lombok.Getter;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Getter
@Command(name = "eleicao",
        version = "1.0.0",
        description = "Obtém informações sobre as eleições presidenciais 2022 diretamente da api do TSE",
        resourceBundle = "i18n",
        subcommands = { BulletinToFileCLI.class, BulletinToWebHookCLI.class, CityCLI.class})
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
        new CommandLine(new ElectionsCLI()).execute(args);
    }
}
