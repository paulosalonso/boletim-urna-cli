package com.github.paulosalonso.election.model.bu;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IdentificacaoUrna {

    private final IdentificacaoSecaoEleitoral identificacaoSecaoEleitoral;
    private final IdentificacaoContingencia identificacaoContingencia;
}
