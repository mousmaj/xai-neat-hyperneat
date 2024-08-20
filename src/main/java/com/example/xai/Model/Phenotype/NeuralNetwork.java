package com.example.xai.Model.Phenotype;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import com.example.xai.Model.Genotype.Genome;
import com.example.xai.Model.Genotype.Layer;
import com.example.xai.Model.Genotype.NodeGene;
import org.graphstream.graph.*;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.view.Viewer.ThreadingModel;

import com.example.xai.Model.Genotype.ConnectionGene;

public class NeuralNetwork {
    Genome genome;
    Graph graph;
    FxViewer viewer;

    public enum TYPE {
        FULL, PARTLY
    }

    public NeuralNetwork(Genome genome, TYPE type) {
        // Set the system property to use JavaFX for GraphStream
        System.setProperty("org.graphstream.ui", "javafx");

        this.genome = genome;
        this.graph = new SingleGraph("NeuralNetwork");
        this.viewer = new FxViewer(graph, ThreadingModel.GRAPH_IN_GUI_THREAD);

        if(type == TYPE.FULL) {
            buildFullPhenotype();
        } else {
        buildPhenoytpe();
        }
    }

    // Build the phenotype of the genome.
    @SuppressWarnings("unchecked")
    private void buildPhenoytpe() {
        int xOffset = 100; // Abstand zwischen den Schichten
        int yOffset = 50; // Abstand zwischen den Knoten in derselben Schicht
    
        int calcMaxHiddenLayer = genome.getHiddenNodes().stream().mapToInt(NodeGene::getLayerIndex).max().orElse(0);
        int maxHiddenLayer = calcMaxHiddenLayer == 0 ? 1 : calcMaxHiddenLayer;
    
        int maxNodesInLayer = Math.max(genome.getInputNodes().size(),
            Math.max(genome.getHiddenNodes().size(), genome.getOutputNodes().size()));
            
    
        // Create one Node which represents the input layer. He got the name "Input" and as visible attribute the count of all inputs.
        Node inputNode = graph.addNode("Eingangsneuronen");
        inputNode.setAttribute("ui.label", "Eingangsneuronen");
        inputNode.setAttribute("ui.style", "shape:circle;fill-color:#3498db;size:40px;");
        inputNode.setAttribute("xyz", 0, ((maxNodesInLayer - 1) / 2.0) * yOffset, 0);
        inputNode.setAttribute("layer", "input");
        inputNode.setAttribute("layout.frozen"); // Fixiere den Knoten
    
        // Gehe über alle Edges und erstelle die Nodes und deren Verbindungen. Den Input Node haben wir bereits erstellt, weshalb du bei einem Node welcher auf Layer.INPUT liegt, den zuvor erstellten Node nimmst.
        // Vergewiisere dich immer ob die Ids bereits existieren, wenn ja, nimm den bereits erstellten Node.
    
        // List of all represented NodeIds
        List<Integer> representedHiddenNodes = new ArrayList<>();
        List<Integer> representedOutputNodes = new ArrayList<>();

        
        int labelNumber = 0;
        for(ConnectionGene con : genome.getActiveConnections().values()){
            long checkCount = 0;
            Node n = graph.getNode(Integer.toString(con.getFromNode().getId()));
            if (n == null) {
                if (con.getFromNode().getLayer() == Layer.INPUT) {
                    n = inputNode;
                } else {
                    n = graph.addNode(Integer.toString(con.getFromNode().getId()));
                    n.setAttribute("ui.label", Integer.toString(con.getFromNode().getId()));
                    n.setAttribute("layout.frozen"); // Fixiere den Knoten
                    if (con.getFromNode().getLayer() == Layer.OUTPUT) {
                        checkCount = graph.nodes().filter(x -> {
                            Object layerAttribute = x.getAttribute("layer");
                            return layerAttribute != null && layerAttribute.equals("output");
                        }).count();
        
                        n.setAttribute("ui.style", "shape:circle;fill-color:#976FDA;size:30px;");
                        n.setAttribute("xyz", (maxHiddenLayer * xOffset) + xOffset, ((maxNodesInLayer - genome.getOutputNodes().size()) / 2.0 + checkCount) * yOffset, 0);
                        n.setAttribute("layer", "output");
                        n.setAttribute("ui.label", labelNumber);
                        labelNumber++;
                    } else {
                        checkCount = graph.nodes().filter(x -> {
                            Object layerAttribute = x.getAttribute("layer");
                            return layerAttribute != null && layerAttribute.equals("hidden");
                        }).count();
                        n.setAttribute("ui.style", "shape:circle;fill-color:#f1c40f;size:30px;");
                        n.setAttribute("xyz", con.getFromNode().getLayerIndex() == 0 ? 1 * xOffset : (con.getFromNode().getLayerIndex() * xOffset), ((maxNodesInLayer - genome.getHiddenNodes().size()) / 2.0 + checkCount) * yOffset, 0);
                        n.setAttribute("layer", "hidden");
                    }
                }
            }
            Node n2 = graph.getNode(Integer.toString(con.getToNode().getId()));
            if (n2 == null) {
                if (con.getToNode().getLayer() == Layer.INPUT) {
                    n2 = inputNode;
                    throw new IllegalArgumentException("InputNode cannot be a toNode");
                } else {
                    n2 = graph.addNode(Integer.toString(con.getToNode().getId()));
                    n2.setAttribute("ui.label", Integer.toString(con.getToNode().getId()));
                    n2.setAttribute("layout.frozen"); // Fixiere den Knoten
                    if (con.getToNode().getLayer() == Layer.OUTPUT) {
                        checkCount = graph.nodes().filter(x -> {
                            Object layerAttribute = x.getAttribute("layer");
                            return layerAttribute != null && layerAttribute.equals("output");
                        }).count();
                        n2.setAttribute("ui.style", "shape:circle;fill-color:#976FDA;size:30px;");
                        n2.setAttribute("xyz", (maxHiddenLayer * xOffset) + xOffset, ((maxNodesInLayer - genome.getOutputNodes().size()) / 2.0 + checkCount) * yOffset, 0);
                        n2.setAttribute("layer", "output");
                        n2.setAttribute("ui.label", labelNumber);
                        labelNumber++;
                    } else {
                        checkCount = graph.nodes().filter(x -> {
                            Object layerAttribute = x.getAttribute("layer");
                            return layerAttribute != null && layerAttribute.equals("hidden");
                        }).count();
                        n2.setAttribute("ui.style", "shape:circle;fill-color:#f1c40f;size:30px;");
                        n2.setAttribute("xyz", con.getFromNode().getLayerIndex() == 0 ? 1 * xOffset : (con.getFromNode().getLayerIndex() * xOffset), ((maxNodesInLayer - genome.getHiddenNodes().size()) / 2.0 + checkCount) * yOffset, 0);
                        n2.setAttribute("layer", "hidden");
                    }
                }
            }
        
            // Berechne die Dicke der Kante basierend auf dem Gewicht
            double weight = Math.abs(con.getWeight());
            double edgeSize = Math.max(1, weight * 4); // Skaliere das Gewicht für die Anzeige
            String edgeColor = con.getWeight() > 0 ? "rgba(0,200,0,128);" : "rgba(200,0,0,128);"; // Farbe basierend auf dem Vorzeichen
        
            boolean inputHiddenCon = con.getFromNode().getLayer() == Layer.INPUT && con.getToNode().getLayer() == Layer.HIDDEN;
            boolean inputOutputCon = con.getFromNode().getLayer() == Layer.INPUT && con.getToNode().getLayer() == Layer.OUTPUT;
            boolean outputOutputCon = con.getFromNode().getLayer() == Layer.OUTPUT && con.getToNode().getLayer() == Layer.OUTPUT;
            if (outputOutputCon) {
                throw new IllegalArgumentException("OutputNode and OutputNode should not be connected");
            }

        
            Edge edge;
            if (inputHiddenCon) {
                if (!representedHiddenNodes.contains(con.getToNode().getId())) {
                    try {
                        // Add edge
                        edge = graph.addEdge(Integer.toString(con.getInnovationNumber()), n, n2, true);
                        edge.setAttribute("ui.style", "size:" + edgeSize + "px;fill-color:" + edgeColor);
                        representedHiddenNodes.add(con.getToNode().getId());
                    } catch (Exception e) {
                        System.out.println("Error - can't connect Input to Hidden: " + con.getFromNode().getId() + " -> " + con.getToNode().getId() + " " + e.getMessage());
                    }
                }
            } else if (inputOutputCon) {
                if (!representedOutputNodes.contains(con.getToNode().getId())) {
                    try {
                        // Add edge
                        edge = graph.addEdge(Integer.toString(con.getInnovationNumber()), n, n2, true);
                        edge.setAttribute("ui.style", "size:" + edgeSize + "px;fill-color:" + edgeColor);
                        representedOutputNodes.add(con.getToNode().getId());
                    } catch (Exception e) {
                        System.out.println("Error - can't connect Input to Output: " + con.getFromNode().getId() + " -> " + con.getToNode().getId() + " " + e.getMessage());
                    }
                }
            } else {
                // if innovation number is not already represented in graph edges
                if (graph.getEdge(Integer.toString(con.getInnovationNumber())) == null) {
                    try {
                        // Add edge
                        edge = graph.addEdge(Integer.toString(con.getInnovationNumber()), n, n2, true);
                        edge.setAttribute("ui.style", "size:" + edgeSize + "px;fill-color:" + edgeColor);
                    } catch (Exception e) {
                        System.out.println("Error - can't connect Input to Output: " + con.getFromNode().getId() + " -> " + con.getToNode().getId() + " " + e.getMessage());
                    }
                }
            }
        }

    }

private void buildFullPhenotype() {

    int xOffset = 100; // Abstand zwischen den Schichten
    int yOffset = 50; // Abstand zwischen den Knoten in derselben Schicht

    int calcMaxHiddenLayer = genome.getHiddenNodes().stream().mapToInt(n -> n.getLayerIndex()).max().orElse(0);
    int maxHiddenLayer = calcMaxHiddenLayer == 0 ? 1 : calcMaxHiddenLayer;

    int maxNodesInLayer = Math.max(genome.getInputNodes().size(),
        Math.max(genome.getHiddenNodes().size(), genome.getOutputNodes().size()));

    for(ConnectionGene con : genome.getConnections().values()){
        long checkCount = 0;
        Node n = graph.getNode(Integer.toString(con.getFromNode().getId()));
        if (n == null) {
            n = graph.addNode(Integer.toString(con.getFromNode().getId()));
            n.setAttribute("ui.label", Integer.toString(con.getFromNode().getId()));
            n.setAttribute("layout.frozen"); // Fixiere den Knoten
            if (con.getFromNode().getLayer() == Layer.OUTPUT) {
                checkCount = graph.nodes().filter(x -> {
                    Object layerAttribute = x.getAttribute("layer");
                    return layerAttribute != null && layerAttribute.equals("output");
                }).count();

                n.setAttribute("ui.style", "shape:circle;fill-color:#976FDA;size:30px;");
                n.setAttribute("xyz", (maxHiddenLayer * xOffset) + xOffset, ((maxNodesInLayer - genome.getOutputNodes().size()) / 2.0 + checkCount) * yOffset, 0);
                n.setAttribute("layer", "output");
            } else if (con.getFromNode().getLayer() == Layer.HIDDEN) {
                checkCount = graph.nodes().filter(x -> {
                    Object layerAttribute = x.getAttribute("layer");
                    return layerAttribute != null && layerAttribute.equals("hidden");
                }).count();
                n.setAttribute("ui.style", "shape:circle;fill-color:#f1c40f;size:30px;");
                n.setAttribute("xyz", con.getFromNode().getLayerIndex() == 0 ? 1 * xOffset : (con.getFromNode().getLayerIndex() * xOffset), ((maxNodesInLayer - genome.getHiddenNodes().size()) / 2.0 + checkCount) * yOffset, 0);
                n.setAttribute("layer", "hidden");
            } else {
                checkCount = graph.nodes().filter(x -> {
                    Object layerAttribute = x.getAttribute("layer");
                    return layerAttribute != null && layerAttribute.equals("input");
                }).count();
                n.setAttribute("ui.style", "shape:circle;fill-color:#3498db;size:40px;");
                n.setAttribute("xyz", 0, ((maxNodesInLayer - genome.getInputNodes().size()) / 2.0 + checkCount) * yOffset, 0);
                n.setAttribute("layer", "input");
            }
        }
        Node n2 = graph.getNode(Integer.toString(con.getToNode().getId()));
        if (n2 == null) {
            n2 = graph.addNode(Integer.toString(con.getToNode().getId()));
            n2.setAttribute("ui.label", Integer.toString(con.getToNode().getId()));
            n2.setAttribute("layout.frozen"); // Fixiere den Knoten
            if (con.getToNode().getLayer() == Layer.OUTPUT) {
                checkCount = graph.nodes().filter(x -> {
                    Object layerAttribute = x.getAttribute("layer");
                    return layerAttribute != null && layerAttribute.equals("output");
                }).count();
                n2.setAttribute("ui.style", "shape:circle;fill-color:#976FDA;size:30px;");
                n2.setAttribute("xyz", (maxHiddenLayer * xOffset) + xOffset, ((maxNodesInLayer - genome.getOutputNodes().size()) / 2.0 + checkCount) * yOffset, 0);
                n2.setAttribute("layer", "output");
            } else if (con.getToNode().getLayer() == Layer.HIDDEN) {
                checkCount = graph.nodes().filter(x -> {
                    Object layerAttribute = x.getAttribute("layer");
                    return layerAttribute != null && layerAttribute.equals("hidden");
                }).count();
                n2.setAttribute("ui.style", "shape:circle;fill-color:#f1c40f;size:30px;");
                n2.setAttribute("xyz", con.getToNode().getLayerIndex() == 0 ? 1 * xOffset : (con.getToNode().getLayerIndex() * xOffset), ((maxNodesInLayer - genome.getHiddenNodes().size()) / 2.0 + checkCount) * yOffset, 0);
                n2.setAttribute("layer", "hidden");
            } else {
                checkCount = graph.nodes().filter(x -> {
                    Object layerAttribute = x.getAttribute("layer");
                    return layerAttribute != null && layerAttribute.equals("input");
                }).count();
                n2.setAttribute("ui.style", "shape:circle;fill-color:#3498db;size:40px;");
                n2.setAttribute("xyz", 0, ((maxNodesInLayer - genome.getInputNodes().size()) / 2.0 + checkCount) * yOffset, 0);
                n2.setAttribute("layer", "input");
            }
        }

        // Berechne die Dicke der Kante basierend auf dem Gewicht
        double weight = Math.abs(con.getWeight());
        double edgeSize;
        String edgeColor;
        if(con.isEnabled()) {
            edgeSize = Math.max(1, weight * 4); // Skaliere das Gewicht für die Anzeige
            edgeColor = con.getWeight() > 0 ? "rgba(0,200,0,128);" : "rgba(200,0,0,128);"; // Farbe basierend auf dem Vorzeichen 
        } else {
            edgeSize = 1;
            // grey color
            edgeColor = "rgba(128,128,128,128);";
        }

        Edge edge;
        // Add edge
        edge = graph.addEdge(Integer.toString(con.getInnovationNumber()), n, n2, true);
        edge.setAttribute("ui.style", "size:" + edgeSize + "px;fill-color:" + edgeColor);

    }
}

    
    

    public Graph getGraph() {
        return graph;
    }
}
