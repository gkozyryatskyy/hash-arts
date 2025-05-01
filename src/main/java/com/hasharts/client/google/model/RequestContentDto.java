package com.hasharts.client.google.model;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RequestContentDto {

    private List<RequestContentPartDto> parts;

    public RequestContentDto(String text) {
        this.parts = List.of(new RequestContentPartDto(text));
    }
}
