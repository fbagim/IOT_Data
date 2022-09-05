package com.iot.api.controller;

import java.text.SimpleDateFormat;
import java.util.Locale;

import com.iot.api.util.UtilConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;

import com.iot.api.model.EventDataRequest;
import com.iot.api.service.EventService;
import com.iot.api.model.EventDataResponse;

/**
 * EventController to provide API Data service to API consumers
 */
@RestController
@RequestMapping(value = "/events")
public class EventController {
    private final Logger logger = LoggerFactory.getLogger(EventController.class);
    @Autowired
    private EventService eventService;

    @Operation(summary = "Query IOT Data Events")
    @PostMapping(value = "/")
    public ResponseEntity<EventDataResponse> getEvents(@RequestBody EventDataRequest eventDataRequest) {
        // Validate input EventType - cannot be null
        if (eventDataRequest.getEventType().equals(null) || eventDataRequest.getEventType().isEmpty()) {
            logger.info(String.format("BAD_REQUEST -EventType Cannot be null"));
            return new ResponseEntity("EventType Cannot be null", HttpStatus.BAD_REQUEST);
        }
        // Validate input FormTime - cannot be null
        if (eventDataRequest.getFormTime().equals(null) || eventDataRequest.getFormTime().isEmpty()) {
            logger.info(String.format("BAD_REQUEST -From Date/time Cannot be null"));
            return new ResponseEntity("From Date/time Cannot be null", HttpStatus.BAD_REQUEST);
        }
        // Validate input ToTime - cannot be null
        if (eventDataRequest.getToTime().equals(null) || eventDataRequest.getToTime().isEmpty()) {
            logger.info(String.format("BAD_REQUEST -From Date/time Cannot be null"));
            return new ResponseEntity("To Date/time Cannot be null", HttpStatus.BAD_REQUEST);
        }
        try {
            // Validate input From-time to To-Time range is valid or not
            if (validateDateRange(eventDataRequest.getFormTime(), eventDataRequest.getToTime())) {
                EventDataResponse eventDataResponse = eventService.getAllEvents(eventDataRequest);
                if (eventDataResponse != null) {
                    return new ResponseEntity<>(eventDataResponse, HttpStatus.OK);
                } else {
                    logger.info(String.format("FORBIDDEN - eventDataResponse null"));
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                }
            } else {
                logger.info(String.format("BAD_REQUEST -From-TO Date/Time Range invalid"));
                return new ResponseEntity("From-TO Date/Time Range invalid", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            logger.info(String.format("INTERNAL_SERVER_ERROR -" + e.getMessage()));
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    /**
     * TO validate date range String fromDate, String toDate in yyyy-MM-dd'T'HH:mm:ss.SSS'Z' format
     *
     * @param fromDate
     * @param toDate
     * @return
     * @throws Exception
     */
    public boolean validateDateRange(String fromDate, String toDate) throws Exception {
        if (fromDate == null || fromDate.isEmpty() || toDate == null || toDate.isEmpty()) {
            logger.info(String.format("Error - Date inputs are invalid"));
            throw new Exception("Date inputs are invalid");
        }
        SimpleDateFormat sdf = new SimpleDateFormat(UtilConstant.ACCEPT_DATE_TIME_FORMAT, Locale.US);
        return sdf.parse(fromDate).before(sdf.parse(toDate));
    }
}