package com.github.paulosalonso.election.model.bu;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TipoApuracaoSA {

    private final ApuracaoMistaMR apuracaoMistaMR;
    private final ApuracaoMistaBUAE apuracaoMistaBUAE;
    private final ApuracaoTotalmenteManualDigitacaoAE apuracaoTotalmenteManual;
    private final ApuracaoEletronica apuracaoEletronica;
}
