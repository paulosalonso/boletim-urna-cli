package com.github.paulosalonso.election.model.bu;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MotivoApuracaoMistaComMR implements EnumComCodigo {
    NAO_OBTEVE_EXITO_CONTINGENCIA(1),
    INDISPONIBILIDADE_URNA_CONTINGENCIA(2),
    INDISPONIBILIDADE_FLASH_CONTINGENCIA(3),
    PROBLEMA_ENERGIA_ELETRICA(4),
    NAO_FOI_POSSIVEL_TROCAR_URNA(5),
    NAO_FOI_SOLICITADA_TROCA_URNA(6),
    OUTROS(99);

    private final int codigo;
}
