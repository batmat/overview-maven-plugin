package com.agilejava.maven.plugins.overview.render;

import edu.uci.ics.jung.graph.decorators.VertexShapeFunction;
import edu.uci.ics.jung.graph.Vertex;

import java.awt.*;
import java.awt.geom.Ellipse2D;

import com.agilejava.maven.plugins.overview.vo.ArtifactVertex;

public class MyVertexShapeFunction implements VertexShapeFunction {
    public Shape getShape(Vertex vertex) {
        ArtifactVertex artifactVertex = (ArtifactVertex) vertex;
        double size;
        switch (artifactVertex.getDistance()) {
            case 0:
                size = 40;
                break;
            case 1:
                size = 30;
                break;
            case 2:
                size = 25;
                break;
            default:
                size = 20;
        }
        return new Ellipse2D.Double(-(size / 2), -(size / 2), size,
                size);
    }
}
