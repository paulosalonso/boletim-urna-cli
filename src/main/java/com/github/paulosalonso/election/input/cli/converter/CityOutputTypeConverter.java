package com.github.paulosalonso.election.input.cli.converter;

import com.github.paulosalonso.election.model.CityOutputType;
import com.github.paulosalonso.election.tools.text.MessageFormatter;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine.ITypeConverter;

import java.util.Map;

@Slf4j
public class CityOutputTypeConverter implements ITypeConverter<CityOutputType> {

    private static final String OUTPUT_TYPE_NOT_FOUND_MESSAGE =
            "It was not found a output type for value '${value}'. CONSOLE is selected as output type.";

    private static final Map<String, CityOutputType> VALUES_MAP = Map.of(
            "c", CityOutputType.CONSOLE,
            "console", CityOutputType.CONSOLE,
            "w", CityOutputType.WEBHOOK,
            "webhook", CityOutputType.WEBHOOK);

    @Override
    public CityOutputType convert(String value) {
        final var outputType = VALUES_MAP.get(value.toLowerCase());

        if (outputType == null) {
            log.warn(MessageFormatter.format(OUTPUT_TYPE_NOT_FOUND_MESSAGE, "value", value));
            return CityOutputType.CONSOLE;
        }

        return outputType;
    }
}
