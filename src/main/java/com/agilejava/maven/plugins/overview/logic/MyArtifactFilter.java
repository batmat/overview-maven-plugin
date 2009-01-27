package com.agilejava.maven.plugins.overview.logic;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.plugin.logging.Log;

import java.util.ArrayList;
import java.util.List;

import com.agilejava.maven.plugins.overview.Exclusion;

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
   * List of {@link Exclusion}s.
   */
  private List<Exclusion> exclusions;
  /**
   * List of included groupIDs.
   */
  private List<String> includes;

  /**
   * Ctor setting finltering parameters.
   *
   * @param includes List of included groupIDs.
   * @param exclusions List of {@link Exclusion}s.
   * @param log      logger.
   */
  public MyArtifactFilter(
      final List < String > includes, List<Exclusion> exclusions, final Log log) {
    this.exclusions = exclusions;
    this.includes = includes != null ? new ArrayList <String> (includes) : null;
    log.debug(
        "MyArtifactFilter: includes: \'" + includes + "\'.");
    log.debug(
        "MyArtifactFilter: excludes: \'" + exclusions + "\'.");
  }

  /**
   * {@inheritDoc}
   */
  public boolean include(Artifact artifact) {
    if (includes != null && !includes.isEmpty()) {
      boolean incl = false;
      for (String include : includes) {
        if (artifact.getGroupId().startsWith(include)) {
          incl = true;
          break;
        }
      }
      return incl;
    } else
      return exclusions == null || exclusions.isEmpty() || !exclusions.contains(
          artifact.getId());
  }
}
