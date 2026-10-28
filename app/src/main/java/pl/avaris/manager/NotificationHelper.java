package pl.avaris.manager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

public final class NotificationHelper {
    public static final String CHANNEL_ID = "avaris_reminders";
    private NotificationHelper() {}

    public static void createChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Przypomnienia AVARIS", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Powiadomienia o nagraniach, publikacjach, eventach i zadaniach AVARIS SMP.");
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }
    }
}
