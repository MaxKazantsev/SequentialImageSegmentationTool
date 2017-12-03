package org.mag.model;

import java.util.ArrayList;

public class Vertex {

    private int x;
    private int y;
    private short intensity;
    private ArrayList<Edge> edges;
    private Segment segment;

    public Vertex(int y, int x, short intensity) {
        this.y = y;
        this.x = x;
        this.intensity = intensity;
        this.edges = new ArrayList<>();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public short getIntensity() {
        return intensity;
    }

    public void setIntensity(short intensity) {
        this.intensity = intensity;
    }

    public Segment getSegment() {
        return segment;
    }

    public void setSegment(Segment segment) {
        this.segment = segment;
    }

    public ArrayList<Edge> getEdges() {
        return edges;
    }

    public Edge getShortestEdge() {
        Edge shortest = this.edges.get(0);

        for(int i = 1; i < edges.size(); i++) {
            if(edges.get(i).getLength() < shortest.getLength()) {
                shortest = edges.get(i);
            }
        }
        return shortest;
    }

    @Override
    public boolean equals(Object otherObject) {
        if(this == otherObject)
            return true;

        if (otherObject == null)
            return false;

        if (this.getClass() != otherObject.getClass())
            return false;

        Vertex other = (Vertex) otherObject;

        return this.x == other.x && this.y == other.y;
    }

    @Override
    public String toString() {
        return "X: " + x + " Y: " + y + " Intensity: " + intensity;
    }
}
