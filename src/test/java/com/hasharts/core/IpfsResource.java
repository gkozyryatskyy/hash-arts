package com.hasharts.core;

import io.quarkus.test.common.DevServicesContext;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;
import lombok.extern.jbosslog.JBossLog;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.DockerHealthcheckWaitStrategy;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;

@JBossLog
public class IpfsResource implements QuarkusTestResourceLifecycleManager, DevServicesContext.ContextAware {

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<String> containerNetworkId;
    private GenericContainer<?> container;

    @Override
    public void setIntegrationTestContext(DevServicesContext context) {
        this.containerNetworkId = context.containerNetworkId();
    }

    @Override
    public Map<String, String> start() {
        // start a container making sure to call withNetworkMode() with the value of containerNetworkId if present
        this.container = new GenericContainer<>("ipfs/kubo:v0.18.1")
                .withExposedPorts(4001, 5001, 8080, 8081)
                .withCommand("daemon --enable-pubsub-experiment")
                .waitingFor(new DockerHealthcheckWaitStrategy().withStartupTimeout(Duration.of(60, ChronoUnit.SECONDS)));
//                .waitingFor(new LogMessageWaitStrategy().withRegEx(".*Daemon is ready.*\\s").withTimes(2)
//                        .withStartupTimeout(Duration.of(60, ChronoUnit.SECONDS)));
        // apply the network to the container
        containerNetworkId.ifPresent(container::withNetworkMode);
        this.container.start();
        return ImmutableMap.of(
                "nft.ipfs.tcp.host", container.getHost(),
                "nft.ipfs.tcp.port", container.getMappedPort(5001).toString());
    }

    @Override
    public void stop() {
        this.container.close();
    }
}
