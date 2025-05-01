package com.hasharts.service.image;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hasharts.client.google.GoogleClient;
import com.hasharts.client.google.model.RequestDto;
import com.hasharts.client.google.model.ResponseDto;
import com.hasharts.db.model.nft.Image;
import com.hasharts.service.hedera.model.NftMetadataV2;
import com.hasharts.service.ipfs.IpfsService;
import io.ipfs.api.MerkleNode;
import io.ipfs.multibase.binary.Base64;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.time.Instant;
import lombok.Getter;
import lombok.extern.jbosslog.JBossLog;
import org.eclipse.microprofile.config.inject.ConfigProperty;

// https://ai.google.dev/gemini-api/docs/image-generation
@JBossLog
@ApplicationScoped
public class GoogleImageGenerator {

    @Inject
    GoogleClient client;
    @ConfigProperty(name = "nft.model.google.key")
    String key;
    @Getter
    @ConfigProperty(name = "nft.model.google.name.default", defaultValue = "nft.png")
    String defaultName;
    @Inject
    IpfsService ipfsService;
    @ConfigProperty(name = "nft.token.mint.gateway.prefix")
    String gatewayPrefix;
    @Inject
    ObjectMapper json;

    public ResponseDto generate(String text) {
        log.infof("generate test:%s", text);
        ResponseDto retval = client.generate(key, new RequestDto(text));
        log.info("generate.usageMetadata:" + retval.getUsageMetadata());
        return retval;
    }


    public record GenerateAndUploadResult(MerkleNode node, MerkleNode metaNode, Image entity) {
    }

    @Transactional
    public GenerateAndUploadResult upload(String name, ResponseDto input) throws IOException {
        // persist to IFPS
        String content = input.imageContent();
        if (content == null) {
            throw new IllegalArgumentException("imageContent is null");
        } else {
            // image
            MerkleNode imageNode = ipfsService.add(defaultName, Base64.decodeBase64(content));
            Image image = new Image();
            Instant now = Instant.now();
            image.setCreatedAt(now);
            image.setUpdatedAt(now);
            image.setName(name);
            image.setIpfs(imageNode.hash.toBase58());
            // meta
            String meta = json.writeValueAsString(new NftMetadataV2(image.getName(),
                    gatewayPrefix + image.getIpfs()
//                    "https://hedera.com/assets/images/favicon.png"
//                    "https://bafybeidz7rgnm4e6as3cexmmfe76uxpcdvqinvtfljaq5ckw4s65iuudse.ipfs.dweb.link/?filename=nft2.png"
            ));
            MerkleNode metaNode = ipfsService.add("metadata.json", meta);
            image.setMetaIpfs(metaNode.hash.toBase58());
            image.persist();
            return new GenerateAndUploadResult(imageNode, metaNode, image);
        }
    }

    public GenerateAndUploadResult generateAndUpload(String name, String text) throws IOException {
        ResponseDto resp = generate(text);
        return upload(name, resp);

    }
}
