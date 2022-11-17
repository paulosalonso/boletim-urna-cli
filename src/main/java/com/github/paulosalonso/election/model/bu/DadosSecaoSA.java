package com.github.paulosalonso.election.model.bu;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DadosSecaoSA {

    private final DadosSecao dadosSecao;
    private final DadosSA dadosSA;
}
