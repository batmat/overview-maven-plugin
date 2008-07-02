package com.agilejava.maven.plugins.overview.render;

import org.junit.Before;
import org.junit.Test;import static org.junit.Assert.assertEquals;import static org.junit.Assert.assertNull;
import org.apache.maven.plugin.testing.stubs.ArtifactStub;

import java.awt.*;

import edu.uci.ics.jung.graph.impl.SimpleSparseVertex;
import com.agilejava.maven.plugins.overview.vo.ArtifactVertex;

/**
 * MyVertexPaintFunction test.
 *
 * @author Hubert Iwaniuk
 */
public class MyVertexPaintFunctionTest {
  private MyVertexPaintFunction test;

  @Before
  public void setUp() {
    test = new MyVertexPaintFunction();
  }

  /**
   * Ensure that draw is black.
   */
  @Test
  public void getDrawPaint() {
    assertEquals("Draw should be black.", Color.BLACK, test.getDrawPaint(null));
  }

  @Test
  public void getFillPaint_nullParam() {
    final Color color = (Color) test.getFillPaint(null);
    assertNull("Should not paint for null vertex.", color);
  }

  @Test
  public void getFillPaint_paramNotArtifactVertex() {
    final Color color = (Color) test.getFillPaint(new SimpleSparseVertex());
    assertEquals(
        "Should paint in white for unknown vertex.", Color.white, color);
  }

  @Test
  public void getFillPaint_Level0() {
    final Color color = (Color) test.getFillPaint(
        new ArtifactVertex(new ArtifactStub(), 0));
    assertEquals(
        "Should paint in white for unknown vertex.",
        MyVertexPaintFunction.LEVEL1_COLOR, color);
  }

  @Test
  public void getFillPaint_Level1() {
    final Color color = (Color) test.getFillPaint(
        new ArtifactVertex(new ArtifactStub(), 1));
    assertEquals(
        "Should paint in white for unknown vertex.",
        MyVertexPaintFunction.LEVEL2_COLOR, color);
  }

  @Test
  public void getFillPaint_Level2() {
    final Color color = (Color) test.getFillPaint(
        new ArtifactVertex(new ArtifactStub(), 2));
    assertEquals(
        "Should paint in white for unknown vertex.",
        MyVertexPaintFunction.LEVEL3_COLOR, color);
  }

  @Test
  public void getFillPaint_Level3() {
    final Color color = (Color) test.getFillPaint(
        new ArtifactVertex(new ArtifactStub(), 3));
    assertEquals(
        "Should paint in white for unknown vertex.", Color.white, color);
  }
}
