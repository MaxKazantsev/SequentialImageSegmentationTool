package org.mag.model;

import com.pixelmed.display.SourceImage;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferUShort;
import java.lang.reflect.Array;
import java.util.*;

public class Graph {

    //private ArrayList<Vertex> vertices;
    private ArrayList<ArrayList<Vertex>> vertices;
    private ArrayList<Segment> segments;
    private ArrayList<Edge> edges;
    private Complexity complexity;
    private int height;
    private int width;
    private int k;

    public Graph(SourceImage sourceImage, Complexity complexity, int k) {

        BufferedImage bufferedImage = sourceImage.getBufferedImage();
        short[] data = ((DataBufferUShort) bufferedImage.getRaster().getDataBuffer()).getData();

        this.complexity = complexity;
        this.height = bufferedImage.getHeight();
        this.width = bufferedImage.getWidth();
        this.k = k;

        //short[][] pixels = new short[height][width];
        short[][] pixels = vectorToMatrix(data, height, width);


        short max = 0;
        for (int i = 0; i < pixels.length; i += 64) {
            for(int j = 0; j < pixels[i].length; j += 64) {
                if(pixels[i][j] > max) {
                    max = pixels[i][j];
                }
            }
        }
        System.out.println("Max value: " + max);
        /*for(int j = 0; j < width; j++) {
            for (int i = 0; i < height; i++) {
                pixels[i][j] = (short)bufferedImage.getRGB(j , i);
            }
        }*/

        /*System.out.println("Old pixels: ");
        for (int i = 0; i < pixels.length; i += 64) {
            for(int j = 0; j < pixels[i].length; j += 64) {
                System.out.println(pixels[i][j]);
            }
        }*/

        this.vertices = new ArrayList<>(height);
        this.edges = new ArrayList<>();
        this.segments = new ArrayList<>();
        Segment.k = this.k;
        //init vertices
        initVertices(pixels);

        //add all edges to graph
        for (ArrayList row : vertices) {
            for (Object vertex : row) {
                Vertex v = (Vertex) vertex;
                addAllEdgesTo(v);
            }
        }

        //removing duplicate edges
        //System.out.println("Edges number before: " + edges.size());
        Set<Edge> set = new HashSet<>(edges);
        edges.clear();
        edges.addAll(set);
        //System.out.println("Edges number after: " + edges.size());

    }

