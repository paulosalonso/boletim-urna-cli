package com.github.paulosalonso.election.model.bu;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IdentificacaoVotavel {

    private final Integer partido;
    private final Integer codigo;
}
