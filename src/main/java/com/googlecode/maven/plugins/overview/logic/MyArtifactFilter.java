package com.googlecode.maven.plugins.overview.logic;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.maven.plugins.overview.Exclusion;

/**
 * Artifact filtering.
 * <p/>
 * Configured by two lists:
 * <dl>
 * <dt>includes</dt><dd>List of included <em>groupID</em>s.</dd>
 * <dt>excludes</dt><dd>List of excluded <em>ID</em>s of artifacts.</dd>
 * </dl>
 * <p/>
 * Behavior (<code color="green">true</code> artifact included,
 * <code color="red">false</code> artifact filtered out):<br/>
 * if <code>includes</code> present:
 * <dl>
 * <dt>true</dt><dd>if artifacts groupID is found in <code>includes</code> then
 * <code color="green">true</code>, else <code color="red">false</code>.</dd>
 * <dt>false</dt>
 * <dd>if <code>excludes</code> present:
 * <dl>
 * <dt>true</dt><dd>if artifacts ID found in <code>excludes</code> than
 * <code color="red">false</code>, else <code color="green">true</code>.</dd>
 * <dt>false</dt><dd><code color="green">true</code>.</dd>
 * </dl>
 * </dd>
 * </dl>
 * <p/>
 * TODO: due to https://jira.codehaus.org/browse/MSHARED-4 filtering broken.
 *
 * @author <a href="mailto:Hubert.Iwaniuk@gmail.com">Hubert Iwaniuk</a>
 */
class MyArtifactFilter implements ArtifactFilter {

  /**
   * Reference to the root project in the graph -> should always be included
   */
  private MavenProject rootProject; 

  /**
   * List of {@link Exclusion}s.
   */
  private List<Exclusion> exclusions;
  /**
   * List of included groupIDs.
   */
  private List<String> includes;
  /**
   * List of included scopes.
   */
  private List<String> scopes;

  /**
   * Ctor setting filtering parameters.
 * @param rootProject Artifact of root project, this one should never be excluded.
 * @param includes   List of included groupIDs.
 * @param exclusions List of {@link Exclusion}s.
 * @param scopes List of included scopes.
 * @param log        logger.
   */
  public MyArtifactFilter(
      MavenProject rootProject, final List<String> includes, List<Exclusion> exclusions, List<String> scopes, final Log log) {
    this.rootProject = rootProject;
    this.exclusions = exclusions;
    this.includes = includes != null ? new ArrayList<String>(includes) : null;
    this.scopes = scopes;
    log.debug(
        "MyArtifactFilter: includes: \'" + includes + "\'.");
    log.debug(
        "MyArtifactFilter: excludes: \'" + exclusions + "\'.");
  }

  /**
   * {@inheritDoc}
   */
  public boolean include(Artifact artifact) {
    return isIncluded(artifact) & !isExcluded(artifact);
  }

  private boolean isIncluded(Artifact artifact) {
	//Check scopes
	if (scopes != null && scopes.size() > 0)
	{
		if (!isScopeIncluded(artifact)) return false;
	}
	//Check includes 
    boolean included = false;
    if (includes != null && !includes.isEmpty()) {
      for (String include : includes) {
        if (artifact.getGroupId().startsWith(include)) {
          included = true;
          break;
        }
      }
    } else {
      included = true;
    }
    return included;
  }

    /**
     * Checks if the artifact's scope is in the list of configured scopes
     * @param artifact The artifact to check
     * @return True if the artifact has a scope that is included in scopes, else false. Also true if the artifact is the root node in the diagram or if the artifact has empty scope and the list of configured scopes contain "compile" which is the default scope.
     */
	private boolean isScopeIncluded(Artifact artifact) {
		boolean include = false;
		for (String scope:scopes)
		{
			//If the artifact matches the root project it should always be included (scope is not relevant)
			if (rootProject != null && rootProject.getGroupId().equals(artifact.getGroupId()) && rootProject.getArtifactId().equals(artifact.getArtifactId()))
			{
				include = true;
			}
			else if (artifact.getScope() == null && scope.equals("compile"))
			{
				include = true;
			}
			else if (artifact.getScope() != null && artifact.getScope().equals(scope))
			{
				include = true;
			}
		}
		return include;
	}

  private boolean isExcluded(Artifact artifact) {
    boolean excluded = false;
    if (exclusions != null && !exclusions.isEmpty()) {
      for (Exclusion exclusion : exclusions) {
        if (exclusion.matches(artifact)) {
          excluded = true;
          break;
        }
      }
    } else {
      excluded = false;
    }
    return excluded;
  }
}