    private void initVertices(short[][] pixels) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Vertex v = new Vertex(i, j, pixels[i][j]);
                vertices.add(new ArrayList<>(width));
                vertices.get(i).add(v);
                Segment s = new Segment();
                v.setSegment(s);
                s.getVertices().add(v);
                segments.add(s);
            }
        }
    }

    private void addAllEdgesTo(Vertex vertex) {

        int X = vertex.getX();
        int Y = vertex.getY();

        if(X < width-1) {
            Edge e = new Edge(vertex, vertices.get(Y).get(X+1));
            vertex.getEdges().add(e);
            edges.add(e);
        }
        if(Y < height-1) {
            Edge e = new Edge(vertex, vertices.get(Y+1).get(X));
            vertex.getEdges().add(e);
            edges.add(e);
        }
        if(X > 0) {
            Edge e = new Edge(vertex, vertices.get(Y).get(X-1));
            vertex.getEdges().add(e);
            edges.add(e);
        }
        if(Y > 0) {
            Edge e = new Edge(vertex, vertices.get(Y-1).get(X));
            vertex.getEdges().add(e);
            edges.add(e);
        }

        if (complexity == Complexity.EIGHT_CONNECTED) {

            if(X < width-1 && Y > 0) {
                Edge e = new Edge(vertex, vertices.get(Y-1).get(X+1));
                vertex.getEdges().add(e);
                if(X > 0) {
                    Edge e1 = new Edge(vertex, vertices.get(Y-1).get(X-1));
                    vertex.getEdges().add(e1);
                    edges.add(e1);
                }
                edges.add(e);
            }

            if(Y < height-1 && X > 0) {
                Edge e = new Edge(vertex, vertices.get(Y+1).get(X-1));
                vertex.getEdges().add(e);
                if(X < width-1) {
                    Edge e1 = new Edge(vertex, vertices.get(Y+1).get(X+1));
                    vertex.getEdges().add(e1);
                    edges.add(e1);
                }
                edges.add(e);
            }
        }

        /*if(X < width-1) {
            vertex.getNeighbors().add(vertices.get(Y).get(X+1));
        }
        if(Y < height-1) {
            vertex.getNeighbors().add(vertices.get(Y+1).get(X));
        }
        if(X > 0) {
            vertex.getNeighbors().add(vertices.get(Y).get(X-1));
        }
        if(Y > 0) {
            vertex.getNeighbors().add(vertices.get(Y-1).get(X));
        }

        if (complexity == Complexity.EIGHT_CONNECTED) {

            if(X < width-1 && Y > 0) {
                vertex.getNeighbors().add(vertices.get(Y-1).get(X+1));
                if(X > 0) {
                    vertex.getNeighbors().add(vertices.get(Y-1).get(X-1));
                }
            }

            if(Y < height-1 && X > 0) {
                vertex.getNeighbors().add(vertices.get(Y+1).get(X-1));
                if(X < width-1) {
                    vertex.getNeighbors().add(vertices.get(Y+1).get(X+1));
                }
            }
        }*/
    }

    private short[] matrixToVector(short[][] matrix) {

        short[] vector = new short[matrix.length * matrix[0].length];
        int q = 0;

        for(int i = 0; i < matrix.length; i++) {
            for(int j = 0; j < matrix[i].length; j++) {
                vector[q] = matrix[i][j];
                q++;
            }
        }
        return vector;
    }

    private short[][] vectorToMatrix(short[] vector, int rows, int cols) {
        short[][] matrix = new short[rows][cols];
        int q = 0;

        for(int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = vector[q];
                q++;
            }
        }
        return matrix;
    }

    public ArrayList<ArrayList<Vertex>> getVertices() {
        return vertices;
    }

    public void segmentGraph() {

        edges.sort(Comparator.comparing(Edge::getLength));
        System.out.println("Starting segments number: " + segments.size());
        for (Edge e : edges) {
            Vertex src = e.getSource();
            Vertex dst = e.getDestination();
            Segment srcSegment = src.getSegment();
            Segment dstSegment = dst.getSegment();
            if (srcSegment != dstSegment) {//if not from the same segment
                double srcMax = srcSegment.getMaxEdgeLength();
                double dstMax = dstSegment.getMaxEdgeLength();
                double srcThreshold = srcSegment.getThreshold();
                double dstThreshold = dstSegment.getThreshold();

                double MInt = Double.min(srcMax + srcThreshold, dstMax + dstThreshold);

                if (MInt >= e.getLength())
                    mergeSegments(srcSegment, dstSegment);
            }
        }
        System.out.println("Final segments number: " + segments.size());
        highlightSegments();
    }

    private void mergeSegments(Segment src, Segment dst) {

        for(Edge e: dst.getEdges())
            src.getEdges().add(e);
        for(Vertex v: dst.getVertices()) {
            v.setSegment(src);
            src.getVertices().add(v);
        }
        segments.remove(dst);
    }

    public short[] getNewPixelValues() {
        short[][] newPixels = new short[height][width];

        for (int i = 0; i < height; i++) {
            for(int j = 0; j < width; j++) {
                newPixels[i][j] = vertices.get(i).get(j).getIntensity();
            }
        }

        /*System.out.println("New pixels: ");
        for (int i = 0; i < newPixels.length; i += 64) {
            for(int j = 0; j < newPixels[i].length; j += 64) {
                System.out.println(newPixels[i][j]);
            }
        }*/

        return matrixToVector(newPixels);
    }

    private void highlightSegments() {
        short intensity = 0;
        for(Segment s: segments) {
            /*if(s.getVertices().size() < 1000) {
                intensity = 0;
            }*/
            for (Vertex v: s.getVertices()) {
                v.setIntensity(intensity);
            }
            //intensity = 1300;
            intensity += 100;
        }
    }
}


