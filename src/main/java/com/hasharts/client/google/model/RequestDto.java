package com.hasharts.client.google.model;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RequestDto {

    private List<RequestContentDto> contents;
    private RequestGenerationConfigDto generationConfig;

    public RequestDto(String text) {
        this.contents = List.of(new RequestContentDto(text));
        this.generationConfig = new RequestGenerationConfigDto(List.of("TEXT", "IMAGE"));
    }
}
