package com.hasharts.service.ipfs;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.multihash.Multihash;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.IOException;
import lombok.extern.jbosslog.JBossLog;
import org.eclipse.microprofile.config.inject.ConfigProperty;

// https://docs.ipfs.tech/
// https://github.com/ipfs-shipyard/java-ipfs-http-client
@JBossLog
@ApplicationScoped
public class IpfsService {

    @Inject
    IPFS ipfs;
    @ConfigProperty(name = "nft.token.mint.gateway.prefix")
    String gatewayPrefix;

    public MerkleNode add(String name, String data) throws IOException {
        return add(name, data.getBytes());
    }

    public MerkleNode add(String name, byte[] data) throws IOException {
        NamedStreamable.ByteArrayWrapper file = new NamedStreamable.ByteArrayWrapper(name, data);
        log.infof("Ipfs add name:%s", name);
        MerkleNode reval = ipfs.add(file).getFirst();
        log.infof("Ipfs add hash:%s, url:%s", reval.hash.toBase58(), gatewayPrefix + reval.hash.toBase58());
        return reval;
    }

    public byte[] get(String base58) throws IOException {
        Multihash filePointer = Multihash.fromBase58(base58);
        return ipfs.cat(filePointer);
    }

}
