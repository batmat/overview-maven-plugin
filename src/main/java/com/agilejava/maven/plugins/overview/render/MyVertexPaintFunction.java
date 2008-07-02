package com.agilejava.maven.plugins.overview.render;

import com.agilejava.maven.plugins.overview.vo.ArtifactVertex;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.VertexPaintFunction;

import java.awt.*;

/**
 * Vertex paint function implementaion.
 */
public class MyVertexPaintFunction implements VertexPaintFunction {
  static final Color LEVEL1_COLOR = new Color(0xff1500);
  static final Color LEVEL2_COLOR = new Color(0xff8d60);
  static final Color LEVEL3_COLOR = new Color(0xffceaa);

  /**
   * Draw color.
   * Always black.
   *
   * @param vertex Ignored.
   * @return {@link java.awt.Color#BLACK} always.
   */
  public Paint getDrawPaint(Vertex vertex) {
    return Color.black;
  }

  public Paint getFillPaint(Vertex vertex) {
    if (vertex == null) {
      return null;
    }
    if (!(vertex instanceof ArtifactVertex)) {
      return Color.white;
    }
    ArtifactVertex artifactVertex = (ArtifactVertex) vertex;
    switch (artifactVertex.getDistance()) {
      case 0:
        return LEVEL1_COLOR;
      case 1:
        return LEVEL2_COLOR;
      case 2:
        return LEVEL3_COLOR;
      default:
        return Color.white;
    }
  }
}
