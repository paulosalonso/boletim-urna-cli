package com.github.paulosalonso.election.model.bu;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IdentificacaoSecaoEleitoral {

    private final String municipio;
    private final String zona;
    private final String secao;
    private final String local;
}
