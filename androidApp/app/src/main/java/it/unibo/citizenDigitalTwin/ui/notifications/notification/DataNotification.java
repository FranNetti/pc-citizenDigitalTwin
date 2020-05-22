package it.unibo.citizenDigitalTwin.ui.notifications.notification;

import java.util.List;

import it.unibo.citizenDigitalTwin.commons.LeafCategory;

public class DataNotification extends Notification {

    private final List<LeafCategory> changedCategories;

    public DataNotification(final String sender, final List<LeafCategory> categories) {
        super(Type.DATA, sender);
        this.changedCategories = categories;
    }

    public List<LeafCategory> getChangedCategories() {
        return changedCategories;
    }
}
