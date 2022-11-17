package com.github.paulosalonso.election.model.bu;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TipoVoto implements EnumComCodigo {
    NOMINAL(1),
    BRANCO(2),
    NULO(3),
    LEGENDA(4),
    CARGO_SEM_CANDIDATO(5);

    private final int codigo;
}
