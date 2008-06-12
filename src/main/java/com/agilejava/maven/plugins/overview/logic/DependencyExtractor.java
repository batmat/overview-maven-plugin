package com.agilejava.maven.plugins.overview.logic;

import com.agilejava.maven.plugins.overview.vo.ArtifactVertex;
import com.agilejava.maven.plugins.overview.vo.DependencyEdge;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.exceptions.ConstraintViolationException;
import edu.uci.ics.jung.utils.PredicateUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.shared.dependency.tree.DependencyNode;
import org.apache.maven.shared.dependency.tree.traversal.DependencyNodeVisitor;
import org.apache.maven.plugin.logging.Log;

import java.util.Map;

/** Dependency extraction from Maven2 model. */
public final class DependencyExtractor {

  /** Instance. */
  private static final DependencyExtractor INSTANCE = new DependencyExtractor();

  /**
   * Singleton.
   *
   * @return Singleton instance of extractor.
   */
  public static DependencyExtractor getInstance() {
    return INSTANCE;
  }

  /** Private constructor. */
  private DependencyExtractor() {
  }

  /** Etracting visitor. */
  protected class ExtractorDNV implements DependencyNodeVisitor {
    /** Depth of execution. */
    private int depth = 0;
    /** Graph. */
    private DirectedGraph graph;
    /** Mapping of Artifacts to Vertexes. */
    private Map<Artifact, ArtifactVertex> artifactVertexMap;
    /** Logger. */
    private Log log;

    /**
     * Default constructor setting context for processing.
     *
     * @param directedGraph Graph to populate. In can be prepopulated already.
     * @param vertexMap     Cache of all nodes in <code>graph</code>.
     * @param logger        Maven logger.
     */
    public ExtractorDNV(
        final DirectedGraph directedGraph,
        final Map<Artifact, ArtifactVertex> vertexMap,
        final Log logger) {
      this.graph = directedGraph;
      this.artifactVertexMap = vertexMap;
      this.log = logger;
    }

    /**
     * Starts the visit to the specified dependency node.
     * <p/>
     * Description <ul> <li>If vertex already in graph: <dl> <dt>Vertex not
     * present in graph</dt> <dd>Creates new vertex in <code>graph</code>.</dd>
     * <dt>Vertex already present in graph</dt> <dd>Adjusts distance of visited
     * <code>node</code> if needed.</dd> </dl> </li> <li>If <code>node</code>
     * has a parent, create edge connecting parent to visited node.</li>
     * <li>Increases depth of visited dependency nodes.</li> <li>Allows for
     * processing of child nodes.</li> </ul>
     *
     * @param node the dependency node to visit
     *
     * @return <code>true</code> to visit the specified dependency node's
     *         children, <code>false</code> to skip the specified dependency
     *         node's children and proceed to its next sibling
     */
    public final boolean visit(final DependencyNode node) {
      Artifact artifact = node.getArtifact();
      ArtifactVertex vertex;
      boolean wasHereBefore = false;

      if (log.isDebugEnabled()) {
        log.debug("ExtractorDNV: " + depth + ": " + artifact.getId());
      }

      if (!artifactVertexMap.containsKey(artifact)) {
        // vertex not yet in graph
        vertex = new ArtifactVertex(artifact, depth);
        artifactVertexMap.put(artifact, vertex);
        graph.addVertex(vertex);
        if (log.isDebugEnabled()) {
          log.debug("ExtractorDNV: " + "added vertex.");
        }
      } else {
        // vertex already in graph align depth
        vertex = artifactVertexMap.get(artifact);
        vertex.alignDistance(depth);
        wasHereBefore = true;
        if (log.isDebugEnabled()) {
          log.debug("ExtractorDNV: " + "aligned vertex.");
        }
      }
      final DependencyNode parentNode = node.getParent();
      final DependencyEdge dependencyEdge;
      if (parentNode != null) {
        // create edge connecting parent to visited node.
        final Artifact parentArtifact = parentNode.getArtifact();
        dependencyEdge = new DependencyEdge(
            artifactVertexMap.get(parentArtifact),
            vertex,
            artifact.getScope());

        if (log.isDebugEnabled()) {
          log.debug(
              "ExtractorDNV: " + "adding edge from parent: "
                  + parentArtifact.getId() + ".");
        }
        try {
          graph.addEdge(dependencyEdge);
        } catch (ConstraintViolationException e) {
          /* (2) If the constraint is not simple (as in the example above;
          NotPredicate takes a Predicate as an argument), catch the
          ConstraintViolationException, call getViolatedConstraint() on it, and
          use PredicateUtils.evaluateNestedPredicates() on the constraint to
          find out the results of evaluating each constituent predicate.
          */
          Map result = PredicateUtils.evaluateNestedPredicates(
              e.getViolatedConstraint(), dependencyEdge);
          for (Object entry : result.entrySet()) {
            log.warn(
                "Predicate Violation: key: " + ((Map.Entry) entry).getKey()
                    + ", value: " + ((Map.Entry) entry).getValue());
          }
        }
      } else {
        if (log.isDebugEnabled()) {
          log.debug("ExtractorDNV: was here before: " + wasHereBefore + ".");
          if (parentNode != null) {
            log.debug(
                "ExtractorDNV: parenNode: "
                    + parentNode.getArtifact().getId() + ".");
          }
        }
      }

      depth++; // increment depth
      return !wasHereBefore; // process child nodes if hasn't been here before
    }

    /**
     * Ends the visit to to the specified dependency node.
     * <p/>
     * Description: reduces depth of visited nodes, allows for child nodes
     * processing.
     *
     * @param node the dependency node to visit
     *
     * @return <code>true</code> to visit the specified dependency node's next
     *         sibling, <code>false</code> to skip the specified dependency
     *         node's next siblings and proceed to its parent
     */
    public final boolean endVisit(final DependencyNode node) {
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
   * @param log               Maven logger.
   */
  public void extractGraph(
      final DependencyNode node, final DirectedGraph graph,
      final Map<Artifact, ArtifactVertex> artifactVertexMap, final Log log) {
    node.accept(new ExtractorDNV(graph, artifactVertexMap, log));
  }
}
