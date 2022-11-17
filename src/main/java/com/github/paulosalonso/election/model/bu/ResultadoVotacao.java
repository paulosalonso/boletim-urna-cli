package com.github.paulosalonso.election.model.bu;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ResultadoVotacao {

    private final TipoCargoConsulta tipoCargo;
    private final Integer qtdComparecimento;
    private final List<TotalVotosCargo> totaisVotosCargo;
}
