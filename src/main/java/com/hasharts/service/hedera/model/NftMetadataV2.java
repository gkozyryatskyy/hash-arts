package com.hasharts.service.hedera.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

//https://github.com/hiero-ledger/hiero-improvement-proposals/blob/main/HIP/hip-412.md#default-schema-collectibe-hedera-nfts-format-hip412100
@Getter
@Setter
@ToString
@NoArgsConstructor
public class NftMetadataV2 {

    private String name;
    private String type;
    private String image;
    private List<NftMetadataFile> files;

    public NftMetadataV2(String name, String imageUrl) {
        this.name = name;
        this.type = "image/png";
        this.image = imageUrl;
        this.files = List.of(new NftMetadataFile(imageUrl, true, "image/png"));
    }

    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    public static class NftMetadataFile {
        private String uri;
        @JsonProperty("is_default_file")
        private boolean defaultFile;
        private String type;
    }
}
