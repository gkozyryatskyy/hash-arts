package com.hasharts;

import io.ipfs.multibase.Base36;
import io.ipfs.multibase.Base58;
import io.ipfs.multibase.binary.Base32;
import io.ipfs.multihash.Multihash;
import org.junit.jupiter.api.Test;

public class MainTest {

    @Test
    public void test() {
        String cid = "QmbSG3eZGAJgQRmyNR8krXRHupLZZaEZiX7aU78bDiTxW8";
        Multihash hash = Multihash.fromBase58(cid);
        System.out.println(hash);
        String str = new String(Base58.decode(cid));
        System.out.println(str);
        Base32 base32 = new Base32();
        System.out.println(base32.encodeAsString(Base58.decode(cid)));
        System.out.println(Base36.encode(Base58.decode(cid)));
    }
}
