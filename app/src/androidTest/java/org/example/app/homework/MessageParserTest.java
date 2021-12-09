package org.example.app.homework;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.hamcrest.MatcherAssert;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.List;

public class MessageParserTest {
    final SimpleMessageJsonParser parser = new SimpleMessageJsonParser(new MessageFactory(""));

    @Test
    public void parseSimpleMessage() throws JSONException {
        String json = "{\n" +
                "      \"title\": \"123\",\n" +
                "      \"preview\": \"images/123.jpeg\",\n" +
                "      \"message\": \"hhh\",\n" +
                "      \"time\": \"20:18\"\n" +
                "    }";

        final IMMessage message = parser.parseMessage(new JSONObject(json));

        assertNotNull(message);
        assertEquals("123", message.title);
        assertEquals("images/123.jpeg", message.avatar);
        assertEquals("hhh", message.message);
        assertEquals("20:18", message.time);
    }

    @Test
    public void parseEmptyData() throws JSONException {
        String json = "{ \"msg\": \"OK\",\n" +
                "  \"data\": [] " +
                "}";


        final List<IMMessage> messages = parser.parseData(json);

        MatcherAssert.assertThat(messages, allOf(
                notNullValue(),
                hasSize(0)
        ));
    }

    @Test
    public void parseMessageMissing() throws JSONException {
        String json = "{ \"msg\": \"OK\"\n" +
                "}";


        final List<IMMessage> messages = parser.parseData(json);

        MatcherAssert.assertThat(messages, allOf(
                notNullValue(),
                hasSize(0)
        ));
    }
}
