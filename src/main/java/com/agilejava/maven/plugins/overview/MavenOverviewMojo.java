package com.agilejava.maven.plugins.overview;

import com.agilejava.maven.plugins.overview.logic.DependencyProcessor;
import com.agilejava.maven.plugins.overview.render.MyEdgeStringer;
import com.agilejava.maven.plugins.overview.render.MyVertexPaintFunction;
import com.agilejava.maven.plugins.overview.render.MyVertexShapeFunction;
import com.agilejava.maven.plugins.overview.render.MyVertexStringer;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.visualization.*;
import edu.uci.ics.jung.visualization.contrib.KKLayoutInt;
import edu.uci.ics.jung.visualization.contrib.KKLayout;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactCollector;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Maven Overview Plugin.
 * <p/>
 * Generates overview graph of project dependencies.
 *
 * @author Wilfred Springer
 * @author Hubert Iwaniuk
 * @goal overview
 * @phase site
 */
public class MavenOverviewMojo extends AbstractMavenReport {

    /**
     * Artifacts to be excluded. Regular expressions matching the full artifact
     * designation.
     *
     * @parameter expression="${excludes}"
     */
    private String excludes = "";

    /**
     * Rendered graph width in pixels.
     *
     * @parameter expression="${width}" default-value="1200"
     */
    private int width = 1200;

    /**
     * Rendered graph height in pixels.
     *
     * @parameter expression="${height}" default-value="1200"
     */
    private int height = 1200;

    /**
     * Should vertex name be full artifact ID.
     *
     * @parameter expression="${vertexFullLabel}" default-value="false"
     */
    private boolean vertexFullLabel = false;

    /**
     * Suppressed scopes.
     * <p/>
     * Scopes that are not supposed to be shown on graph as edge labels.
     * <p/>
     *
     * @parameter expression="${suppressedScopes}"
     */
    private String suppressedScopes;

    /**
     * Directory where reports will go.
     *
     * @parameter expression="${project.reporting.outputDirectory}"
     * @required
     * @readonly
     */
    private String outputDirectory;

    /**
     * The projects in the reactor for aggregation report.
     * <p/>
     * Note: This is passed by Maven and must not be configured by the user.
     * </p>
     *
     * @parameter expression="${reactorProjects}"
     * @readonly
     */
    private List reactorProjects;

    /**
     * Maven Project.
     * Default value: ${project}
     *
     * @parameter expression="${project}"
     */
    private MavenProject project;

    /**
     * Local Maven repository.
     *
     * @parameter expression="${localRepository}"
     */
    private ArtifactRepository localRepository;

    /**
     * Artifact collector, needed to resolve dependencies.
     *
     * @component role="org.apache.maven.artifact.resolver.ArtifactCollector"
     * @required
     * @readonly
     */
    private ArtifactCollector artifactCollector;

    /**
     * @component role="org.apache.maven.artifact.metadata.ArtifactMetadataSource" hint="maven"
     */
    protected ArtifactMetadataSource artifactMetadataSource;

    /**
     * @component
     */
    protected ArtifactFactory artifactFactory;

    /**
     * @component
     */
    private DependencyTreeBuilder dependencyTreeBuilder;

    /**
     * Generates the site report.
     *
     * @component
     */
    private Renderer siteRenderer;

    private static final String BUNDLE_NAME = "overview";
    private static final String DESCRIPTION = "report.overview.description";
    private static final String NAME = "report.overview.name";
    private static ResourceBundle info = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault());
    /**
     * Plugin Information
     */
    private String pluginName = info.getString("plugin.name");
    private String pluginVersion = info.getString("plugin.version");
    private String pluginBuilder = info.getString("plugin.buildBy");
    private static final int ITERATIONS = 1000;

    public MavenOverviewMojo() {
        suppressedScopes = "compile";
    }

    protected Renderer getSiteRenderer() {
        return siteRenderer;
    }

    protected String getOutputDirectory() {
        return outputDirectory;
    }

    protected MavenProject getProject() {
        return project;
    }

    public String getOutputName() {
        return "overview";
    }

    public String getName(Locale locale) {
        return getBundle(locale).getString(NAME);
    }

    public String getDescription(Locale locale) {
        return getBundle(locale).getString(DESCRIPTION);
    }

    private ResourceBundle getBundle(Locale locale) {
        return ResourceBundle.getBundle(BUNDLE_NAME, locale, this.getClass().getClassLoader());
    }

    /**
     * Here goes execution of report.
     *
     * @param locale Current locale.
     * @throws MavenReportException Say no more.
     */
    protected void executeReport(Locale locale) throws MavenReportException {
        Sink sink = getSink();
        sink.head();
        sink.title();
        sink.text(getName(locale));
        sink.title_();
        sink.head_();

        sink.body();
        sink.section1();

        sink.sectionTitle1();
        sink.text("Dependency Overview Graph for " + getProject().getName());
        sink.sectionTitle1_();
        sink.lineBreak();
        sink.lineBreak();
        generateOverview();
        sink.figure();
        sink.figureGraphics(getGraphLocationInSite());
        sink.figureCaption();
        sink.text("Dependency Overview Graph");
        sink.figureCaption_();
        sink.figure_();
        sink.lineBreak();
        sink.section1_();
        sink.body_();
        sink.flush();
        sink.close();
    }

    private void generateOverview() {
        getLog().debug(pluginName + " v" + pluginVersion + " build by " + pluginBuilder);
        File outputFile = new File(getGraphLocation());
        if (!outputFile.exists()) {
            getLog().debug("Creating outputFile: " + outputFile.getAbsolutePath());
            outputFile.getParentFile().mkdirs();
            getLog().info("Created outputFile: " + outputFile);
        }

        getLog().debug("Collecting data");
        DependencyProcessor dependencyProcessor = new DependencyProcessor(
                excludes,
                dependencyTreeBuilder,
                localRepository,
                artifactFactory,
                artifactMetadataSource,
                artifactCollector,
                this);

        getLog().debug("Generating graph");
        DirectedGraph graph = dependencyProcessor.createGraph(getProject(), reactorProjects);

        getLog().debug("Rendering graph");
        Layout layout = new KKLayout(graph);
        Dimension preferredSize = new Dimension(width, height);
        layout.initialize(preferredSize);
        layout.resize(preferredSize);
        if (layout.isIncremental()) {
            getLog().info("Incrementing graph");
            for (int i = 0; i < ITERATIONS; i++) {
                layout.advancePositions();
            }
        }

        PluggableRenderer renderer = setupRenderer();
        VisualizationViewer viewer = new VisualizationViewer(layout, renderer,
                preferredSize);
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

        getLog().debug("Writing image to " + outputFile.getAbsolutePath());
        try {
            ImageIO.write(image, "png", outputFile);
            getLog().info("Graph at: " + outputFile);
        } catch (IOException ioe) {
            getLog().error("Couldn't write to: " + outputFile, ioe);
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

    private String getGraphLocation() {
        return getOutputDirectory() + File.separator + getGraphLocationInSite();
    }

    public String getGraphLocationInSite() {
        return "images" + File.separator + getOutputName() + ".png";
    }
}
