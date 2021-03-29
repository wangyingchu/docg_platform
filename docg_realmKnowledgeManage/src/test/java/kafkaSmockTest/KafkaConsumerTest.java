package kafkaSmockTest;

import org.apache.kafka.clients.consumer.CommitFailedException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class KafkaConsumerTest {

    public static void main(String[] args){
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "CountryCounter");
        props.put("key.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String,String>(props);
        consumer.subscribe(Collections.singletonList("CustomerCountry"));
        System.out.println(consumer.listTopics());

/*
        //while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records)
            {
                System.out.printf("topic = %s, partition = %s,offset = %d, customer = %s, country = %s\n",
                record.topic(), record.partition(), record.offset(),
                        record.key(), record.value());
            }
            try {
                consumer.commitSync();
            } catch (CommitFailedException e) {
                //log.error("commit failed", e);
            }
        //}


        consumer.close();
*/


        try {
            while (true) {
                //System.out.println("================");
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(10));
                //System.out.println(records.count());
                //System.out.println("================");
                for (ConsumerRecord<String, String> record : records) {
                    //log.info("topic = %s, partition = %s, offset = %d,customer = %s, country = %s\n",
                    //record.topic(), record.partition(), record.offset(),
                     //       record.key(), record.value());
                    System.out.println(record.key()+" "+record.value()+":"+record.offset());
                }
            }
        } finally {
            consumer.close();
        }


    }
}
