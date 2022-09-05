package com.iot.api.test.servcie;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.core.cql.CqlOperations;
import org.springframework.test.util.ReflectionTestUtils;

import com.iot.api.dao.EventDAO;
import com.iot.api.model.EventData;
import com.iot.api.model.EventDataRequest;
import com.iot.api.model.EventDataResponse;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class EventDAOTest {

    @MockBean
    CassandraTemplate template;

    @MockBean
    EventDAO eventDAO;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test Find All Events For ValidRequest")
    void testFindAllEventsByTimestampBetweenForValidRequest() throws Exception {
        Double resultValue = 4.3535353535;
        EventDataResponse eventDataResponse = new EventDataResponse();
        eventDataResponse.setEventType("TEMPERATURE");
        eventDataResponse.setClusterId(1L);
        eventDataResponse.setOperation("max");
        eventDataResponse.setFormTime("2022-08-31T03:21:01.183657Z");
        eventDataResponse.setToTime("2022-08-31T03:21:26.190439Z");
        eventDataResponse.setResultsValue(resultValue);

        EventDataRequest eventDataRequest = new EventDataRequest();
        eventDataRequest.setEventType("TEMPERATURE");
        eventDataRequest.setOperation("max");
        eventDataRequest.setToTime("2022-08-31T03:21:26.190439Z");
        eventDataRequest.setFormTime("2022-08-31T03:21:01.183657Z");
        eventDataRequest.setClusterId(1L);

        EventDAO eventDAO = new EventDAO();
        CassandraTemplate cassandraTemplate = Mockito.mock(CassandraTemplate.class);
        ReflectionTestUtils.setField(eventDAO, "template", cassandraTemplate);

        Mockito.when(cassandraTemplate.selectOne(Mockito.anyString(), ArgumentMatchers.any())).thenReturn(resultValue);
        EventDataResponse response = eventDAO.findAllEvents(eventDataRequest);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(response.getEventType(), eventDataRequest.getEventType());
        Assertions.assertEquals(response.getClusterId(), eventDataRequest.getClusterId());
        Assertions.assertEquals(response.getFormTime(), eventDataRequest.getFormTime());
        Assertions.assertEquals(response.getToTime(), eventDataRequest.getToTime());
        Assertions.assertEquals(response.getOperation(),  eventDataRequest.getOperation());
        Assertions.assertEquals(response.getResultsValue(),  resultValue);
    }
    @Test
    @DisplayName("Test FindAll Events For Empty OperationValue")
    void testFindAllEventsForEmptyOperationValue() throws Exception {

        EventDataRequest eventDataRequest = new EventDataRequest();
        eventDataRequest.setEventType("TEMPERATURE");
        eventDataRequest.setOperation("");
        eventDataRequest.setToTime("2022-08-31T03:21:26.190439Z");
        eventDataRequest.setFormTime("2022-08-31T03:21:01.183657Z");
        eventDataRequest.setClusterId(1L);

        EventDAO eventDAO = new EventDAO();
        EventDataResponse response = eventDAO.findAllEvents(eventDataRequest);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(response.getResultsValue(),  0,0);
    }
    @Test
    @DisplayName("Test FindAll Events For Null OperationValue")
    void testFindAllForNullOperationValue() throws Exception {
        EventDataRequest eventDataRequest = new EventDataRequest();
        eventDataRequest.setEventType("TEMPERATURE");
        eventDataRequest.setOperation(null);
        eventDataRequest.setToTime("2022-08-31T03:21:26.190439Z");
        eventDataRequest.setFormTime("2022-08-31T03:21:01.183657Z");
        eventDataRequest.setClusterId(1L);

        EventDAO eventDAO = new EventDAO();
        EventDataResponse response = eventDAO.findAllEvents(eventDataRequest);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(response.getResultsValue(),  0,0);
    }
    @Test
    @DisplayName("Test FindAll Invalid Operation Value")
    void testFindAllForInvalidOperationValue() throws Exception {
        EventDataRequest eventDataRequest = new EventDataRequest();
        eventDataRequest.setEventType("TEMPERATURE");
        eventDataRequest.setOperation("AWSDDDDD");
        eventDataRequest.setToTime("2022-08-31T03:21:26.190439Z");
        eventDataRequest.setFormTime("2022-08-31T03:21:01.183657Z");
        eventDataRequest.setClusterId(1L);

        EventDAO eventDAO = new EventDAO();

        Exception thrown = assertThrows(Exception.class, () ->  eventDAO.findAllEvents(eventDataRequest), "Expected to throw EventDataRequest is null, but it didn't");
        Assertions.assertTrue(thrown.getMessage().contains("Invalid OperationType"));

    }
    @Test
    @DisplayName("Test FindAll For Null Request")
    void testFindAllForNullRequest() throws Exception {
        EventDataRequest eventDataRequest = null;
        EventDAO eventDAO = new EventDAO();
        Exception thrown = assertThrows(Exception.class, () -> eventDAO.findAllEvents(eventDataRequest), "Expected to throw EventDataRequest is null, but it didn't");
        Assertions.assertTrue(thrown.getMessage().contains("EventDataRequest cannot be null"));
    }
    @Test
    @DisplayName("Test Find Median For Empty EventData")
    void testFindMedianEmptyEventData() throws Exception {
        List<EventData> eventData = new ArrayList<>();
        EventDAO eventDAO = new EventDAO();
        Exception thrown = assertThrows(Exception.class, () -> eventDAO.findMedian(eventData), "Expected to throw EventData is null, but it didn't");
        Assertions.assertTrue(thrown.getMessage().contains("EventData cannot br null or empty"));
    }
    @Test
    @DisplayName("Test Find Median For Null EventData")
    void testFindMedianNullEventData() throws Exception {
        List<EventData> eventData = null;
        EventDAO eventDAO = new EventDAO();
        Exception thrown = assertThrows(Exception.class, () -> eventDAO.findMedian(eventData), "Expected to throw EventData is null, but it didn't");
        Assertions.assertTrue(thrown.getMessage().contains("EventData cannot br null or empty"));
    }
    @Test
    @DisplayName("Test Find Median For Valid EventData")
    void testFindMedianForValidEventData() throws Exception {

        List<EventData> eventData =  new ArrayList<>();
        EventData eventData1 = new EventData();
        eventData1.setClusterId(1L);
        eventData1.setName("Living Room Temp");
        eventData1.setValue(new BigDecimal(20));

        EventData eventData2 = new EventData();
        eventData2.setClusterId(1L);
        eventData2.setName("Living Room Temp");
        eventData2.setValue(new BigDecimal(20));

        EventData eventData3 = new EventData();
        eventData3.setClusterId(1L);
        eventData3.setName("Living Room Temp");
        eventData3.setValue(new BigDecimal(20));

        EventData eventData4 = new EventData();
        eventData4.setClusterId(1L);
        eventData4.setName("Living Room Temp");
        eventData4.setValue(new BigDecimal(10));

        eventData.add(eventData1);
        eventData.add(eventData2);
        eventData.add(eventData3);
        eventData.add(eventData4);

        EventDAO eventDAO = new EventDAO();
        double expectedMedianValue = 20.0;
        double medianValue = eventDAO.findMedian(eventData);
        Assertions.assertEquals(expectedMedianValue,medianValue);
    }
    @Test
    @DisplayName("Test Find Median For Valid Zero EventData")
    void testFindMedianForValidZeroEventData() throws Exception {

        List<EventData> eventData =  new ArrayList<>();
        EventData eventData1 = new EventData();
        eventData1.setClusterId(1L);
        eventData1.setName("Living Room Temp");
        eventData1.setValue(new BigDecimal(0.0));

        EventData eventData2 = new EventData();
        eventData2.setClusterId(1L);
        eventData2.setName("Living Room Temp");
        eventData2.setValue(new BigDecimal(0.0));

        EventData eventData3 = new EventData();
        eventData3.setClusterId(1L);
        eventData3.setName("Living Room Temp");
        eventData3.setValue(new BigDecimal(0.0));

        eventData.add(eventData1);
        eventData.add(eventData2);
        eventData.add(eventData3);

        EventDAO eventDAO = new EventDAO();
        double expectedMedianValue = 0.0;
        double medianValue = eventDAO.findMedian(eventData);
        Assertions.assertEquals(expectedMedianValue,medianValue);

    }

    @Test
    @DisplayName("Test Query Builder For Valid Event Data Request")
    void testQueryBuilderForValidEventDataRequest() throws Exception {

        EventDataRequest eventDataRequest = new EventDataRequest();
        eventDataRequest.setEventType("TEMPERATURE");
        eventDataRequest.setOperation("max");
        eventDataRequest.setToTime("2022-08-31T03:21:26.190439Z");
        eventDataRequest.setFormTime("2022-08-31T03:21:01.183657Z");
        eventDataRequest.setClusterId(1L);

        EventDAO eventDAO = new EventDAO();
        String expectedResult = "SELECT max(value) From eventdata WHERE timestamp >='2022-08-31T03:21:01.183657Z' AND  timestamp <='2022-08-31T03:21:26.190439Z' AND type = 'TEMPERATURE' AND clusterId = 1 ALLOW FILTERING";
        String actualResul = eventDAO.queryBuilder(eventDataRequest);

        Assertions.assertNotNull(expectedResult,actualResul);
        Assertions.assertEquals(expectedResult,actualResul);
    }
    @Test
    @DisplayName("Test Query Builder For Operation Type Median With EventType NUll EventDataRequest")
    void testQueryBuilderForOperationTypeMedianWithEventTypeNUllEventDataRequest() throws Exception {

        EventDataRequest eventDataRequest = new EventDataRequest();
        eventDataRequest.setEventType(null);
        eventDataRequest.setOperation("median");
        eventDataRequest.setToTime("2022-08-31T03:21:26.190439Z");
        eventDataRequest.setFormTime("2022-08-31T03:21:01.183657Z");
        eventDataRequest.setClusterId(1L);

        EventDAO eventDAO = new EventDAO();
        String expectedResult = "SELECT median(value) From eventdata WHERE timestamp >='2022-08-31T03:21:01.183657Z' AND  timestamp <='2022-08-31T03:21:26.190439Z' AND clusterId = 1 ALLOW FILTERING";
        String actualResul = eventDAO.queryBuilder(eventDataRequest);

        Assertions.assertNotNull(expectedResult,actualResul);
        Assertions.assertEquals(expectedResult,actualResul);
    }
    @Test
    @DisplayName("Test Query Builder For Operation Type Median With Null ClusterID EventDataRequest")
    void testQueryBuilderForOperationTypeMedianWithNullClusterIDEventDataRequest() throws Exception {

        EventDataRequest eventDataRequest = new EventDataRequest();
        eventDataRequest.setEventType("TEMPERATURE");
        eventDataRequest.setOperation("median");
        eventDataRequest.setToTime("2022-08-31T03:21:26.190439Z");
        eventDataRequest.setFormTime("2022-08-31T03:21:01.183657Z");
        eventDataRequest.setClusterId(0L);

        EventDAO eventDAO = new EventDAO();
        String expectedResult = "SELECT median(value) From eventdata WHERE timestamp >='2022-08-31T03:21:01.183657Z' AND  timestamp <='2022-08-31T03:21:26.190439Z' AND type = 'TEMPERATURE'";
        String actualResul = eventDAO.queryBuilder(eventDataRequest);

        Assertions.assertNotNull(expectedResult,actualResul);
        Assertions.assertEquals(expectedResult,actualResul);
    }
    @Test
    @DisplayName("Test Query Builder For Operation Type Median EventDataRequest")
    void testQueryBuilderForOperationTypeMedianEventDataRequest() throws Exception {

        EventDataRequest eventDataRequest = new EventDataRequest();
        eventDataRequest.setEventType("TEMPERATURE");
        eventDataRequest.setOperation("median");
        eventDataRequest.setToTime("2022-08-31T03:21:26.190439Z");
        eventDataRequest.setFormTime("2022-08-31T03:21:01.183657Z");
        eventDataRequest.setClusterId(1L);

        EventDAO eventDAO = new EventDAO();
        String expectedResult = "SELECT median(value) From eventdata WHERE timestamp >='2022-08-31T03:21:01.183657Z' AND  timestamp <='2022-08-31T03:21:26.190439Z' AND type = 'TEMPERATURE' AND clusterId = 1 ALLOW FILTERING";
        String actualResul = eventDAO.queryBuilder(eventDataRequest);

        Assertions.assertNotNull(expectedResult,actualResul);
        Assertions.assertEquals(expectedResult,actualResul);
    }
    @Test
    @DisplayName("Test Query Builder For Operation Type MAX Event DataRequest")
    void testQueryBuilderForOperationTypeMAXEventDataRequest() throws Exception {

        EventDataRequest eventDataRequest = new EventDataRequest();
        eventDataRequest.setEventType("TEMPERATURE");
        eventDataRequest.setOperation("max");
        eventDataRequest.setToTime("2022-08-31T03:21:26.190439Z");
        eventDataRequest.setFormTime("2022-08-31T03:21:01.183657Z");
        eventDataRequest.setClusterId(1L);

        EventDAO eventDAO = new EventDAO();
        String expectedResult = "SELECT max(value) From eventdata WHERE timestamp >='2022-08-31T03:21:01.183657Z' AND  timestamp <='2022-08-31T03:21:26.190439Z' AND type = 'TEMPERATURE' AND clusterId = 1 ALLOW FILTERING";
        String actualResul = eventDAO.queryBuilder(eventDataRequest);

        Assertions.assertNotNull(expectedResult,actualResul);
        Assertions.assertEquals(expectedResult,actualResul);
    }
    @Test
    @DisplayName("Test Query Builder For Operation Type MAX With Null EventType EventDataRequest")
    void testQueryBuilderForOperationTypeMAXWithNullEventTypeEventDataRequest() throws Exception {

        EventDataRequest eventDataRequest = new EventDataRequest();
        eventDataRequest.setEventType("");
        eventDataRequest.setOperation("max");
        eventDataRequest.setToTime("2022-08-31T03:21:26.190439Z");
        eventDataRequest.setFormTime("2022-08-31T03:21:01.183657Z");
        eventDataRequest.setClusterId(1L);

        EventDAO eventDAO = new EventDAO();
        String expectedResult = "SELECT max(value) From eventdata WHERE timestamp >='2022-08-31T03:21:01.183657Z' AND  timestamp <='2022-08-31T03:21:26.190439Z' AND type = '' AND clusterId = 1 ALLOW FILTERING";
        String actualResul = eventDAO.queryBuilder(eventDataRequest);

        Assertions.assertNotNull(expectedResult,actualResul);
        Assertions.assertEquals(expectedResult,actualResul);
    }
    @Test
    @DisplayName("Test Query Builder For Operation Type MAX Without ClusterI EventDataRequest")
    void testQueryBuilderForOperationTypeMAXWithOutClusterIdEventDataRequest() throws Exception {

        EventDataRequest eventDataRequest = new EventDataRequest();
        eventDataRequest.setEventType("");
        eventDataRequest.setOperation("max");
        eventDataRequest.setToTime("2022-08-31T03:21:26.190439Z");
        eventDataRequest.setFormTime("2022-08-31T03:21:01.183657Z");
        eventDataRequest.setClusterId(0L);

        EventDAO eventDAO = new EventDAO();
        String expectedResult = "SELECT max(value) From eventdata WHERE timestamp >='2022-08-31T03:21:01.183657Z' AND  timestamp <='2022-08-31T03:21:26.190439Z' AND type = ''";
        String actualResul = eventDAO.queryBuilder(eventDataRequest);

        Assertions.assertNotNull(expectedResult,actualResul);
        Assertions.assertEquals(expectedResult,actualResul);
    }
    @Test
    @DisplayName("Test Query Builder For Operation Type MIN EventDataRequest")
    void testQueryBuilderForOperationTypeMINEventDataRequest() throws Exception {
        EventDataRequest eventDataRequest = new EventDataRequest();
        eventDataRequest.setEventType("TEMPERATURE");
        eventDataRequest.setOperation("min");
        eventDataRequest.setToTime("2022-08-31T03:21:26.190439Z");
        eventDataRequest.setFormTime("2022-08-31T03:21:01.183657Z");
        eventDataRequest.setClusterId(1L);

        EventDAO eventDAO = new EventDAO();
        String expectedResult = "SELECT min(value) From eventdata WHERE timestamp >='2022-08-31T03:21:01.183657Z' AND  timestamp <='2022-08-31T03:21:26.190439Z' AND type = 'TEMPERATURE' AND clusterId = 1 ALLOW FILTERING";
        String actualResul = eventDAO.queryBuilder(eventDataRequest);

        Assertions.assertNotNull(expectedResult,actualResul);
        Assertions.assertEquals(expectedResult,actualResul);
    }
    @Test
    @DisplayName("Test Query Builder For Operation Type AVG EventDataRequest")
    void testQueryBuilderForOperationTypeAVGEventDataRequest() throws Exception {

        EventDataRequest eventDataRequest = new EventDataRequest();
        eventDataRequest.setEventType("TEMPERATURE");
        eventDataRequest.setOperation("avg");
        eventDataRequest.setToTime("2022-08-31T03:21:26.190439Z");
        eventDataRequest.setFormTime("2022-08-31T03:21:01.183657Z");
        eventDataRequest.setClusterId(1L);

        EventDAO eventDAO = new EventDAO();
        String expectedResult = "SELECT avg(value) From eventdata WHERE timestamp >='2022-08-31T03:21:01.183657Z' AND  timestamp <='2022-08-31T03:21:26.190439Z' AND type = 'TEMPERATURE' AND clusterId = 1 ALLOW FILTERING";
        String actualResul = eventDAO.queryBuilder(eventDataRequest);

        Assertions.assertNotNull(expectedResult,actualResul);
        Assertions.assertEquals(expectedResult,actualResul);
    }
    @Test
    @DisplayName("Test Query Builder For Null EventData")
    void testQueryBuilderForNullEventDataRequest() throws Exception {
        EventDAO eventDAO = new EventDAO();
        String expectedResult = null;
        String actualResul = eventDAO.queryBuilder(null);
        Assertions.assertEquals(expectedResult,actualResul);
    }
    @Test
    @DisplayName("Test Save for Valid EventData")
    void testSaveEvent() throws Exception {
        CassandraTemplate cassandraTemplate = Mockito.mock(CassandraTemplate.class);
        EventDAO eventDAO = new EventDAO();
        ReflectionTestUtils.setField(eventDAO, "template", cassandraTemplate);
        CqlOperations cqlOperations = Mockito.mock(CqlOperations.class);

        String inputString = "{\"id\":1,\"type\":\"TEMPERATURE\",\"name\":\"Living Room Temp\",\"clusterId\":1,\"timestamp\":\"2022-08-31T18:34:19.086568Z\",\"value\":67.35184511064352,\"initialized\":true}";
        Mockito.when(cassandraTemplate.getCqlOperations()).thenReturn(cqlOperations);
        Mockito.when(cassandraTemplate.getCqlOperations().execute(Mockito.anyString())).thenReturn(true);
        String expectedResult = inputString;
        Boolean actualResul = eventDAO.saveEvent(inputString);

        Assertions.assertTrue(actualResul);
    }
    @Test
    @DisplayName("Test Save for Null EventData")
    void testSaveForNullEvent() throws Exception {
        EventDAO eventDAO = new EventDAO();
        String inputString = null;
        String expectedResult = inputString;
        Boolean actualResul = eventDAO.saveEvent(inputString);
        Assertions.assertEquals(false,actualResul);
    }
}
