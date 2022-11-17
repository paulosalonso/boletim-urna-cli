package com.github.paulosalonso.election.model.bu;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MotivoApuracaoManual implements EnumComCodigo {
    URNA_COM_DEFEITO(1),
    URNA_INDISPONIVEL_INICIO(2),
    URNA_OUTRA_SECAO(3),
    OUTROS(99);

    private final int codigo;
}
