package com.hasharts.config;

import io.ipfs.api.IPFS;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Produces;
import lombok.extern.jbosslog.JBossLog;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@JBossLog
@ApplicationScoped
public class IpfsClientProducer {

    @ConfigProperty(name = "nft.ipfs.tcp.host", defaultValue = "127.0.0.1")
    String host;
    @ConfigProperty(name = "nft.ipfs.tcp.port", defaultValue = "5001")
    Integer port;
    private IPFS ipfs;

    @Produces
    @Singleton
    public IPFS ipfsClient() {
        log.infof("Initialising IPFS client. %s:%s", host, port);
        this.ipfs = new IPFS(host, port);
        return this.ipfs;
    }

    // shutdown() stops IPFS service
//    @PreDestroy
//    void destroy() throws IOException {
//        if (this.ipfs != null) {
//            log.infof("Closing IPFS client. %s:%s", host, port);
//            this.ipfs.shutdown();
//        }
//    }
}
