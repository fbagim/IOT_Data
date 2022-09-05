package com.iot.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import com.iot.api.model.EventData;
import com.iot.api.util.JsonParser;
/**
 * KafKaConsumerService - use to consume and save captured events from producer
 */
@Service
public class KafKaConsumerService {
    private final Logger logger = LoggerFactory.getLogger(KafKaConsumerService.class);
    @Autowired
    private EventService eventService;

    /** KafkaListener to consume data from kafka stream producer
     * @param eventData      consume eventData from producer
     * @param acknowledgment acknowledgment once after consume data
     * @param partition      partition identification for consume data
     * @throws Exception
     */
    @KafkaListener(topics = "${general.topic.name}", groupId = "${general.topic.group.id}")
    public void consume(String eventData, Acknowledgment acknowledgment, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) throws Exception {
        if (eventData == null || eventData.isEmpty()) {
            logger.info(" Error Reading Event Data - Partition: {} receivedMessagedCount:{}", partition, eventData);
            throw new Exception("Error Reading Event Data" + eventData);
        }
        logger.info("Partition: {} receivedMessagedCount:{}", partition, eventData);
          if(consumeData(eventData)){
            acknowledgment.acknowledge();
          };
        logger.info(String.format("sensorData created -> %s", eventData));
    }

    /**
     * consumeData convert and saved consume eventData from producer
     * @param data
     * @throws Exception
     */
    private Boolean consumeData(String data) throws Exception {
        try {
            // Convert and validate json string with defined schema
            String eventData = new JsonParser().validateEventData(data);
            if (eventData != null) {
                Boolean result = eventService.saveEvent(eventData);
                logger.info(String.format("sensorData created -> %s", data));
                return result;
            }
        } catch (Exception e) {
            logger.info(String.format("Error Parsing Event Data-> %s", e.getMessage()));
            throw new Exception("Error Parsing Event Data " + e.getMessage());
        }
        return false;
    }
}
