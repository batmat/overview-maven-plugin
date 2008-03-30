package com.agilejava.maven.plugins.overview.logic;

import com.agilejava.maven.plugins.overview.vo.ArtifactVertex;
import com.agilejava.maven.plugins.overview.vo.DependencyEdge;
import edu.uci.ics.jung.graph.DirectedGraph;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.shared.dependency.tree.DependencyNode;
import org.apache.maven.shared.dependency.tree.traversal.DependencyNodeVisitor;

import java.util.Map;

/**
 * Dependency extraction from Maven2 model.
 */
public class DependencyExtractor {

    private static final DependencyExtractor instnace = new DependencyExtractor();

    public static DependencyExtractor getInstance() { return instnace; }

    private DependencyExtractor() { }

    /**
     * Etracting visitor.
     */
    protected class ExtractorDNV implements DependencyNodeVisitor {
        private int depth = 0;
        private DirectedGraph graph;
        private Map<Artifact, ArtifactVertex> artifactVertexMap;

        public ExtractorDNV(DirectedGraph graph, Map<Artifact, ArtifactVertex> artifactVertexMap) {
            this.graph = graph;
            this.artifactVertexMap = artifactVertexMap;
        }

        public boolean visit(DependencyNode node) {
            Artifact artifact = node.getArtifact();
            ArtifactVertex vertex;
            System.out.println(depth + ": " + artifact.getId());
            if (!artifactVertexMap.containsKey(artifact)) {
                // vertex not yet in graph
                vertex = new ArtifactVertex(artifact, depth);
                artifactVertexMap.put(artifact, vertex);
                graph.addVertex(vertex);
            } else {
                // vertex already in graph align depth
                vertex = artifactVertexMap.get(artifact);
                vertex.alignDistance(depth);
            }
            if (node.getParent() != null) {
                final Artifact parentArtifact = node.getParent().getArtifact();
                graph.addEdge(
                        new DependencyEdge(
                                artifactVertexMap.get(parentArtifact),
                                vertex,
                                artifact.getScope())
                );
            }
            depth++; // increment depth
            return true; // process child nodes
        }

        public boolean endVisit(DependencyNode node) {
            depth--; // decrement depth
            return true; // process child nodes
        }
    }

    public void extractGraph(DependencyNode node, DirectedGraph graph, Map<Artifact, ArtifactVertex> artifactVertexMap) {
        node.accept(new ExtractorDNV(graph, artifactVertexMap));
    }
}
