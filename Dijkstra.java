// Dijkstra.java
// Finds the shortest paths from a source using Dijkstra's algorithm
import java.util.*;

public class Dijkstra {
    // Returns the shortest distance to all nodes from the start node
    public static Map<Integer, Double> findShortestPaths(Graph graph, int startId) {
        Map<Integer, Double> distances = new HashMap<>();  // store the least distance to reach every node
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingDouble(a -> a[1])); // priority queue based on distance

        //put all distances  = infinity except the start node = 0
        for (int id : graph.nodes.keySet()) {
            distances.put(id, Double.POSITIVE_INFINITY);
        }
        distances.put(startId, 0.0);
        pq.add(new int[]{startId, 0});

        // start with the nearest node and expand from it
        while (!pq.isEmpty()) {
            int[] current = pq.poll();
            int currentId = current[0];
            double currentDist = distances.get(currentId);

            // calc new distance for any neighbor
            for (Edge edge : graph.adjacencyList.getOrDefault(currentId, new ArrayList<>())) {
                int neighbor = (edge.from == currentId) ? edge.to : edge.from;
                double newDist = currentDist + edge.distance;

                // if the new road shorter .. save it and put it into the queue
                if (newDist < distances.get(neighbor)) {
                    distances.put(neighbor, newDist);
                    pq.add(new int[]{neighbor, (int) newDist});
                }
            }
        }

        return distances;
    }

    // Returns the shortest path from start to end
    public static List<Integer> findShortestPath(Graph graph, int startId, int endId) {
        Map<Integer, Double> distances = new HashMap<>();
        // keep the source for each node so we can build the path later
        Map<Integer, Integer> previous = new HashMap<>();
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingDouble(a -> a[1]));

        for (int id : graph.nodes.keySet()) {
            distances.put(id, Double.POSITIVE_INFINITY);
        }
        distances.put(startId, 0.0);
        pq.add(new int[]{startId, 0});

        while (!pq.isEmpty()) {
            int[] current = pq.poll();
            int currentId = current[0];

            // stop searching as soon as we reach the end
            if (currentId == endId) break;

            for (Edge edge : graph.adjacencyList.getOrDefault(currentId, new ArrayList<>())) {
                int neighbor = (edge.from == currentId) ? edge.to : edge.from;
                double newDist = distances.get(currentId) + edge.distance;

                if (newDist < distances.get(neighbor)) {
                    distances.put(neighbor, newDist);
                    previous.put(neighbor, currentId);
                    pq.add(new int[]{neighbor, (int) newDist});
                }
            }
        }

        List<Integer> path = new ArrayList<>();
        Integer current = endId;
        // building the path in reverse from the end to the beginning using previous
        while (current != null && previous.containsKey(current)) {
            path.add(current);
            current = previous.get(current);
        }
        if (current != null) path.add(startId);
        Collections.reverse(path);
        return path;
    }
}


