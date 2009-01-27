package com.agilejava.maven.plugins.overview;

import org.apache.maven.artifact.Artifact;

import java.util.regex.Pattern;

/**
 * Exclusion configuation holder.
 *
 * @author Hubert Iwaniuk
 * @since Jan 27, 2009
 */
public class Exclusion {

  /**
   * Field artifactId
   */
  private String artifactId;

  /**
   * Field groupId
   */
  private String groupId;

  /**
   * Field scope
   */
  private String scope;

  /**
   * Field version
   */
  private String version;

  /**
   * Field packaging
   */
  private String packaging;

  public String getArtifactId() {
    return artifactId;
  }

  public void setArtifactId(final String artifactId) {
    this.artifactId = artifactId;
  }

  public String getGroupId() {
    return groupId;
  }

  public void setGroupId(final String groupId) {
    this.groupId = groupId;
  }

  public String getScope() {
    return scope;
  }

  public void setScope(final String scope) {
    this.scope = scope;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(final String version) {
    this.version = version;
  }

  public void setPackaging(final String packaging) {
    this.packaging = packaging;
  }

  public String getPackaging() {
    return packaging;
  }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Exclusion exclusion = (Exclusion) o;

    if (artifactId != null ? !artifactId.equals(exclusion.artifactId) : exclusion.artifactId != null)
      return false;
    if (groupId != null ? !groupId.equals(exclusion.groupId) : exclusion.groupId != null)
      return false;
    if (packaging != null ? !packaging.equals(exclusion.packaging) : exclusion.packaging != null)
      return false;
    if (scope != null ? !scope.equals(exclusion.scope) : exclusion.scope != null)
      return false;
    if (version != null ? !version.equals(exclusion.version) : exclusion.version != null)
      return false;

    return true;
  }

  public int hashCode() {
    int result;
    result = (artifactId != null ? artifactId.hashCode() : 0);
    result = 31 * result + (groupId != null ? groupId.hashCode() : 0);
    result = 31 * result + (scope != null ? scope.hashCode() : 0);
    result = 31 * result + (version != null ? version.hashCode() : 0);
    result = 31 * result + (packaging != null ? packaging.hashCode() : 0);
    return result;
  }

  public String toString() {
    StringBuilder buf = new StringBuilder(this.getClass().getName());
    if (artifactId != null) {
      buf.append(" artifactId: '").append(artifactId).append("'");
    }
    if (groupId != null) {
      buf.append(" groupId: '").append(groupId).append("'");
    }
    if (version != null) {
      buf.append(" version: '").append(version).append("'");
    }
    if (packaging != null) {
      buf.append(" packaging: '").append(packaging).append("'");
    }
    if (scope != null) {
      buf.append(" scope: '").append(scope).append("'");
    }
    return buf.toString();
  }

  public boolean matches(Artifact artifact) {
    // TODO: compile patterns when set
    boolean matches = false;
    if (artifactId != null) {
      if (!Pattern.matches(artifactId, artifact.getArtifactId())) {
        return false;
      } else {
        matches = true;
      }
    }
    if (groupId != null) {
      if (!Pattern.matches(groupId, artifact.getGroupId())) {
        return false;
      } else {
        matches = true;
      }
    }
    if (version != null) {
      if (!Pattern.matches(version, artifact.getVersion())) {
        return false;
      } else {
        matches = true;
      }
    }
    if (scope != null && artifact.getScope() != null) {
      if (!Pattern.matches(scope, artifact.getScope())) {
        return false;
      } else {
        matches = true;
      }
    }
    if (packaging != null) {
      if (!Pattern.matches(packaging, artifact.getType())) {
        return false;
      } else {
        matches = true;
      }
    }
    return matches;
  }
}
