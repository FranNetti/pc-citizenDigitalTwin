package it.unibo.citizenDigitalTwin.data.notification;

import java.util.List;

import it.unibo.citizenDigitalTwin.data.category.LeafCategory;

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
