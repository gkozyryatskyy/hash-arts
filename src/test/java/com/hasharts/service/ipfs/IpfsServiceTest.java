package com.hasharts.service.ipfs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hasharts.client.google.model.ResponseDto;
import com.hasharts.util.ImageUtil;
import io.ipfs.api.MerkleNode;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@QuarkusTest
//@QuarkusTestResource(IpfsResource.class)
public class IpfsServiceTest {

    @Inject
    IpfsService ipfsService;
    @Inject
    ObjectMapper json;

    @Test
    public void uploadIpfsImage() throws Throwable {
        String name = "nft.png";
        // http://localhost:8080/ipfs/QmbSG3eZGAJgQRmyNR8krXRHupLZZaEZiX7aU78bDiTxW8
        ResponseDto image = json.readValue(
                this.getClass().getClassLoader().getResourceAsStream("images/google-image.json"), ResponseDto.class);
        MerkleNode node = ipfsService.add(name, image.imageContent()).await().indefinitely();
        Assertions.assertEquals(name, node.name.orElse(null));
        Assertions.assertTrue(node.largeSize.isPresent());
        Assertions.assertNotNull(node.hash);
    }

    @Disabled
    @Test
    public void show() throws IOException {
        ResponseDto image = json.readValue(
                this.getClass().getClassLoader().getResourceAsStream("images/google-image.json"), ResponseDto.class);
        ImageUtil.base64ToFile("test.png",
                image.imageContent());
    }
}
