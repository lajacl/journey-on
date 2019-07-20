package com.lovelylavette.android.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lovelylavette.android.model.Trip;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static java.lang.reflect.Modifier.TRANSIENT;

public class TripPrefs {
    private static final String PREFS_NAME = "com.lovelylavette.android.TripPrefs";
    private static SharedPreferences settings;
    private static SharedPreferences.Editor editor;

    public TripPrefs(Context context) {
        if (settings == null) {
            settings = context.getSharedPreferences(PREFS_NAME,
                    Context.MODE_PRIVATE);
        }
        editor = settings.edit();
    }

    public void saveTrip(Trip trip) {
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .excludeFieldsWithModifiers(TRANSIENT) // STATIC|TRANSIENT in the default configuration - Modifier.STATIC, Modifier.TRANSIENT, Modifier.VOLATILE
                .create();
        String tripJson = gson.toJson(trip);
        Log.i("TripPrefs", "Trip JSON: " + tripJson);
        String id = trip.getId().toString();

        editor.putString(id, tripJson);
        editor.commit();
    }

    public List<Trip> getTrips() {
        List<Trip> tripsArray = new ArrayList<>();
        Map tripMap = settings.getAll();
        Gson gson = new Gson();
        Iterator<Map.Entry<String, String>> iterator = tripMap.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, String> next = iterator.next();
            Trip trip = gson.fromJson(next.getValue(), Trip.class);
            tripsArray.add(trip);
        }
        return tripsArray;
    }
}
