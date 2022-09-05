package com.iot.api.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

/**
 * KafkaConsumerConfig use to consume Kafka Consumer Stream
 */
@Configuration
public class KafkaConsumerConfig {
    private final Logger logger = LoggerFactory.getLogger(KafkaConsumerConfig.class);

    @Value(value = "${kafka.bootstrapAddress}")
    private String bootstrapAddress;

    @Value(value = "${general.topic.group.id}")
    private String groupId;

    @Autowired
    private KafkaProperties kafkaProperties;

    /**
     * Build DefaultKafkaConsumerFactory with given properties on application properties. allow to change values -
     * general.topic.name,
     * general.topic.group.id,
     * spring.kafka.consumer.enable-auto-commit,
     * spring.kafka.listener.concurrency
     *
     * @param kafkaProperties
     * @return ConsumerFactory<String, String>
     */
    @Bean
    public ConsumerFactory<String, String> consumerFactory(KafkaProperties kafkaProperties) {
        logger.info(String.format("kafkaProperties loaded -> buildConsumerProperties"));
        return new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties());
    }
}
