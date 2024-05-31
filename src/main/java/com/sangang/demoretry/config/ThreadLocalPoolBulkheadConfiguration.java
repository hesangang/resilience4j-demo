package com.sangang.demoretry.config;

import io.github.resilience4j.bulkhead.ThreadPoolBulkhead;
import io.github.resilience4j.bulkhead.ThreadPoolBulkheadConfig;
import io.github.resilience4j.bulkhead.ThreadPoolBulkheadRegistry;
import io.github.resilience4j.bulkhead.event.BulkheadEvent;
import io.github.resilience4j.common.CompositeCustomizer;
import io.github.resilience4j.common.bulkhead.configuration.ThreadPoolBulkheadConfigCustomizer;
import io.github.resilience4j.common.bulkhead.configuration.ThreadPoolBulkheadConfigurationProperties;
import io.github.resilience4j.consumer.EventConsumerRegistry;
import io.github.resilience4j.core.registry.RegistryEventConsumer;
import io.vavr.collection.HashMap;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Description: 处理获取不到ThreadLocal，两种方式
 *  1、{@link ThreadLocalPoolBulkheadConfiguration}
 *  2、配置contextPropagator：处理类路径
 *
 * https://github.com/resilience4j/resilience4j/issues/565
 * @author: SanGang
 * @author: 2024-05-30 09:45
 */
@Configuration
public class ThreadLocalPoolBulkheadConfiguration {

    private ThreadPoolBulkheadConfig getThreadPoolBulkheadConfig() {
        ThreadPoolBulkheadConfig bulkheadConfig = ThreadPoolBulkheadConfig.custom()
                .maxThreadPoolSize(ThreadPoolBulkheadConfig.ofDefaults().getMaxThreadPoolSize())
                .coreThreadPoolSize(ThreadPoolBulkheadConfig.ofDefaults().getCoreThreadPoolSize())
                .queueCapacity(ThreadPoolBulkheadConfig.ofDefaults().getQueueCapacity())
                .contextPropagator(new CustomContextPropagator())
                .build();
        return bulkheadConfig;
    }


    @Bean
    public ThreadPoolBulkheadRegistry threadPoolBulkheadRegistry(ThreadPoolBulkheadConfigurationProperties bulkheadConfigurationProperties, EventConsumerRegistry<BulkheadEvent> bulkheadEventConsumerRegistry, RegistryEventConsumer<ThreadPoolBulkhead> threadPoolBulkheadRegistryEventConsumer, @Qualifier("compositeThreadPoolBulkheadCustomizer") CompositeCustomizer<ThreadPoolBulkheadConfigCustomizer> compositeThreadPoolBulkheadCustomizer) {
        /* ThreadPoolBulkheadRegistry bulkheadRegistry = this.createBulkheadRegistry(bulkheadConfigurationProperties, threadPoolBulkheadRegistryEventConsumer, compositeThreadPoolBulkheadCustomizer);
        this.registerEventConsumer(bulkheadRegistry, bulkheadEventConsumerRegistry, bulkheadConfigurationProperties);

        bulkheadConfigurationProperties.getBackends().forEach((name, properties) -> {
            bulkheadRegistry.bulkhead(name, getThreadPoolBulkheadConfig());
        }); */
        return ThreadPoolBulkheadRegistry.of(getThreadPoolBulkheadConfig());
    }

    private ThreadPoolBulkheadRegistry createBulkheadRegistry(ThreadPoolBulkheadConfigurationProperties threadPoolBulkheadConfigurationProperties, RegistryEventConsumer<ThreadPoolBulkhead> threadPoolBulkheadRegistryEventConsumer, CompositeCustomizer<ThreadPoolBulkheadConfigCustomizer> compositeThreadPoolBulkheadCustomizer) {
        Map<String, ThreadPoolBulkheadConfig> configs = (Map)threadPoolBulkheadConfigurationProperties.getConfigs().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, (entry) -> {
            return threadPoolBulkheadConfigurationProperties.createThreadPoolBulkheadConfig((ThreadPoolBulkheadConfigurationProperties.InstanceProperties)entry.getValue(), compositeThreadPoolBulkheadCustomizer, (String)entry.getKey());
        }));
        return ThreadPoolBulkheadRegistry.of(configs, threadPoolBulkheadRegistryEventConsumer, HashMap.ofAll(threadPoolBulkheadConfigurationProperties.getTags()));
    }

    private void registerEventConsumer(ThreadPoolBulkheadRegistry bulkheadRegistry, EventConsumerRegistry<BulkheadEvent> eventConsumerRegistry, ThreadPoolBulkheadConfigurationProperties properties) {
        bulkheadRegistry.getEventPublisher().onEntryAdded((event) -> {
            this.registerEventConsumer(eventConsumerRegistry, (ThreadPoolBulkhead)event.getAddedEntry(), properties);
        }).onEntryReplaced((event) -> {
            this.registerEventConsumer(eventConsumerRegistry, (ThreadPoolBulkhead)event.getNewEntry(), properties);
        });
    }

    private void registerEventConsumer(EventConsumerRegistry<BulkheadEvent> eventConsumerRegistry, ThreadPoolBulkhead bulkHead, ThreadPoolBulkheadConfigurationProperties bulkheadConfigurationProperties) {
        int eventConsumerBufferSize = (Integer)Optional.ofNullable(bulkheadConfigurationProperties.getBackendProperties(bulkHead.getName())).map(ThreadPoolBulkheadConfigurationProperties.InstanceProperties::getEventConsumerBufferSize).orElse(100);
        bulkHead.getEventPublisher().onEvent(eventConsumerRegistry.createEventConsumer(String.join("-", ThreadPoolBulkhead.class.getSimpleName(), bulkHead.getName()), eventConsumerBufferSize));
    }

}
