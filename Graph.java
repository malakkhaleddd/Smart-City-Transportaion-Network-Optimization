// Graph.java
// Core graph structure containing all nodes and edges
import java.util.*;

public class Graph {
    public Map<Integer, Node> nodes = new HashMap<>(); // All nodes indexed by ID
    public List<Edge> edges = new ArrayList<>(); // All edges in the network
    public Map<Integer, List<Edge>> adjacencyList = new HashMap<>(); // Neighbors for each node

    // Adds a new node to the graph
    public void addNode(Node node) {
        nodes.put(node.id, node);
        adjacencyList.putIfAbsent(node.id, new ArrayList<>());
    }

    // Adds an edge and its reverse (since the roads are bidirectional)
    public void addEdge(Edge edge) {
        edges.add(edge);
        adjacencyList.get(edge.from).add(edge);
        // Add reverse edge for bidirectionality
        adjacencyList.get(edge.to).add(new Edge(edge.to, edge.from, edge.distance, edge.capacity, edge.condition, edge.isExisting));
    }

    // Prints a summary of the graph (nodes, edges, and adjacency)
    public void printGraphSummary() {
        System.out.println("Total Nodes: " + nodes.size());
        System.out.println("Total Edges: " + edges.size());
        for (var entry : adjacencyList.entrySet()) {
            System.out.print("Node " + entry.getKey() + " → ");
            for (var edge : entry.getValue()) {
                System.out.print(edge.to + " ");
            }
            System.out.println();
        }
    }
}