package com.iot.api.service;

import com.iot.api.model.EventDataRequest;
import com.iot.api.model.EventDataResponse;
import com.iot.api.dao.EventDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * EventService used to support EventDataRequest /EventDataResponse and DAO operations
 */
@Service
public class EventService {
    private final Logger logger = LoggerFactory.getLogger(EventService.class);
    @Autowired
    private EventDAO eventDAO;

    /**
     * Use to retrive all AllEvents for given EventDataRequest criteria 's from DAO/ database
     * @param eventDataRequest
     * @return EventDataResponse
     * @throws Exception
     */
    public EventDataResponse getAllEvents(EventDataRequest eventDataRequest) throws Exception {
        if (eventDataRequest == null) {
            logger.info(String.format("EventService -> EventDataRequest is null"));
            throw new Exception("EventDataRequest is null");
        }
        return eventDAO.findAllEvents(eventDataRequest);
    }

    /**
     * Use to save all Events to DAO/ database
     * @param eventData
     * @return EventData
     * @throws Exception
     */
    public Boolean saveEvent(String eventData) throws Exception {
        if (eventData == null) {
            throw new Exception("EventData is null");
        }
        return eventDAO.saveEvent(eventData);
    }
}
