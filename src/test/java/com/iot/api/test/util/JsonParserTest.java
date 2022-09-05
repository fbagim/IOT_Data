package com.iot.api.test.util;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import com.iot.api.model.EventData;
import com.iot.api.util.JsonParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class JsonParserTest {
    private JsonParser jsonParser;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        jsonParser = new JsonParser();
    }

    @Test
    @DisplayName("Test Parse Json With Valid Json")
    void testParseJsonWithValidJson() throws Exception {
        String jsonInput = "{\"id\":1,\"type\":\"TEMPERATURE\",\"name\":\"Living Room Temp\",\"clusterId\":1,\"timestamp\":\"2022-08-31T18:34:19.086568Z\",\"value\":67.35184511064352363973739556968212127685546875,\"initialized\":true}";
        EventData eventData = new EventData();
        eventData.setClusterId(1L);
        eventData.setName("Living Room Temp");
        eventData.setValue(new BigDecimal(67.35184511064352));
        eventData.setType("TEMPERATURE");
        eventData.setTimestamp("2022-08-31T18:34:19.086568Z");
        String actualResul = jsonParser.validateEventData(jsonInput);

        Assertions.assertNotNull(actualResul);
        Assertions.assertEquals(jsonInput,actualResul);
    }

    @Test
    @DisplayName("Test Parse Json With Invalid Json")
    void testParseJsonWithInValidJson() throws Exception {
        String jsonInput = "{\"id\":1,\"type\":\"TEMPERATURE\",\"name\":\"Living Room T}";
        Exception thrown = assertThrows(Exception.class, () -> jsonParser.validateEventData(jsonInput), "Expected to throw EventData is null, but it didn't");
        Assertions.assertNotNull(thrown);
        Assertions.assertTrue(thrown.getMessage().startsWith("Event Data Processing Errorcom.google.gson.stream.MalformedJsonException"));
    }

    @Test
    @DisplayName("Test Get Json String For Valid EventData")
    void testGetJsonStringForValidEventData() throws Exception {
        EventData eventData = new EventData();
        eventData.setClusterId(1L);
        eventData.setName("Living Room Temp");
        eventData.setValue(new BigDecimal(67.35184511064352));
        eventData.setType("TEMPERATURE");
        eventData.setTimestamp("2022-08-31T18:34:19.086568Z");
        String expectedJson = "{\"type\":\"TEMPERATURE\",\"timestamp\":\"2022-08-31T18:34:19.086568Z\",\"clusterId\":1,\"name\":\"Living Room Temp\",\"value\":67.35184511064352363973739556968212127685546875}";
        String actualResul = jsonParser.getJsonString(eventData);
        Assertions.assertNotNull(actualResul);
        Assertions.assertEquals(actualResul, expectedJson);
    }

    @Test
    @DisplayName("Test Get Json String For Null EventData")
    void testGetJsonStringForNullEventData() throws Exception {
        Exception thrown = assertThrows(Exception.class, () -> jsonParser.getJsonString(null), "Expected to throw EventData is null, but it didn't");
        Assertions.assertNotNull(thrown);
        Assertions.assertTrue(thrown.getMessage().startsWith("Event Data Processing ErrorEvent Data Parsing Error"));
    }
}
