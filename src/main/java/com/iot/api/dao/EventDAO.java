package com.iot.api.dao;

import com.iot.api.model.EventData;
import com.iot.api.model.EventDataRequest;
import com.iot.api.model.EventDataResponse;
import com.iot.api.model.OperationType;

import com.iot.api.util.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * EventDAO to provide data access for Event API service
 */
@Component
public class EventDAO {
    private final Logger logger = LoggerFactory.getLogger(EventDAO.class);
    @Autowired
    CassandraTemplate template;
    private JsonParser jsonParser =new JsonParser();
    /**
     * @param eventDataRequest use to query All Events By given criteria's
     * @return EventDataResponse
     * @throws Exception
     */
    public EventDataResponse findAllEvents(EventDataRequest eventDataRequest) throws Exception {
        if (eventDataRequest == null) {
            logger.info(String.format("Data Access error -> %s", "EventDataRequest cannot be null"));
            throw new Exception("EventDataRequest cannot be null");
        }
        // create EventDataResponse payload object by requested data
        EventDataResponse eventDataResponse = new EventDataResponse();
        eventDataResponse.setEventType(eventDataRequest.getEventType());
        eventDataResponse.setClusterId(eventDataRequest.getClusterId());
        eventDataResponse.setOperation(eventDataRequest.getOperation());
        eventDataResponse.setFormTime(eventDataRequest.getFormTime());
        eventDataResponse.setToTime(eventDataRequest.getToTime());
        try {
            // validate Operation type
            if (eventDataRequest.getOperation() == null || eventDataRequest.getOperation().isEmpty()) {
                eventDataResponse.setResultsValue(0.0);
                return eventDataResponse;
            } else {
                String cql = queryBuilder(eventDataRequest);
                // validate Operation type if AVG ,MAX or MIN
                if (eventDataRequest.getOperation().equalsIgnoreCase(OperationType.AVG.getType()) || eventDataRequest.getOperation().equalsIgnoreCase(OperationType.MAX.getType()) || eventDataRequest.getOperation().equalsIgnoreCase(OperationType.MIN.getType())) {
                    Double aDouble = template.selectOne(cql, Double.class);
                    eventDataResponse.setResultsValue(aDouble);
                    return eventDataResponse;
                    // validate Operation type if MEDIAN
                } else if (eventDataRequest.getOperation().equalsIgnoreCase(OperationType.MEDIAN.getType())) {
                    List<EventData> eventData = template.select(cql, EventData.class);
                    Double median = findMedian(eventData);
                    eventDataResponse.setResultsValue(median);
                    return eventDataResponse;
                } else {
                    logger.info(String.format("Data Access error -> %s", "Invalid OperationType"));
                    throw new Exception("Invalid OperationType");
                }
            }
        } catch (Exception e) {
            logger.info(String.format("Data Access error -> %s", e.getMessage()));
            throw new Exception("Data Access error" + e.getMessage());
        }
    }

    /**
     * used to calculate Median value by given dataset
     *
     * @param eventData List<EventData>
     * @return double
     * @throws Exception
     */
    public double findMedian(List<EventData> eventData) throws Exception {
        if (eventData == null || eventData.size() == 0) {
            logger.info(String.format("Data Access error -> %s", "Invalid EventData"));
            throw new Exception("EventData cannot br null or empty");
        }
        List<BigDecimal> list = eventData.stream().map(x -> x.getValue()).sorted(Comparator.naturalOrder()).collect(Collectors.toList());
        Double median;
        if (list.size() % 2 == 0) {
            median = (list.get(list.size() / 2).doubleValue() + list.get(list.size() / 2 - 1).doubleValue()) / 2;
        } else {
            median = list.get(list.size() / 2).doubleValue();
        }
        return median;
    }

    /**
     * used to generate Query for given EventDataRequest criteria's
     *
     * @param eventDataRequest request data for query criteria
     * @return String query
     * @throws Exception
     */
    public String queryBuilder(EventDataRequest eventDataRequest) throws Exception {
        if (eventDataRequest != null) {
            StringBuilder stringBuilder = new StringBuilder();
            // Build Query for Min,Max and Average
            if (!eventDataRequest.getOperation().equals(OperationType.MEDIAN.getType())) {
                stringBuilder.append("SELECT " + eventDataRequest.getOperation().trim() + "(value) From eventdata WHERE timestamp >='" + eventDataRequest.getFormTime().trim() + "' AND  timestamp <='" + eventDataRequest.getToTime().trim() + "'");
                if (eventDataRequest.getEventType() != null) {
                    stringBuilder.append(" AND type = '" + eventDataRequest.getEventType().trim() + "'");
                }
                if (eventDataRequest.getClusterId() > 0) {
                    stringBuilder.append(" AND clusterId = " + eventDataRequest.getClusterId() + " ALLOW FILTERING");
                }
            } else {
                // Build Query for MEDIAN Operation
                stringBuilder.append("SELECT * From eventdata WHERE timestamp >='" + eventDataRequest.getFormTime().trim() + "' AND  timestamp <='" + eventDataRequest.getToTime().trim() + "'");
                if (eventDataRequest.getEventType() != null) {
                    stringBuilder.append(" AND type = '" + eventDataRequest.getEventType().trim() + "'");
                }
                if (eventDataRequest.getClusterId() > 0) {
                    stringBuilder.append(" AND clusterId = " + eventDataRequest.getClusterId() + " ALLOW FILTERING");
                }
            }
            return stringBuilder.toString();
        }
        return null;
    }

    /**
     * @param eventData save EventData to casendra data stores
     * @return EventData
     * @throws Exception
     */
    public Boolean saveEvent(String jsonString) throws Exception {
        try {
            if(jsonString==null || jsonString.isEmpty()){
                logger.info(String.format("EventData Saving error -> %s","Input jsonString empty"));
                return false;
            }
            Boolean result = template.getCqlOperations().execute("INSERT INTO eventdata JSON '"+jsonString.trim()+"'");
            return result;
        } catch (Exception e) {
            logger.info(String.format("EventData Saving error -> %s", e.getMessage()));
            throw new Exception("EventData Saving error ->" + e);
        }
    }
}
