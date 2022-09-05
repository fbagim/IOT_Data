package com.iot.api.util;

import com.google.gson.Gson;
import com.iot.api.model.EventData;

/**
 * JsonParser to Convert and validate json string using Gson
 */
public class JsonParser {
    private Gson gson = new Gson();

    /**
     * parseJsonToEvent to convert JSON string to EventData objects
     *
     * @param json
     * @return EventData
     * @throws Exception
     */
    public String validateEventData(String json) throws Exception {
        try {
            if (json == null || json.length() == 0) {
                throw new Exception("Event Data Processing Error");
            }
            EventData eventData = gson.fromJson(json, EventData.class);
            if (eventData != null) {
                return json;
            }
        } catch (Exception e) {
            throw new Exception("Event Data Processing Error" + e.getMessage());
        }
        return null;
    }

    public String getJsonString(EventData eventData) throws Exception {
        try {
            if (eventData == null) {
                throw new Exception("Event Data Parsing Error");
            }
            String eventDataJson = gson.toJson(eventData);
            return eventDataJson;
        } catch (Exception e) {
            throw new Exception("Event Data Processing Error" + e.getMessage());
        }
    }
}
