
// GraphVisualizer.java with interactive buttons to toggle MST, Dijkstra, and A* visualization

// GraphVisualizer.java (Updated to show labels, fix A* / Dijkstra visibility)
// GraphVisualizer.java (with Legend and Distance Labels)
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.*;

public class GraphVisualizer extends Application {

    public static Graph graph;
    public static List<Edge> highlightEdges = new ArrayList<>();

    private double orgSceneX, orgSceneY;
    private double translateX = 0, translateY = 0;
    private double zoom = 1.0;
    private Pane pane;

    private boolean showAllEdges = false;

    @Override
    public void start(Stage stage) {
        pane = new Pane();
        VBox root = new VBox();

        Button showMST = new Button("🔗 Show MST");
        Button showShortest = new Button("📍 Show Dijkstra");
        Button showAStar = new Button("🚨 Show A*");
        Button showAll = new Button("🌐 Show All Edges");
        Button checkDisconnected = new Button("⚠️ Check Disconnected Nodes");

        HBox legend = new HBox(20);
        legend.setStyle("-fx-padding: 8; -fx-border-color: black; -fx-border-width: 1; -fx-background-color: #f0f0f0;");
        legend.getChildren().addAll(
                new Label("⬤ Black: City Node"),
                new Label("⬤ Orange: Facility Node"),
                new Label("━ Gray: Existing Road"),
                new Label("━ Blue Dashed: Potential Road"),
                new Label("━ Red: Highlighted Edge")
        );

        showMST.setOnAction(e -> {
            showAllEdges = false;
            highlightEdges = MSTBuilder.buildMST(graph);
            redrawGraph();
        });

        showShortest.setOnAction(e -> {
            showAllEdges = false;
            List<Integer> path = Dijkstra.findShortestPath(graph, 1, 5);
            highlightEdges = buildEdgePath(path);
            redrawGraph();
        });

        showAStar.setOnAction(e -> {
            try {
                showAllEdges = false;
                TrafficData trafficData = new TrafficData("src/traffic_data.csv");
                List<Integer> path = AStarSearch.findPath(graph, trafficData, 1, 109, TrafficTime.MORNING);
                highlightEdges = buildEdgePath(path);
                redrawGraph();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        showAll.setOnAction(e -> {
            showAllEdges = true;
            highlightEdges.clear();
            redrawGraph();
        });

        checkDisconnected.setOnAction(e -> {
            List<Integer> disconnected = new ArrayList<>();
            for (int id : graph.nodes.keySet()) {
                if (!graph.adjacencyList.containsKey(id) || graph.adjacencyList.get(id).isEmpty()) {
                    disconnected.add(id);
                }
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Disconnected Nodes");
            alert.setHeaderText("⚠️ Nodes with no connections:");
            if (disconnected.isEmpty()) {
                alert.setContentText("All nodes are connected.");
            } else {
                StringBuilder sb = new StringBuilder();
                for (int id : disconnected) {
                    sb.append(graph.nodes.get(id).name).append("\n");
                }
                alert.setContentText(sb.toString());
            }
            alert.showAndWait();
        });

        root.getChildren().addAll(legend, showMST, showShortest, showAStar, showAll, checkDisconnected, pane);

        Scene scene = new Scene(root, 1200, 600);
        stage.setTitle("🚦 Greater Cairo Transportation Network");
        stage.setScene(scene);
        stage.show();

        scene.setOnKeyPressed((KeyEvent e) -> {
            switch (e.getCode()) {
                case UP -> pane.setTranslateY(pane.getTranslateY() - 20);
                case DOWN -> pane.setTranslateY(pane.getTranslateY() + 20);
                case LEFT -> pane.setTranslateX(pane.getTranslateX() - 20);
                case RIGHT -> pane.setTranslateX(pane.getTranslateX() + 20);
                case PLUS, EQUALS -> {
                    zoom *= 1.1;
                    pane.setScaleX(zoom);
                    pane.setScaleY(zoom);
                }
                case MINUS -> {
                    zoom *= 0.9;
                    pane.setScaleX(zoom);
                    pane.setScaleY(zoom);
                }
            }
        });

        pane.setOnScroll((ScrollEvent event) -> {
            double factor = event.getDeltaY() > 0 ? 1.1 : 0.9;
            zoom *= factor;
            pane.setScaleX(zoom);
            pane.setScaleY(zoom);
        });

        pane.setOnMousePressed((MouseEvent e) -> {
            orgSceneX = e.getSceneX();
            orgSceneY = e.getSceneY();
        });

        pane.setOnMouseDragged((MouseEvent e) -> {
            double deltaX = e.getSceneX() - orgSceneX;
            double deltaY = e.getSceneY() - orgSceneY;
            translateX += deltaX;
            translateY += deltaY;
            pane.setTranslateX(translateX);
            pane.setTranslateY(translateY);
            orgSceneX = e.getSceneX();
            orgSceneY = e.getSceneY();
        });

        highlightEdges = MSTBuilder.buildMST(graph);
        redrawGraph();
    }

    private void redrawGraph() {
        pane.getChildren().clear();

        double width = 1200, height = 800, margin = 50;
        double minX = Double.MAX_VALUE, maxX = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE, maxY = Double.MIN_VALUE;

        for (Node node : graph.nodes.values()) {
            minX = Math.min(minX, node.x);
            maxX = Math.max(maxX, node.x);
            minY = Math.min(minY, node.y);
            maxY = Math.max(maxY, node.y);
        }

        double scaleX = (width - 2 * margin) / (maxX - minX + 1) * 3.0;
        double scaleY = (height - 2 * margin) / (maxY - minY + 1) * 3.0;

        for (Edge edge : graph.edges) {
            if (!showAllEdges && !highlightEdges.contains(edge)) continue;

            Node from = graph.nodes.get(edge.from);
            Node to = graph.nodes.get(edge.to);

            double x1 = (from.x - minX) * scaleX + margin;
            double y1 = (from.y - minY) * scaleY + margin;
            double x2 = (to.x - minX) * scaleX + margin;
            double y2 = (to.y - minY) * scaleY + margin;

            Line line = new Line(x1, y1, x2, y2);
            line.setStrokeWidth(1.5);

            if (highlightEdges.contains(edge)) {
                line.setStroke(Color.RED);
                Text distanceLabel = new Text((x1 + x2) / 2, (y1 + y2) / 2, String.format("%.1f km", edge.distance));
                distanceLabel.setStyle("-fx-font-size: 9px; -fx-fill: red;");
                pane.getChildren().add(distanceLabel);
            } else if (!edge.isExisting) {
                line.setStroke(Color.LIGHTBLUE);
                line.getStrokeDashArray().addAll(5.0, 5.0);
            } else {
                line.setStroke(Color.GRAY);
            }

            pane.getChildren().add(line);
        }

        for (Node node : graph.nodes.values()) {
            double x = (node.x - minX) * scaleX + margin;
            double y = (node.y - minY) * scaleY + margin;

            Circle circle = new Circle(x, y, 6, node.isFacility ? Color.ORANGE : Color.BLACK);
            Text label = new Text(x + 8, y - 6, node.name);
            label.setStyle("-fx-font-size: 10px;");
            pane.getChildren().addAll(circle, label);
        }
    }

    private List<Edge> buildEdgePath(List<Integer> path) {
        List<Edge> edges = new ArrayList<>();
        for (int i = 0; i < path.size() - 1; i++) {
            int from = path.get(i);
            int to = path.get(i + 1);
            for (Edge edge : graph.adjacencyList.get(from)) {
                if ((edge.from == from && edge.to == to) || (edge.from == to && edge.to == from)) {
                    edges.add(edge);
                    break;
                }
            }
        }
        return edges;
    }

    public static void main(String[] args) {
        launch(args);
    }
}




