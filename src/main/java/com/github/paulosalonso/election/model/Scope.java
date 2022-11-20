package com.github.paulosalonso.election.model;

import lombok.RequiredArgsConstructor;

import static com.github.paulosalonso.election.tools.text.MessageFormatter.format;

@RequiredArgsConstructor
public enum Scope {
    STATE("estado"),
    CITY("municipio"),
    ZONE("zone");

    private final String description;

    public static Scope getByDescription(String description) {
        for (var scope : values()) {
            if (scope.description.equals(description)) {
                return scope;
            }
        }

        throw new IllegalArgumentException(format(
                "Scope with description '${description}' not found", "description", description));
    }
}
