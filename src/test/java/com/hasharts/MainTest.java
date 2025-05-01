package com.hasharts;

import io.ipfs.multihash.Multihash;
import org.junit.jupiter.api.Test;

public class MainTest {

    @Test
    public void test() {
        Multihash hash = Multihash.fromBase58("QmbSG3eZGAJgQRmyNR8krXRHupLZZaEZiX7aU78bDiTxW8");
        System.out.println(hash);
    }
}
