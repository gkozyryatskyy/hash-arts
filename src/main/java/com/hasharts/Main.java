package com.hasharts;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;
import java.util.Arrays;
import org.jboss.logging.Logger;

@QuarkusMain
public class Main {

    private static final Logger log = Logger.getLogger(Main.class.getName());

    public static void main(String... args) {
        Runtime runtime = Runtime.getRuntime();
        log.infof("Running from main method. CPU:[%s],MEM.total:[%s],MEM.max:[%s] Args:%s",
                runtime.availableProcessors(),
                runtime.totalMemory(),
                runtime.maxMemory(),
                Arrays.toString(args));
        Quarkus.run(args);
    }
}
