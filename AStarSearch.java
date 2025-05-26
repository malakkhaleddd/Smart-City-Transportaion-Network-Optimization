// Implements A* Search Algorithm for emergency routing considering traffic conditions
import java.util.*;

public class AStarSearch {

    // Internal helper class to represent a node with its f-score (g + h)
    static class NodeRecord {
        int nodeId;   // Unique identifier for the node
        double f;     // Estimated total cost from start to goal through this node (f = g + h)

        public NodeRecord(int nodeId, double f) {
            this.nodeId = nodeId;
            this.f = f;
        }
    }

    /**
     * Performs A* search on the graph to find the shortest path from startId to goalId
     * while considering traffic conditions based on the time of day.
     */
    public static List<Integer> findPath(Graph graph, TrafficData trafficData, int startId, int goalId, TrafficTime time) {
        // gScore stores the actual cost from start node to each node
        Map<Integer, Double> gScore = new HashMap<>();

        // fScore stores the estimated total cost (g + h) for each node
        Map<Integer, Double> fScore = new HashMap<>();

        // cameFrom stores the parent of each node to reconstruct the path at the end
        Map<Integer, Integer> cameFrom = new HashMap<>();

        // Initialize all nodes with infinite cost
        for (int id : graph.nodes.keySet()) {
            gScore.put(id, Double.POSITIVE_INFINITY);
            fScore.put(id, Double.POSITIVE_INFINITY);
        }

        // Set the cost of the start node to 0
        gScore.put(startId, 0.0);

        // Estimate the cost from the start node to the goal using the heuristic
        fScore.put(startId, heuristic(graph.nodes.get(startId), graph.nodes.get(goalId)));

        // PriorityQueue to select the node with the lowest fScore to explore next
        PriorityQueue<NodeRecord> openSet = new PriorityQueue<>(Comparator.comparingDouble(a -> a.f));
        openSet.add(new NodeRecord(startId, fScore.get(startId)));

        // Main loop: continues until there are no nodes left to explore
        while (!openSet.isEmpty()) {
            // Get the node with the lowest fScore
            int current = openSet.poll().nodeId;

            // If we reached the goal, stop the loop
            if (current == goalId) break;

            // Explore neighbors of the current node
            for (Edge edge : graph.adjacencyList.getOrDefault(current, new ArrayList<>())) {
                // Determine the neighbor node (edge can be bidirectional)
                int neighbor = (edge.from == current) ? edge.to : edge.from;

                // Create traffic keys in both directions to access traffic data
                String key1 = edge.from + "-" + edge.to;
                String key2 = edge.to + "-" + edge.from;

                // Use the correct key depending on which direction is present
                String trafficKey = trafficData.trafficMap.containsKey(key1) ? key1 : key2;

                // Get traffic flow for the current edge at the given time
                int traffic = trafficData.getTrafficFlow(trafficKey, time);

                // Calculate a traffic factor: higher traffic means a larger weight
                double trafficFactor = 4000.0 / Math.max(traffic, 500); // Avoid division by very small number

                // Calculate effective distance considering traffic
                double effectiveDistance = edge.distance * trafficFactor;

                // Tentative gScore (current path cost + cost to neighbor)
                double tentativeG = gScore.get(current) + effectiveDistance;

                // If this path to neighbor is better than any previous one
                if (tentativeG < gScore.get(neighbor)) {
                    // Record this path as the best so far
                    cameFrom.put(neighbor, current);

                    // Update cost scores
                    gScore.put(neighbor, tentativeG);
                    fScore.put(neighbor, tentativeG + heuristic(graph.nodes.get(neighbor), graph.nodes.get(goalId)));

                    // Add neighbor to the open set for future exploration
                    openSet.add(new NodeRecord(neighbor, fScore.get(neighbor)));
                }
            }
        }

        // Reconstruct the path from goal to start using the cameFrom map
        List<Integer> path = new ArrayList<>();
        Integer current = goalId;

        // Move backwards from goal to start and collect the path
        while (current != null && cameFrom.containsKey(current)) {
            path.add(current);
            current = cameFrom.get(current);
        }

        // Include the start node if we reached it successfully
        if (current != null && current == startId) path.add(startId);

        // Reverse the path to get it from start to goal
        Collections.reverse(path);
        return path; // Return the final path as a list of node IDs
    }

    /**
     * Heuristic function that estimates the cost to reach goal from a given node.
     * Uses Euclidean distance based on x and y coordinates.
     */
    private static double heuristic(Node a, Node b) {
        double dx = a.x - b.x;
        double dy = a.y - b.y;
        return Math.sqrt(dx * dx + dy * dy);
    }
}

