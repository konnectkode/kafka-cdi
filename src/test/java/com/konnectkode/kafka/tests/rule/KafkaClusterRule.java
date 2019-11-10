package com.konnectkode.kafka.tests.rule;

import io.debezium.kafka.KafkaCluster;
import io.debezium.util.Testing;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class KafkaClusterRule implements TestRule {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaClusterRule.class);

    private final int zookeeperPort = 2282;

    private File dataDir;
    private KafkaCluster kafkaCluster;
    private List<KafkaConsumer> consumers = new ArrayList<>();
    private List<KafkaProducer> producers = new ArrayList<>();

    private int firstKafkaPort;
    private int brokers = 1;

    public KafkaClusterRule(int firstKafkaPort, int brokers) {
        this.firstKafkaPort = firstKafkaPort;
        this.brokers = brokers;
    }

    public KafkaClusterRule(int firstKafkaPort) {
        this.firstKafkaPort = firstKafkaPort;
    }

    public <K, V> KafkaConsumer<K, V> createKafkaConsumer(final Properties properties, String groupId) {
        Properties clusterProperties = this.kafkaCluster.useTo().getConsumerProperties(groupId, "", OffsetResetStrategy.EARLIEST);
        clusterProperties.putAll(properties);

        KafkaConsumer<K, V> consumer = new KafkaConsumer<>(clusterProperties);
        this.consumers.add(consumer);

        return consumer;
    }

    public <K, V> KafkaProducer<K, V> createKafkaProducer(final Properties properties) {
        Properties clusterProperties = this.kafkaCluster.useTo().getProducerProperties(null);
        clusterProperties.putAll(properties);

        KafkaProducer<K, V> producer = new KafkaProducer<>(clusterProperties);
        this.producers.add(producer);

        return producer;
    }

    public void closeKafkaProduces() {
        producers.forEach(this::closeKafkaProducer);
    }

    public void closeKafkaConsumers() {
        consumers.forEach(this::closeKafkaConsumer);
    }

    public void closeAll() {
        closeKafkaConsumers();
        closeKafkaProduces();
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try {
                    startupKafkaCluster();
                    base.evaluate();
                }
                finally {
                    tearDownKafkaCluster();
                }
            }
        };
    }

    private void startupKafkaCluster() {
        this.dataDir = Testing.Files.createTestingDirectory("cluster");
        this.kafkaCluster = new KafkaCluster()
                .usingDirectory(dataDir)
                .deleteDataPriorToStartup(true)
                .deleteDataUponShutdown(true)
                .withPorts(zookeeperPort, firstKafkaPort);

        dataDir.deleteOnExit();

        try {
            Properties properties = new Properties();

            properties.put("transaction.state.log.replication.factor", "1");
            properties.put("transaction.state.log.min.isr", "1");

            this.kafkaCluster.addBrokers(brokers)
                    .withKafkaConfiguration(properties)
                    .startup();
        } catch (IOException e) {
            LOG.error("Failed start broker: Dir {}, ZookeeperPort {}, FirstKafkaPort {}",
                    dataDir.getName(), zookeeperPort, firstKafkaPort);
        }
    }

    private void tearDownKafkaCluster() {
        closeAll();
        try {
            if (kafkaCluster != null) {
                kafkaCluster.shutdown();
                kafkaCluster = null;
            }
        }
        catch (Exception e) {
            LOG.error("Exception during KafkaCluster tearDown", e);
        }
        finally {
            boolean delete = dataDir.delete();

            if (!delete) {
                dataDir.deleteOnExit();
            }
        }
    }

    private void closeKafkaConsumer(KafkaConsumer<?, ?> consumer) {
        if (consumer != null) {
            consumer.wakeup();
            consumer.close(Duration.ofMillis(1));
        }
    }

    private void closeKafkaProducer(KafkaProducer<?, ?> producer) {
        if (producer != null) {
            producer.close(Duration.ofMillis(1));
        }
    }

}
