package com.hasharts.client.google.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseDto {

    private List<ResponseCandidateDto> candidates;
    private ResponseUsageMetadataDto usageMetadata;

    // shortener
    public String imageContent() {
        return getCandidates().getFirst().getContent().getParts().getFirst().getInlineData().getData();
    }
}
