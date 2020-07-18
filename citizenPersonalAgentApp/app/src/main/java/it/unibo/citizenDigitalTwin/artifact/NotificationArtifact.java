package it.unibo.citizenDigitalTwin.artifact;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import cartago.OPERATION;
import cartago.ObsProperty;
import it.unibo.citizenDigitalTwin.R;
import it.unibo.citizenDigitalTwin.data.category.GroupCategory;
import it.unibo.citizenDigitalTwin.db.AppDatabase;
import it.unibo.citizenDigitalTwin.db.dao.NotificationDAO;
import it.unibo.citizenDigitalTwin.db.entity.data.Data;
import it.unibo.citizenDigitalTwin.db.entity.notification.DataNotification;
import it.unibo.citizenDigitalTwin.db.entity.notification.MessageNotification;
import it.unibo.citizenDigitalTwin.db.entity.notification.Notification;
import it.unibo.pslab.jaca_android.core.JaCaArtifact;

/**
 * Artifact that represent the current state of the user.
 * @obsProperty logged the identifier of the current logged user
 * @obsProperty state the current user state
 * @obsProperty notifications the current user notifications
 */
public class NotificationArtifact extends JaCaArtifact {

    private static final String TAG = "[NotificationArtifact]";

    private static final String PROP_NOTIFICATIONS = "notifications";
    private static final String FIRST_RUN = "first_run";
    private static final String SHARED_PREFERENCES_ID = "it.unibo.cdt.notification";
    private static final String CHANNEL_ID = "it.unibo.cdt.notification";

    private NotificationDAO dbNotifications;
    private boolean isFirstRun;
    private final AtomicInteger notificationId = new AtomicInteger(16);

    public void init() {
        final AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        this.dbNotifications = db.notificationDAO();
        defineObsProperty(PROP_NOTIFICATIONS, new ArrayList<Notification>());
        isFirstRun = isFirstRun();
        createNotificationChannel();

        /* RxJava Flowable */
        dbNotifications.getAllMessageNotifications().forEach(notifications -> {
            updatePropNotification(notifications, MessageNotification.class);
        });

        dbNotifications.getAllDataNotifications().forEach(notifications -> {
            updatePropNotification(notifications, DataNotification.class);
        });
    }

    /**
     * Generate new notifications from the given list of data.
     * @param dataList the list of data
     */
    @OPERATION
    public void createNotifications(final String userIdentifier, final List<Data> dataList){
        final List<DataNotification> notifications = dataList.stream()
                .filter(x -> x.getLeafCategory().getGroupCategory() != GroupCategory.PERSONAL_DATA || !isFirstRun)
                .filter(x -> x.getFeeder().isResource() && !x.getFeeder().getUri().equals(userIdentifier))
                .collect(Collectors.groupingBy(x -> x.getFeeder().getUri(), Collectors.toSet()))
                .entrySet().stream()
                .map(x -> new DataNotification(x.getKey(), x.getValue().stream().map(Data::getLeafCategory).collect(Collectors.toList())))
                .collect(Collectors.toList());
        dbNotifications.insertDataNotifications(notifications);

        createAndroidDataNotifications(notifications);
    }

    /**
     * Set the given notifications as read.
     * @param notifications the notification
     */
    @OPERATION
    public void setNotificationsRead(final List<Notification> notifications){
        final List<MessageNotification> msgNotifications = new ArrayList<>();
        final List<DataNotification> dataNotifications = new ArrayList<>();
        notifications.forEach(x -> {
            x.setRead(true);
            switch (x.getType()){
                case DATA: dataNotifications.add((DataNotification)x); break;
                case MESSAGE: msgNotifications.add((MessageNotification)x); break;
                default: Log.e(TAG, "Unhandled notification in updateNotificationsStatus: " + x.getType());
            }
        });
        if(!msgNotifications.isEmpty()){
            dbNotifications.updateMessageNotifications(msgNotifications);
        }
        if(!dataNotifications.isEmpty()){
            dbNotifications.updateDataNotifications(dataNotifications);
        }
    }

    private void createAndroidDataNotifications(final List<DataNotification> notifications){
        createNotificationChannel();
        final Context context = getApplicationContext();
        final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notifications.stream()
                .map(not -> {
                    final String message = not.getChangedCategories().stream()
                            .map(x -> x.getDisplayName(context))
                            .reduce((a,b) -> a + ", " + b)
                            .orElse("");
                    return new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.application_icon_black_24dp)
                        .setContentTitle(not.getSender() + " " + context.getString(R.string.data_change))
                        .setContentText(message)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                })
                .forEach(not -> notificationManager.notify(notificationId.getAndIncrement(), not.build()));

    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final Context context = getApplicationContext();
            final CharSequence name = context.getString(R.string.notifications_channel_name);
            final String description = context.getString(R.string.notifications_channel_description);
            final int importance = NotificationManager.IMPORTANCE_DEFAULT;
            final NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            final NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            Objects.requireNonNull(notificationManager).createNotificationChannel(channel);
        }
    }

    private void updatePropNotification(final List<? extends Notification> notifications, final Class<?> notClass){
        beginExternalSession();
        ObsProperty prop = getObsProperty(PROP_NOTIFICATIONS);
        final List<Notification> savedNotifications = (List<Notification>)prop.getValue();
        final List<Notification> notificationToDelete = savedNotifications.stream()
                .filter(x -> x.getClass().isAssignableFrom(notClass))
                .collect(Collectors.toList());
        savedNotifications.removeAll(notificationToDelete);
        savedNotifications.addAll(notifications);
        savedNotifications.sort((a,b) -> b.getDate().compareTo(a.getDate()));
        prop.updateValue(savedNotifications);
        endExternalSession(true);
    }

    private boolean isFirstRun() {
        final boolean isFirstRun = readSharedPreference(SHARED_PREFERENCES_ID, FIRST_RUN, true);
        if (isFirstRun) {
            writeSharedPreference(SHARED_PREFERENCES_ID, FIRST_RUN, false);
        }
        return isFirstRun;
    }

}
