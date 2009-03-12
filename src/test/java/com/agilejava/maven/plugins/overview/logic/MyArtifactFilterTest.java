package com.agilejava.maven.plugins.overview.logic;

import com.agilejava.maven.plugins.overview.Exclusion;
import junit.framework.TestCase;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.plugin.testing.SilentLog;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * MyArtifactFilter Tester.
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class MyArtifactFilterTest extends TestCase {

    private MavenProject rootProject = new MavenProjectStub();

    @Before
    public void before() {
        rootProject.setGroupId("sampleGroupId");
        rootProject.setArtifactId("sampleArtifactId");
    }

  /**
   * {In,Ex}cludes list both null.
   */
  @Test
  public void testBothNull() {
    MyArtifactFilter filter = new MyArtifactFilter(
        rootProject, null, null, null, new SilentLog());
    assertTrue(
        filter.include(
            new DefaultArtifact(
                "s", "s", VersionRange.createFromVersion("1.0"), "test", "jar",
                "", new DefaultArtifactHandler())));
  }

  /**
   * Excludes list empty, includes null.
   */
  @Test
  public void testExcludeEmptyIncludesNull() {
    MyArtifactFilter filter = new MyArtifactFilter(
        rootProject, null, new ArrayList<Exclusion>(), null, new SilentLog());
    assertTrue(
        filter.include(
            new DefaultArtifact(
                "s", "s", VersionRange.createFromVersion("1.0"), "test", "jar",
                "", new DefaultArtifactHandler())));
  }

  /**
   * Excludes match, includes null.
   */
  @Test
  public void testExclusionsMatchIncludesNull() {
    final ArrayList<Exclusion> excludedList = new ArrayList<Exclusion>(1);
    final Exclusion exclusion = new Exclusion();
    exclusion.setScope("test");
    excludedList.add(exclusion);
    MyArtifactFilter filter = new MyArtifactFilter(
        rootProject, null, excludedList, null, new SilentLog());
    assertFalse(
        "Expected Exclusion to filter artifact out.",
        filter.include(
            new DefaultArtifact(
                "s", "s", VersionRange.createFromVersion("1.0"), "test", "jar",
                "", new DefaultArtifactHandler())));
  }

  /**
   * Excludes doesn't match, includes null.
   */
  @Test
  public void testExcludeNotMatchIncludesNull() {
    final ArrayList<Exclusion> excludedList = new ArrayList<Exclusion>(1);
    final Exclusion exclusion = new Exclusion();
    exclusion.setGroupId("s");
    exclusion.setArtifactId("s");
    exclusion.setVersion("1.1");
    exclusion.setPackaging("jar");
    excludedList.add(exclusion);

    MyArtifactFilter filter = new MyArtifactFilter(
        rootProject, null, excludedList, null, new SilentLog());
    assertTrue(
        filter.include(
            new DefaultArtifact(
                "s", "s", VersionRange.createFromVersion("1.0"), "test", "jar",
                "", new DefaultArtifactHandler())));
  }

  /**
   * Includes list empty, excludes null.
   */
  @Test
  public void testIncludeEmptyExcludesNull() {
    MyArtifactFilter filter = new MyArtifactFilter(
        rootProject, new ArrayList<String>(), null, null, new SilentLog());
    assertTrue(
        filter.include(
            new DefaultArtifact(
                "s", "s", VersionRange.createFromVersion("1.0"), "test", "jar",
                "", new DefaultArtifactHandler())));
  }

  /**
   * Includes match, excludes null.
   */
  @Test
  public void testIncludeMatchExcludesNull() {
    final ArrayList<String> includes = new ArrayList<String>();
    includes.add("testGroupID");
    MyArtifactFilter filter = new MyArtifactFilter(
        rootProject, includes, null, null, new SilentLog());
    assertTrue(
        filter.include(
            new DefaultArtifact(
                "testGroupID", "s", VersionRange.createFromVersion("1.0"), "test",
                "jar", "", new DefaultArtifactHandler())));
  }

  /**
   * Includes doesn't match, excludes null.
   */
  @Test
  public void testIncludeNotMatchExcludesNull() {
    final ArrayList<String> includes = new ArrayList<String>();
    includes.add("testGroupID");
    MyArtifactFilter filter = new MyArtifactFilter(
        rootProject, includes, null, null, new SilentLog());
    assertFalse(
        filter.include(
            new DefaultArtifact(
                "notTestGroupID", "s", VersionRange.createFromVersion("1.0"),
                "test", "jar", "", new DefaultArtifactHandler())));
  }
  
  
  /**
   * Test that artifacts with scope "test" are included but not artifacts with scope "compile"
   */
  @Test
  public void testScope() {
    MyArtifactFilter filter = new MyArtifactFilter(
        rootProject, null, null, Arrays.asList("test"), new SilentLog());
    assertTrue(
        filter.include(
            new DefaultArtifact(
                "a.group.id", "myArtifactId", VersionRange.createFromVersion("1.0"),
                "test", "jar", "", new DefaultArtifactHandler())));
    assertFalse(
            filter.include(
                new DefaultArtifact(
                    "a.group.id", "myArtifactId", VersionRange.createFromVersion("1.0"),
                    "compile", "jar", "", new DefaultArtifactHandler())));
  }
  
}
