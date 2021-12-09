package org.example.app.homework;

import androidx.annotation.NonNull;

public class MessageFactory {
    private final String baseUrl;

    public MessageFactory(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public IMMessage createMessage(@NonNull String title, @NonNull String avatar, @NonNull String message, @NonNull String time) {
        return new IMMessage(title, baseUrl + avatar, message, time);
    }
}
