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

    public MyVertexStringer(boolean fullLabel) {
        this.fullLabel = fullLabel;
    }

    public String getLabel(ArchetypeVertex vertex) {
        if (vertex instanceof ArtifactVertex) {
            Artifact artifact = ((ArtifactVertex) vertex).getArtifact();
            return fullLabel ? artifact.getId() : artifact.getArtifactId();
        } else {
            return null;
        }
    }
}