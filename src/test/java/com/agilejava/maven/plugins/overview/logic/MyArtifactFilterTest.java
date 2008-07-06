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

  /**
   * {In,Ex}cludes list both null.
   */
  @Test public void testBothNull() {
    MyArtifactFilter filter = new MyArtifactFilter(null, null);
    assertTrue(
        filter.include(
            new DefaultArtifact(
                "s", "s", VersionRange.createFromVersion("1.0"), "test", "jar",
                "", new DefaultArtifactHandler())));
  }

  /**
   * Excludes list empty, includes null.
   */
  @Test public void testExcludeEmptyIncludesNull() {
    MyArtifactFilter filter = new MyArtifactFilter(
        new ArrayList<String>(), null);
    assertTrue(
        filter.include(
            new DefaultArtifact(
                "s", "s", VersionRange.createFromVersion("1.0"), "test", "jar",
                "", new DefaultArtifactHandler())));
  }

  /**
   * Excludes match, includes null.
   */
  @Test public void testExcludeMatchIncludesNull() {
    final ArrayList<String> excludedList = new ArrayList<String>(1);
    excludedList.add("s:s:jar:1.0");
    MyArtifactFilter filter = new MyArtifactFilter(excludedList, null);
    assertFalse(
        filter.include(
            new DefaultArtifact(
                "s", "s", VersionRange.createFromVersion("1.0"), "test", "jar",
                "", new DefaultArtifactHandler())));
  }

  /**
   * Excludes doesn't match, includes null.
   */
  @Test public void testExcludeNotMatchIncludesNull() {
    final ArrayList<String> excludedList = new ArrayList<String>(1);
    excludedList.add("s:s:jar:1.1");
    MyArtifactFilter filter = new MyArtifactFilter(excludedList, null);
    assertTrue(
        filter.include(
            new DefaultArtifact(
                "s", "s", VersionRange.createFromVersion("1.0"), "test", "jar",
                "", new DefaultArtifactHandler())));
  }

  /**
   * Includes list empty, excludes null.
   */
  @Test public void testIncludeEmptyExcludesNull() {
    MyArtifactFilter filter = new MyArtifactFilter(
        null, new ArrayList<String>());
    assertTrue(
        filter.include(
            new DefaultArtifact(
                "s", "s", VersionRange.createFromVersion("1.0"), "test", "jar",
                "", new DefaultArtifactHandler())));
  }

  /**
   * Includes match, excludes null.
   */
  @Test public void testIncludeMatchExcludesNull() {
    final ArrayList<String> includes = new ArrayList<String>();
    includes.add("testGroupID");
    MyArtifactFilter filter = new MyArtifactFilter(null, includes);
    assertTrue(
        filter.include(
            new DefaultArtifact(
                "testGroupID", "s", VersionRange.createFromVersion("1.0"),
                "test", "jar",
                "", new DefaultArtifactHandler())));
  }

  /**
   * Includes doesn't match, excludes null.
   */
  @Test public void testIncludeNotMatchExcludesNull() {
    final ArrayList<String> includes = new ArrayList<String>();
    includes.add("testGroupID");
    MyArtifactFilter filter = new MyArtifactFilter(null, includes);
    assertFalse(
        filter.include(
            new DefaultArtifact(
                "notTestGroupID", "s", VersionRange.createFromVersion("1.0"),
                "test", "jar",
                "", new DefaultArtifactHandler())));
  }
}
