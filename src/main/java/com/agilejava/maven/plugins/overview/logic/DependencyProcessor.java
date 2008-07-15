package com.agilejava.maven.plugins.overview.logic;

import com.agilejava.maven.plugins.overview.vo.ArtifactVertex;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactCollector;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.tree.DependencyNode;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilder;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilderException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Logic of dependency processing. */
public class DependencyProcessor {
  private List<String> excludes;
  private List<String> includes;
  private DependencyTreeBuilder dependencyTreeBuilder;
  private ArtifactRepository localRepository;
  private ArtifactFactory factory;
  private ArtifactMetadataSource artifactMetadataSource;
  private ArtifactCollector collector;
  private AbstractMojo abstractMojo;

  /**
   * Constructor injecting required dependencies.
   *
   * @param includes               String with coma separated includes groupsIds
   *                               like: "com.gr1, com.gr2"
   * @param excludes               String with coma separated excluded artifacts
   *                               like: "artifact1,artifact2".
   * @param dependencyTreeBuilder  Maven toolbox.
   * @param localRepository        Maven toolbox.
   * @param factory                Maven toolbox.
   * @param artifactMetadataSource Maven toolbox.
   * @param collector              Maven toolbox.
   * @param abstractMojo           Maven toolbox.
   */
  public DependencyProcessor(
      String includes,
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
    final String[] exclSplited = excludes.split(",");
    processSplitedFilter(
        this.excludes = new ArrayList<String>(exclSplited.length), exclSplited,
        "excludes");
    final String[] inclSplited = includes.split(",");
    processSplitedFilter(
        this.includes = new ArrayList<String>(inclSplited.length), inclSplited,
        "includes");
    if (this.abstractMojo.getLog().isDebugEnabled()) {
      this.abstractMojo.getLog()
          .debug("DependencyProcessor: includes: " + this.includes);
      this.abstractMojo.getLog()
          .debug("DependencyProcessor: excludes: " + this.excludes);
    }
  }

  /**
   * Processes exclusion list.
   *
   * @param saveTo      Save splitetd exclusions.
   * @param splitedData Splited exclusions.
   * @param desc        description
   */
  private void processSplitedFilter(
      final List<String> saveTo, final String[] splitedData,
      final String desc) {
    for (String string : splitedData) {
      String trimmed = string.trim();
      if (trimmed.length() > 0) {
        saveTo.add(trimmed);
        logExclusion(trimmed, desc);
      }
    }
  }

  private void logExclusion(final String trimmed, final String desc) {
    if (this.abstractMojo.getLog().isDebugEnabled()) {
      this.abstractMojo.getLog()
          .debug("DependencyProcessor: " + desc + ": " + trimmed);
    }
  }

  /**
   * Create dependency graph for project and sub-projects.
   *
   * @param reactorProjects Sub projects.
   * @return Graph representing dependency.
   */
  public DirectedGraph createGraph(List reactorProjects) {
    DirectedGraph graph = new DirectedSparseGraph();
    Map<Artifact, ArtifactVertex> processed
        = new HashMap<Artifact, ArtifactVertex>();

    final MyArtifactFilter myArtifactFilter = new MyArtifactFilter(
        includes, excludes, abstractMojo.getLog());
    // For pom project, process all modules.
    for (Object reactorProject : reactorProjects) {
      process(
          (MavenProject) reactorProject,
          graph,
          processed, myArtifactFilter
      );
    }
    return graph;
  }

  private void process(
      MavenProject node,
      DirectedGraph graph,
      Map<Artifact, ArtifactVertex> processed,
      final MyArtifactFilter artifactFilter
  ) {
    abstractMojo.getLog()
        .debug("DependencyProcessor: Processing: " + node.getId());
    process(
        resolveProject(node, artifactFilter), graph, processed, artifactFilter);
  }

  private void process(
      DependencyNode node, DirectedGraph graph,
      Map<Artifact, ArtifactVertex> processed,
      final MyArtifactFilter artifactFilter) {
    DependencyExtractor extractor = DependencyExtractor.getInstance();
    extractor.extractGraph(
        node, graph, processed, this.abstractMojo.getLog(),
        artifactFilter);
  }

  private DependencyNode resolveProject(
      MavenProject project, final MyArtifactFilter filter) {
    try {
      abstractMojo.getLog()
          .debug("DependencyProcessor: Resolving project: " + project.getId());
      return dependencyTreeBuilder.buildDependencyTree(
          project,
          localRepository,
          factory,
          artifactMetadataSource,
          filter,
          collector);
    } catch (DependencyTreeBuilderException e) {
      abstractMojo.getLog()
          .error("DependencyProcessor: Unable to build dependency tree.", e);
      return null;
    }
  }
}
