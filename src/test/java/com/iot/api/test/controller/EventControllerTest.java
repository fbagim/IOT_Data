package com.iot.api.test.controller;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.iot.api.config.KafkaConsumerConfig;
import com.iot.api.util.JsonParser;
import org.apache.catalina.security.SecurityConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import com.iot.api.service.EventService;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.iot.api.controller.EventController;
import com.iot.api.dao.EventDAO;
import com.iot.api.model.EventDataRequest;
import com.iot.api.model.EventDataResponse;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(EventController.class)
@ContextConfiguration
@Import(SecurityConfig.class)
public class EventControllerTest {
    private final Logger logger = LoggerFactory.getLogger(KafkaConsumerConfig.class);
    @MockBean
    EventService eventService;
    @Autowired
    MockMvc mockMvc;

    @MockBean
    EventDAO eventDAO;

    @MockBean
    JsonParser jsonParser;

    @MockBean
    CassandraTemplate template;

    EventController eventController;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        eventController = new EventController();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"}, password = "root")
    @DisplayName("Test Get Events For Valid JSON Payload")
    public void testGetEventsForValidJSONPayload() throws Exception {
        EventDataResponse eventDataResponse = new EventDataResponse();
        eventDataResponse.setEventType("TEMPERATURE");
        eventDataResponse.setClusterId(1L);
        eventDataResponse.setOperation("max");
        eventDataResponse.setFormTime("2022-08-31T03:21:01.183657Z");
        eventDataResponse.setToTime("2022-08-31T03:21:26.190439Z");
        eventDataResponse.setResultsValue(4.3535353535);

        Mockito.when(eventService.getAllEvents(any(EventDataRequest.class))).thenReturn(eventDataResponse);
        mockMvc.perform(MockMvcRequestBuilders.post("/events/")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content("{\n" + "  \"eventType\": \"TEMPERATURE\",\n" + "  \"formTime\": \"2022-08-31T03:21:01.183657Z\",\n" + "  \"toTime\": \"2022-08-31T03:21:26.190439Z\",\n" + "  \"operation\": \"max\",\n" + "  \"clusterId\": 1\n" + "}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.eventType").value("TEMPERATURE"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.formTime").value("2022-08-31T03:21:01.183657Z"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.toTime").value("2022-08-31T03:21:26.190439Z"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.operation").value("max"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.clusterId").value("1"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"}, password = "root")
    @DisplayName("Test Get Events For Internal Server Error")
    public void testGetEventsForInternalServerError() throws Exception {
        Mockito.when(eventService.getAllEvents(any(EventDataRequest.class))).thenReturn(null);
        mockMvc.perform(MockMvcRequestBuilders.post("/events/")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content("{\n" + "  \"eventType\": \"TEMPERATURE\",\n" + "  \"formTime\": \"2022-08-31T03:21:01.183657Z\",\n" + "  \"toTime\": \"2022-08-31T03:21:26.190439Z\",\n" + "  \"operation\": \"max\",\n" + "  \"clusterId\": 1\n" + "}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"}, password = "root")
    @DisplayName("Test Get Events For InValid JSON Payload")
    public void testGetEventsForInValidJSONPayload() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/events/")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content("{rrewrwrr}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
    @Test
    @DisplayName("Test Get Events For InValid Auth")
    public void testGetEventsForInValidAuth() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/events/")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content("{rrewrwrr}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"}, password = "root")
    @DisplayName("Test Get Events For Empty Payload")
    public void testGetEventsForEmptyPayload() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/events/")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content("{\n" + "  \"eventType\": \"TEMPERATURE\",\n" + "  \"formTime\": \"2022-08-31T03:21:01.183657Z\",\n" + "  \"toTime\": \"2022-08-31T03:21:26.190439Z\",\n" + "  \"operation\": \"max\",\n" + "  \"clusterId\": 1\n" + "}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"}, password = "root")
    @DisplayName("Test Get Events For EventType Null Payload")
    public void testGetEventsForEventTypeNullPayload() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/events/")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content("{\n" + "  \"eventType\": \"\",\n" + "  \"formTime\": \"2022-08-31T03:21:01.183657Z\",\n" + "  \"toTime\": \"2022-08-31T03:21:26.190439Z\",\n" + "  \"operation\": \"max\",\n" + "  \"clusterId\": 1\n" + "}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"}, password = "root")
    @DisplayName("Test Get Events For FormTime Null Payload")
    public void testGetEventsForFormTimeNullPayload() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/events/")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content("{\n" + "  \"eventType\": \"TEMPERATURE\",\n" + "  \"formTime\": \"\",\n" + "  \"toTime\": \"2022-08-31T03:21:26.190439Z\",\n" + "  \"operation\": \"max\",\n" + "  \"clusterId\": 1\n" + "}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"}, password = "root")
    @DisplayName("Test Get Events For ToTime Null Payload")
    public void testGetEventsForToTimeNullPayload() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/events/")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content("{\n" + "  \"eventType\": \"TEMPERATURE\",\n" + "  \"formTime\": 2022-08-31T03:21:26.190439Z\"\",\n" + "  \"toTime\": \"\",\n" + "  \"operation\": \"max\",\n" + "  \"clusterId\": 1\n" + "}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("Test Validate DateRange For Valid Dates")
    void testValidateDateRangeForValidDates() throws Exception {
        Boolean result = eventController.validateDateRange("2022-08-31T03:21:01.183657Z", "2022-08-31T03:21:26.190439Z");
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("Test Validate DateRange For InValid DateRange")
    void testValidateDateRangeForInValidDateRange() throws Exception {
        Boolean result = eventController.validateDateRange("2022-08-31T03:21:26.190439Z", "2022-08-31T03:21:01.183657Z");
        Assertions.assertNotNull(result);
        Assertions.assertFalse(result);
    }

    @Test
    @DisplayName("Test Validate DateRange For Both Empty DateRange")
    void testValidateDateRangeForBothEmptyDateRange() throws Exception {
        Exception thrown = assertThrows(Exception.class, () -> eventController.validateDateRange("", ""), "Expected to throw EventData is null, but it didn't");
        Assertions.assertNotNull(thrown);
        Assertions.assertTrue(thrown.getMessage().contains("Date inputs are invalid"));
    }

    @Test
    @DisplayName("Test Validate DateRange For Both Null DateRange")
    void testValidateDateRangeForBothNullDateRange() throws Exception {
        Exception thrown = assertThrows(Exception.class, () -> eventController.validateDateRange(null, null), "Expected to throw EventData is null, but it didn't");
        Assertions.assertNotNull(thrown);
        Assertions.assertTrue(thrown.getMessage().contains("Date inputs are invalid"));
    }

    @Test
    @DisplayName("Test Validate DateRange For Empty FromDate")
    void testValidateDateRangeForEmptyFromDate() throws Exception {
        Exception thrown = assertThrows(Exception.class, () -> eventController.validateDateRange("", "2022-08-31T03:21:01.183657Z"), "Expected to throw EventData is null, but it didn't");
        Assertions.assertNotNull(thrown);
        Assertions.assertTrue(thrown.getMessage().contains("Date inputs are invalid"));
    }

    @Test
    @DisplayName("Test Validate DateRange For Empty ToDate")
    void testValidateDateRangeForEmptyToDate() throws Exception {
        Exception thrown = assertThrows(Exception.class, () -> eventController.validateDateRange("2022-08-31T03:21:01.183657Z", ""), "Expected to throw EventData is null, but it didn't");
        Assertions.assertNotNull(thrown);
        Assertions.assertTrue(thrown.getMessage().contains("Date inputs are invalid"));
    }
}
