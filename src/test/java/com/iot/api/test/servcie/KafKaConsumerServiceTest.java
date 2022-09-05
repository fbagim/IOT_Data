package com.iot.api.test.servcie;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.iot.api.model.EventData;
import com.iot.api.service.EventService;
import com.iot.api.service.KafKaConsumerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class KafKaConsumerServiceTest {
    @MockBean
    EventService eventService;

    @MockBean
    KafKaConsumerService kafKaConsumerService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test Consume For Empty Stream")
    void testConsumeForEmptyStream() throws Exception {
        EventData eventData = new EventData();
        eventData.setClusterId(1L);
        eventData.setName("Living Room Temp");
        eventData.setValue(new BigDecimal(93.11236279170537));
        kafKaConsumerService = new KafKaConsumerService();

        ReflectionTestUtils.setField(kafKaConsumerService, "eventService", eventService);
        Acknowledgment acknowledgment = Mockito.mock(Acknowledgment.class);
        eventService = Mockito.mock(EventService.class);

        Exception thrown = assertThrows(Exception.class, () -> kafKaConsumerService.consume("", acknowledgment, 1), "Expected to throw EventDataRequest is null, but it didn't");
        Assertions.assertTrue(thrown.getMessage().contains("Error Reading Event Data"));
    }

    @Test
    @DisplayName("Test Consume For Invalid Stream")
    void testConsumeForInvalidStream() throws Exception {
        EventData eventData = new EventData();
        eventData.setClusterId(1L);
        eventData.setName("Living Room Temp");
        eventData.setValue(new BigDecimal(93.11236279170537));
        kafKaConsumerService = new KafKaConsumerService();

        ReflectionTestUtils.setField(kafKaConsumerService, "eventService", eventService);
        Acknowledgment acknowledgment = Mockito.mock(Acknowledgment.class);
        eventService = Mockito.mock(EventService.class);

        String errorInput = "<xml>qeqeqweqweqweqe</xml>";
        Exception thrown = assertThrows(Exception.class, () -> kafKaConsumerService.consume(errorInput, acknowledgment, 1), "Expected to throw EventDataRequest is null, but it didn't");
        Assertions.assertTrue(thrown.getMessage().startsWith("Error Parsing Event Data Event Data Processing"));
    }

    @Test
    @DisplayName("Test Consume For Null Stream")
    void testConsumeForNullStream() throws Exception {
        EventData eventData = new EventData();
        eventData.setClusterId(1L);
        eventData.setName("Living Room Temp");
        eventData.setValue(new BigDecimal(93.11236279170537));
        kafKaConsumerService = new KafKaConsumerService();

        ReflectionTestUtils.setField(kafKaConsumerService, "eventService", eventService);
        Acknowledgment acknowledgment = Mockito.mock(Acknowledgment.class);
        eventService = Mockito.mock(EventService.class);

        String errorInput = null;
        Exception thrown = assertThrows(Exception.class, () -> kafKaConsumerService.consume(errorInput, acknowledgment, 1), "Expected to throw EventDataRequest is null, but it didn't");
        Assertions.assertTrue(thrown.getMessage().contains("Error Reading Event Data"));
    }

    @Test
    @DisplayName("Test Consume For Valid Stream")
    void testConsumeForValidStream() throws Exception {
        EventData eventData = new EventData();
        eventData.setClusterId(1L);
        eventData.setName("Living Room Temp");
        eventData.setValue(new BigDecimal(93.11236279170537));
        kafKaConsumerService = new KafKaConsumerService();
        eventService = Mockito.mock(EventService.class);
        ReflectionTestUtils.setField(kafKaConsumerService, "eventService", eventService);
        Acknowledgment acknowledgment = Mockito.mock(Acknowledgment.class);

        String inputString = "{\"id\":1,\"type\":\"TEMPERATURE\",\"name\":\"Living Room Temp\",\"clusterId\":1,\"timestamp\":\"2022-08-31T18:34:19.086568Z\",\"value\":67.35184511064352,\"initialized\":true}";
        Mockito.when(eventService.saveEvent(ArgumentMatchers.any(String.class))).thenReturn(true);
        assertDoesNotThrow(() -> kafKaConsumerService.consume(inputString, acknowledgment, 1), "Expected does not to throw Exceptions");
    }

}
