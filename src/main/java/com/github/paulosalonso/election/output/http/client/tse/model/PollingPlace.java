package com.github.paulosalonso.election.output.http.client.tse.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class PollingPlace {
    private final String state;
    private final String cityCode;
    private final String cityName;
    private final String zone;
    private final String section;
}
