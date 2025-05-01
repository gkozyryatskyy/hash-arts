package com.hasharts.service.image;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hasharts.client.google.model.ResponseDto;
import com.hasharts.service.image.GoogleImageGenerator.GenerateAndUploadResult;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.vertx.VertxContextSupport;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@QuarkusTest
//@QuarkusTestResource(IpfsResource.class)
public class GoogleImageGeneratorTest {

    @Inject
    GoogleImageGenerator imageGenerator;
    @Inject
    ObjectMapper json;

    @Disabled // for not to use tokens from tests
    @Test
    public void generateImage() {
        ResponseDto image = imageGenerator.generate("Generate pixelated image of NFT with baby Yoda")
                .await().indefinitely();
        Assertions.assertNotNull(
                image.getCandidates().getFirst().getContent().getParts().getFirst().getInlineData().getData());
    }

    @Test
    public void uploadImage() throws Throwable {
        String name = "nft.png";
        // http://localhost:8080/ipfs/QmbSG3eZGAJgQRmyNR8krXRHupLZZaEZiX7aU78bDiTxW8
        ResponseDto image = json.readValue(
                this.getClass().getClassLoader().getResourceAsStream("images/google-image.json"), ResponseDto.class);
        GenerateAndUploadResult res = VertxContextSupport.subscribeAndAwait(() -> imageGenerator.upload(image));
        // test node
        Assertions.assertEquals(name, res.node().name.orElse(null));
        Assertions.assertTrue(res.node().largeSize.isPresent());
        Assertions.assertNotNull(res.node().hash);
        // test entry
        Assertions.assertNotNull(res.entity().getId());
        Assertions.assertNotNull(res.entity().getCreatedAt());
        Assertions.assertNotNull(res.entity().getUpdatedAt());
        Assertions.assertNotNull(res.node().hash, res.entity().getIpfs());
    }
}
