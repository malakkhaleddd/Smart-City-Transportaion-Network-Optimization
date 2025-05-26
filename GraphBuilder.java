// GraphBuilder.java
// Builds the graph by reading nodes and edges from CSV files

import java.io.*;
import java.util.*;

public class GraphBuilder {

    // This method builds and returns a Graph object by reading data from CSV files
    public static Graph buildGraphFromFiles(String nodesFile, String existingEdgesFile, String potentialEdgesFile) throws FileNotFoundException {
        Graph graph = new Graph(); // Initialize an empty graph

        // ----------- Load Nodes -----------
        Scanner nodeScanner = new Scanner(new File(nodesFile)); // Open the nodes CSV file
        nodeScanner.nextLine(); // Skip the header line

        // Read each node line
        while (nodeScanner.hasNextLine()) {
            String line = nodeScanner.nextLine();
            if (line.startsWith("#") || line.isBlank()) continue; // Skip comments or blank lines

            String[] parts = line.split(","); // Split the CSV line into components

            // Parse node information
            int id = Integer.parseInt(parts[0].trim());              // Node ID
            String name = parts[1].trim();                           // Node name
            int pop = (parts[2].trim().isEmpty()) ? 0 : Integer.parseInt(parts[2].trim()); // Population (default 0 if empty)

            // Determine node type based on the number of CSV columns
            String type = parts.length >= 6 ? parts[3].trim() : parts[2].trim();

            // Parse node coordinates (X, Y) from last columns
            double x = Double.parseDouble(parts[parts.length - 2].trim());
            double y = Double.parseDouble(parts[parts.length - 1].trim());

            // Determine if node is a facility based on ID or column count
            boolean isFacility = id >= 100 || parts.length < 6;

            // Create and add node to the graph
            Node node = new Node(id, name, type, x, y, pop, isFacility);
            graph.addNode(node);
        }

        // ----------- Load Existing Edges -----------
        Scanner edgeScanner = new Scanner(new File(existingEdgesFile)); // Open existing edges CSV
        if (edgeScanner.hasNextLine()) edgeScanner.nextLine(); // Skip header

        // Read each existing edge line
        while (edgeScanner.hasNextLine()) {
            String line = edgeScanner.nextLine();
            if (line.startsWith("#") || line.isBlank()) continue; // Skip comments or blank lines

            String[] parts = line.split(","); // Split the line into columns

            // Parse existing edge data
            int from = Integer.parseInt(parts[0].trim());         // Source node ID
            int to = Integer.parseInt(parts[1].trim());           // Destination node ID
            double dist = Double.parseDouble(parts[2].trim());    // Distance
            int cap = Integer.parseInt(parts[3].trim());          // Capacity
            int cond = Integer.parseInt(parts[4].trim());         // Condition

            // Create and add the existing edge to the graph (existing = true)
            Edge edge = new Edge(from, to, dist, cap, cond, true);
            graph.addEdge(edge);
        }

        // ----------- Load Potential Edges -----------
        Scanner potentialScanner = new Scanner(new File(potentialEdgesFile)); // Open potential edges CSV
        if (potentialScanner.hasNextLine()) potentialScanner.nextLine(); // Skip header

        // Read each potential edge line
        while (potentialScanner.hasNextLine()) {
            String line = potentialScanner.nextLine();
            if (line.startsWith("#") || line.isBlank()) continue; // Skip comments or blank lines

            String[] parts = line.split(","); // Split the line into columns

            // Parse potential edge data
            int from = Integer.parseInt(parts[0].trim());          // Source node ID
            int to = Integer.parseInt(parts[1].trim());            // Destination node ID
            double dist = Double.parseDouble(parts[2].trim());     // Distance
            int cap = Integer.parseInt(parts[3].trim());           // Capacity
            double cost = Double.parseDouble(parts[4].trim());     // Construction cost

            // Create and add the potential edge to the graph (condition not required)
            Edge edge = new Edge(from, to, dist, cap, cost);
            graph.addEdge(edge);
        }

        return graph; // Return the final constructed graph
    }
}
