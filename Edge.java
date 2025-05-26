// Edge.java
// Represents a connection between two nodes (a road)
public class Edge {
    public int from, to; // Node IDs representing the connection
    public double distance; // Length of the road in kilometers
    public int capacity; // Vehicles per hour capacity
    public int condition; // Road condition from 1-10 (only for existing roads)
    public double cost; // Construction cost (only for potential roads)
    public boolean isExisting; // True if the road already exists

    // Constructor for existing roads
    public Edge(int from, int to, double distance, int capacity, int condition, boolean isExisting) {
        this.from = from;
        this.to = to;
        this.distance = distance;
        this.capacity = capacity;
        this.condition = condition;
        this.isExisting = isExisting;
        this.cost = -1; // not applicable
    }

    // Constructor for potential roads
    public Edge(int from, int to, double distance, int capacity, double cost) {
        this.from = from;
        this.to = to;
        this.distance = distance;
        this.capacity = capacity;
        this.cost = cost;
        this.isExisting = false;
        this.condition = -1; // not applicable
    }

    @Override
    public String toString() {
        return from + " → " + to + " (" + distance + " km)";
    }
}


