package com.agilejava.maven.plugins.overview;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import java.io.File;
import java.util.Locale;

/**
 * Basic integration test.
 *
 * @author Hubert Iwaniuk
 * @since 1.2
 */
public class OverviewMojoTest extends AbstractMojoTestCase {
  /**
   * Expected width.
   */
  private static final int EXPECTED_WIDTH = 900;

  /**
   * Expected height.
   */
  private static final int EXPECTED_HEIGHT = 900;

  /**
   * setUp().
   */
  protected void setUp() throws Exception {
    // required for mojo lookups to work
    super.setUp();
  }

  /**
   * Tests the proper discovery and configuration of the mojo.
   * <p/>
   * No Includes or Excludes set.
   *
   * @throws Exception Failure.
   */
  public void testOverviewTestEnvironment() throws Exception {
    File testPom = new File(
        getBasedir(),
        "target/test-classes/unit/overview-basic-test/plugin-config.xml");
    MavenOverviewMojo mojo = (MavenOverviewMojo) lookupMojo(
        "overview", testPom);
    assertNotNull(mojo);
    assertEquals(
        "Width should be set to value from test configuration.",
        EXPECTED_WIDTH, mojo.width);
    assertEquals(
        "Height should be set to value from test configuration.",
        EXPECTED_HEIGHT, mojo.height);
    assertEquals(
        "Vertex full labeling should be from test configuration.",
        true, mojo.vertexFullLabel);
    assertEquals(
        "Suppressed scopes should be from test configuration.",
        "provided, test", mojo.suppressedScopes);
    assertNotNull("SiteRenderer should not be null.", mojo.getSiteRenderer());
    assertNotNull("OutputName should not be null.", mojo.getOutputName());
    assertNotNull(
        "Name should not be null.", mojo.getName(Locale.getDefault()));
    assertNotNull(
        "getDescription should not be null.",
        mojo.getDescription(Locale.getDefault()));
    assertNotNull(
        "GraphLocationInSite should not be null.",
        mojo.getGraphLocationInSite());
    assertEquals(
        "Includes should be empty.",
        0, mojo.includes.length());
    assertEquals(
        "Excludes should be empty.",
        0, mojo.excludes.length());
  }

  /**
   * Tests the proper discovery and configuration of the mojo.
   * <p/>
   * Includes set.
   *
   * @throws Exception Failure.
   */
  public void testOverviewTestEnvironmentIncludesSet() throws Exception {
    File testPom = new File(
        getBasedir(),
        "target/test-classes/unit/overview-basic-test/plugin-config-includes.xml");
    MavenOverviewMojo mojo = (MavenOverviewMojo) lookupMojo(
        "overview", testPom);
    assertNotNull(mojo);
    assertEquals(
        "Width should be set to value from test configuration.",
        EXPECTED_WIDTH, mojo.width);
    assertEquals(
        "Height should be set to value from test configuration.",
        EXPECTED_HEIGHT, mojo.height);
    assertEquals(
        "Vertex full labeling should be from test configuration.",
        true, mojo.vertexFullLabel);
    assertEquals(
        "Suppressed scopes should be default 'compile'.",
        "compile", mojo.suppressedScopes);
    assertNotNull("SiteRenderer should not be null.", mojo.getSiteRenderer());
    assertNotNull("OutputName should not be null.", mojo.getOutputName());
    assertNotNull(
        "Name should not be null.", mojo.getName(Locale.getDefault()));
    assertNotNull(
        "getDescription should not be null.",
        mojo.getDescription(Locale.getDefault()));
    assertNotNull(
        "GraphLocationInSite should not be null.",
        mojo.getGraphLocationInSite());
    assertEquals(
        "Includes should be 'org.test1, org.test2'.",
        "org.test1, org.test2", mojo.includes);
    assertEquals(
        "Excludes should be empty.",
        0, mojo.excludes.length());
  }

  /**
   * Tests the proper discovery and configuration of the mojo.
   * <p/>
   * Excludes set.
   *
   * @throws Exception Failure.
   */
  public void testOverviewTestEnvironmentExcludesSet() throws Exception {
    File testPom = new File(
        getBasedir(),
        "target/test-classes/unit/overview-basic-test/plugin-config-excludes.xml");
    MavenOverviewMojo mojo = (MavenOverviewMojo) lookupMojo(
        "overview", testPom);
    assertNotNull(mojo);
    assertEquals(
        "Width should be set to value from test configuration.",
        EXPECTED_WIDTH, mojo.width);
    assertEquals(
        "Height should be set to value from test configuration.",
        EXPECTED_HEIGHT, mojo.height);
    assertEquals(
        "Vertex full labeling should be from test configuration.",
        true, mojo.vertexFullLabel);
    assertEquals(
        "Suppressed scopes should be default 'compile'.",
        "compile", mojo.suppressedScopes);
    assertNotNull("SiteRenderer should not be null.", mojo.getSiteRenderer());
    assertNotNull("OutputName should not be null.", mojo.getOutputName());
    assertNotNull(
        "Name should not be null.", mojo.getName(Locale.getDefault()));
    assertNotNull(
        "getDescription should not be null.",
        mojo.getDescription(Locale.getDefault()));
    assertNotNull(
        "GraphLocationInSite should not be null.",
        mojo.getGraphLocationInSite());
    assertEquals(
        "Includes should be empty.",
        0, mojo.includes.length());
    assertEquals(
        "Excludes should be 'g:a1:jar:1.0, g:a2:jar:1.0'.",
        "g:a1:jar:1.0, g:a2:jar:1.0", mojo.excludes);
  }

  /**
   * Tests the proper discovery and configuration of the mojo.
   * <p/>
   * Includes and Excludes set.
   *
   * @throws Exception Failure.
   */
  public void testOverviewTestEnvironmentIncludesExcludesSet()
      throws Exception {
    File testPom = new File(
        getBasedir(),
        "target/test-classes/unit/overview-basic-test/plugin-config-includes-excludes.xml");
    MavenOverviewMojo mojo = (MavenOverviewMojo) lookupMojo(
        "overview", testPom);
    assertNotNull(mojo);
    assertEquals(
        "Width should be set to value from test configuration.",
        EXPECTED_WIDTH, mojo.width);
    assertEquals(
        "Height should be set to value from test configuration.",
        EXPECTED_HEIGHT, mojo.height);
    assertEquals(
        "Vertex full labeling should be from test configuration.",
        true, mojo.vertexFullLabel);
    assertEquals(
        "Suppressed scopes should be default 'compile'.",
        "compile", mojo.suppressedScopes);
    assertNotNull("SiteRenderer should not be null.", mojo.getSiteRenderer());
    assertNotNull("OutputName should not be null.", mojo.getOutputName());
    assertNotNull(
        "Name should not be null.", mojo.getName(Locale.getDefault()));
    assertNotNull(
        "getDescription should not be null.",
        mojo.getDescription(Locale.getDefault()));
    assertNotNull(
        "GraphLocationInSite should not be null.",
        mojo.getGraphLocationInSite());
    assertEquals(
        "Includes should be 'org.test1, org.test2'.",
        "org.test1, org.test2", mojo.includes);
    assertEquals(
        "Excludes should be 'g:a1:jar:1.0, g:a2:jar:1.0'.",
        "g:a1:jar:1.0, g:a2:jar:1.0", mojo.excludes);
  }
}
