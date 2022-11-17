package com.github.paulosalonso.election.model.bu;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class BoletimUrna {

    private final Cabecalho cabecalho;
    private final Fase fase;
    private final Urna urna;
    private final IdentificacaoSecaoEleitoral identificacaoSessao;
    private final LocalDateTime dataHoraEmissao;
    private final DadosSecaoSA dadosSessaoSA;
    private final Integer qtdEleitoresLibCodigo;
    private final Integer qtdEleitoresCompBiometrico;
    private final List<ResultadoVotacaoPorEleicao> resultadosVotacaoPorEleicao;
    private final List<CorrespondenciaResultado> historicoCorrespondencias;
    private final List<HistoricoVotoImpresso> historicoVotoImpresso;
    private final String chaveAssinaturaVotosVotavel;
}
