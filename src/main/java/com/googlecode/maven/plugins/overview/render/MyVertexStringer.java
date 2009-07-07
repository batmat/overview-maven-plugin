package com.googlecode.maven.plugins.overview.render;

import com.googlecode.maven.plugins.overview.vo.ArtifactVertex;
import edu.uci.ics.jung.graph.ArchetypeVertex;
import edu.uci.ics.jung.graph.decorators.VertexStringer;
import org.apache.maven.artifact.Artifact;

/**
 * Vertex string value provider.
 */
public class MyVertexStringer implements VertexStringer {
  private boolean fullLabel;
  private boolean showVersion;

  /**
   * Ctor for vertex stringer.
   *
   * @param fullLabel if <code>true</code> labels are going to be full
   *                  <em>ID</em>s, if <code>false</code> labels are going to be
   * @param showVersion if artifact version should be shown on graph.
   */
  public MyVertexStringer(boolean fullLabel, boolean showVersion) {
    this.fullLabel = fullLabel;
    this.showVersion = showVersion;
  }

  /** {@inheritDoc} */
  public String getLabel(ArchetypeVertex vertex) {
    if (vertex instanceof ArtifactVertex) {
      Artifact artifact = ((ArtifactVertex) vertex).getArtifact();
      return fullLabel ?
              artifact.getId() :
              artifact.getArtifactId() +
                      (showVersion ?
                        ':' + artifact.getVersion() :
                        "");
    } else {
      return null;
    }
  }
}