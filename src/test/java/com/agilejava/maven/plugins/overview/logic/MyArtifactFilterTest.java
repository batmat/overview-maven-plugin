package com.agilejava.maven.plugins.overview.logic;

import junit.framework.TestCase;
import org.junit.Test;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.artifact.versioning.VersionRange;

import java.util.ArrayList;

/**
 * MyArtifactFilter Tester.
 */
public class MyArtifactFilterTest extends TestCase {
    @Test public void testIncludeNull() {
        MyArtifactFilter filter = new MyArtifactFilter(null);
        assertTrue(filter.include(new DefaultArtifact("s", "s", VersionRange.createFromVersion("1.0"), "test", "jar", "", new DefaultArtifactHandler())));
    }

    @Test public void testIncludeEmpty() {
        MyArtifactFilter filter = new MyArtifactFilter(new ArrayList<String>());
        assertTrue(filter.include(new DefaultArtifact("s", "s", VersionRange.createFromVersion("1.0"), "test", "jar", "", new DefaultArtifactHandler())));
    }
}
