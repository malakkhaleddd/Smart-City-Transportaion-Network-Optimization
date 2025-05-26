// TrafficDijkstra.java
// Variant of Dijkstra that considers traffic levels for edge weights
// The main difference from regular Dijkstra: multiply the distance by a congestion factor based on traffic

import java.util.*;

public class TrafficDijkstra {

    // Finds the shortest path from startId to endId considering traffic at a given time
    public static List<Integer> findPathWithTraffic(Graph graph, TrafficData trafficData, int startId, int endId, TrafficTime time) {
        Map<Integer, Double> distances = new HashMap<>(); // Holds the shortest known distance to each node
        Map<Integer, Integer> previous = new HashMap<>(); // Holds the previous node for path reconstruction

        // Priority queue to process nodes with the smallest current distance
        PriorityQueue<double[]> pq = new PriorityQueue<>(Comparator.comparingDouble(a -> a[1]));

        // Initialize all distances to infinity
        for (int id : graph.nodes.keySet()) {
            distances.put(id, Double.POSITIVE_INFINITY);
        }

        // Set starting node distance to 0 and add it to the priority queue
        distances.put(startId, 0.0);
        pq.add(new double[]{startId, 0});

        // Main Dijkstra loop
        while (!pq.isEmpty()) {
            double[] current = pq.poll(); // Get node with the smallest distance
            int currentId = (int) current[0];

            // If we reached the destination node, stop the loop
            if (currentId == endId) break;

            // Check all neighbors of the current node
            for (Edge edge : graph.adjacencyList.getOrDefault(currentId, new ArrayList<>())) {
                // Determine the neighboring node
                int neighbor = (edge.from == currentId) ? edge.to : edge.from;

                // Build the traffic key (either "from-to" or "to-from") to match the traffic map
                String key1 = edge.from + "-" + edge.to;
                String key2 = edge.to + "-" + edge.from;
                String trafficKey = trafficData.trafficMap.containsKey(key1) ? key1 : key2;

                // Get the traffic flow for the current edge at the specified time
                int traffic = trafficData.getTrafficFlow(trafficKey, time);

                // Calculate a congestion factor: lower traffic → higher factor (to simulate congestion)
                double trafficFactor = 4000.0 / Math.max(traffic, 500); // Avoid division by very small numbers

                // Compute effective distance by multiplying original distance by traffic factor
                double effectiveDistance = edge.distance * trafficFactor;

                // Calculate new possible distance to neighbor
                double newDist = distances.get(currentId) + effectiveDistance;

                // If this path is shorter, update distance and previous node, and add neighbor to queue
                if (newDist < distances.get(neighbor)) {
                    distances.put(neighbor, newDist);
                    previous.put(neighbor, currentId);
                    pq.add(new double[]{neighbor, (int) newDist});
                }
            }
        }

        // Reconstruct the path from endId to startId using the previous map
        List<Integer> path = new ArrayList<>();
        Integer current = endId;
        while (current != null && previous.containsKey(current)) {
            path.add(current);
            current = previous.get(current);
        }

        // Add startId if path exists, then reverse the path to go from start to end
        if (current != null) path.add(startId);
        Collections.reverse(path);

        return path; // Return the final path as a list of node IDs
    }
}


