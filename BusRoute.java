// BusRoute.java
// Represents a bus route including stops, buses assigned, and passenger count
import java.util.List;

public class BusRoute {
    public String id; // Route identifier (e.g., B1, B2)
    public List<Integer> stops; // List of node IDs that are stops on this route
    public int busesAssigned; // Number of buses available to the route
    public int passengers; // Estimated number of daily passengers

    public BusRoute(String id, List<Integer> stops, int busesAssigned, int passengers) {
        this.id = id;
        this.stops = stops;
        this.busesAssigned = busesAssigned;
        this.passengers = passengers;
    }
}


