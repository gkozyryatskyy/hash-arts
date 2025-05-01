package com.hasharts.client.google.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class RequestGenerationConfigDto {

    private List<String> responseModalities;
}
