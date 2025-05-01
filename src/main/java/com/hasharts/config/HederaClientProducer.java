package com.hasharts.config;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.PrivateKey;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Produces;
import java.util.concurrent.TimeoutException;
import lombok.extern.jbosslog.JBossLog;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@JBossLog
@ApplicationScoped
public class HederaClientProducer {

    @ConfigProperty(name = "nft.hedera.net", defaultValue = "testnet")
    String net;
    @ConfigProperty(name = "nft.operator.id")
    String operatorId;
    @ConfigProperty(name = "nft.operator.key.der")
    String operatorKeyDer;
    private Client client;

    @Produces
    @Singleton
    public Client hederaClient() {
        log.info("Initialising Hedera client. network:" + net);
        this.client = Client.forName(net);
        client.setOperator(AccountId.fromString(operatorId), PrivateKey.fromString(operatorKeyDer));
        return this.client;
    }

    @PreDestroy
    void destroy() throws TimeoutException {
        if (this.client != null) {
            log.info("Closing Hedera client. network:" + net);
            this.client.close();
        }
    }
}
