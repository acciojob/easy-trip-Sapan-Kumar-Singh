package com.driver.controllers;

import com.driver.model.Airport;
import com.driver.model.City;
import com.driver.model.Flight;
import com.driver.model.Passenger;
import io.swagger.models.auth.In;

import javax.swing.text.html.Option;
import java.util.*;

public class AirportRepository {

    HashMap<String,Airport>airportMap=new HashMap<>();
    HashMap<Integer, Flight>flightMap=new HashMap<>();
    HashMap<Integer,Passenger>passengerMap=new HashMap<>();
    HashMap<Integer,Set<Integer>>bookTicketMap=new HashMap<>(); // book ticket for flight
    HashMap<Integer,Integer>calculateRevenueMap=new HashMap<>();

    public void saveAirport(Airport airport) {
        //Simply add airport details to your database
        this.airportMap.put( airport.getAirportName(),airport);
    }

    public void saveFlight(Flight flight) {
        this.flightMap.put(flight.getFlightId(),flight);

    }

    public void savePassenger(Passenger passenger) {
        this.passengerMap.put(passenger.getPassengerId(),passenger);
    }


    public Optional<String> getLargestAirportName() {
        //Largest airport is in terms of terminals. 3 terminal airport is larger than 2 terminal airport
        //Incase of a tie return the Lexicographically smallest airportName
          String name=null;
          int noOfTerminals=-1;

        for( String key : airportMap.keySet()){
            Airport obj=this.airportMap.get(key);
            if(obj.getNoOfTerminals()>noOfTerminals){
                name=obj.getAirportName();
            } else if(obj.getNoOfTerminals()==noOfTerminals){
               if(name.compareTo(obj.getAirportName())>0){
                   name= obj.getAirportName();;
               }
            }
        }
           if(name.equals(null)){
               return Optional.empty();
           }
           return Optional.of(name);
    }

    public Optional<Double> getShortestDurationBetweenCities(City fromCity, City toCity) {
        //Find the duration by finding the shortest flight that connects these 2 cities directly
        //If there is no direct flight between 2 cities return -1.
            double ans=Double.MAX_VALUE;
            for(Integer key :this.flightMap.keySet()){
                Flight obj=flightMap.get(key);
                if(fromCity.equals(obj.getFromCity()) && toCity.equals(obj.getToCity())){
                    ans=Math.min(ans,obj.getDuration());
                }
            }
            return Optional.of(ans);
    }

    public int getNumberOfPeopleOn(Date date, String airportName) {
       int cnt=0;
        String city= String.valueOf(this.airportMap.get(airportName).getCity());
        for(Integer key :this.flightMap.keySet()){
           Flight obj=flightMap.get(key);
           if(obj.getFlightDate().compareTo(date)==0 && (obj.getFromCity().equals(city) || obj.getToCity().equals(city))){
               cnt+=obj.getMaxCapacity();
           }

        }
        return cnt;
    }


    public int calculateFlightFare(Integer flightId) {
        //Calculation of flight prices is a function of number of people who have booked the flight already.
        //Price for any flight will be : 3000 + noOfPeopleWhoHaveAlreadyBooked*50
        //Suppose if 2 people have booked the flight already : the price of flight for the third person will be 3000 + 2*50 = 3100
        //This will not include the current person who is trying to book, he might also be just checking price


        int price=3000;
        if(this.bookTicketMap.containsKey(flightId)){
            price+=this.bookTicketMap.get(flightId).size()*50;
        }
        return price;
    }

    public Optional<Boolean> bookATicket(Integer flightId, Integer passengerId) {
        //If the numberOfPassengers who have booked the flight is greater than : maxCapacity, in that case :
        //return a String "FAILURE"
        //Also if the passenger has already booked a flight then also return "FAILURE".
        //else if you are able to book a ticket then return "SUCCESS"
        if(this.flightMap.get(flightId).getMaxCapacity()<this.bookTicketMap.get(flightId).size() || this.bookTicketMap.get(flightId).contains(passengerId)){
            return  Optional.empty();
        }
        // HashMap<Integer,Set<Integer>>bookTicketMap=new HashMap<>();
        //  HashMap<Integer,Integer>calculateRevenueMap=new HashMap<>();
       Set<Integer>temp= this.bookTicketMap.get(flightId);
        temp.add(passengerId);
        this.bookTicketMap.put(flightId,temp);
        this.calculateRevenueMap.put(flightId,this.calculateRevenueMap.getOrDefault(flightId,0)+3000+temp.size()*50);
       return Optional.of(true);

    }

    public Optional<Boolean> cancelTicket(Integer flightId, Integer passengerId) {

        //If the passenger has not booked a ticket for that flight or the flightId is invalid or in any other failure case
        // then return a "FAILURE" message
        // Otherwise return a "SUCCESS" message
        // and also cancel the ticket that passenger had booked earlier on the given flightId
        if(!this.bookTicketMap.containsKey(flightId) || !this.bookTicketMap.get(flightId).contains(passengerId)){
            return Optional.empty();
        }
        Set<Integer>temp=this.bookTicketMap.get(flightId);
        temp.remove(passengerId);
        this.calculateRevenueMap.put(flightId,this.calculateRevenueMap.get(flightId)-(3000+temp.size()*50));
        this.bookTicketMap.put(flightId,temp);
        return Optional.of(true);

    }

    public int countTotalBookingByPassenger(Integer passengerId) {
        int cnt=0;
        for(Integer key : this.bookTicketMap.keySet()){
            if(this.bookTicketMap.get(key).contains(passengerId))
                cnt++;
        }
        return cnt;
    }

    public Optional<String> getAirportNameFromFlightId(Integer flightId) {
        //We need to get the starting airportName from where the flight will be taking off (Hint think of City variable if that can be of some use)
        //return null incase the flightId is invalid or you are not able to find the airportName

         City city=flightMap.get(flightId).getFromCity();
         for( String key : airportMap.keySet()){
             if(city.equals(this.airportMap.get(key).getCity())){
                 return Optional.of(this.airportMap.get(key).getAirportName());
             }
         }
         return Optional.empty();
    }

    public int calulateTotalRevenueOfFlight(Integer flightId) {

        return this.calculateRevenueMap.get(flightId);
    }
}
