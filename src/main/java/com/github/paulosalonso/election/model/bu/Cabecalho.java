package com.github.paulosalonso.election.model.bu;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class Cabecalho {

    private final LocalDateTime dataGeracao;
    private final IdEleitoral idEleitoral;
}
