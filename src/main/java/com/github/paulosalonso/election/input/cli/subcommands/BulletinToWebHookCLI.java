package com.github.paulosalonso.election.input.cli.subcommands;

import com.github.paulosalonso.election.input.cli.ElectionsCLI;
import com.github.paulosalonso.election.output.http.client.tse.Semaphore;
import com.github.paulosalonso.election.output.http.client.tse.TseHttpClient;
import com.github.paulosalonso.election.output.http.client.webhook.WebHookClient;
import com.github.paulosalonso.election.output.http.retry.HttpRetryExecutor;
import com.github.paulosalonso.election.service.BulletinToWebHookService;
import com.github.paulosalonso.election.service.PollingPlaceService;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

import java.net.http.HttpClient;

@Command(name = "boletim-webhook", description = "Envia os boletins de urna para um endpoint HTTP")
public class BulletinToWebHookCLI implements Runnable {

    @ParentCommand
    private ElectionsCLI parent;

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
        final var webHookClient = new WebHookClient(url, webhookTimeoutInSeconds, HttpClient.newHttpClient());
        final var pollingPlaceService = new PollingPlaceService(tseHttpClient);
        final var bulletinToWebHookService =
                new BulletinToWebHookService(tseHttpClient, webHookClient, pollingPlaceService);

        bulletinToWebHookService.sendToWebHook(parent.getState(),
                parent.getCity(), parent.getZone(), parent.getSection(), parent.getScopeToContinue());
    }
}
