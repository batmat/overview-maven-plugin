package com.agilejava.maven.plugins.overview.render;

import com.agilejava.maven.plugins.overview.vo.ArtifactVertex;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.VertexPaintFunction;

import java.awt.*;

public class MyVertexPaintFunction implements VertexPaintFunction {

    public Paint getDrawPaint(Vertex vertex) {
        return Color.black;
    }

    public Paint getFillPaint(Vertex vertex) {
        ArtifactVertex artifactVertex = (ArtifactVertex) vertex;
        switch (artifactVertex.getDistance()) {
            case 0:
                return new Color(0xff1500);
            case 1:
                return new Color(0xff8d60);
            case 2:
                return new Color(0xffceaa);
            default:
                return Color.white;
        }
    }
}
