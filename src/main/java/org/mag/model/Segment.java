package org.mag.model;

import java.util.ArrayList;

public class Segment {

    private ArrayList<Vertex> vertices;
    private ArrayList<Edge> edges;
    private int maxEdgeLength;
    public static int k;
    private double threshold;

    public Segment() {
        this.vertices = new ArrayList<>();
        this.edges = new ArrayList<>();
        this.maxEdgeLength = 0;
    }

    public int getMaxEdgeLength() {
        if(edges.size() > 0) {
            for(int i = 0; i < edges.size(); i++) {
                if(edges.get(i).getLength() > maxEdgeLength) {
                    maxEdgeLength = edges.get(i).getLength();
                }
            }
        }

        return maxEdgeLength;
    }

    public double getThreshold() {
        threshold = k / vertices.size();
        return threshold;
    }

    public ArrayList<Vertex> getVertices() {
        return vertices;
    }

    public ArrayList<Edge> getEdges() {
        return edges;
    }
}
