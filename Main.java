// Main.java (Enhanced Version with Fixed A* Distance Calculation)
// Entry point for the entire project. Executes all features: MST, Shortest Paths, Optimization, and Visualization.
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Graph graph = null;
        try {
            // 1. Load graph from CSV files (nodes, existing roads, potential roads)
            graph = GraphBuilder.buildGraphFromFiles(
                    "src/nodes.csv",
                    "src/existing_roads.csv",
                    "src/potential_roads.csv"
            );

            System.out.println("📊 Total Nodes: " + graph.nodes.size());
            System.out.println("🛣️ Total Edges: " + graph.edges.size());

            // 2. Minimum Spanning Tree (MST) construction
            List<Edge> mst = MSTBuilder.buildMST(graph);
            double totalCost = 0;
            System.out.println("\n🌐 MST Edges:");
            for (Edge e : mst) {
                System.out.println("From " + graph.nodes.get(e.from).name + " to " + graph.nodes.get(e.to).name +
                        " (" + e.distance + " km" + (e.isExisting ? ", existing" : ", new") + ")");
                totalCost += e.distance;
            }

            // 3. Connect unlinked facilities to MST (greedy approach)
            Set<Integer> connected = new HashSet<>();
            for (Edge e : mst) {
                connected.add(e.from);
                connected.add(e.to);
            }

            List<Integer> facilityNodes = new ArrayList<>();
            for (Node node : graph.nodes.values()) {
                if (node.isFacility && !connected.contains(node.id)) {
                    facilityNodes.add(node.id);
                }
            }

            for (int facilityId : facilityNodes) {
                Edge bestEdge = null;
                double minDist = Double.MAX_VALUE;
                for (Edge e : graph.edges) {
                    if ((e.from == facilityId && connected.contains(e.to)) ||
                            (e.to == facilityId && connected.contains(e.from))) {
                        if (e.distance < minDist) {
                            minDist = e.distance;
                            bestEdge = e;
                        }
                    }
                }
                if (bestEdge != null) {
                    mst.add(bestEdge);
                    totalCost += bestEdge.distance;
                    connected.add(facilityId);
                    System.out.println("🔗 Connected facility: " + graph.nodes.get(facilityId).name +
                            " via " + graph.nodes.get(bestEdge.from).name + " ↔ " + graph.nodes.get(bestEdge.to).name +
                            " (" + bestEdge.distance + " km)");
                } else {
                    System.out.println("⚠️ Could not connect facility: " + graph.nodes.get(facilityId).name);
                }
            }

            System.out.printf("\n✅ Final Total Distance (with facilities): %.2f km\n", totalCost);

            // 4. Dijkstra shortest path without traffic
            System.out.println("\n📍 Shortest path (no traffic):");
            List<Integer> normalPath = Dijkstra.findShortestPath(graph, 1, 5);
            for (int id : normalPath) {
                System.out.print(graph.nodes.get(id).name + " → ");
            }
            System.out.println("done.");

            // 5. Dijkstra with traffic data for all times
            TrafficData trafficData = new TrafficData("src/traffic_data.csv");
            for (TrafficTime time : TrafficTime.values()) {
                System.out.println("\n🚦 Shortest path (with traffic – " + time + "):");
                List<Integer> trafficPath = TrafficDijkstra.findPathWithTraffic(graph, trafficData, 1, 5, time);
                for (int id : trafficPath) {
                    System.out.print(graph.nodes.get(id).name + " → ");
                }
                System.out.println("done.");
            }

            // 6. A* Search for emergency vehicle for all times
            System.out.println("\n🚨 A* Emergency Paths:");
            int start = 1;       // Maadi
            int goal = 109;      // Qasr El Aini Hospital
            for (TrafficTime time : TrafficTime.values()) {
                System.out.println("Time: " + time);
                List<Integer> aStarPath = AStarSearch.findPath(graph, trafficData, start, goal, time);
                System.out.println("Raw A* path: " + aStarPath);

                if (aStarPath.size() < 2) {
                    System.out.println("⚠️ No valid path found.");
                    continue;
                }

                double total = 0;
                for (int i = 0; i < aStarPath.size(); i++) {
                    int id = aStarPath.get(i);
                    System.out.print(graph.nodes.get(id).name);
                    if (i < aStarPath.size() - 1) {
                        System.out.print(" → ");
                        int from = aStarPath.get(i);
                        int to = aStarPath.get(i + 1);
                        for (Edge edge : graph.adjacencyList.get(from)) {
                            if ((edge.from == from && edge.to == to) || (edge.from == to && edge.to == from)) {
                                total += edge.distance;
                                break;
                            }
                        }
                    }
                }
                System.out.printf("\nEstimated emergency distance: %.2f km\n", total);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // 7. Optimizing Bus Allocation
        List<BusRoute> routes = new ArrayList<>();
        routes.add(new BusRoute("B1", Arrays.asList(1, 3, 6, 9), 25, 35000));
        routes.add(new BusRoute("B2", Arrays.asList(7, 15, 8, 10, 3), 30, 42000));
        routes.add(new BusRoute("B3", Arrays.asList(2, 5, 101), 20, 28000));
        routes.add(new BusRoute("B4", Arrays.asList(4, 14, 2, 3), 22, 31000));
        routes.add(new BusRoute("B5", Arrays.asList(8, 12, 1), 18, 25000));
        routes.add(new BusRoute("B6", Arrays.asList(11, 5, 2), 24, 33000));

        int totalAvailableBuses = 60;
        List<BusRoute> optimized = TransitOptimizer.optimizeBusAllocation(routes, totalAvailableBuses);

        System.out.println("\n🚌 Optimized Bus Route Allocation:");
        for (BusRoute route : optimized) {
            System.out.println("Route " + route.id + " → " + route.passengers + " passengers, Buses used: " + route.busesAssigned);
        }

        // 8. Road Maintenance Optimization
        System.out.println("\n🛠️ Road Maintenance Optimization:");
        double maxBudget = 1000; // 1000 million EGP

        List<RoadRepair> repairs = new ArrayList<>();
        for (Edge edge : graph.edges) {
            if (edge.isExisting && edge.condition < 8) {
                double cost = 10 * (10 - edge.condition); // Estimated cost
                double benefit = edge.distance * edge.capacity * edge.condition;
                repairs.add(new RoadRepair(edge, cost, benefit));
            }
        }

        List<RoadRepair> selectedRepairs = MaintenanceOptimizer.optimizeRepairs(repairs, maxBudget);
        double totalUsed = 0;

        for (RoadRepair repair : selectedRepairs) {
            System.out.println("Repair road: " + graph.nodes.get(repair.edge.from).name + " ↔ " +
                    graph.nodes.get(repair.edge.to).name +
                    ", Cost: " + repair.cost + "M EGP, Benefit: " + repair.benefit);
            totalUsed += repair.cost;
        }

        System.out.printf("Total Repair Budget Used: %.2fM EGP\n", totalUsed);

        // 9. Visualization using JavaFX
        System.out.println("DEBUG: Nodes = " + graph.nodes.size() + ", Edges = " + graph.edges.size());
        GraphVisualizer.graph = graph;
        GraphVisualizer.highlightEdges = MSTBuilder.buildMST(graph);
        GraphVisualizer.main(null);
    }
}
