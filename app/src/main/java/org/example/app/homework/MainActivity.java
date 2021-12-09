package org.example.app.homework;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private static final int GET_MESSAGES = 1;
    private static final int NETWORK_ERROR = -1;
    private static final String TAG = "homework.main";
    private final List<IMMessage> messages = new ArrayList<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private SimpleViewHolderAdapter adapter;
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case GET_MESSAGES:
                    //noinspection unchecked
                    final List<IMMessage> newMessages = (List<IMMessage>) msg.obj;
                    messages.addAll(newMessages);
                    final RecyclerView.Adapter<?> adapter = MainActivity.this.adapter;
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                    break;
                case NETWORK_ERROR:
                    Toast.makeText(MainActivity.this.getApplicationContext(), "加载出错", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final RecyclerView recycleView = findViewById(R.id.recycler_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycleView.setLayoutManager(layoutManager);
        recycleView.addItemDecoration(new DividerItemDecoration(this, layoutManager.getOrientation()));
        final SimpleViewHolderAdapter adapter = new SimpleViewHolderAdapter();
        recycleView.setAdapter(adapter);

        this.adapter = adapter;
        executor.submit(() -> {
            final String baseUrl = "http://res.xingcheng.me/service/";

            try {
                //TODO
                // 1. 通过GET请求，从http://res.xingcheng.me/service/api/messages 拿到json数据
                // 2. 拿到json后，解析并创建IMMessage对象，并通知UI更新
                final List<IMMessage> newMessages = new ArrayList<>();

                final Message message = handler.obtainMessage(GET_MESSAGES);
                message.obj = newMessages;
                handler.sendMessage(message);

            } catch (IOException e) {
                Log.e(TAG, "failed to request data", e);
                handler.sendMessage(handler.obtainMessage(NETWORK_ERROR));
            } catch (JSONException e) {
                if (BuildConfig.DEBUG) {
                    throw new AssertionError("解析JSON出错");
                } else {
                    Log.e(TAG, "解析JSON出错", e);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
        handler.removeCallbacksAndMessages(null);
    }

    private static class SimpleViewHolder extends RecyclerView.ViewHolder {
        @NonNull
        public ImageView avatar;
        @NonNull
        public TextView title;
        @NonNull
        public TextView previewMessage;
        @NonNull
        public TextView time;

        private SimpleViewHolder(View itemView, @NonNull TextView title, @NonNull ImageView avatar,
                                 @NonNull TextView previewMessage, @NonNull TextView time) {
            super(itemView);
            this.title = notNull(title);
            this.avatar = notNull(avatar);
            this.previewMessage = notNull(previewMessage);
            this.time = notNull(time);
        }

        private static <T> T notNull(T object) {
            if (object == null) {
                throw new NullPointerException();
            }

            return object;
        }

        public static SimpleViewHolder create(Activity activity,
                                              ViewGroup parent) {
            final View itemView = activity.getLayoutInflater().inflate(R.layout.main_item, parent, false);
            final ImageView avatar = itemView.findViewById(R.id.item_avatar);
            final TextView title = itemView.findViewById(R.id.item_title);
            final TextView previewMessage = itemView.findViewById(R.id.item_preview_message);
            final TextView time = itemView.findViewById(R.id.item_time);

            return new SimpleViewHolder(itemView, title, avatar, previewMessage, time);
        }
    }

    private class SimpleViewHolderAdapter extends RecyclerView.Adapter<SimpleViewHolder> {
        @NonNull
        @Override
        public SimpleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return SimpleViewHolder.create(MainActivity.this, parent);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull SimpleViewHolder holder, int position) {
            final IMMessage message = messages.get(position);
            holder.title.setText(message.title);
            holder.previewMessage.setText(message.message);
            holder.time.setText(message.time);

            Glide.with(holder.avatar).clear(holder.avatar);
            Glide.with(holder.avatar).load(message.avatar).into(holder.avatar);
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }
    }
}