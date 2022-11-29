package com.github.paulosalonso.election.tools.text;

import org.apache.commons.text.StrSubstitutor;

import java.util.HashMap;
import java.util.Map;

/**
 * It allows to format messages based on named placeholders
 */
public class MessageFormatter {

    /**
     * Replace placeholders by respective values
     *
     * A placeholder must have the following pattern: ${placeholder-name}
     *
     * @param source Message pattern with placeholders
     * @param values Pair of key/values where key must be equal the placeholder name
     * @return The formated string
     */
    public static String format(String source, String... values) {
        if (values.length % 2 != 0) {
            throw new IllegalArgumentException("Parameter values must have even length");
        }

        final var valuesMap = new HashMap<String, String>();

        for (var i = 0; i < values.length; i += 2) {
            valuesMap.put(values[i], values[i + 1]);
        }

        return StrSubstitutor.replace(source, valuesMap);
    }

    /**
     * Replace placeholders by respective values
     *
     * A placeholder must have the following pattern: ${placeholder-name}
     *
     * @param source Message pattern with placeholders
     * @param values String map where key must be equal the placeholder name
     * @return The formated string
     */
    public static String format(String source, Map<String, String> values) {
        return StrSubstitutor.replace(source, values);
    }
}
