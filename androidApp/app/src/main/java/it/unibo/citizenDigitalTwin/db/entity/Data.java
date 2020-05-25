package it.unibo.citizenDigitalTwin.db.entity;

import java.util.Date;
import java.util.NoSuchElementException;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import it.unibo.citizenDigitalTwin.commons.LeafCategory;

@Entity(tableName = "state")
public class Data {

    @PrimaryKey @NonNull @ColumnInfo(name = "leafCategory") private String leafCategoryName;
    @ColumnInfo(name = "date") private long numericDate;
    @ColumnInfo(defaultValue = "") private String uri;
    @ColumnInfo() private String value;
    @Embedded private Feeder feeder;

    @Ignore private Date date;
    @Ignore private LeafCategory leafCategory;

    @Ignore
    public Data(final String uri, final Date date, final Feeder feeder, final LeafCategory dataCategory, final String value) {
        this.uri = uri;
        this.numericDate = date.getTime();
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

    public Data(final String uri, final long numericDate, final Feeder feeder, final String leafCategoryName, final String value) throws NoSuchElementException {
        this(uri, new Date(numericDate), feeder, LeafCategory.findByName(leafCategoryName).get(), value);
    }

    public String getUri() {
        return uri;
    }

    public Date getDate() {
        return date;
    }

    public long getNumericDate() {
        return numericDate;
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
        LeafCategory.findByName(leafCategoryName).ifPresent(value -> this.leafCategory = value);
    }

    public void setNumericDate(long numericDate) {
        this.numericDate = numericDate;
        this.date = new Date(numericDate);
    }

    public void setDate(Date date) {
        this.date = date;
        this.numericDate = date.getTime();
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setFeeder(Feeder feeder) {
        this.feeder = feeder;
    }
}
