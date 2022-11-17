package com.github.paulosalonso.election.model.bu;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IdentificacaoContingencia {

    private final String municipio;
    private final String zona;
}
