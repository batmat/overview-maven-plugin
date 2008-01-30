package com.agilejava.maven.plugins.overview;

import com.agilejava.maven.plugins.overview.logic.DependencyProcessor;
import com.agilejava.maven.plugins.overview.render.MyEdgeStringer;
import com.agilejava.maven.plugins.overview.render.MyVertexPaintFunction;
import com.agilejava.maven.plugins.overview.render.MyVertexShapeFunction;
import com.agilejava.maven.plugins.overview.render.MyVertexStringer;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.visualization.PluggableRenderer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.contrib.KKLayout;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactCollector;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author Wilfred Springer
 * @author Hubert Iwaniuk
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
     * @parameter expression="${width}" default-value="1200"
     */
    private int width = 1200;

    /**
     * @parameter expression="${height}" default-value="1200"
     */
    private int height = 1200;

    /**
     * @parameter expression="${vertexFullLabel}" default-value="false"
     */
    private boolean vertexFullLabel = false;

    /**
     * Suppressed scopes.
     * <p/>
     * Scopes that are not supposed to be shown on graph as edge labels.
     * <p/>
     * @parameter expression="${suppressedScopes}"
     */
    private String suppressedScopes;

    /**
     * The projects in the reactor for aggregation report.
     * <p/>
     * Note: This is passed by Maven and must not be configured by the user.
     * </p>
     *
     * @parameter expression="${reactorProjects}"
     * @readonly
     */
    @SuppressWarnings({"UnusedDeclaration", "MismatchedQueryAndUpdateOfCollection"})
    private List reactorProjects;

    /**
     * @parameter expression="${basedir}/target/${project.artifactId}.png"
     */
    @SuppressWarnings({"UnusedDeclaration"})
    private File outputFile;

    /**
     * @parameter expression="${project}"
     */
    @SuppressWarnings({"UnusedDeclaration"})
    private MavenProject project;

    /**
     * @parameter expression="${localRepository}"
     */
    @SuppressWarnings({"UnusedDeclaration"})
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
     * hint="maven"
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
    @SuppressWarnings({"UnusedDeclaration"})
    private ArtifactCollector collector;

    /**
     * @component
     */
    protected ArtifactFactory factory;

    /**
     * @component
     */
    @SuppressWarnings({"UnusedDeclaration"})
    private DependencyTreeBuilder dependencyTreeBuilder;

    public MavenOverviewPlugin() {
        suppressedScopes = "compile";
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (!outputFile.exists()) {
            outputFile.getParentFile().mkdirs();
        }
        getLog().info("Preparing graph.");
        DependencyProcessor dependencyProcessor = new DependencyProcessor(
                excludes,
                dependencyTreeBuilder,
                localRepository,
                factory,
                artifactMetadataSource,
                collector,
                this);

        DirectedGraph graph = dependencyProcessor.createGraph(project, reactorProjects);

        // TreeLayout layout = new TreeLayout(graph);
        // DAGLayout layout = new DAGLayout(graph);
        // SpringLayout layout = new SpringLayout(graph);
        // FRLayout layout = new FRLayout(graph);
        KKLayout layout = new KKLayout(graph);
        // ISOMLayout layout = new ISOMLayout(graph);
        PluggableRenderer renderer = setupRenderer();
        VisualizationViewer viewer = new VisualizationViewer(layout, renderer,
                new Dimension(width, height));
        viewer.setDoubleBuffered(false);
        viewer.setSize(width, height);
        viewer.setBackground(Color.WHITE);
        Container container = new Container();
        container.addNotify();
        container.add(viewer);
        container.setVisible(true);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();

        container.paintComponents(graphics);
        getLog().info("Generating " + outputFile.getAbsolutePath());
        try {
            ImageIO.write(image, "png", outputFile);
        } catch (IOException ioe) {
            getLog().error("Whoops.", ioe);
        }
    }

    private PluggableRenderer setupRenderer() {
        PluggableRenderer renderer = new PluggableRenderer();
        renderer.setVertexStringer(new MyVertexStringer(vertexFullLabel));
        renderer.setVertexPaintFunction(new MyVertexPaintFunction());
        renderer.setVertexShapeFunction(new MyVertexShapeFunction());
        renderer.setVertexLabelCentering(true);
        renderer.setEdgeStringer(new MyEdgeStringer(suppressedScopes));
        return renderer;
    }
}
