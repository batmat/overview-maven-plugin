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

    /**
     * Singleton.
     *
     * @return Singleton instance of extractor.
     */
    public static DependencyExtractor getInstance() {
        return instnace;
    }

    private DependencyExtractor() {
    }

    /**
     * Etracting visitor.
     */
    protected class ExtractorDNV implements DependencyNodeVisitor {
        private int depth = 0;
        private DirectedGraph graph;
        private Map<Artifact, ArtifactVertex> artifactVertexMap;

        /**
         * Default constructor setting context for processing.
         *
         * @param graph             Graph to populate. In can be prepopulated already.
         * @param artifactVertexMap Cache of all nodes in <code>graph</code>.
         *                          content managed by extractor.
         */
        public ExtractorDNV(DirectedGraph graph, Map<Artifact, ArtifactVertex> artifactVertexMap) {
            this.graph = graph;
            this.artifactVertexMap = artifactVertexMap;
        }

        /**
         * Starts the visit to the specified dependency node.
         * <p/>
         * Description
         * <ul>
         * <li>If vertex already in graph:
         * <dl>
         * <dt>Vertex not present in graph</dt>
         * <dd>Creates new vertex in <code>graph</code>.</dd>
         * <dt>Vertex already present in graph</dt>
         * <dd>Adjusts distance of visited <code>node</code> if needed.</dd>
         * </dl>
         * </li>
         * <li>If <code>node</code> has a parent, create edge connecting parent to visited node.</li>
         * <li>Increases depth of visited dependency nodes.</li>
         * <li>Allows for processing of child nodes.</li>
         * </ul>
         *
         * @param node the dependency node to visit
         * @return <code>true</code> to visit the specified dependency node's children, <code>false</code> to skip the
         *         specified dependency node's children and proceed to its next sibling
         */
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
                // create edge connecting parent to visited node.
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

        /**
         * Ends the visit to to the specified dependency node.
         * <p/>
         * Description: reduces depth of visited nodes, allows for child nodes processing.
         *
         * @param node the dependency node to visit
         * @return <code>true</code> to visit the specified dependency node's next sibling, <code>false</code> to skip
         *         the specified dependency node's next siblings and proceed to its parent
         */
        public boolean endVisit(DependencyNode node) {
            depth--; // decrement depth
            return true; // process child nodes
        }
    }

    /**
     * Extracts graph from dependency node.
     *
     * @param node              Node to start extracting from.
     * @param graph             Populate this graph with extracted dependencies.
     * @param artifactVertexMap Storage for (artifact, vertex) mapping.
     */
    public void extractGraph(DependencyNode node, DirectedGraph graph, Map<Artifact, ArtifactVertex> artifactVertexMap) {
        node.accept(new ExtractorDNV(graph, artifactVertexMap));
    }
}
