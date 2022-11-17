package com.github.paulosalonso.election.model.bu;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TipoCargoConsulta implements EnumComCodigo {
    MAJORITARIO(1),
    PROPORCIONAL(2),
    CONSULTA(3);

    private final int codigo;
}
