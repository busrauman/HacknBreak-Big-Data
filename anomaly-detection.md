
bin/kafka-topics.sh --create --topic user-click-stream  --zookeeper localhost:2182 --partitions 1 --replication-factor 1

bin/kafka-topics.sh --create --topic anomaly-users  --zookeeper localhost:2182 --partitions 1 --replication-factor 1
