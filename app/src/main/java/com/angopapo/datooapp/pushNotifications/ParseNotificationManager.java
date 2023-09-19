package com.angopapo.datooapp.pushNotifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * A utility class for building and showing notifications.
 */
class ParseNotificationManager {
    private final AtomicInteger notificationCount = new AtomicInteger(0);

    public static ParseNotificationManager getInstance() {
        return Singleton.INSTANCE;
    }

    public void showNotification(Context context, Notification notification) {
        if (context != null && notification != null) {
            notificationCount.incrementAndGet();

            // Fire off the notification
            NotificationManager nm =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            // Pick an id that probably won't overlap anything
            int notificationId = (int) System.currentTimeMillis();

            try {
                if (nm != null) {
                    nm.notify(notificationId, notification);
                }
            } catch (SecurityException e) {
                // Some phones throw an exception for unapproved vibration
                notification.defaults = Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND;
                nm.notify(notificationId, notification);
            }
        }
    }

    private static class Singleton {
        private static final ParseNotificationManager INSTANCE = new ParseNotificationManager();
    }
}
