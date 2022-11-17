package com.github.paulosalonso.election.input;

import com.github.paulosalonso.election.model.OutputType;
import com.github.paulosalonso.election.model.Scope;
import com.github.paulosalonso.election.output.file.FileCreator;
import com.github.paulosalonso.election.output.http.client.webhook.WebHookClient;
import com.github.paulosalonso.election.service.BulletinToFileService;
import com.github.paulosalonso.election.service.BulletinToWebHookService;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.nio.file.Path;
import java.util.concurrent.Callable;

@Command(name = "eleicao", mixinStandardHelpOptions = true, version = "1.0.0", description = "Faz o download e tradução dos boletins de urna das eleições brasileiras 2022", helpCommand = true)
public class BoletimUrnaCLI implements Callable<Void> {

    @Option(names = "--estado", description = "Sigla do estado", required = true)
    String state;

    @Option(names = "--municipio", description = "Código do município")
    String city;

    @Option(names = "--zona", description = "Zona de votação")
    String zone;

    @Option(names = "--secao", description = "Seção de votação")
    String section;

    @Option(names = "--continuar",
            description = "Indica que o download deve continuar a partir da seção informada, para o escopo informado. Os valores possíveis são: tudo, estado, municipio e zona. Por exemplo, ao informar um local de votação no município de São Paulo, e nesse campo informar município, todos os boletins da cidade de São Paulo, a partir da sessão informada, serão baixados. Só tem efeito se todos os dados do local de votação forem informados.",
            converter = ScopeConverter.class)
    Scope scopeToContinue;

    @Option(names = "--diretorio", description = "Diretorio de saída dos arquivos")
    String rootDirectory;

    @Option(names = "--json", description = "Flag que indica que a saída deve ser no formato JSON, não recebe valor. Se não for informado, o arquivo é salvo em formato binário com a extenção *.bu")
    boolean json;

    @Option(names = "--webhook", description = "Indica uma URL na qual será enviada uma requisição POST com o boletim no formato JSON")
    String webhook;

    @Option(names = "--console", description = "Habilita a impressão do boletim no console")
    boolean console;

    public static void main(String[] args) {
        new CommandLine(new BoletimUrnaCLI()).execute(args);
    }

    @Override
    public Void call() {
        if (rootDirectory != null) {
            FileCreator.setRootPath(Path.of(rootDirectory));

            var outputType = getOutputType();

            if (state != null && city != null && zone != null && section != null) {
                if (scopeToContinue != null) {
                    BulletinToFileService.keepOnSaving(state, city, zone, section, scopeToContinue, outputType);
                } else {
                    BulletinToFileService.saveBySection(state, city, zone, section, outputType);
                }
            } else if (state != null && city != null && zone != null) {
                BulletinToFileService.saveByZone(state, city, zone, outputType);
            } else if (state != null && city != null) {
                BulletinToFileService.saveByCity(state, city, outputType);
            } else if (state != null) {
                BulletinToFileService.saveByState(state, outputType);
            }
        }

        if (webhook != null) {
            WebHookClient.setWebHookUri(webhook);

            if (state != null && city != null && zone != null && section != null) {
                if (scopeToContinue != null) {
                    BulletinToWebHookService.keepOnSendingToWebHook(state, city, zone, section, scopeToContinue);
                } else {
                    BulletinToWebHookService.sendToWebHookBySection(state, city, zone, section);
                }
            } else if (state != null && city != null && zone != null) {
                BulletinToWebHookService.sendToWebHookByZone(state, city, zone);
            } else if (state != null && city != null) {
                BulletinToWebHookService.sendToWebHookByCity(state, city);
            } else if (state != null) {
                BulletinToWebHookService.sendToWebHookByState(state);
            }
        }

        return null;
    }

    private OutputType getOutputType() {
        if (json) {
            return OutputType.JSON;
        }

        return OutputType.BU;
    }
}
