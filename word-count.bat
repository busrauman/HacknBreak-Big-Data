rem create input topic with two partitions
bin\windows\kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 2 --topic word-count-input

rem create output topic
bin\windows\kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 2 --topic word-count-output

rem launch a Kafka consumer
bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9092 ^
    --topic word-count-output ^
    --from-beginning ^
    --formatter kafka.tools.DefaultMessageFormatter ^
    --property print.key=true ^
    --property print.value=true ^
    --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer ^
    --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer

rem launch the streams application

rem then produce data to it
bin\windows\kafka-console-producer.bat --broker-list localhost:9092 --topic word-count-input

rem package your application as a fat jar
mvn clean package

rem run your fat jar
java -jar <your jar here>.jar

rem list all topics that we have in Kafka (so we can observe the internal topics)
bin\windows\kafka-topics.bat --list --zookeeper localhost:2181
