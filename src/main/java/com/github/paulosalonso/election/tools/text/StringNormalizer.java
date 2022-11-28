package com.github.paulosalonso.election.tools.text;

import lombok.NoArgsConstructor;

import java.text.Normalizer;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class StringNormalizer {

    public static String removeAccents(String input) {
        final var normalized = Normalizer.normalize(input, Normalizer.Form.NFKD);
        return normalized.replaceAll("\\p{M}", "");
    }
}
