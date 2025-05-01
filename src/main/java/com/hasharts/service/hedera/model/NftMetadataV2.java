package com.hasharts.service.hedera.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

//https://github.com/hiero-ledger/hiero-improvement-proposals/blob/main/HIP/hip-412.md#default-schema-collectibe-hedera-nfts-format-hip412100
@Getter
@Setter
@ToString
@AllArgsConstructor
public class NftMetadataV2 {

    public String name;
    public String type;
    public String image;
}
