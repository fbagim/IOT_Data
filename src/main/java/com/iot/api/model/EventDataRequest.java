package com.iot.api.model;

import java.io.Serializable;
public class EventDataRequest implements Serializable {
    private String eventType;
    private String formTime;
    private String toTime;
    private String operation;
    private Long clusterId;

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getFormTime() {
        return formTime;
    }

    public void setFormTime(String formTime) {
        this.formTime = formTime;
    }

    public String getToTime() {
        return toTime;
    }

    public void setToTime(String toTime) {
        this.toTime = toTime;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }
}
