// RoadRepair.java
// Represents a repair project for a road with associated cost and estimated benefit
public class RoadRepair {
    public Edge edge; // The edge to be repaired
    public double cost; // Repair cost in millions EGP
    public double benefit; // Estimated benefit (e.g., traffic relief, usage improvement)

    public RoadRepair(Edge edge, double cost, double benefit) {
        this.edge = edge;
        this.cost = cost;
        this.benefit = benefit;
    }
}


