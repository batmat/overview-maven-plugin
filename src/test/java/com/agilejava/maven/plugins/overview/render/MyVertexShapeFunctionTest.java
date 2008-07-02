package com.agilejava.maven.plugins.overview.render;

import com.agilejava.maven.plugins.overview.vo.ArtifactVertex;
import edu.uci.ics.jung.graph.impl.SimpleSparseVertex;
import org.apache.maven.plugin.testing.stubs.ArtifactStub;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * MyVertexShapeFunction test.
 *
 * @author Hubert Iwaniuk
 */
public class MyVertexShapeFunctionTest {
  private MyVertexShapeFunction test;

  @Before
  public void setUp() {
    test = new MyVertexShapeFunction();
  }

  @Test
  public void getShape_nullParam() {
    assertNull(test.getShape(null));
  }

  @Test
  public void getShape_paramNotArtifactVertex() {
    assertEquals(
        MyVertexShapeFunction.DEFAULT, test.getShape(new SimpleSparseVertex()));
  }

  @Test
  public void getShape_Level0() {
    assertEquals(
        MyVertexShapeFunction.LEVEL0,
        test.getShape(new ArtifactVertex(new ArtifactStub(), 0)));
  }

  @Test
  public void getShape_Level1() {
    assertEquals(
        MyVertexShapeFunction.LEVEL1,
        test.getShape(new ArtifactVertex(new ArtifactStub(), 1)));
  }

  @Test
  public void getShape_Level2() {
    assertEquals(
        MyVertexShapeFunction.LEVEL2,
        test.getShape(new ArtifactVertex(new ArtifactStub(), 2)));
  }

  @Test
  public void getShape_Level3() {
    assertEquals(
        MyVertexShapeFunction.DEFAULT,
        test.getShape(new ArtifactVertex(new ArtifactStub(), 3)));
  }
}