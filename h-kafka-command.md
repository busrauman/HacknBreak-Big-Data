### Topic List

kafka-topics.sh --list --zookeeper localhost:2181

### Create Topic
kafka-topics.sh --create --topic test-topic  --zookeeper localhost:2182 --partitions 1 --replication-factor 1


# Topic publish message
kafka-console-producer.sh --topic test-topic --broker-list localhost:9092 \
--property parse.key=true \
--property key.separator=,

# Listen Topic
kafka-console-consumer.sh --bootstrap-server localhost:9092 --from-beginning --topic test-topic \
--property print.key=true \
--property key.seperator=, \
--property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
--property value.deserializer=org.apache.kafka.common.serialization.StringDeserializer


# Topic Log Compacted

kafka-topics --create --zookeeper zookeeper:2181 --topic latest-product-price --replication-factor 1 --partitions 1 --config cleanup.policy=compact --config delete.retention.ms=100  --config segment.ms=100 --config min.cleanable.dirty.ratio=0.01
