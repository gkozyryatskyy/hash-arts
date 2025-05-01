package com.hasharts.service.image;

import com.hasharts.client.google.GoogleClient;
import com.hasharts.client.google.model.RequestDto;
import com.hasharts.client.google.model.ResponseDto;
import com.hasharts.db.model.nft.Image;
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

    public ResponseDto generate(String text) {
        log.infof("generate test:%s", text);
        ResponseDto retval = client.generate(key, new RequestDto(text));
        log.info("generate.usageMetadata:" + retval.getUsageMetadata());
        return retval;
    }


    public record GenerateAndUploadResult(MerkleNode node, Image entity) {
    }

    @Transactional
    public GenerateAndUploadResult upload(String name, ResponseDto input) throws IOException {
        // persist to IFPS
        String content = input.imageContent();
        if (content == null) {
            throw new IllegalArgumentException("imageContent is null");
        } else {
            MerkleNode reval = ipfsService.add(defaultName, Base64.decodeBase64(content));
            Image image = new Image();
            Instant now = Instant.now();
            image.setCreatedAt(now);
            image.setUpdatedAt(now);
            image.setName(name);
            image.setIpfs(reval.hash.toBase58());
            image.persist();
            return new GenerateAndUploadResult(reval, image);
        }
    }

    public GenerateAndUploadResult generateAndUpload(String name, String text) throws IOException {
        ResponseDto resp = generate(text);
        return upload(name, resp);

    }
}
