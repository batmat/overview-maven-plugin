package com.agilejava.maven.plugins.overview;

/**
 * Exclusion configuation holder.
 *
 * @author Hubert Iwaniuk
 * @since Jan 27, 2009
 */
public class Exclusion {

    /** Field artifactId */
    private String artifactId;

    /** Field groupId */
    private String groupId;

    /** Field scope */
    private String scope;

    /** Field version */
    private String version;

    /** Field packaging */
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
}
