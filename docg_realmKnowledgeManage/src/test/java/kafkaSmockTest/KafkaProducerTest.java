package kafkaSmockTest;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class KafkaProducerTest {

    public static void main(String[] args){
        Properties kafkaProps = new Properties();
        kafkaProps.put("bootstrap.servers", "localhost:9092");
        kafkaProps.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
        kafkaProps.put("value.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProps.put("transactional.id","id0001");
        Producer producer = new KafkaProducer<String, String>(kafkaProps);
        producer.initTransactions();
        producer.beginTransaction();
        ProducerRecord<String, String> record = new ProducerRecord<>("CustomerCountry", "Precision Products12","France512189901234567890");
       try {
            Object result = producer.send(record).get();
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        producer.commitTransaction();
        producer.close();
    }
}
