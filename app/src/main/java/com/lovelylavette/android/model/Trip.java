package com.lovelylavette.android.model;

import com.amadeus.resources.FlightOffer;
import com.amadeus.resources.HotelOffer;
import com.amadeus.resources.PointOfInterest;
import com.google.android.libraries.places.api.model.Place;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class Trip implements Serializable {
    private UUID id;
    private boolean flightNeeded;
    private boolean hotelNeeded;
    private boolean sightsNeeded;
    private int budget;
    private Place origin;
    private Place destination;
    private String originAirport;
    private String destinationAirport;
    private int numPlaneSeats;
    private boolean roundTrip;
    private Calendar departureDate;
    private Calendar returnDate;
    private int numHotelGuests;
    private Calendar checkInDate;
    private Calendar checkOutDate;
    private FlightOffer flight;
    private HotelOffer hotel;
    private List<PointOfInterest> sights;
}
