package com.tarasbarabash.acesteps.models;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class CustomNotification {
    private static final String TAG = CustomNotification.class.getSimpleName();

    private NotificationCompat.Builder mBuilder;
    private Context mContext;

    private NotificationChannel createChannel(Context context, String id, String name,
                                              String description, int importance) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(id, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager =
                    context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            return channel;
        }
        return null;
    }

    public CustomNotification(Context context, String channelId, String channelName,
                              String channelDescription, int channelImportance,
                              String title, String text, int drawableId, int priority
    ) {
        mContext = context;
        createChannel(
                context,
                channelId,
                channelName,
                channelDescription,
                channelImportance
        );
        mBuilder = new NotificationCompat.Builder(context, channelId);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(text);
        mBuilder.setSmallIcon(drawableId);
        mBuilder.setPriority(priority);
    }

    public NotificationCompat.Builder getBuilder() {
        return mBuilder;
    }

    public CustomNotification setIntent(PendingIntent intent) {
        mBuilder.setContentIntent(intent);
        return this;
    }

    public CustomNotification setAutoCancel() {
        mBuilder.setAutoCancel(true);
        return this;
    }

    public void sendNotification(int notificationId) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
        notificationManager.notify(notificationId, mBuilder.build());
    }

    public Notification getNotification() {
        return mBuilder.build();
    }
}
