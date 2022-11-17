package com.github.paulosalonso.election.model.bu;

import static com.github.paulosalonso.election.tools.text.MessageFormatter.format;

public interface EnumComCodigo {

    int getCodigo();

    static <T extends EnumComCodigo> T getByCodigo(T[] values, int codigo) {
        for (var value : values) {
            if (value.getCodigo() == codigo) {
                return value;
            }
        }

        throw new IllegalArgumentException(format("${nomeTipo} não encontrado(a) com o código ${codigo}",
                "nomeTipo", values[0].getClass().getSimpleName(),
                "codigo", Integer.toString(codigo)));
    }
}
