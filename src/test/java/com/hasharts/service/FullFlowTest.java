package com.hasharts.service;

import com.hasharts.db.model.nft.Token;
import com.hasharts.service.hedera.HederaNftService;
import com.hasharts.service.hedera.HederaNftService.MintNftResult;
import com.hasharts.service.image.GoogleImageGenerator;
import com.hasharts.service.image.GoogleImageGenerator.GenerateAndUploadResult;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.vertx.VertxContextSupport;
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
                        "Generate image for NFT with baby Yoda holding Hedera devcon medal")
                .await().indefinitely();
        // mint nft for existed collection
        Token token = VertxContextSupport.subscribeAndAwait(() -> Panache.withSession(() -> Token.findById(1000000L)));
        MintNftResult res = VertxContextSupport.subscribeAndAwait(
                () -> service.mint(token, image.entity()));
        // sout
        System.out.println(res);
    }
}
