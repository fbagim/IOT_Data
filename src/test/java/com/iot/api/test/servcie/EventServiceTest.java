package com.iot.api.test.servcie;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

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
import org.springframework.test.util.ReflectionTestUtils;

import com.iot.api.dao.EventDAO;
import com.iot.api.model.EventData;
import com.iot.api.model.EventDataRequest;
import com.iot.api.model.EventDataResponse;
import com.iot.api.service.EventService;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {
    @MockBean
    EventService eventService;

    @MockBean
    EventDAO eventDAO;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test Get All Events")
    void testGetAllEvents() throws Exception {

        EventDataResponse eventDataResponse = new EventDataResponse();
        eventDataResponse.setEventType("TEMPERATURE");
        eventDataResponse.setClusterId(1L);
        eventDataResponse.setOperation("max");
        eventDataResponse.setFormTime("2022-08-31T03:21:01.183657Z");
        eventDataResponse.setToTime("2022-08-31T03:21:26.190439Z");
        eventDataResponse.setResultsValue(4.3535353535);
        EventService eventServiceReal = new EventService();

        EventDAO eventDAO = Mockito.mock(EventDAO.class);
        ReflectionTestUtils.setField(eventServiceReal, "eventDAO", eventDAO);
        EventDataRequest eventDataRequest = Mockito.spy(new EventDataRequest());

        Mockito.when(eventDAO.findAllEvents(ArgumentMatchers.any())).thenReturn(eventDataResponse);
        EventDataResponse response = null;
        try {
            response = eventServiceReal.getAllEvents(eventDataRequest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Assertions.assertNotNull(response);
    }

    @Test
    @DisplayName("Test Get All For Null Event DataRequest")
    void testGetAllEventsForNullEventDataRequest() throws Exception {

        EventDataResponse eventDataResponse = new EventDataResponse();
        eventService = new EventService();

        EventDAO eventDAO = Mockito.spy(new EventDAO());
        ReflectionTestUtils.setField(eventService, "eventDAO", eventDAO);

        Exception thrown = assertThrows(Exception.class, () -> eventService.getAllEvents(null), "Expected to throw EventDataRequest is null, but it didn't");
        Assertions.assertTrue(thrown.getMessage().contains("EventDataRequest is null"));

    }

    @Test
    @DisplayName("Test Save Events For Valid Json")
    void testSaveEvents() throws Exception {
        String inputString = "{\"id\":1,\"type\":\"TEMPERATURE\",\"name\":\"Living Room Temp\",\"clusterId\":1,\"timestamp\":\"2022-08-31T18:34:19.086568Z\",\"value\":67.35184511064352,\"initialized\":true}";
        eventService = new EventService();

        EventDAO eventDAO = Mockito.mock(EventDAO.class);
        ReflectionTestUtils.setField(eventService, "eventDAO", eventDAO);

        EventData eventDataSave = Mockito.spy(new EventData());
        Mockito.when(eventDAO.saveEvent(ArgumentMatchers.any(String.class))).thenReturn(true);

        Boolean response;
        try {
            response = eventService.saveEvent(inputString);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Assertions.assertNotNull(response);
    }

    @Test
    @DisplayName("Test Save Events For Null EventData")
    void testSaveForNullEvents() throws Exception {

        EventData eventData = new EventData();
        eventData.setClusterId(1L);
        eventData.setName("Living Room Temp");
        eventData.setValue(new BigDecimal(93.11236279170537));

        eventService = new EventService();
        EventDAO eventDAO = Mockito.mock(EventDAO.class);
        ReflectionTestUtils.setField(eventService, "eventDAO", eventDAO);

        Exception thrown = assertThrows(Exception.class, () -> eventService.getAllEvents(null), "Expected to throw EventDataRequest is null, but it didn't");
        Assertions.assertTrue(thrown.getMessage().contains("EventDataRequest is null"));
    }
}
