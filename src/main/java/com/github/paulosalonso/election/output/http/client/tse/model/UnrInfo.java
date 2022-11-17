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
public class UnrInfo {

    @JsonProperty("dg")
    private String generationDate;

    @JsonProperty("hg")
    private String generationTime;

    @JsonProperty("st")
    private String status;

    @JsonProperty("hashes")
    private List<Hash> hashes;
}