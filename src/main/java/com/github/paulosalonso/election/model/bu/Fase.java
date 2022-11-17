package com.github.paulosalonso.election.model.bu;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Fase implements EnumComCodigo {
    SIMULADO(1),
    OFICIAL(2),
    TREINAMENTO(3);

    private final int codigo;
}
