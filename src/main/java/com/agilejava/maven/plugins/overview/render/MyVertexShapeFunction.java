package com.agilejava.maven.plugins.overview.render;

import edu.uci.ics.jung.graph.decorators.VertexShapeFunction;
import edu.uci.ics.jung.graph.Vertex;

import java.awt.*;
import java.awt.geom.Ellipse2D;

import com.agilejava.maven.plugins.overview.vo.ArtifactVertex;

/**
 * Vertex shaping.
 */
public class MyVertexShapeFunction implements VertexShapeFunction {
  private static final int LEVEL0_SIZE = 40;
  private static final int LEVEL1_SIZE = 30;
  private static final int LEVEL2_SIZE = 25;
  private static final int DEFAULT_SIZE = 20;

  static final Shape LEVEL0 = new Ellipse2D.Double(
      -(LEVEL0_SIZE / 2), -(LEVEL0_SIZE / 2), LEVEL0_SIZE,
      LEVEL0_SIZE);
  static final Shape LEVEL1 = new Ellipse2D.Double(
      -(LEVEL1_SIZE / 2), -(LEVEL1_SIZE / 2), LEVEL1_SIZE,
      LEVEL1_SIZE);
  static final Shape LEVEL2 = new Ellipse2D.Double(
      -(LEVEL2_SIZE / 2), -(LEVEL2_SIZE / 2), LEVEL2_SIZE,
      LEVEL2_SIZE);
  static final Shape DEFAULT = new Ellipse2D.Double(
      -(DEFAULT_SIZE / 2), -(DEFAULT_SIZE / 2), DEFAULT_SIZE,
      DEFAULT_SIZE);

  public Shape getShape(Vertex vertex) {
    if (vertex == null) {
      return null;
    }
    if (!(vertex instanceof ArtifactVertex)) {
      return DEFAULT;
    }
    ArtifactVertex artifactVertex = (ArtifactVertex) vertex;
    switch (artifactVertex.getDistance()) {
      case 0:
        return LEVEL0;
      case 1:
        return LEVEL1;
      case 2:
        return LEVEL2;
      default:
        return DEFAULT;
    }
  }
}
