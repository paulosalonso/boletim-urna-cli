package com.github.paulosalonso.election.model.bu;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ResultadoVotacaoPorEleicao {

    private final Integer idEleicao;
    private final Integer qtdEleitoresAptos;
    private final List<ResultadoVotacao> resultadosVotacao;
}
