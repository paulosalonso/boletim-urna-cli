package com.github.paulosalonso.election.model.bu;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class HistoricoVotoImpresso {

    private final Integer idImpressoraVotos;
    private final Integer idRepositorioVotos;
    private final LocalDateTime dataHoraLigamento;
}
