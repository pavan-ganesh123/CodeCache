package com.example.demo.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;

import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.example.demo.kafka.KafkaTopics;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.properties.security.protocol}")
    private String securityProtocol;

    @Value("${spring.kafka.properties.sasl.mechanism}")
    private String saslMechanism;

    @Value("${spring.kafka.properties.sasl.jaas.config}")
    private String saslJaasConfig;

    @Value("${spring.kafka.properties.ssl.truststore.type}")
    private String truststoreType;

    @Value("classpath:certs/aiven-kafka-truststore.p12")
    private Resource truststoreResource;

    @Value("${spring.kafka.properties.ssl.truststore.password}")
    private String truststorePassword;


    /*
     * Common Kafka security properties
     */
    private void addSecurityProperties(Map<String, Object> props) {

        props.put(
            "security.protocol",
            securityProtocol
        );

        props.put(
            "sasl.mechanism",
            saslMechanism
        );

        props.put(
            "sasl.jaas.config",
            saslJaasConfig
        );

        props.put(
            "ssl.truststore.type",
            truststoreType
        );

        // truststoreResource.getFile() only resolves when the classpath is
        // exploded onto disk (local IDE / mvn spring-boot:run). Inside a
        // packaged executable jar (Render), this resource lives as a zip
        // entry inside BOOT-INF/classes with no real filesystem path — so
        // instead we stream its bytes out to an actual temp file and point
        // Kafka at that, which works identically either way.
        try {
            File truststoreFile = File.createTempFile("kafka-truststore", ".p12");
            truststoreFile.deleteOnExit();

            try (InputStream in = truststoreResource.getInputStream()) {
                Files.copy(in, truststoreFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

            props.put(
                "ssl.truststore.location",
                truststoreFile.getAbsolutePath()
            );
        } catch (IOException e) {
            throw new RuntimeException(
                "Could not load Kafka truststore",
                e
            );
        }

        props.put(
            "ssl.truststore.password",
            truststorePassword
        );
    }


    @Bean
    ProducerFactory<String, Object> producerFactory() {

        Map<String, Object> props = new HashMap<>();

        props.put(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
            bootstrapServers
        );

        addSecurityProperties(props);

        ObjectMapper mapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();

        JsonSerializer<Object> serializer =
                new JsonSerializer<>(mapper);

        return new DefaultKafkaProducerFactory<>(
                props,
                new StringSerializer(),
                serializer
        );
    }


    @Bean
    KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }


    @Bean
    ConsumerFactory<String, Object> consumerFactory() {

        Map<String, Object> props = new HashMap<>();

        props.put(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
            bootstrapServers
        );

        props.put(
            ConsumerConfig.GROUP_ID_CONFIG,
            "notification-group"
        );

        props.put(
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
            "earliest"
        );

        props.put(
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
            StringDeserializer.class
        );

        props.put(
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
            JsonDeserializer.class
        );

        props.put(
            JsonDeserializer.TRUSTED_PACKAGES,
            "*"
        );

        addSecurityProperties(props);

        return new DefaultKafkaConsumerFactory<>(props);
    }


    /*
     * Topics
     *
     * Aiven's free tier caps topics at 5, so related event types share
     * one topic and carry a "type" field in the payload instead of each
     * getting its own topic:
     *
     *   POST_ACTIVITY   -> LIKE, SHARE, COMMENT
     *   FRIEND_ACTIVITY -> REQUEST, ACCEPTED
     *   MESSAGE         -> unchanged, kept on its own topic
     */

    @Bean
    NewTopic postActivityTopic() {

        return TopicBuilder
                .name(KafkaTopics.POST_ACTIVITY)
                .partitions(1)
                .replicas(2)
                .build();
    }


    @Bean
    NewTopic friendActivityTopic() {

        return TopicBuilder
                .name(KafkaTopics.FRIEND_ACTIVITY)
                .partitions(1)
                .replicas(2)
                .build();
    }


    @Bean
    NewTopic messageTopic() {

        return TopicBuilder
                .name(KafkaTopics.MESSAGE)
                .partitions(1)
                .replicas(2)
                .build();
    }


    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object>
    kafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory());

        return factory;
    }
}
