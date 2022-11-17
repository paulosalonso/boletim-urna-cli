package com.github.paulosalonso.election.model.bu;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CargoConstitucional implements EnumComCodigo {

    PRESIDENTE(1),
    VICE_PRESIDENTE(2),
    GOVERNADOR(3),
    VICE_GOVERNADOR(4),
    SENADOR(5),
    DEPUTADO_FEDERAL(6),
    DEPUTADO_ESTADUAL(7),
    DEPUTADO_DISTRITAL(8),
    PRIMEIRO_SUPLENTE_SENADOR(9),
    SEGUNDO_SUPLENTE_SENADOR(10),
    PREFEITO(11),
    VICE_PREFEITO(12),
    VEREADOR(13);

    private final int codigo;
}
