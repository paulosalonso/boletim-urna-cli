package com.github.paulosalonso.election.model.bu;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TipoArquivo implements EnumComCodigo {
    VOTACAO_UE(1, "Urna eletrônica"),
    VOTACAO_RED(2, "Recuperador de Dados - Responsável por gerar uma nova memória de resultado a partir da urna originária"),
    SA_MISTA_MR_PARCIAL_CEDULA(3, "Sistema de Apuração (Votação Mista - Memória de Resultado e Cédulas)"),
    SA_MISTA_BU_IMPRESSAO_CEDULA(4, "Sistema de Apuração (Votação Mista - Boletim de Urna impresso e Cédulas)"),
    SA_MANUAL(5, "Sistema de Apuração (Votação totalmente manual - Cédulas)"),
    SA_ELETRONICA(6, "Sistema de Apuração (Votação totalmente eletrônica)");

    private final int codigo;
    private final String descricao;
}
