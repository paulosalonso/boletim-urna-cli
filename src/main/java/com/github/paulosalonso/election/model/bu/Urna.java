package com.github.paulosalonso.election.model.bu;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Urna {

    private final TipoUrna tipoUrna;
    private final String versaoVotacao;
    private final CorrespondenciaResultado correspondenciaResultado;
    private final TipoArquivo tipoArquivo;
    private final String numeroSerieFV;
    private final TipoApuracaoSA motivoUtilizacaoSA;
}
