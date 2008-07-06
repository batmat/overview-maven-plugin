package com.agilejava.maven.plugins.overview.render;

import com.agilejava.maven.plugins.overview.vo.ArtifactVertex;
import edu.uci.ics.jung.graph.impl.SimpleDirectedSparseVertex;
import static junit.framework.Assert.assertEquals;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.artifact.versioning.VersionRange;
import static org.junit.Assert.assertNull;
import org.junit.Test;

/**
 * MyVertexStringer tests.
 *
 * @author Hubert Iwaniuk
 * @see com.agilejava.maven.plugins.overview.render.MyVertexStringer
 * @since 1.2
 */
public class MyVertexStringerTest {

  /**
   * getLabel: null parameter.
   */
  @Test public void getLabelNullArtifact() {
    MyVertexStringer stringer = new MyVertexStringer(false);
    assertNull(stringer.getLabel(null));
  }

  /**
   * getLabel: not
   * {@link com.agilejava.maven.plugins.overview.vo.ArtifactVertex} parameter.
   */
  @Test public void getLabelNotArtifactVertex() {
    MyVertexStringer stringer = new MyVertexStringer(false);
    assertNull(stringer.getLabel(new SimpleDirectedSparseVertex()));
  }

  /**
   * getLabel: short label.
   */
  @Test public void getLabelShort() {
    MyVertexStringer stringer = new MyVertexStringer(false);
    assertEquals(
        "a", stringer.getLabel(
        new ArtifactVertex(
            new DefaultArtifact(
                "g", "a", VersionRange.createFromVersion("1.0"), "test", "jar",
                "", new DefaultArtifactHandler()),
            1
        )));
  }

  /**
   * getLabel: long label.
   */
  @Test public void getLabelLong() {
    MyVertexStringer stringer = new MyVertexStringer(true);
    assertEquals(
        "g:a:jar:1.0",
        stringer.getLabel(
            new ArtifactVertex(
                new DefaultArtifact(
                    "g", "a", VersionRange.createFromVersion("1.0"), "test",
                    "jar",
                    "", new DefaultArtifactHandler()),
                1
            )));
  }
}
