package com.enterprise.cartservice.config;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.*;
import java.util.*;
@Configuration
public class KafkaProducerConfig {
    @Value("${spring.kafka.bootstrap-servers}") private String bs;
    @Value("${kafka.topic.cart-events}") private String topic;
    @Bean public ProducerFactory<String,String> producerFactory() {
        Map<String,Object> p = new HashMap<>();
        p.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bs);
        p.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        p.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        p.put(ProducerConfig.ACKS_CONFIG, "all"); p.put(ProducerConfig.RETRIES_CONFIG, 3);
        return new DefaultKafkaProducerFactory<>(p);
    }
    @Bean public KafkaTemplate<String,String> kafkaTemplate() { return new KafkaTemplate<>(producerFactory()); }
    @Bean public NewTopic cartEventsTopic() { return new NewTopic(topic, 3, (short)1); }
}
