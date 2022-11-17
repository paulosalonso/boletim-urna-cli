package com.github.paulosalonso.election.model.bu;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TipoUrna implements EnumComCodigo {
    SECAO(1),
    CONTINGENCIA(3),
    RESERVA_SECAO(4),
    RESERVA_ENCERRANDO_SECAO(6);

    private final int codigo;
}
