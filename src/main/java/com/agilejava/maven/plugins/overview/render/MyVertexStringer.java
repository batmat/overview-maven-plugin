package com.agilejava.maven.plugins.overview.render;

import com.agilejava.maven.plugins.overview.vo.ArtifactVertex;
import edu.uci.ics.jung.graph.ArchetypeVertex;
import edu.uci.ics.jung.graph.decorators.VertexStringer;
import org.apache.maven.artifact.Artifact;

/**
 * Vertex string value provider.
 */
public class MyVertexStringer implements VertexStringer {
  private boolean fullLabel;

  /**
   * Ctor for vertex stringer.
   *
   * @param fullLabel if <code>true</code> labels are going to be full
   *                  <em>ID</em>s, if <code>false</code> labels are going to be
   *                  <em>artifactID</em>s.
   */
  public MyVertexStringer(boolean fullLabel) {
    this.fullLabel = fullLabel;
  }

  /** {@inheritDoc} */
  public String getLabel(ArchetypeVertex vertex) {
    if (vertex instanceof ArtifactVertex) {
      Artifact artifact = ((ArtifactVertex) vertex).getArtifact();
      return fullLabel ? artifact.getId() : artifact.getArtifactId();
    } else {
      return null;
    }
  }
}