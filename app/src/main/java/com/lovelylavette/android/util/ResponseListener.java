package com.lovelylavette.android.util;

import com.amadeus.resources.Airline;
import com.amadeus.resources.FlightDestination;
import com.amadeus.resources.Location;
import com.amadeus.resources.PointOfInterest;

import java.util.List;

public class ResponseListener {

    public interface Locations {
        void onResponseReceive(Location[] locations);
    }

    public interface FlightOffers {
        void onResponseReceive(com.amadeus.resources.FlightOffer[] flightOffers);
    }

    public interface Airlines {
        void onResponseReceive(Airline[] airlines);
    }

    public interface HotelOffers {
        void onResponseReceive(com.amadeus.resources.HotelOffer[] hotelOffers);
    }

    public interface FlightOffer {
        void onResponseReceive(com.amadeus.resources.FlightOffer flightOffer);
    }

    public interface HotelOffer {
        void onResponseReceive(com.amadeus.resources.HotelOffer hotelOffer);
    }

    public interface PointsOfInterest {
        void onResponseReceive(PointOfInterest[] pointsOfInterest);
    }

    public interface Sights {
        void onResponseReceive(List<PointOfInterest> pointsOfInterest);
    }

    public interface FlightDestinations {
        void onResponseReceive(FlightDestination[] flightDestinations);
    }
}
