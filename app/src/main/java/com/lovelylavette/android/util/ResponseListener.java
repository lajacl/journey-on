package com.lovelylavette.android.util;

import com.amadeus.resources.Location;

public class ResponseListener {

    public interface Locations {
        void onResponseReceive(Location[] locations);
    }
}
