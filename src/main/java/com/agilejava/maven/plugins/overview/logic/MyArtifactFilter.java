package com.agilejava.maven.plugins.overview.logic;

import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.Artifact;

import java.util.List;

/**
 * Artifact filtering.
 *
 * @author <a href="mailto:Hubert.Iwaniuk@gmail.com">Hubert Iwaniuk</a>
 */
class MyArtifactFilter implements ArtifactFilter {
    private List<String> excludes;

    public MyArtifactFilter(List<String> excludes) {
        this.excludes = excludes;
    }

    public boolean include(Artifact artifact) {
        return excludes == null || excludes.isEmpty() || !excludes.contains(artifact.getId());
    }
}
