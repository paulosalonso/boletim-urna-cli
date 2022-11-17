package com.github.paulosalonso.election.output.http.client.tse.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class State {

    @JsonProperty("cd")
    private String code;

    @JsonProperty("ds")
    private String name;

    @ToString.Exclude
    @JsonProperty("mu")
    private List<City> cities;
}
