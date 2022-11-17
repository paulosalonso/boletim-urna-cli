package com.github.paulosalonso.election.model.bu;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DadosSA {

    private final Integer juntaApuradora;
    private final Integer turmaApuradora;
    private final String numeroInternoUrnaOrigem;
}
