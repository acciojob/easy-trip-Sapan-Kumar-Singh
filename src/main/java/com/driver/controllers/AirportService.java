package com.driver.controllers;

import com.driver.model.Airport;
import com.driver.model.City;
import com.driver.model.Flight;
import com.driver.model.Passenger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Optional;

public class AirportService {

   // @Autowired;
    AirportRepository airportRepository=new AirportRepository();
    public void addAirport(Airport airport) {
        airportRepository.saveAirport(airport);
    }

    public void addFlight(Flight flight) {
        airportRepository.saveFlight(flight);
    }

    public void addPassenger(Passenger passenger) {
        airportRepository.savePassenger(passenger);
    }

    public String getLargestAirportName() throws Exception {
        Optional<String>nameOpt=airportRepository.getLargestAirportName();
        if(nameOpt.isEmpty()){
            throw new Exception("Not AirPort Exists");
        }
         return nameOpt.get();
    }

    public double getShortestDurationBetweenCities(City fromCity, City toCity) {
        //Find the duration by finding the shortest flight that connects these 2 cities directly
        //If there is no direct flight between 2 cities return -1.
        Optional<Double> durationOpt=airportRepository.getShortestDurationBetweenCities(fromCity,toCity);
        return  durationOpt.get();
    }





    public int getNumberOfPeopleOn(Date date, String airportName) {
        //Calculate the total number of people who have flights on that day on a particular airport
        //This includes both the people who have come for a flight and who have landed on an airport after their flight
        return airportRepository.getNumberOfPeopleOn(date,airportName);
    }

    public int calculateFlightFare(Integer flightId) {

        return airportRepository.calculateFlightFare(flightId);
    }

    public String bookATicket(Integer flightId, Integer passengerId) {
        //If the numberOfPassengers who have booked the flight is greater than : maxCapacity, in that case :
        //return a String "FAILURE"
        //Also if the passenger has already booked a flight then also return "FAILURE".
        //else if you are able to book a ticket then return "SUCCESS"
        Optional<Boolean>bookTicketOpt=airportRepository.bookATicket(flightId,passengerId);
        if(bookTicketOpt.isEmpty()){
            return "FAILURE";
        }
        return "SUCCESS";
    }

    public String cancelATicket(Integer flightId, Integer passengerId) {
        //If the passenger has not booked a ticket for that flight or the flightId is invalid or in any other failure case
        // then return a "FAILURE" message
        // Otherwise return a "SUCCESS" message
        // and also cancel the ticket that passenger had booked earlier on the given flightId
        Optional<Boolean>cancelTicketOpt=airportRepository.cancelTicket(flightId,passengerId);
        if(cancelTicketOpt.isEmpty()){
            return "FAILURE";
        }
        return "SUCCESS";
    }

    public int countTotalBookingByPassenger(Integer passengerId) {
        return airportRepository.countTotalBookingByPassenger(passengerId);

    }

    public String getAirportNameFromFlightId(Integer flightId) {
        //We need to get the starting airportName from where the flight will be taking off (Hint think of City variable if that can be of some use)
        //return null incase the flightId is invalid or you are not able to find the airportName

        Optional<String>airportNameOpt=airportRepository.getAirportNameFromFlightId(flightId);
           if(airportNameOpt.isEmpty()){
               return null;
           }
           return airportNameOpt.get();
    }

    public int calculateTotalRevenueOfFlight(Integer flightId) {

        return airportRepository.calulateTotalRevenueOfFlight(flightId);
    }
}
