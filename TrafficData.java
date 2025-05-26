import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class TrafficData {
    // Map to store traffic flow for each road in different time periods
    // Example: "1-3" → [morning, afternoon, evening, night]
    public Map<String, int[]> trafficMap = new HashMap<>();

    // Constructor that reads traffic data from a CSV file
    public TrafficData(String filePath) throws FileNotFoundException {
        // Create a Scanner to read the file
        Scanner scanner = new Scanner(new File(filePath));

        // Skip the header line if it exists
        if (scanner.hasNextLine()) scanner.nextLine();

        // Read each line of the file
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            // Ignore blank lines
            if (line.isBlank()) continue;

            // Split the line by comma to get all values
            String[] parts = line.split(",");

            // The first value is the key (e.g., "1-3")
            String key = parts[0].trim();

            // Create an array to store traffic flows for 4 time periods
            int[] flow = new int[4];

            // Parse and fill the flow values from the next 4 columns
            for (int i = 0; i < 4; i++) {
                flow[i] = Integer.parseInt(parts[i + 1].trim());
            }

            // Store the key and associated traffic flows in the map
            trafficMap.put(key, flow);
        }
    }

    // Returns traffic flow for a given road and time of day
    public int getTrafficFlow(String fromToKey, TrafficTime time) {
        // Get the array of traffic flows for the given road key
        // If the key doesn't exist, return a default array with average values
        int[] flows = trafficMap.getOrDefault(fromToKey, new int[]{1000, 1000, 1000, 1000});

        // Return the traffic value corresponding to the given time of day
        // ordinal() gives the index: 0 for MORNING, 1 for AFTERNOON, etc.
        return flows[time.ordinal()];
    }
}
