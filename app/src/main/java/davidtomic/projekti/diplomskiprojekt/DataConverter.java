package davidtomic.projekti.diplomskiprojekt;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;

public class DataConverter {
    @TypeConverter
    public static HashMap<String, String> fromString(String value) {
        Type mapType = new TypeToken<HashMap<String, String>>() {
        }.getType();
        return new Gson().fromJson(value, mapType);
    }

    @TypeConverter
    public static String fromStringMap(HashMap<String, String> map) {
        Gson gson = new Gson();
        return gson.toJson(map);
    }
}