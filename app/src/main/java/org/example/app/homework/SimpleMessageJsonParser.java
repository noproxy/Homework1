package org.example.app.homework;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SimpleMessageJsonParser {
    private final MessageFactory messageFactory;

    public SimpleMessageJsonParser(MessageFactory messageFactory) {
        this.messageFactory = messageFactory;
    }

    @NonNull
    public List<IMMessage> parseData(String json) throws JSONException {
        final JSONObject jsonObject = new JSONObject(json);
        final Object msg = jsonObject.get("msg");
        if (!msg.equals("OK")) {
            throw new JSONException("");
        }

        final JSONArray data = jsonObject.getJSONArray("data");
        final List<IMMessage> messages = new ArrayList<>();
        for (int i = 0; i < data.length(); i++) {
            final JSONObject messageJson = data.getJSONObject(i);
            final IMMessage message = parseMessage(messageJson);
            messages.add(message);
        }

        return messages;
    }


    public IMMessage parseMessage(JSONObject object) throws JSONException {
        final String title = object.getString("title");
        final String preview = object.getString("avatar");
        final String message = object.getString("message");
        final String time = object.getString("time");

        return messageFactory.createMessage(title, preview, message, time);
    }

}


