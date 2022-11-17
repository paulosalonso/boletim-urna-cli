package com.github.paulosalonso.election.model.bu;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TotalVotosVotavel {

    private final TipoVoto tipoVoto;
    private final Integer quantidadeVotos;
    private final IdentificacaoVotavel identificacaoVotavel;
    private final String assinatura;
}
