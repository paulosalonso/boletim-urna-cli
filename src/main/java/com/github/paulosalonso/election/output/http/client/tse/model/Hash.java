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
public class Hash {

    @JsonProperty("hash")
    private String hash;

    @JsonProperty("dr")
    private String date;

    @JsonProperty("hr")
    private String time;

    @JsonProperty("st")
    private String status;

    @JsonProperty("nmarq")
    private List<String> fileNames;

    public String getBulletinFileName() {
        return fileNames.stream()
                .filter(fileName -> fileName.endsWith(".bu") || fileName.endsWith(".busa"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Urn Bulletin file name not found"));
    }
}
