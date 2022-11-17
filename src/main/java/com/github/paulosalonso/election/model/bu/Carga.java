package com.github.paulosalonso.election.model.bu;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class Carga {

    private final String numeroInternoUrna;
    private final String numeroSerieFC;
    private final LocalDateTime dataHoraCarga;
    private final String codigoCarga;
}
