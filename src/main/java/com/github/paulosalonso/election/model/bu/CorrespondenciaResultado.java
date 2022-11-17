package com.github.paulosalonso.election.model.bu;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CorrespondenciaResultado {

    private final IdentificacaoUrna identificacaoUrna;
    private final Carga carga;
}
