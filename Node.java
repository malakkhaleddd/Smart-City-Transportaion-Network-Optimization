// Node.java
// Represents a node in the transportation network (either a district or facility)
public class Node {
    public int id; // Unique identifier for the node
    public String name; // Name of the district/facility
    public String type; // Type (Residential, Mixed, Facility, etc.)
    public double x, y; // Coordinates on the map
    public int population; // Number of people (0 for facilities)
    public boolean isFacility; // True if this node is a facility (e.g., hospital, station)

    // Constructor to initialize all attributes
    public Node(int id, String name, String type, double x, double y, int population, boolean isFacility) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.x = x;
        this.y = y;
        this.population = population;
        this.isFacility = isFacility;
    }

    // Used when printing the node (for debugging/logging)
    @Override
    public String toString() {
        return name + " (" + type + ")";
    }
}



