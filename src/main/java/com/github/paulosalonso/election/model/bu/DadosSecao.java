package com.github.paulosalonso.election.model.bu;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class DadosSecao {

    private final LocalDateTime dataHoraAbertura;
    private final LocalDateTime dataHoraEncerramento;
    private final LocalDateTime dataHoraDesligamentoVotoImpresso;
}
