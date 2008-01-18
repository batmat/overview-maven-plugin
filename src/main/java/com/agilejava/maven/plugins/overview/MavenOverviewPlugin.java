package com.agilejava.maven.plugins.overview;

import edu.uci.ics.jung.graph.ArchetypeEdge;
import edu.uci.ics.jung.graph.ArchetypeVertex;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.EdgeStringer;
import edu.uci.ics.jung.graph.decorators.VertexPaintFunction;
import edu.uci.ics.jung.graph.decorators.VertexShapeFunction;
import edu.uci.ics.jung.graph.decorators.VertexStringer;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.visualization.PluggableRenderer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.contrib.KKLayout;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactCollector;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.tree.DependencyNode;
import org.apache.maven.shared.dependency.tree.DependencyTree;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilder;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilderException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Wilfred Springer
 * @goal generate
 */
public class MavenOverviewPlugin extends AbstractMojo {

    /**
     * Artifacts to be excluded. Regular expressions matching the full artifact
     * designation.
     * 
     * @parameter expression="${excludes}"
     */
    private String excludes = "";

    /**
     * The projects in the reactor for aggregation report.
     * 
     * <p>
     * Note: This is passed by Maven and must not be configured by the user.
     * </p>
     * 
     * @parameter expression="${reactorProjects}"
     * @readonly
     */
    private List reactorProjects;

    /**
     * @parameter expression="${basedir}/target/${project.artifactId}.png"
     */
    @SuppressWarnings( { "UnusedDeclaration" })
    private File outputFile;

    /**
     * @parameter expression="${project}"
     */
    @SuppressWarnings( { "UnusedDeclaration" })
    private MavenProject project;

    /**
     * @parameter expression="${localRepository}"
     */
    @SuppressWarnings( { "UnusedDeclaration" })
    private ArtifactRepository localRepository;

    /**
     * Artifact collector, needed to resolve dependencies.
     * 
     * @component role="org.apache.maven.artifact.resolver.ArtifactCollector"
     * @required
     * @readonly
     */
    protected ArtifactCollector artifactCollector;

    /**
     * Artifact resolver, needed to download source jars for inclusion in
     * classpath.
     * 
     * @component role="org.apache.maven.artifact.resolver.ArtifactResolver"
     * @required
     * @readonly
     */
    protected ArtifactResolver artifactResolver;

    /**
     * Artifact factory, needed to download source jars for inclusion in
     * classpath.
     * 
     * @component role="org.apache.maven.artifact.factory.ArtifactFactory"
     * @required
     * @readonly
     */
    protected ArtifactFactory artifactFactory;

    /**
     * @component role="org.apache.maven.artifact.metadata.ArtifactMetadataSource"
     *            hint="maven"
     */
    protected ArtifactMetadataSource artifactMetadataSource;

    /**
     * Remote repositories which will be searched for source attachments.
     * 
     * @parameter expression="${project.remoteArtifactRepositories}"
     * @required
     * @readonly
     */
    protected List remoteArtifactRepositories;

    /**
     * @component
     */
    @SuppressWarnings( { "UnusedDeclaration" })
    private ArtifactCollector collector;

    /**
     * @component
     */
    protected ArtifactFactory factory;

    /**
     * @component
     */
    @SuppressWarnings( { "UnusedDeclaration" })
    private DependencyTreeBuilder dependencyTreeBuilder;

    /**
     * Suppressed scopes.
     * 
     * Scopes that are not supposed to be shown on graph as edge labels.
     */
    private List<String> suppressedScopes;

    public MavenOverviewPlugin() {
        suppressedScopes = new ArrayList<String>();
        suppressedScopes.add("compile");
    }

    private DependencyTree resolveProject() {
        return resolveProject(project);
    }

