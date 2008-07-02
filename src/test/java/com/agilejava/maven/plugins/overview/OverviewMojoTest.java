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
   *
   * @throws Exception Failure.
   */
  public void testCompilerTestEnvironment() throws Exception {
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
  }
}
