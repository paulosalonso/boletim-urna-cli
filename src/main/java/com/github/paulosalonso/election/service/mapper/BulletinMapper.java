package com.github.paulosalonso.election.service.mapper;

import com.beanit.jasn1.ber.types.BerInteger;
import com.beanit.jasn1.ber.types.BerOctetString;
import com.beanit.jasn1.ber.types.string.BerGeneralString;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.paulosalonso.election.model.bu.*;
import com.github.paulosalonso.election.tse.asn1.modulobu.CabecalhoEntidade;
import com.github.paulosalonso.election.tse.asn1.modulobu.EntidadeBoletimUrna;
import com.github.paulosalonso.election.tse.asn1.modulobu.EntidadeEnvelopeGenerico;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BulletinMapper {

    private static final String ERROR_MESSAGE = "Error mapping bulletin";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");

    private static final ObjectMapper MAPPER;

    static {
        MAPPER = new ObjectMapper();
        MAPPER.registerModule(new JavaTimeModule());
        MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public static EntidadeBoletimUrna toAsn1Object(InputStream inputStream) {
        try {
            final var envelopeGenerico = new EntidadeEnvelopeGenerico();
            envelopeGenerico.decode(inputStream);

            final var boletimUrnaInputStream = new ByteArrayInputStream(envelopeGenerico.getConteudo().value);

            final var boletimUrna = new EntidadeBoletimUrna();
            boletimUrna.decode(boletimUrnaInputStream);

            return boletimUrna;
        } catch (Exception e) {
            throw new BulletinMappinggException(ERROR_MESSAGE, e);
        }
    }

    public static String toJson(BoletimUrna boletimUrna) {
        try {
            return MAPPER.writeValueAsString(boletimUrna);
        } catch (JsonProcessingException e) {
            throw new BulletinMappinggException(ERROR_MESSAGE, e);
        }
    }

    public static BoletimUrna toModel(EntidadeBoletimUrna asn1Object) {
        final var builder = BoletimUrna.builder();

        builder
                .cabecalho(buildCabecalho(asn1Object.getCabecalho()))
                .fase(EnumComCodigo.getByCodigo(Fase.values(), toInt(asn1Object.getFase())))
                .urna(buildUrna(asn1Object))
                .identificacaoSessao(buildIdendificacaoSessao(asn1Object))
                .dataHoraEmissao(toLocalDateTime(asn1Object.getDataHoraEmissao()))
                .dadosSessaoSA(buildDadosSessaoSA(asn1Object.getDadosSecaoSA()))
                .qtdEleitoresLibCodigo(toInt(asn1Object.getQtdEleitoresLibCodigo()))
                .qtdEleitoresCompBiometrico(toInt(asn1Object.getQtdEleitoresCompBiometrico()))
                .resultadosVotacaoPorEleicao(asn1Object.getResultadosVotacaoPorEleicao().getResultadoVotacaoPorEleicao().stream()
                        .map(BulletinMapper::buildResultadoVotacaoPorEleicao)
                        .toList())
                .chaveAssinaturaVotosVotavel(toString(asn1Object.getChaveAssinaturaVotosVotavel()));

        if (asn1Object.getHistoricoCorrespondencias() != null) {
            builder.historicoCorrespondencias(asn1Object.getHistoricoCorrespondencias().getCorrespondenciaResultado().stream()
                    .map(BulletinMapper::buildCorrespondenciaResultado)
                    .toList());
        }

        if (asn1Object.getHistoricoVotoImpresso() != null) {
            builder.historicoVotoImpresso(asn1Object.getHistoricoVotoImpresso().getHistoricoVotoImpresso().stream()
                    .map(BulletinMapper::buildHistoricoVotoImpresso)
                    .toList());
        }

        return builder.build();
    }

    private static Cabecalho buildCabecalho(CabecalhoEntidade cabecalho) {
        return Cabecalho.builder()
                .dataGeracao(toLocalDateTime(cabecalho.getDataGeracao()))
                .idEleitoral(IdEleitoral.builder()
                        .idProcessoEleitoral(toInt(cabecalho.getIdEleitoral().getIdProcessoEleitoral()))
                        .idPleito(toInt(cabecalho.getIdEleitoral().getIdPleito()))
                        .idEleicao(toInt(cabecalho.getIdEleitoral().getIdEleicao()))
                        .build())
                .build();
    }

    private static Urna buildUrna(EntidadeBoletimUrna asn1Object) {
        return Urna.builder()
                .tipoUrna(EnumComCodigo.getByCodigo(TipoUrna.values(), toInt(asn1Object.getUrna().getTipoUrna())))
                .versaoVotacao(toString(asn1Object.getUrna().getVersaoVotacao()))
                .correspondenciaResultado(buildCorrespondenciaResultado(asn1Object.getUrna().getCorrespondenciaResultado()))
                .tipoArquivo(EnumComCodigo.getByCodigo(TipoArquivo.values(), toInt(asn1Object.getUrna().getTipoArquivo())))
                .numeroSerieFV(toString(asn1Object.getUrna().getNumeroSerieFV()))
                .motivoUtilizacaoSA(buildTipoApuracaoSA(asn1Object.getUrna()))
                .build();
    }

    private static TipoApuracaoSA buildTipoApuracaoSA(com.github.paulosalonso.election.tse.asn1.modulobu.Urna urna) {
        if (urna.getMotivoUtilizacaoSA() == null) {
            return null;
        }

        final var builder = TipoApuracaoSA.builder();

        if (urna.getMotivoUtilizacaoSA().getApuracaoMistaMR() != null) {
            builder.apuracaoMistaMR(ApuracaoMistaMR.builder()
                    .tipoApuracao(EnumComCodigo.getByCodigo(TipoApuracao.values(), toInt(urna.getMotivoUtilizacaoSA().getApuracaoMistaMR().getTipoApuracao())))
                    .motivoApuracao(EnumComCodigo.getByCodigo(MotivoApuracaoMistaComMR.values(), toInt(urna.getMotivoUtilizacaoSA().getApuracaoMistaMR().getMotivoApuracao())))
                    .build());
        }

        if (urna.getMotivoUtilizacaoSA().getApuracaoMistaBUAE() != null) {
            builder.apuracaoMistaBUAE(ApuracaoMistaBUAE.builder()
                    .tipoApuracao(EnumComCodigo.getByCodigo(TipoApuracao.values(), toInt(urna.getMotivoUtilizacaoSA().getApuracaoMistaBUAE().getTipoapuracao())))
                    .motivoApuracao(EnumComCodigo.getByCodigo(MotivoApuracaoMistaComBU.values(), toInt(urna.getMotivoUtilizacaoSA().getApuracaoMistaBUAE().getMotivoApuracao())))
                    .build());
        }

        if (urna.getMotivoUtilizacaoSA().getApuracaoTotalmenteManual() != null) {
            builder.apuracaoTotalmenteManual(ApuracaoTotalmenteManualDigitacaoAE.builder()
                    .tipoApuracao(EnumComCodigo.getByCodigo(TipoApuracao.values(), toInt(urna.getMotivoUtilizacaoSA().getApuracaoTotalmenteManual().getTipoapuracao())))
                    .motivoApuracao(EnumComCodigo.getByCodigo(MotivoApuracaoManual.values(), toInt(urna.getMotivoUtilizacaoSA().getApuracaoTotalmenteManual().getMotivoApuracao())))
                    .build());
        }

        if (urna.getMotivoUtilizacaoSA().getApuracaoEletronica() != null) {
            builder.apuracaoEletronica(ApuracaoEletronica.builder()
                    .tipoApuracao(EnumComCodigo.getByCodigo(TipoApuracao.values(), toInt(urna.getMotivoUtilizacaoSA().getApuracaoEletronica().getTipoapuracao())))
                    .motivoApuracao(EnumComCodigo.getByCodigo(MotivoApuracaoEletronica.values(), toInt(urna.getMotivoUtilizacaoSA().getApuracaoEletronica().getMotivoApuracao())))
                    .build());
        }

        return builder.build();
    }

    private static IdentificacaoSecaoEleitoral buildIdendificacaoSessao(EntidadeBoletimUrna asn1Object) {
        return IdentificacaoSecaoEleitoral.builder()
                .municipio(toString(asn1Object.getIdentificacaoSecao().getMunicipioZona().getMunicipio()))
                .zona(toString(asn1Object.getIdentificacaoSecao().getMunicipioZona().getZona()))
                .secao(toString(asn1Object.getIdentificacaoSecao().getSecao()))
                .local(toString(asn1Object.getIdentificacaoSecao().getLocal()))
                .build();
    }

    private static DadosSecaoSA buildDadosSessaoSA(com.github.paulosalonso.election.tse.asn1.modulobu.DadosSecaoSA dadosSecaoSA) {
        final var builder = DadosSecaoSA.builder();

        builder.dadosSecao(DadosSecao.builder()
                        .dataHoraAbertura(toLocalDateTime(dadosSecaoSA.getDadosSecao().getDataHoraAbertura()))
                        .dataHoraEncerramento(toLocalDateTime(dadosSecaoSA.getDadosSecao().getDataHoraEncerramento()))
                        .dataHoraDesligamentoVotoImpresso(toLocalDateTime(dadosSecaoSA.getDadosSecao().getDataHoraDesligamentoVotoImpresso()))
                        .build());

        if (dadosSecaoSA.getDadosSA() != null) {
            builder.dadosSA(DadosSA.builder()
                    .juntaApuradora(toInt(dadosSecaoSA.getDadosSA().getJuntaApuradora()))
                    .turmaApuradora(toInt(dadosSecaoSA.getDadosSA().getTurmaApuradora()))
                    .numeroInternoUrnaOrigem(toString(dadosSecaoSA.getDadosSA().getNumeroInternoUrnaOrigem()))
                    .build());
        }

        return builder.build();
    }

    private static ResultadoVotacao buildResultadoVotacao(com.github.paulosalonso.election.tse.asn1.modulobu.ResultadoVotacao resultadoVotacao) {
        return ResultadoVotacao.builder()
                .tipoCargo(EnumComCodigo.getByCodigo(TipoCargoConsulta.values(), toInt(resultadoVotacao.getTipoCargo())))
                .qtdComparecimento(toInt(resultadoVotacao.getQtdComparecimento()))
                .totaisVotosCargo(resultadoVotacao.getTotaisVotosCargo().getTotalVotosCargo().stream()
                        .map(totalVotosCargo -> TotalVotosCargo.builder()
                                .codigoCargo(CodigoCargoConsulta.builder()
                                        .cargoConstitucional(EnumComCodigo.getByCodigo(CargoConstitucional.values(), toInt(totalVotosCargo.getCodigoCargo().getCargoConstitucional())))
                                        .numeroCargoConsultaLivre(toInt(totalVotosCargo.getCodigoCargo().getNumeroCargoConsultaLivre()))
                                        .build())
                                .ordemImpressao(toInt(totalVotosCargo.getOrdemImpressao()))
                                .votosVotaveis(totalVotosCargo.getVotosVotaveis().getTotalVotosVotavel().stream()
                                        .map(BulletinMapper::buildTotalVotosVotavel)
                                        .toList())
                                .build())
                        .toList())
                .build();
    }

    private static TotalVotosVotavel buildTotalVotosVotavel(com.github.paulosalonso.election.tse.asn1.modulobu.TotalVotosVotavel totalVotosVotavel) {
        final var builder = TotalVotosVotavel.builder();

        builder.tipoVoto(EnumComCodigo.getByCodigo(TipoVoto.values(), toInt(totalVotosVotavel.getTipoVoto())))
                .quantidadeVotos(toInt(totalVotosVotavel.getQuantidadeVotos()))
                .assinatura(toString(totalVotosVotavel.getAssinatura()));

        if (totalVotosVotavel.getIdentificacaoVotavel() != null) {
            builder.identificacaoVotavel(IdentificacaoVotavel.builder()
                    .partido(toInt(totalVotosVotavel.getIdentificacaoVotavel().getPartido()))
                    .codigo(toInt(totalVotosVotavel.getIdentificacaoVotavel().getCodigo()))
                    .build());
        }

        return builder.build();
    }

    private static ResultadoVotacaoPorEleicao buildResultadoVotacaoPorEleicao(com.github.paulosalonso.election.tse.asn1.modulobu.ResultadoVotacaoPorEleicao resultadoVotacaoPorEleicao) {
        return ResultadoVotacaoPorEleicao.builder()
                .idEleicao(toInt(resultadoVotacaoPorEleicao.getIdEleicao()))
                .qtdEleitoresAptos(toInt(resultadoVotacaoPorEleicao.getQtdEleitoresAptos()))
                .resultadosVotacao(resultadoVotacaoPorEleicao.getResultadosVotacao().getResultadoVotacao().stream()
                        .map(BulletinMapper::buildResultadoVotacao)
                        .toList())
                .build();
    }

    private static CorrespondenciaResultado buildCorrespondenciaResultado(com.github.paulosalonso.election.tse.asn1.modulobu.CorrespondenciaResultado correspondenciaResultado) {
        return CorrespondenciaResultado.builder()
                .identificacaoUrna(buildIdentificacaoUrna(correspondenciaResultado.getIdentificacao()))
                .carga(Carga.builder()
                        .numeroInternoUrna(toString(correspondenciaResultado.getCarga().getNumeroInternoUrna()))
                        .numeroSerieFC(toString(correspondenciaResultado.getCarga().getNumeroSerieFC()))
                        .dataHoraCarga(toLocalDateTime(correspondenciaResultado.getCarga().getDataHoraCarga()))
                        .codigoCarga(toString(correspondenciaResultado.getCarga().getCodigoCarga()))
                        .build())
                .build();
    }

    private static IdentificacaoUrna buildIdentificacaoUrna(com.github.paulosalonso.election.tse.asn1.modulobu.IdentificacaoUrna identificacaoUrna) {
        final var builder = IdentificacaoUrna.builder();

        if (identificacaoUrna.getIdentificacaoSecaoEleitoral() != null) {
            builder.identificacaoSecaoEleitoral(IdentificacaoSecaoEleitoral.builder()
                    .municipio(toString(identificacaoUrna.getIdentificacaoSecaoEleitoral().getMunicipioZona().getMunicipio()))
                    .zona(toString(identificacaoUrna.getIdentificacaoSecaoEleitoral().getMunicipioZona().getZona()))
                    .secao(toString(identificacaoUrna.getIdentificacaoSecaoEleitoral().getSecao()))
                    .local(toString(identificacaoUrna.getIdentificacaoSecaoEleitoral().getLocal()))
                    .build());
        }

        if (identificacaoUrna.getIdentificacaoContingencia() != null) {
            builder.identificacaoContingencia(IdentificacaoContingencia.builder()
                .municipio(identificacaoUrna.getIdentificacaoContingencia().getMunicipioZona().getMunicipio().value.toString())
                .zona(identificacaoUrna.getIdentificacaoContingencia().getMunicipioZona().getZona().value.toString())
                .build());
        }

        return builder.build();
    }

    private static HistoricoVotoImpresso buildHistoricoVotoImpresso(com.github.paulosalonso.election.tse.asn1.modulobu.HistoricoVotoImpresso historicoVotoImpresso) {
        return HistoricoVotoImpresso.builder()
                .idImpressoraVotos(toInt(historicoVotoImpresso.getIdImpressoraVotos()))
                .idRepositorioVotos(toInt(historicoVotoImpresso.getIdRepositorioVotos()))
                .dataHoraLigamento((toLocalDateTime(historicoVotoImpresso.getDataHoraLigamento())))
                .build();
    }

    private static String toString(BerGeneralString value) {
        if (value == null) {
            return null;
        }

        return new String(value.value, StandardCharsets.UTF_8);
    }

    private static String toString(BerOctetString value) {
        if (value == null) {
            return null;
        }

        return value.toString();
    }

    private static String toString(BerInteger value) {
        if (value == null) {
            return null;
        }

        return value.value.toString();
    }

    private static Integer toInt(BerInteger value) {
        if (value == null) {
            return null;
        }

        return value.value.intValue();
    }

    private static LocalDateTime toLocalDateTime(BerGeneralString value) {
        if (value == null) {
            return null;
        }

        return LocalDateTime.from(DATE_TIME_FORMATTER.parse(toString(value)));
    }
}
