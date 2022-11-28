package com.github.paulosalonso.election.input.cli.subcommands;

import com.github.paulosalonso.election.service.CityToConsoleService;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "buscar-cidade", description = "Exibe os códigos das cidades no console")
public class CityToConsole implements Runnable {

    @Option(names = "--busca")
    private String search;

    @Override
    public void run() {
        CityToConsoleService.printCities(search);
    }
}
