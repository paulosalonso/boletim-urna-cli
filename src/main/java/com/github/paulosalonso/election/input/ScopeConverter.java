package com.github.paulosalonso.election.input;

import com.github.paulosalonso.election.model.Scope;
import picocli.CommandLine;

public class ScopeConverter implements CommandLine.ITypeConverter<Scope> {
    @Override
    public Scope convert(String value) {
        return Scope.getByDescription(value);
    }
}
