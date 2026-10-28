package pl.avaris.manager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public final class ReminderScheduler {
    private static final String PREFS = "avaris_reminders";
    private ReminderScheduler() {}

    public static void schedule(Context context, long id, long when, String title, String message) {
        if (when <= System.currentTimeMillis()) return;
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        prefs.edit().putLong("r_" + id + "_time", when)
                .putString("r_" + id + "_title", title)
                .putString("r_" + id + "_message", message).apply();
        setAlarm(context, id, when, title, message);
    }

    public static void cancel(Context context, long id) {
        Intent intent = new Intent(context, ReminderReceiver.class);
        PendingIntent pending = PendingIntent.getBroadcast(context, (int)(id & 0x7fffffff), intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarms != null) alarms.cancel(pending);
        pending.cancel();
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
                .remove("r_" + id + "_time").remove("r_" + id + "_title").remove("r_" + id + "_message").apply();
    }

    private static void setAlarm(Context context, long id, long when, String title, String message) {
        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra("title", title == null ? "AVARIS Manager" : title);
        intent.putExtra("message", message == null ? "Masz zaplanowane zadanie." : message);
        PendingIntent pending = PendingIntent.getBroadcast(context, (int)(id & 0x7fffffff), intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarms != null) alarms.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, when, pending);
    }

    public static void restoreAfterReboot(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        for (String key : prefs.getAll().keySet()) {
            if (!key.startsWith("r_") || !key.endsWith("_time")) continue;
            try {
                long id = Long.parseLong(key.substring(2, key.length() - 5));
                long when = prefs.getLong(key, 0L);
                if (when <= System.currentTimeMillis()) { cancel(context, id); continue; }
                setAlarm(context, id, when, prefs.getString("r_" + id + "_title", "AVARIS Manager"),
                        prefs.getString("r_" + id + "_message", "Masz zaplanowane zadanie."));
            } catch (Exception ignored) { }
        }
    }
}
