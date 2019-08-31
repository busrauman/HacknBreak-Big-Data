package kafkastreams;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;

import java.time.Duration;
import java.util.Properties;

public class AnomaliDetection {


    public static void main(String[] args) {

        Properties config = new Properties();
        // bu iki değer kafka clusterımızda unique değerler oluşturmamızı sağlayacak yani belli verileri cacheleyip çalıştığı için
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "anomaly-detection-example");
        config.put(StreamsConfig.CLIENT_ID_CONFIG, "anomaly-detection-lambda-example-client");


        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        // Set the commit interval to 500ms so that any changes are flushed frequently. The low latency
        // would be important for anomaly detection.
        // normalde 3000 ms olan refresh mekanizmasını 500ms e alalım ki daha verimli bir tesbit yapalım
        config.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 500);


        final StreamsBuilder builder = new StreamsBuilder();

        final KStream<String, String> clickStream = builder.stream("user-click-stream");

        final KTable<Windowed<String>, Long> anomalousUsers = clickStream
                .map((ignoredKey, username) -> new KeyValue<>(username, username))
                .groupByKey()
                .windowedBy(TimeWindows.of(Duration.ofMinutes(1)))
                .count()
                .filter((windowedUser,count) -> count > 3);

            KStream<String , Long> detectedUsers =  anomalousUsers.toStream().filter(( windowedUser, count ) -> count != null)
                .map(( windowedUserId, count ) -> new KeyValue<>(windowedUserId.toString(), count));

            detectedUsers.to("anomaly-users", Produced.with(Serdes.String(),Serdes.Long()));

        final KafkaStreams streams = new KafkaStreams(builder.build(), config);

        streams.cleanUp();
        streams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

    }
}
