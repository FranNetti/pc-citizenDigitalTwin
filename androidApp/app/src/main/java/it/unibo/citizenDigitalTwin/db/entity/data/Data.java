package it.unibo.citizenDigitalTwin.db.entity.data;

import android.util.Log;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import it.unibo.citizenDigitalTwin.data.category.LeafCategory;
import it.unibo.citizenDigitalTwin.data.connection.CommunicationStandard;
import it.unibo.citizenDigitalTwin.data.connection.JsonSerializable;
import it.unibo.citizenDigitalTwin.db.entity.Feeder;

@Entity(tableName = "state")
public class Data implements Serializable, JsonSerializable {

    private static final String TAG = "[Data]";

    private static final String IDENTIFIER = "id";
    private static final String FEEDER = "feeder";
    private static final String TIMESTAMP = "timestamp";
    private static final String DATA_CATEGORY = "category";
    private static final String VALUE = "value";
    private static final String NAME = "name";

    @PrimaryKey @NonNull @ColumnInfo(name = "leafCategory") private String leafCategoryName;
    @ColumnInfo private Date date;
    @ColumnInfo(defaultValue = "") private String identifier;
    @ColumnInfo() private Map<CommunicationStandard, String> information;
    @Embedded private Feeder feeder;

    @Ignore private LeafCategory leafCategory;

    @Ignore
    public Data(
            final String identifier,
            final Date date,
            final Feeder feeder,
            final LeafCategory dataCategory,
            final Map<CommunicationStandard, String> information) {
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
                final Map<CommunicationStandard, String> information) {
        this("", date, feeder, dataCategory, information);
    }

    @Ignore
    public Data(final JSONObject data) throws JSONException {
        this(data.getString(IDENTIFIER),
                new Date(data.getLong(TIMESTAMP)),
                new Feeder(data.getJSONObject(FEEDER)),
                data.getString(DATA_CATEGORY),
                Data.decodeInformation(data.get(VALUE))
        );
    }

    public Data(
            final String identifier,
            final Date date,
            final Feeder feeder,
            final String leafCategoryName,
            final Map<CommunicationStandard, String> information) throws NoSuchElementException {
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

    @NotNull
    public String getLeafCategoryName() {
        return leafCategoryName;
    }

    public Map<CommunicationStandard, String> getInformation() {
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

    public void setInformation(Map<CommunicationStandard, String> information) {
        this.information = information;
    }

    public void setFeeder(Feeder feeder) {
        this.feeder = feeder;
    }

    @Override
    public JSONObject toJson() throws JSONException {
        final Object value;
        if (information.keySet().size() == 1) {
            final String v = information.get(CommunicationStandard.DEFAULT_VALUE_IDENTIFIER);
            value = CommunicationStandard.DEFAULT_VALUE_IDENTIFIER.getInferType(leafCategory,v);
        } else {
            value = new JSONObject();
            final JSONObject json = (JSONObject)value;
            for (final Map.Entry<CommunicationStandard,String> info : information.entrySet()) {
                final CommunicationStandard key = info.getKey();
                json.put(key.getIdentifier(),key.getInferType(leafCategory,info.getValue()));
            }
        }

        return new JSONObject()
                .put(IDENTIFIER,identifier)
                .put(FEEDER,feeder.toJson())
                .put(TIMESTAMP,date.getTime())
                .put(DATA_CATEGORY,leafCategory.getIdentifier())
                .put(VALUE,value);
    }

    private static Map<CommunicationStandard, String> decodeInformation(final Object obj) {
        final Map<CommunicationStandard, String> information = new HashMap<>();
        if (obj instanceof JSONObject) {
            final JSONObject json = (JSONObject) obj;
            json.keys().forEachRemaining(name -> {
                try {
                    information.put(CommunicationStandard.findByIdentifier(name).get(), json.getString(name));
                } catch (final Exception e){
                    Log.e(TAG, "Error in decodeInformation: " + e.getLocalizedMessage());
                }
            });
        } else {
            information.put(CommunicationStandard.DEFAULT_VALUE_IDENTIFIER,obj.toString());
        }
        return information;
    }
}
