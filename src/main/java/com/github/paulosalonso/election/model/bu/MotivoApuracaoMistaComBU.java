package com.github.paulosalonso.election.model.bu;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MotivoApuracaoMistaComBU implements EnumComCodigo {
    URNA_DATA_HORA_INCORRETA(1),
    URNA_COM_DEFEITO(2),
    URNA_OUTRA_SECAO(3),
    URNA_PREPARADA_INCORRETAMENTE(4),
    URNA_CHEGOU_APOS_INICIO_VOTACAO(5),
    OUTROS(99);

    private final int codigo;
}
