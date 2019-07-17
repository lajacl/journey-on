package com.lovelylavette.android.util;

import com.amadeus.resources.FlightOffer;
import com.amadeus.resources.Location;

public class ResponseListener {

    public interface Locations {
        void onResponseReceive(Location[] locations);
    }

    public interface FlightOffers {
        void onResponseReceive(com.amadeus.resources.FlightOffer[] flightOffers);
    }

    public interface Airlines {
        void onResponseReceive(com.amadeus.resources.Airline[] airlines);
    }

    public interface FlightOffer {
        void onResponseReceive(com.amadeus.resources.FlightOffer flightOffer);
    }
}
