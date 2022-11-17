package com.github.paulosalonso.election.model.bu;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IdEleitoral {

    private final Integer idProcessoEleitoral;
    private final Integer idPleito;
    private final Integer idEleicao;
}
