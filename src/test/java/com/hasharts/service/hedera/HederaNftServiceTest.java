package com.hasharts.service.hedera;

import com.hasharts.db.model.nft.Image;
import com.hasharts.db.model.nft.Token;
import com.hasharts.service.hedera.HederaNftService.CreateNftResult;
import com.hasharts.service.hedera.HederaNftService.MintNftResult;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import lombok.extern.jbosslog.JBossLog;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

@JBossLog
@QuarkusTest
public class HederaNftServiceTest {

    @Inject
    HederaNftService service;
    Token token;

    @Disabled
    @Test
    @Order(1)
    public void createToken() throws Throwable {
        String name = "hash-arts-nft-test";
        String symbol = "haTestNft";
        CreateNftResult res = service.create(name, symbol, 200);
        log.info("createToken: " + res);
        // INFO: Nft create tx sent. Tx:0.0.5640351@1746090549.968000649
        // INFO: Nft create receipt. Token:0.0.5932316
        Assertions.assertNotNull(res.receipt().transactionId);
        Assertions.assertNotNull(res.receipt().tokenId);
        this.token = res.entity();
        Assertions.assertNotNull(res.entity().getId());
        Assertions.assertNotNull(res.entity().getCreatedAt());
        Assertions.assertNotNull(res.entity().getUpdatedAt());
        Assertions.assertEquals(res.receipt().tokenId.toString(), res.entity().getHederaTokenId());
        Assertions.assertEquals(name, res.entity().getName());
        Assertions.assertEquals(symbol, res.entity().getSymbol());
    }

    @Test
    @Order(2)
    public void mintNft() throws Throwable {
        if (token == null) {
            token = Token.findById(1000000L);
        }
        Image image = Image.findById(1000000L);
        MintNftResult res = service.mint(token, image);
        log.info("mintNft: " + res);
//        INFO: mintNft: MintNftResult[receipt=TransactionReceipt{transactionId=0.0.5640351@1746092299.562000440, status=SUCCESS, exchangeRate=ExchangeRate{hbars=30000, cents=547697, expirationTime=2025-05-01T10:00:00Z, exchangeRateInCents=18.256566666666668}, nextExchangeRate=ExchangeRate{hbars=30000, cents=554314, expirationTime=2025-05-01T11:00:00Z, exchangeRateInCents=18.477133333333335}, accountId=null, fileId=null, contractId=null, topicId=null, tokenId=null, topicSequenceNumber=null, topicRunningHash=null, totalSupply=1, scheduleId=null, scheduledTransactionId=null, serials=[1], nodeId=0, duplicates=[], children=[]}, entity=Nft(super=BaseEntity(super=IdEntity(id=1), createdAt=2025-05-01T09:38:31.862344Z, updatedAt=2025-05-01T09:38:31.862344Z), tokenId=1000000, hederaTokenId=0.0.5932364, serials=[1], data=[QmbSG3eZGAJgQRmyNR8krXRHupLZZaEZiX7aU78bDiTxW8])]
        Assertions.assertNotNull(res.receipt().transactionId);
        Assertions.assertNotNull(res.receipt().serials);
        Assertions.assertNotNull(res.entity().getId());
        Assertions.assertNotNull(res.entity().getCreatedAt());
        Assertions.assertNotNull(res.entity().getUpdatedAt());
    }

//    @Test
//    @Order(3)
//    public void deleteToken() throws Throwable {
//        VertxContextSupport.subscribeAndAwait(() -> service.delete(null, "0.0.5932316"));
//    }
}
