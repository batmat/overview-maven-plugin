package com.agilejava.maven.plugins.overview.vo;

import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import org.apache.maven.artifact.Artifact;

public class ArtifactVertex extends DirectedSparseVertex {

    private Artifact artifact;

    private int distance;

    public ArtifactVertex(Artifact artifact, int distance) {
        super();
        this.artifact = artifact;
        this.distance = distance;
    }

    public Artifact getArtifact() {
        return artifact;
    }

    public boolean equals(Object object) {
        return object instanceof ArtifactVertex && artifact.equals(((ArtifactVertex) object).getArtifact());
    }

    public int hashCode() {
        return artifact.hashCode();
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

}
