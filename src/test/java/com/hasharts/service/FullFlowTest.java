package com.hasharts.service;

import com.hasharts.db.model.nft.Token;
import com.hasharts.service.hedera.HederaNftService;
import com.hasharts.service.hedera.HederaNftService.MintNftResult;
import com.hasharts.service.image.GoogleImageGenerator;
import com.hasharts.service.image.GoogleImageGenerator.GenerateAndUploadResult;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import lombok.extern.jbosslog.JBossLog;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
@JBossLog
@QuarkusTest
public class FullFlowTest {

    @Inject
    GoogleImageGenerator imageGenerator;
    @Inject
    HederaNftService service;

    @Test
    public void test() throws Throwable {
        // generate image, upload to ipfs
        GenerateAndUploadResult image = imageGenerator.generateAndUpload("full_flow_test_image",
                        "Generate image for NFT with baby Yoda holding Hedera devcon medal");
        // mint nft for existed collection
        Token token = Token.findById(1000000L);
        MintNftResult res = service.mint(token, image.entity());
        // sout
        System.out.println(res);
    }
}
