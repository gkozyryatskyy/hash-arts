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
public class ResponseCandidateDto {

    private ResponseContentDto content;
    private String finishReason;
    private Integer index;

    @Getter
    @Setter
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResponseContentDto {

        private List<ResponsePartDto> parts;
        private String role;
    }

    @Getter
    @Setter
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResponsePartDto {

        private ResponseInlineDataDto inlineData;

    }

    @Getter
    @Setter
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResponseInlineDataDto {

        private String mimeType;
        private String data;
    }
}
