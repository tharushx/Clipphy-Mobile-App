package com.app.clipphy.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class TinyDB {
    private SharedPreferences sharedPreferences;

    public TinyDB(Context context) {
        sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
    }

    public void putListObject(String key, ArrayList<?> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        sharedPreferences.edit().putString(key, json).apply();
    }

    public <T> ArrayList<T> getListObject(String key, Class<T> classOfT) {
        Gson gson = new Gson();
        String json = sharedPreferences.getString(key, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = TypeToken.getParameterized(ArrayList.class, classOfT).getType();
        return gson.fromJson(json, type);
    }
}
