package org.example.app.homework;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class SimpleMessageNetworkLoader {
    private final String baseUrl;

    public SimpleMessageNetworkLoader(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @WorkerThread
    @NonNull
    public String loadMessages() throws IOException {
        OkHttpClient client = new OkHttpClient();

        final Response response = client.newCall(new Request.Builder()
                .url(baseUrl + "api/messages")
                .get()
                .build()
        ).execute();

        final int code = response.code();
        if (code != 200) {
            throw new IOException("code != 200: " + code);
        }
        final ResponseBody body = response.body();

        if (body == null) {
            throw new IOException("body == null");
        }
        return body.string();
    }
}
