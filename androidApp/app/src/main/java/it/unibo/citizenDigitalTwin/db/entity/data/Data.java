package it.unibo.citizenDigitalTwin.db.entity.data;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
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
public class Data implements Serializable {

    @PrimaryKey @NonNull @ColumnInfo(name = "leafCategory") private String leafCategoryName;
    @ColumnInfo private Date date;
    @ColumnInfo(defaultValue = "") private String identifier;
    @ColumnInfo() private Map<String, String> information;
    @Embedded private Feeder feeder;

    @Ignore private LeafCategory leafCategory;

    @Ignore
    public Data(
            final String identifier,
            final Date date,
            final Feeder feeder,
            final LeafCategory dataCategory,
            final Map<String, String> information) {
        this.identifier = identifier;
        this.date = date;
        this.feeder = feeder;
        this.leafCategory = dataCategory;
        this.leafCategoryName = dataCategory.getIdentifier();
        this.information = information;
    }

    @Ignore
    public Data(final Date date,
                final Feeder feeder,
                final LeafCategory dataCategory,
                final Map<String, String> information) {
        this("", date, feeder, dataCategory, information);
    }

    public Data(
            final String identifier,
            final Date date,
            final Feeder feeder,
            final String leafCategoryName,
            final Map<String, String> information) throws NoSuchElementException {
        this(identifier, date, feeder, LeafCategory.findByLeafIdentifier(leafCategoryName).get(), information);
    }

    public String getIdentifier() {
        return identifier;
    }

    public Date getDate() {
        return date;
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

    public Map<String, String> getInformation() {
        return information;
    }

    public void setLeafCategory(LeafCategory leafCategory) {
        this.leafCategory = leafCategory;
        this.leafCategoryName = leafCategory.getIdentifier();
    }

    public void setLeafCategoryName(String leafCategoryName) {
        this.leafCategoryName = leafCategoryName;
        LeafCategory.findByLeafIdentifier(leafCategoryName).ifPresent(value -> this.leafCategory = value);
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setInformation(Map<String, String> information) {
        this.information = information;
    }

    public void setFeeder(Feeder feeder) {
        this.feeder = feeder;
    }
}
