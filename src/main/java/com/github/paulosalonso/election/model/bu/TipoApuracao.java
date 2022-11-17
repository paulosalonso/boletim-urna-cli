package com.github.paulosalonso.election.model.bu;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TipoApuracao implements EnumComCodigo {
    TOTALMENTE_MANUAL(1),
    TOTALMENTE_ELETRONICA(2),
    MISTA_BU(3),
    MISTA_MR(4);

    private final int codigo;
}
