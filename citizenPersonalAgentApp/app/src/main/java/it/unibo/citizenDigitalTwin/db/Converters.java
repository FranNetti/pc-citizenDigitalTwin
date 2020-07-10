package it.unibo.citizenDigitalTwin.db;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.room.TypeConverter;
import it.unibo.citizenDigitalTwin.data.category.LeafCategory;
import it.unibo.citizenDigitalTwin.data.connection.CommunicationStandard;

public class Converters {

    private static final String TAG = "[Converters]";
    private static final String LIST_LEAF_TAG = "leaves";

    @TypeConverter
    public static Map<CommunicationStandard, String> fromStringToMap(final String string){
        final Map<CommunicationStandard, String> map = new HashMap<>();
        try{
            final JSONObject object = new JSONObject(string);
            object.keys().forEachRemaining(name -> {
                try {
                    map.put(CommunicationStandard.valueOf(name), object.getString(name));
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
    public static String fromMapToString(final Map<CommunicationStandard,String> value){
        final JSONObject object = new JSONObject();
        try{
            for(final Map.Entry<CommunicationStandard, String> entry: value.entrySet()){
                object.put(entry.getKey().name(), entry.getValue());
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
        final JSONArray array = new JSONArray();
        try{
            categoryList.forEach(x -> array.put(x.name()));
            object.put(LIST_LEAF_TAG, array);
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
            final List<LeafCategory> list = new ArrayList<>();
            final JSONArray array = object.getJSONArray(LIST_LEAF_TAG);
            final int length = array.length();
            for(int x = 0; x < length; x++){
                list.add(LeafCategory.valueOf(array.getString(x)));
            }
            return list;
        } catch (final Exception e){
            Log.e(TAG, "Error in fromStringToList: " + e.getLocalizedMessage());
        }
        return new ArrayList<>();
    }

    @TypeConverter
    public static String fromJSONArrayToString(final JSONArray array){
        return array.toString();
    }

    @TypeConverter
    public static JSONArray fromStringToJSONArray(final String value){
        try {
            return new JSONArray(value);
        } catch (final JSONException e) {
            Log.e(TAG, "Error in fromStringToJSONArray: " + e.getLocalizedMessage());
            return new JSONArray();
        }
    }

}
