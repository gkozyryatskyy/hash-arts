package com.hasharts.service.hedera;

import com.hasharts.db.model.nft.Nft;
import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TokenCreateTransaction;
import com.hedera.hashgraph.sdk.TokenId;
import com.hedera.hashgraph.sdk.TokenMintTransaction;
import com.hedera.hashgraph.sdk.TokenSupplyType;
import com.hedera.hashgraph.sdk.TokenType;
import com.hedera.hashgraph.sdk.TransactionReceipt;
import com.hasharts.db.model.nft.Token;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import lombok.extern.jbosslog.JBossLog;
import org.eclipse.microprofile.config.inject.ConfigProperty;

// https://docs.hedera.com/hedera/tutorials/token/create-and-transfer-your-first-nft
@JBossLog
@ApplicationScoped
public class HederaNftService {

    @Inject
    Client client;
    @ConfigProperty(name = "nft.operator.id")
    String operatorId;
    @ConfigProperty(name = "nft.operator.key.der")
    String operatorKeyDer;
    @ConfigProperty(name = "nft.token.mint.max-transaction-fee.hbar", defaultValue = "10")
    Integer maxTransactionFee;

    public record CreateNftResult(TransactionReceipt receipt, Token token) {
    }

    public Uni<CreateNftResult> create(String name, String symbol, long maxSupply) {
        PrivateKey supplyKey = PrivateKey.generateECDSA();
        TokenCreateTransaction nftCreate = new TokenCreateTransaction()
                .setTokenName(name)
                .setTokenSymbol(symbol)
                .setTokenType(TokenType.NON_FUNGIBLE_UNIQUE)
                .setDecimals(0)
                .setInitialSupply(0)
                .setTreasuryAccountId(AccountId.fromString(operatorId))
                .setSupplyType(TokenSupplyType.FINITE)
                .setMaxSupply(maxSupply)
                .setSupplyKey(supplyKey)
                .freezeWith(client);
        TokenCreateTransaction nftCreateTxSign = nftCreate.sign(PrivateKey.fromStringDER(operatorKeyDer));
        return Uni.createFrom().completionStage(nftCreateTxSign.executeAsync(client))
                .invoke(e -> log.infof("Nft create tx sent. Tx:%s", e.transactionId))
                // get receipt
                .chain(e -> Uni.createFrom().completionStage(e.getReceiptAsync(client)))
                // log
                .invoke(e -> log.infof("Nft create receipt. Token:%s", e.tokenId))
                // persist
                .chain(e -> {
                    Token token = new Token();
                    Instant now = Instant.now();
                    token.setCreatedAt(now);
                    token.setUpdatedAt(now);
                    token.setHederaTokenId(Objects.requireNonNull(e.tokenId).toString());
                    token.setName(nftCreate.getTokenName());
                    token.setSymbol(nftCreate.getTokenSymbol());
                    token.setSupplyPrivateKey(supplyKey.toStringDER());
                    token.setSupplyPublicKey(supplyKey.getPublicKey().toStringDER());
                    return token.<Token>persist().map(entry -> new CreateNftResult(e, entry));
                });
    }

    public record MintNftResult(TransactionReceipt receipt, Nft nft) {
    }

    public Uni<MintNftResult> mint(String tokenId, String supplyKey, List<String> data) {
        TokenMintTransaction mintTx = new TokenMintTransaction()
                .setTokenId(TokenId.fromString(tokenId))
                .setMaxTransactionFee(new Hbar(maxTransactionFee));
        for (String cid : data) {
            mintTx.addMetadata(cid.getBytes());
        }
        mintTx.freezeWith(client);
        TokenMintTransaction mintTxSign = mintTx.sign(PrivateKey.fromStringDER(supplyKey));
        return Uni.createFrom().completionStage(mintTxSign.executeAsync(client))
                .invoke(e -> log.infof("Nft mint tx sent. Tx:%s", e.transactionId))
                // get receipt
                .chain(e -> Uni.createFrom().completionStage(e.getReceiptAsync(client)))
                // log
                .invoke(e -> log.infof("Nft mint receipt. Serials:%s", e.serials))
                // persist
                .chain(e -> {
                    Nft nft = new Nft();
                    Instant now = Instant.now();
                    nft.setCreatedAt(now);
                    nft.setUpdatedAt(now);
                    nft.setTokenId(tokenId);
                    nft.setSerials(e.serials);
                    nft.setData(data);
                    return nft.<Nft>persist().map(entry -> new MintNftResult(e, entry));
                });
    }
}
