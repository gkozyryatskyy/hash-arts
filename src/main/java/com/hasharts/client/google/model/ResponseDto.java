package com.hasharts.client.google.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(includeFieldNames = false)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseDto {

    private List<ResponseCandidateDto> candidates;
    private ResponseUsageMetadataDto usageMetadata;

    // shortener
    public String imageContent() {
        return getCandidates().stream().filter(e -> e.getContent() != null)
                .findFirst().flatMap(cand -> cand.getContent().getParts().stream()
                        .filter(e -> e.getInlineData() != null)
                        .findFirst().map(part -> part.getInlineData().getData())).orElse(null);
    }
}
