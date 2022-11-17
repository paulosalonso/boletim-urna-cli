package com.github.paulosalonso.election.model.bu;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class TotalVotosCargo {

    private final CodigoCargoConsulta codigoCargo;
    private final Integer ordemImpressao;
    private final List<TotalVotosVotavel> votosVotaveis;
}
