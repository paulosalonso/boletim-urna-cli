package com.github.paulosalonso.election.input;

import com.github.paulosalonso.election.configuration.Configuration;
import com.github.paulosalonso.election.model.OutputType;
import com.github.paulosalonso.election.service.BulletinToFileService;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

@Command(name = "boletim-arquivo", description = "Grava os boletins de urna em disco")
public class BulletinToFile implements Runnable {

    @ParentCommand
    private ElectionsCLI parent;

    @Option(names = "--diretorio")
    private String rootDirectory;

    @Option(names = "--json", defaultValue = "false")
    private boolean json = false;

    @Override
    public void run() {
        Configuration.setRootDirectory(rootDirectory);

        var outputType = json ? OutputType.JSON : OutputType.BU;

        BulletinToFileService.save(parent.getState(), parent.getCity(),
                parent.getZone(), parent.getSection(), parent.getScopeToContinue(), outputType);
    }
}
