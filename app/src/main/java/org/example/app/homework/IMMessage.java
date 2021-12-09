package org.example.app.homework;

import androidx.annotation.NonNull;

public class IMMessage {
    public final String title;
    public final String avatar;
    public final String message;
    public final String time;

    IMMessage(@NonNull String title, @NonNull String avatar, @NonNull String message, @NonNull String time) {
        this.title = title;
        this.avatar = avatar;
        this.message = message;
        this.time = time;
    }
}
