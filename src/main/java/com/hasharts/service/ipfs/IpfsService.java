package com.hasharts.service.ipfs;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.multihash.Multihash;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.Vertx;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.jbosslog.JBossLog;

// https://docs.ipfs.tech/
// https://github.com/ipfs-shipyard/java-ipfs-http-client
@JBossLog
@ApplicationScoped
public class IpfsService {

    @Inject
    IPFS ipfs;
    @Inject
    Vertx vertx;

    public Uni<MerkleNode> add(String name, String data) {
        return add(name, data.getBytes());
    }

    public Uni<MerkleNode> add(String name, byte[] data) {
        NamedStreamable.ByteArrayWrapper file = new NamedStreamable.ByteArrayWrapper(name, data);
        log.infof("Ipfs add name:%s", name);
        return vertx.executeBlocking(() -> ipfs.add(file).getFirst());
    }

    public Uni<byte[]> get(String base58) {
        Multihash filePointer = Multihash.fromBase58(base58);
        return vertx.executeBlocking(() -> ipfs.cat(filePointer));
    }

}
