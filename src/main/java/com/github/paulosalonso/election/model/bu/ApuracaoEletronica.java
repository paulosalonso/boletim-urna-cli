package com.github.paulosalonso.election.model.bu;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApuracaoEletronica {

    private final TipoApuracao tipoApuracao;
    private final MotivoApuracaoEletronica motivoApuracao;
}
