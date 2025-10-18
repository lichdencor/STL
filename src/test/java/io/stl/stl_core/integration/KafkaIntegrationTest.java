package io.stl.stl_core.integration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest
class KafkaIntegrationTest {

  @Container
  static KafkaContainer kafka = new KafkaContainer(
      DockerImageName.parse("apache/kafka:4.0.1"));

  @DynamicPropertySource
  static void registerKafka(DynamicPropertyRegistry registry) {
    registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
  }

  @Test
  void testKafkaContainerStarts() {
    assertTrue(kafka.isRunning(), "Kafka container should be running");
    System.out.println("Kafka bootstrap: " + kafka.getBootstrapServers());
  }
}
