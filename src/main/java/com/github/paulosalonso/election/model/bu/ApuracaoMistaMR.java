package com.github.paulosalonso.election.model.bu;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApuracaoMistaMR {

    private final TipoApuracao tipoApuracao;
    private final MotivoApuracaoMistaComMR motivoApuracao;
}
