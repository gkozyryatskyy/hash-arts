package com.hasharts.service.hedera;

import com.hasharts.db.model.nft.Image;
import com.hasharts.db.model.nft.Nft;
import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.PrecheckStatusException;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.ReceiptStatusException;
import com.hedera.hashgraph.sdk.TokenCreateTransaction;
import com.hedera.hashgraph.sdk.TokenId;
import com.hedera.hashgraph.sdk.TokenMintTransaction;
import com.hedera.hashgraph.sdk.TokenSupplyType;
import com.hedera.hashgraph.sdk.TokenType;
import com.hedera.hashgraph.sdk.TransactionReceipt;
import com.hasharts.db.model.nft.Token;
import com.hedera.hashgraph.sdk.TransactionResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeoutException;
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
    @ConfigProperty(name = "nft.token.mint.gateway.prefix")
    String gatewayPrefix;

    public record CreateNftResult(TransactionReceipt receipt, Token entity) {
    }

    @Transactional
    public CreateNftResult create(String name, String symbol, long maxSupply)
            throws PrecheckStatusException, TimeoutException, ReceiptStatusException {
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
        log.infof("Nft create Name:%s symbol:%s", name, symbol);
        TransactionResponse tx = nftCreateTxSign.execute(client);
        log.infof("Nft create tx sent. Tx:%s", tx.transactionId);
        // get receipt
        TransactionReceipt receipt = tx.getReceipt(client);
        log.infof("Nft create receipt. Token:%s", receipt.tokenId);
        // persist
        Token token = new Token();
        Instant now = Instant.now();
        token.setCreatedAt(now);
        token.setUpdatedAt(now);
        token.setHederaTokenId(Objects.requireNonNull(receipt.tokenId).toString());
        token.setName(nftCreate.getTokenName());
        token.setSymbol(nftCreate.getTokenSymbol());
        token.setSupplyPublicKey(supplyKey.getPublicKey().toStringDER());
        token.setSupplyPrivateKey(supplyKey.toStringDER());
        token.persist();
        return new CreateNftResult(receipt, token);
    }

    public record MintNftResult(TransactionReceipt receipt, Nft entity) {
    }

    public MintNftResult mint(Token token, Image image)
            throws ReceiptStatusException, PrecheckStatusException, TimeoutException {
        return mint(token.getId(), token.getHederaTokenId(), token.getSupplyPrivateKey(), List.of(gatewayPrefix + image.getMetaIpfs()));
    }

    @Transactional
    public MintNftResult mint(Long tokenId, String hederaTokenId, String supplyPrivateKey, List<String> data)
            throws ReceiptStatusException, PrecheckStatusException, TimeoutException {
        TokenMintTransaction mintTx = new TokenMintTransaction()
                .setTokenId(TokenId.fromString(hederaTokenId))
                .setMaxTransactionFee(new Hbar(maxTransactionFee));
        for (String cid : data) {
            mintTx.addMetadata(cid.getBytes());
        }
        mintTx.freezeWith(client);
        TokenMintTransaction mintTxSign = mintTx.sign(PrivateKey.fromStringDER(supplyPrivateKey));
        log.infof("Nft mint TokenId:%s HederaTokenId:%s, Data:%s", tokenId, hederaTokenId, data);
        TransactionResponse tx = mintTxSign.execute(client);
        log.infof("Nft mint tx sent. Tx:%s", tx.transactionId);
        TransactionReceipt receipt = tx.getReceipt(client);
        log.infof("Nft mint receipt. Serials:%s", receipt.serials);
        // persist
        Nft nft = new Nft();
        Instant now = Instant.now();
        nft.setCreatedAt(now);
        nft.setUpdatedAt(now);
        nft.setTokenId(tokenId);
        nft.setHederaTokenId(hederaTokenId);
        nft.setSerials(receipt.serials);
        nft.setData(data);
        nft.persist();
        return new MintNftResult(receipt, nft);
    }
}
