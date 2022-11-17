package com.github.paulosalonso.election.model.bu;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CodigoCargoConsulta {

    private final CargoConstitucional cargoConstitucional;
    private final Integer numeroCargoConsultaLivre;
}
