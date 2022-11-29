package com.github.paulosalonso.election.input.cli.subcommands;

import com.github.paulosalonso.election.configuration.Configuration;
import com.github.paulosalonso.election.input.cli.ElectionsCLI;
import com.github.paulosalonso.election.model.BulletinOutputType;
import com.github.paulosalonso.election.service.BulletinToFileService;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

@Command(name = "boletim-arquivo", description = "Grava os boletins de urna em disco")
public class BulletinToFileCLI implements Runnable {

    @ParentCommand
    private ElectionsCLI parent;

    @Option(names = "--diretorio")
    private String rootDirectory;

    @Option(names = "--json", defaultValue = "false")
    private boolean json = false;

    @Override
    public void run() {
        Configuration.setRootDirectory(rootDirectory);

        var outputType = json ? BulletinOutputType.JSON : BulletinOutputType.BU;

        BulletinToFileService.save(parent.getState(), parent.getCity(),
                parent.getZone(), parent.getSection(), parent.getScopeToContinue(), outputType);
    }
}
