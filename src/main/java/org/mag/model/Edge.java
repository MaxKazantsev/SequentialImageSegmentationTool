package org.mag.model;

public class Edge {

    private Vertex source;
    private Vertex destination;
    private int length;

    public Edge(Vertex from, Vertex to) {
        this.source = from;
        this.destination = to;
        this.length = calculateLength();
    }

    private int calculateLength() {
        return Math.abs(source.getIntensity() - destination.getIntensity());
        /*double x2 = Math.pow(source.getX() - destination.getX(), 2);
        double y2 = Math.pow(source.getY() - destination.getY(), 2);
        double i2 = Math.pow(source.getIntensity() - destination.getIntensity(), 2);
        return (int)Math.sqrt(x2 + y2 + i2);*/
    }

    public Vertex getSource() {
        return source;
    }

    public Vertex getDestination() {
        return destination;
    }

    public int getLength() {
        return length;
    }

    @Override
    public boolean equals(Object otherObject) {

        if(this == otherObject)
            return true;

        if (otherObject == null)
            return false;

        if (this.getClass() != otherObject.getClass())
            return false;

        Edge other = (Edge) otherObject;

        return (this.source == other.destination) && (this.destination == other.source);
    }

    @Override
    public int hashCode() {
        return (source.hashCode() + destination.hashCode());
    }

    @Override
    public String toString() {
        return "Src: " + this.source.toString() + " Dst: " + this.destination.toString();
    }
}
