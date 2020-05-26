package it.unibo.citizenDigitalTwin.view_model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import it.unibo.citizenDigitalTwin.data.notification.Notification;

public class NotificationsViewModel {

    private final MutableLiveData<List<Notification>> notifications;

    public NotificationsViewModel() {
        this.notifications = new MutableLiveData<>(new ArrayList<>());
    }

    public void addNotification(final Notification notification){
        final List<Notification> not = getCurrentNotifications();
        not.add(notification);
        this.notifications.setValue(not);
    }

    public void addNotifications(final List<Notification> notifications){
        final List<Notification> not = getCurrentNotifications();
        not.addAll(notifications);
        this.notifications.setValue(not);
    }

    public LiveData<List<Notification>> getNotifications() {
        return notifications;
    }

    public void deleteNotifications(List<Notification> notifications){
        final List<Notification> not = getCurrentNotifications();
        not.removeAll(notifications);
        this.notifications.setValue(not);
    }

    private List<Notification> getCurrentNotifications(){
        final List<Notification> not = this.notifications.getValue();
        return Objects.isNull(not) ? new ArrayList<>() : not;
    }
}