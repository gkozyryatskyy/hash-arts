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
public class ResponseUsageMetadataDto {

    private Integer promptTokenCount;
    private Integer totalTokenCount;
    private List<PromptTokensDetailsDto> promptTokensDetails;


    @Getter
    @Setter
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PromptTokensDetailsDto {
        private String modality;
        private Integer tokenCount;
    }
}
