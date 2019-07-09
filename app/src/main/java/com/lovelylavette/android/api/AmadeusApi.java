package com.lovelylavette.android.api;

import android.os.AsyncTask;
import android.util.Log;

import com.amadeus.Amadeus;
import com.amadeus.Params;
import com.amadeus.exceptions.ResponseException;
import com.amadeus.resources.FlightOffer;
import com.amadeus.resources.HotelOffer;
import com.amadeus.resources.Location;
import com.amadeus.resources.PointOfInterest;
import com.google.android.gms.maps.model.LatLng;
import com.lovelylavette.android.BuildConfig;
import com.lovelylavette.android.util.ResponseListener;

public final class AmadeusApi {
    private static final String TAG = "AmadeusApi";
    private static final Amadeus amadeus = Amadeus.builder(BuildConfig.AmadeusApiKey,
            BuildConfig.AmadeusApiSecret).build();


    public static final class findNearestRelevantAirports extends AsyncTask<LatLng, Void, Location[]> {
        ResponseListener.Locations listener;

        public void setOnResponseListener(ResponseListener.Locations listener) {
            this.listener = listener;
        }

        @Override
        protected Location[] doInBackground(LatLng... latLngs) {
            Location[] airports = null;

            try {
                airports = amadeus.referenceData.locations.airports.get(Params
                        .with("latitude", latLngs[0].latitude)
                        .and("longitude", latLngs[0].longitude)
                        .and("radius", 100)
                        .and("page[limit]", 5));

            } catch (ResponseException e) {
                e.printStackTrace();
            }
            return airports;
        }

        @Override
        protected void onPostExecute(Location[] airports) {
            super.onPostExecute(airports);
            if(airports != null && airports.length > 0) {
                Log.i(TAG, airports.length + " Airports Found");
                listener.onResponseReceive(airports);
            }
        }
    }

    public static final class findLowFareFlights extends AsyncTask<Location, Void, FlightOffer[]> {

        @Override
        protected FlightOffer[] doInBackground(Location... locations) {
            FlightOffer[] flightOffers = null;

            try {
                flightOffers = amadeus.shopping.flightOffers.get(Params
                        .with("origin", "ATL")
                        .and("destination", (locations[0].getIataCode()))
                        .and("departureDate", "2019-08-30")
                        .and("returnDate", "2019-09-29"));

            } catch (ResponseException e) {
                e.printStackTrace();
            }
            return flightOffers;
        }

        @Override
        protected void onPostExecute(FlightOffer[] flightOffers) {
            super.onPostExecute(flightOffers);
            if(flightOffers != null && flightOffers.length > 0) {
                Log.i(TAG, flightOffers[0].toString());
            }
        }
    }

    public static final class findHotels extends AsyncTask<LatLng, Void, HotelOffer[]> {

        @Override
        protected HotelOffer[] doInBackground(LatLng... latLngs) {
            HotelOffer[] hotelOffers = null;

            try {
                hotelOffers = amadeus.shopping.hotelOffers.get(Params
                        .with("latitude", latLngs[0].latitude)
                        .and("longitude", latLngs[0].longitude)
                        .and("checkInDate", "2019-08-30")
                        .and("checkOutDate", "2019-09-29"));

            } catch (ResponseException e) {
                e.printStackTrace();
            }
            return hotelOffers;
        }

        @Override
        protected void onPostExecute(HotelOffer[] hotelOffers) {
            super.onPostExecute(hotelOffers);
            if(hotelOffers != null && hotelOffers.length > 0) {
                Log.i(TAG, hotelOffers[0].toString());
            }
        }
    }

    public static final class findPointsOfInterest extends AsyncTask<LatLng, Void, PointOfInterest[]> {

        @Override
        protected PointOfInterest[] doInBackground(LatLng... latLngs) {
            PointOfInterest[] pointsOfInterest = null;

            try {
                pointsOfInterest = amadeus.referenceData.locations.pointsOfInterest.get(Params
                        .with("latitude", latLngs[0].latitude)
                        .and("longitude", latLngs[0].longitude));

            } catch (ResponseException e) {
                e.printStackTrace();
            }
            return pointsOfInterest;
        }

        @Override
        protected void onPostExecute(PointOfInterest[] pointsOfInterest) {
            super.onPostExecute(pointsOfInterest);
            if(pointsOfInterest != null && pointsOfInterest.length > 0) {
                Log.i(TAG, pointsOfInterest[0].toString());
            }
        }
    }
}
