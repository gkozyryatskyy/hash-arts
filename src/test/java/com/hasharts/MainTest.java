package com.hasharts;

import org.junit.jupiter.api.Test;

public class MainTest {

    @Test
    public void test() {
        System.out.println("Daemon is ready\n\r\n".matches("(?s).*Daemon is ready.*\\s"));
    }
}