    private DependencyTree resolveProject(MavenProject project) {
        try {
            return dependencyTreeBuilder
                    .buildDependencyTree(project, localRepository, factory,
                            artifactMetadataSource, collector);
        } catch (DependencyTreeBuilderException e) {
            getLog().error("Unable to build dependency tree.", e);
            return null;
        }
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
        DependencyTree dependencyTree = resolveProject();
        DependencyNode node = dependencyTree.getRootNode();
        if (!outputFile.exists()) {
            outputFile.getParentFile().mkdirs();
        }
        getLog().info("Preparing graph.");
        DirectedGraph graph = new DirectedSparseGraph();
        Map<Artifact, Vertex> processed = new HashMap<Artifact, Vertex>();
        processDependencies(node, graph, processed, false);
        Iterator iterator = reactorProjects.iterator();
        while (iterator.hasNext()) {
            MavenProject sub = (MavenProject) iterator.next();
            dependencyTree = resolveProject(sub);
            node = dependencyTree.getRootNode();
            processDependencies(node, graph, processed, false);
        }
        // TreeLayout layout = new TreeLayout(graph);
        // DAGLayout layout = new DAGLayout(graph);
        // SpringLayout layout = new SpringLayout(graph);
        // FRLayout layout = new FRLayout(graph);
        KKLayout layout = new KKLayout(graph);
        // ISOMLayout layout = new ISOMLayout(graph);
        PluggableRenderer renderer = new PluggableRenderer();
        renderer.setVertexStringer(new VertexStringer() {
            public String getLabel(ArchetypeVertex vertex) {
                if (vertex instanceof ArtifactVertex) {
                    return ((ArtifactVertex) vertex).getArtifact()
                            .getArtifactId();
                } else {
                    return null;
                }
            }

        });
        renderer.setVertexPaintFunction(new VertexPaintFunction() {
            public Paint getDrawPaint(Vertex vertex) {
                return Color.black;
            }

            public Paint getFillPaint(Vertex vertex) {
                ArtifactVertex artifactVertex = (ArtifactVertex) vertex;
                switch (artifactVertex.getDistance()) {
                case 0:
                    return new Color(0xff1500);
                case 1:
                    return new Color(0xff8d60);
                case 2:
                    return new Color(0xffceaa);
                default:
                    return Color.white;
                }
            }
        });
        renderer.setVertexShapeFunction(new VertexShapeFunction() {
            public Shape getShape(Vertex vertex) {
                ArtifactVertex artifactVertex = (ArtifactVertex) vertex;
                double size;
                switch (artifactVertex.getDistance()) {
                case 0:
                    size = 40;
                    break;
                case 1:
                    size = 30;
                    break;
                case 2:
                    size = 25;
                    break;
                default:
                    size = 20;
                }
                return new Ellipse2D.Double(-(size / 2), -(size / 2), size,
                        size);
            }
        });
        renderer.setEdgeStringer(new EdgeStringer() {
            public String getLabel(ArchetypeEdge archetypeEdge) {
                if (archetypeEdge instanceof DependencyEdge) {
                    DependencyEdge edge = (DependencyEdge) archetypeEdge;
                    String scope = edge.toString();
                    if (!suppressedScopes.contains(scope)) {
                        return scope;
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            }
        });
        VisualizationViewer viewer = new VisualizationViewer(layout, renderer,
                new Dimension(1200, 1200));
        viewer.setDoubleBuffered(false);
        viewer.setSize(1200, 1200);
        viewer.setBackground(Color.WHITE);
        Container container = new Container();
        container.addNotify();
        container.add(viewer);
        container.setVisible(true);
        BufferedImage image = new BufferedImage(1200, 1200,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();

        // try {
        // Thread.sleep(20000);
        // } catch (InterruptedException ie) {
        // // Ok, apparently we need to move on.
        // }
        //
        // while (viewer.isVisRunnerRunning()) {
        // try {
        // Thread.sleep(1000);
        // getLog().info("Still running.");
        // } catch(InterruptedException ie) {
        // // Ok, apparently we need to move on.
        // }
        // }
        container.paintComponents(graphics);
        getLog().info("Generating " + outputFile.getAbsolutePath());
        try {
            ImageIO.write(image, "png", outputFile);
        } catch (IOException ioe) {
            System.err.println("Whoops.");
        }
    }

    private Vertex processDependencies(DependencyNode node,
            DirectedGraph graph, Map<Artifact, Vertex> processed, boolean module) {
        Artifact artifact = node.getArtifact();
        if (!processed.containsKey(artifact)) {
            Vertex vertex = new ArtifactVertex(artifact, node.getDepth(),
                    module);
            vertex = graph.addVertex(vertex);
            processed.put(artifact, vertex);
            for (Object child : node.getChildren()) {
                DependencyNode next = (DependencyNode) child;
                Artifact nextArtifact = next.getArtifact();
                boolean match = false;
                String[] excluded = excludes.split(",");
                for (int i = 0; i < excluded.length; i++) {
                    if (excluded[i].trim().length() > 0) {
                        match |= nextArtifact.getId().contains(excluded[i]);
                    }
                }
                if (!match) {
                    processDependencies(next, graph, processed, module);
                    graph.addEdge(new DependencyEdge(vertex, processed
                            .get(nextArtifact), nextArtifact.getScope()));
                }
            }
            return vertex;
        } else {
            ArtifactVertex vertex = (ArtifactVertex) processed.get(artifact);
            vertex.setDistance(Math.max(vertex.getDistance(), node.getDepth()));
            return vertex;
        }
    }

    private static class ArtifactVertex extends DirectedSparseVertex {

        private Artifact artifact;

        private int distance;

        private boolean module;

        public ArtifactVertex(Artifact artifact, int distance, boolean module) {
            super();
            this.artifact = artifact;
            this.distance = distance;
            this.module = module;
        }

        public Artifact getArtifact() {
            return artifact;
        }

        public boolean equals(Object object) {
            if (object instanceof ArtifactVertex) {
                return artifact.equals(((ArtifactVertex) object).getArtifact());
            } else {
                return false;
            }
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

    private static class DependencyEdge extends DirectedSparseEdge {
        private String label;

        public DependencyEdge(ArtifactVertex vertex1, ArtifactVertex vertex2) {
            super(vertex1, vertex2);
        }

        public DependencyEdge(Vertex vertex1, Vertex vertex2) {
            super(vertex1, vertex2);
        }

        public DependencyEdge(Vertex vertex1, Vertex vertex2, String label) {
            super(vertex1, vertex2);
            this.label = label;
        }

        public String toString() {
            return label != null ? label : super.toString();
        }
    }

}
