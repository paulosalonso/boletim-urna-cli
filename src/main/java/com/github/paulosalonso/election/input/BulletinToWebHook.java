package com.github.paulosalonso.election.input;

import com.github.paulosalonso.election.configuration.Configuration;
import com.github.paulosalonso.election.service.BulletinToWebHookService;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

@Command(name = "boletim-webhook", description = "Envia os boletins de urna para um endpoint HTTP")
public class BulletinToWebHook implements Runnable {

    @ParentCommand
    private ElectionsCLI parent;

    @Option(names = "--url")
    private String url;

    @Option(names = "--timeout", defaultValue = "10")
    private Integer webhookTimeoutInSeconds = 10;

    @Override
    public void run() {
        Configuration.setWebHookUri(url);
        Configuration.setWebHookTimeout(webhookTimeoutInSeconds);

        BulletinToWebHookService.sendToWebHook(parent.getState(),
                parent.getCity(), parent.getZone(), parent.getSection(), parent.getScopeToContinue());
    }
}
