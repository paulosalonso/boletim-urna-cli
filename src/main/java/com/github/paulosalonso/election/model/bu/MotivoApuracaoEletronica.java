package com.github.paulosalonso.election.model.bu;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MotivoApuracaoEletronica implements EnumComCodigo {
    NAO_FOI_POSSIVEL_RECUPERAR_RESULTADO(1),
    URNA_NAO_CHEGOU_MIDIA_DEFEITUOSA(2),
    URNA_NAO_CHEGOU_MIDIA_EXTRAVIADA(3),
    OUTROS(99);

    private final int codigo;
}
