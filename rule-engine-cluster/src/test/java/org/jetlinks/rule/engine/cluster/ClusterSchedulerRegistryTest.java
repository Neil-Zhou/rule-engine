package org.jetlinks.rule.engine.cluster;

import lombok.SneakyThrows;
import org.jetlinks.core.rpc.RpcService;
import org.jetlinks.core.rpc.RpcServiceFactory;
import org.jetlinks.rule.engine.cluster.scheduler.ClusterLocalScheduler;
import org.jetlinks.supports.event.BrokerEventBus;
import org.jetlinks.supports.rpc.DefaultRpcServiceFactory;
import org.jetlinks.supports.rpc.EventBusRcpService;
import org.junit.Ignore;
import org.junit.Test;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;

@Ignore
public class ClusterSchedulerRegistryTest {


    BrokerEventBus eventBus = new BrokerEventBus();
    RpcService rpcService = new EventBusRcpService(eventBus);


    @Test
    @SneakyThrows
    public void test() {
        eventBus.setPublishScheduler(Schedulers.immediate());
        RpcServiceFactory factory=new DefaultRpcServiceFactory(rpcService);

        {
            ClusterSchedulerRegistry registry = new ClusterSchedulerRegistry(eventBus, factory);
            registry.setKeepaliveInterval(Duration.ofMillis(500));
            ClusterLocalScheduler scheduler = new ClusterLocalScheduler("test", factory);
            registry.register(scheduler);
            registry.setup();
        }

        ClusterSchedulerRegistry registry = new ClusterSchedulerRegistry(eventBus, factory);
        registry.setKeepaliveInterval(Duration.ofMillis(500));
        ClusterLocalScheduler scheduler = new ClusterLocalScheduler("test2", factory);

        registry.register(scheduler);
        registry.setup();
        Thread.sleep(2000);
        registry.getSchedulers()
                .as(StepVerifier::create)
                .expectNextCount(2)
                .verifyComplete();

    }

}