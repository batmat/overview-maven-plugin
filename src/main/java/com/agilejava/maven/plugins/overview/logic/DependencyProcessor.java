package com.agilejava.maven.plugins.overview.logic;

import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import org.apache.maven.shared.dependency.tree.*;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ArtifactCollector;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.project.MavenProject;
import org.apache.maven.plugin.AbstractMojo;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import com.agilejava.maven.plugins.overview.vo.ArtifactVertex;
import com.agilejava.maven.plugins.overview.vo.DependencyEdge;

/**
 * Logic of dependency processing.
 */
public class DependencyProcessor {
    private List<String> excludes;
    private DependencyTreeBuilder dependencyTreeBuilder;
    private ArtifactRepository localRepository;
    private ArtifactFactory factory;
    private ArtifactMetadataSource artifactMetadataSource;
    private ArtifactCollector collector;
    private AbstractMojo abstractMojo;

    /**
     * Constructor injecting required dependencies.
     *
     * @param excludes List of excluded artifacts like: "artifact1,artifact2".
     * @param dependencyTreeBuilder Maven toolbox.
     * @param localRepository Maven toolbox.
     * @param factory Maven toolbox.
     * @param artifactMetadataSource Maven toolbox.
     * @param collector Maven toolbox.
     * @param abstractMojo Maven toolbox.
     */
    public DependencyProcessor(
            String excludes,
            DependencyTreeBuilder dependencyTreeBuilder,
            ArtifactRepository localRepository,
            ArtifactFactory factory,
            ArtifactMetadataSource artifactMetadataSource,
            ArtifactCollector collector,
            AbstractMojo abstractMojo) {
        this.dependencyTreeBuilder = dependencyTreeBuilder;
        this.localRepository = localRepository;
        this.factory = factory;
        this.artifactMetadataSource = artifactMetadataSource;
        this.collector = collector;
        this.abstractMojo = abstractMojo;
        if (this.abstractMojo.getLog().isDebugEnabled())
            this.abstractMojo.getLog().debug("excludes: " + excludes);
        String[] strings = excludes.split(",");
        this.excludes = new ArrayList<String>(strings.length);
        for (String string : strings) {
            String trimmed = string.trim();
            if (trimmed.length() > 0) {
                this.excludes.add(trimmed);
                if (this.abstractMojo.getLog().isDebugEnabled())
                    this.abstractMojo.getLog().debug("excluding: " + trimmed);
            }
        }
    }

    /**
     * Create dependency graph for project and sub-projects.
     *
     * @param project         Main project.
     * @param reactorProjects Sub projects.
     * @return Graph representing dependency.
     */
    public DirectedGraph createGraph(MavenProject project, List reactorProjects) {
        DirectedGraph graph = new DirectedSparseGraph();
        Map<Artifact, Vertex> processed = new HashMap<Artifact, Vertex>();

        // Single project processing, in case of aggregate projects graph is empty.
        process(project,
                graph,
                processed
        );

        // For pom project, process all modules.
        for (Object reactorProject : reactorProjects) {
            process((MavenProject) reactorProject,
                    graph,
                    processed
            );
        }
        return graph;
    }

    private Vertex process(
            MavenProject node,
            DirectedGraph graph,
            Map<Artifact, Vertex> processed
    ) {
        return process(resolveProject(node).getRootNode(), graph, processed);
    }

    private Vertex process(DependencyNode node, DirectedGraph graph, Map<Artifact, Vertex> processed) {
        Artifact artifact = node.getArtifact();
        if (!processed.containsKey(artifact)) {
            Vertex vertex = new ArtifactVertex(artifact, node.getDepth());
            vertex = graph.addVertex(vertex);
            processed.put(artifact, vertex);
            for (Object child : node.getChildren()) {
                DependencyNode next = (DependencyNode) child;
                Artifact nextArtifact = next.getArtifact();

                String id = nextArtifact.getId();
                if (!excludes.contains(id)) {
                    if (this.abstractMojo.getLog().isDebugEnabled())
                        this.abstractMojo.getLog().debug("Including artifact: " + id);
                    process(next, graph, processed);
                    graph.addEdge(new DependencyEdge(vertex, processed.get(nextArtifact), nextArtifact.getScope()));
                } else {
                    if (this.abstractMojo.getLog().isDebugEnabled())
                        this.abstractMojo.getLog().debug("Excluded artifact: " + id);
                }
            }
            return vertex;
        } else {
            ArtifactVertex vertex = (ArtifactVertex) processed.get(artifact);
            vertex.setDistance(Math.max(vertex.getDistance(), node.getDepth()));
            return vertex;
        }
    }

    private DependencyTree resolveProject(MavenProject project) {
        try {
            return dependencyTreeBuilder.buildDependencyTree(
                    project,
                    localRepository,
                    factory,
                    artifactMetadataSource,
                    collector);
        } catch (DependencyTreeBuilderException e) {
            abstractMojo.getLog().error("Unable to build dependency tree.", e);
            return null;
        }
    }
}