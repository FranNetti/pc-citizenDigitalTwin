package it.unibo.citizenDigitalTwin.db;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.room.TypeConverter;
import it.unibo.citizenDigitalTwin.data.category.LeafCategory;
import it.unibo.citizenDigitalTwin.db.entity.notification.Notification;

public class Converters {

    private static final String TAG = "[Converters]";
    private static final String LIST_LEAF_TAG = "leaves";

    @TypeConverter
    public static Map<String, String> fromStringToMap(final String string){
        final Map<String, String> map = new HashMap<>();
        try{
            final JSONObject object = new JSONObject(string);
            object.keys().forEachRemaining(name -> {
                try {
                    map.put(name, object.getString(name));
                } catch (final Exception e){
                    Log.e(TAG, "Error in fromStringToMap: " + e.getLocalizedMessage());
                }
            });
        } catch (final Exception e){
            Log.e(TAG, "Error in fromStringToMap: " + e.getLocalizedMessage());
        }
        return map;
    }

    @TypeConverter
    public static String fromMapToString(final Map<String,String> value){
        final JSONObject object = new JSONObject();
        try{
            for(final Map.Entry<String, String> entry: value.entrySet()){
                object.put(entry.getKey(), entry.getValue());
            }
            return object.toString();
        } catch (final JSONException e){
            Log.e(TAG, "Error in fromMapToString: " + e.getLocalizedMessage());
            return "";
        }
    }

    @TypeConverter
    public static long fromDateToLong(final Date date){
        return date.getTime();
    }

    @TypeConverter
    public static Date fromLongToDate(final long value){
        return new Date(value);
    }

    @TypeConverter
    public static String fromListToString(final List<LeafCategory> categoryList){
        final JSONObject object = new JSONObject();
        try{
            object.put(LIST_LEAF_TAG, categoryList);
            return object.toString();
        } catch (final JSONException e){
            Log.e(TAG, "Error in fromListToString: " + e.getLocalizedMessage());
            return "";
        }
    }

    @TypeConverter
    public static List<LeafCategory> fromStringToList(final String string){
        try{
            final JSONObject object = new JSONObject(string);
            return (List<LeafCategory>)object.get(LIST_LEAF_TAG);
        } catch (final Exception e){
            Log.e(TAG, "Error in fromStringToMap: " + e.getLocalizedMessage());
        }
        return new ArrayList<>();
    }

}
