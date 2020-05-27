package it.unibo.citizenDigitalTwin.db.entity.data;

import java.io.Serializable;
import java.util.Date;
import java.util.NoSuchElementException;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import it.unibo.citizenDigitalTwin.data.category.LeafCategory;
import it.unibo.citizenDigitalTwin.db.entity.Feeder;

@Entity(tableName = "state")
public class Data {

    @PrimaryKey @NonNull @ColumnInfo(name = "leafCategory") private String leafCategoryName;
    @ColumnInfo private long timestamp;
    @ColumnInfo(defaultValue = "") private String identifier;
    @ColumnInfo() private String value;
    @Embedded private Feeder feeder;

    @Ignore private Date date;
    @Ignore private LeafCategory leafCategory;

    @Ignore
    public Data(final String identifier, final Date date, final Feeder feeder, final LeafCategory dataCategory, final String value) {
        this.identifier = identifier;
        this.timestamp = date.getTime();
        this.date = date;
        this.feeder = feeder;
        this.leafCategory = dataCategory;
        this.leafCategoryName = dataCategory.getIdentifier();
        this.value = value;
    }

    @Ignore
    public Data(final Date date, final Feeder feeder, final LeafCategory dataCategory, final String value) {
        this("", date, feeder, dataCategory, value);
    }

    public Data(
            final String identifier,
            final long timestamp,
            final Feeder feeder,
            final String leafCategoryName,
            final String value) throws NoSuchElementException {
        this(identifier, new Date(timestamp), feeder, LeafCategory.findByLeafIdentifier(leafCategoryName).get(), value);
    }

    public String getIdentifier() {
        return identifier;
    }

    public Date getDate() {
        return date;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Feeder getFeeder() {
        return feeder;
    }

    public LeafCategory getLeafCategory() {
        return leafCategory;
    }

    public String getLeafCategoryName() {
        return leafCategoryName;
    }

    public String getValue() {
        return value;
    }

    public void setLeafCategory(LeafCategory leafCategory) {
        this.leafCategory = leafCategory;
        this.leafCategoryName = leafCategory.getIdentifier();
    }

    public void setLeafCategoryName(String leafCategoryName) {
        this.leafCategoryName = leafCategoryName;
        LeafCategory.findByLeafIdentifier(leafCategoryName).ifPresent(value -> this.leafCategory = value);
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        this.date = new Date(timestamp);
    }

    public void setDate(Date date) {
        this.date = date;
        this.timestamp = date.getTime();
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setFeeder(Feeder feeder) {
        this.feeder = feeder;
    }
}
