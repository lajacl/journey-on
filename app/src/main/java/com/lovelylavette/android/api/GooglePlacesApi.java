package com.lovelylavette.android.api;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;

public final class GooglePlacesApi {

    public static void showAutocomplete(Context context, Fragment fragment, int requestCode) {
        List<Place.Field> fields = Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG,
                Place.Field.ADDRESS);

        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, fields)
                .setTypeFilter(TypeFilter.CITIES)
                .build(context);
        fragment.startActivityForResult(intent, requestCode);
    }
}
