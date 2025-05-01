package com.hasharts.service.image;

import com.hasharts.client.google.GoogleClient;
import com.hasharts.client.google.model.RequestDto;
import com.hasharts.client.google.model.ResponseDto;
import com.hasharts.db.model.nft.Image;
import com.hasharts.service.ipfs.IpfsService;
import io.ipfs.api.MerkleNode;
import io.ipfs.multibase.binary.Base64;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
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

    public Uni<ResponseDto> generate(String text) {
        log.infof("generate test:%s", text);
        return client.generate(key, new RequestDto(text))
                .invoke(e -> log.info("generate.usageMetadata:" + e.getUsageMetadata()));
    }


    public record GenerateAndUploadResult(MerkleNode node, Image entity) {
    }

    @WithTransaction
    public Uni<GenerateAndUploadResult> upload(String name, ResponseDto input) {
        // persist to IFPS
        String content = input.imageContent();
        if (content == null) {
            return Uni.createFrom().failure(new IllegalArgumentException("imageContent is null"));
        } else {
            return ipfsService.add(defaultName, Base64.decodeBase64(content))
                    .chain(e -> {
                        Image image = new Image();
                        Instant now = Instant.now();
                        image.setCreatedAt(now);
                        image.setUpdatedAt(now);
                        image.setName(name);
                        image.setIpfs(e.hash.toBase58());
                        return image.<Image>persist().map(entry -> new GenerateAndUploadResult(e, entry));
                    });
        }
    }

    public Uni<GenerateAndUploadResult> generateAndUpload(String name, String text) {
        return generate(text).chain(e -> upload(name, e));

    }
}
