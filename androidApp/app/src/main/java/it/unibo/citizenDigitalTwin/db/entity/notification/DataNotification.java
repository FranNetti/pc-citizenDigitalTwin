package it.unibo.citizenDigitalTwin.db.entity.notification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import it.unibo.citizenDigitalTwin.data.category.LeafCategory;

@Entity(tableName = "dataNotification")
public class DataNotification extends Notification {

    @ColumnInfo private List<LeafCategory> changedCategories;

    @Ignore
    public DataNotification(final String sender, final Collection<LeafCategory> categories) {
        super(Type.DATA, sender);
        this.changedCategories = new ArrayList<>(categories);
    }

    public DataNotification(final long id, final Date date, final String sender, final List<LeafCategory> changedCategories, final boolean read) {
        super(Type.DATA, id, date, sender, read);
        this.changedCategories = changedCategories;
    }

    public List<LeafCategory> getChangedCategories() {
        return changedCategories;
    }

    public void setChangedCategories(List<LeafCategory> changedCategories) {
        this.changedCategories = changedCategories;
    }
}
