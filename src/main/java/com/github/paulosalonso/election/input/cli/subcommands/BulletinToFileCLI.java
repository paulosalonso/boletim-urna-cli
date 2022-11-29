package com.github.paulosalonso.election.input.cli.subcommands;

import com.github.paulosalonso.election.input.cli.ElectionsCLI;
import com.github.paulosalonso.election.model.BulletinOutputType;
import com.github.paulosalonso.election.output.file.FileCreator;
import com.github.paulosalonso.election.output.http.client.tse.Semaphore;
import com.github.paulosalonso.election.output.http.client.tse.TseHttpClient;
import com.github.paulosalonso.election.output.http.retry.HttpRetryExecutor;
import com.github.paulosalonso.election.service.BulletinToFileService;
import com.github.paulosalonso.election.service.PollingPlaceService;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

import java.net.http.HttpClient;

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
        var outputType = json ? BulletinOutputType.JSON : BulletinOutputType.BU;

        final var httpRetryExecutor = new HttpRetryExecutor(
                parent.getRetryMaxAttempts(), parent.getRetryIntervalInSeconds(), HttpClient.newHttpClient());
        final var tseHttpClient = new TseHttpClient(parent.getTseTimeoutInSeconds(),
                parent.getRequestIntervalInMillis(), new Semaphore(), httpRetryExecutor);
        final var pollingPlaceService = new PollingPlaceService(tseHttpClient);
        final var fileCreator = new FileCreator(rootDirectory);
        final var bulletinToFileService = new BulletinToFileService(tseHttpClient, pollingPlaceService, fileCreator);

        bulletinToFileService.save(parent.getState(), parent.getCity(),
                parent.getZone(), parent.getSection(), parent.getScopeToContinue(), outputType);
    }
}
