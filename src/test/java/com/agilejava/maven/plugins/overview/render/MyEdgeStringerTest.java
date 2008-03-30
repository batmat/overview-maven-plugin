package com.agilejava.maven.plugins.overview.render;

import junit.framework.TestCase;
import org.junit.Test;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import edu.uci.ics.jung.graph.ArchetypeEdge;
import edu.uci.ics.jung.graph.ArchetypeGraph;
import edu.uci.ics.jung.graph.ArchetypeVertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.utils.UserDataContainer;

import java.util.Set;
import java.util.Iterator;

import com.agilejava.maven.plugins.overview.vo.DependencyEdge;
import com.agilejava.maven.plugins.overview.vo.ArtifactVertex;

/**
 * MyEdgeStringer test
 *
 * @author Hubert Iwaniuk
 */
public class MyEdgeStringerTest extends TestCase {
    /**
     * Test getLabel behavior on not DependencyEdge.
     */
    @Test
    public void testGetLabelNotDependencyEdge() {
        MyEdgeStringer underTest = new MyEdgeStringer(null);
        assertNull(underTest.getLabel(
                new ArchetypeEdge() {
                    public Set getIncidentVertices() {
                        return null;
                    }
                    @SuppressWarnings({"CloneDoesntCallSuperClone"})
                    public Object clone() throws CloneNotSupportedException {
                        return null;
                    }
                    public ArchetypeEdge getEqualEdge(ArchetypeGraph g) {
                        return null;
                    }
                    public ArchetypeEdge getEquivalentEdge(ArchetypeGraph g) {
                        return null;
                    }
                    public int numVertices() {
                        return 0;
                    }
                    public boolean isIncident(ArchetypeVertex v) {
                        return false;
                    }
                    public ArchetypeEdge copy(ArchetypeGraph g) {
                        return null;
                    }
                    public ArchetypeGraph getGraph() {
                        return null;
                    }
                    public Set getIncidentElements() {
                        return null;
                    }
                    public void addUserDatum(Object key, Object datum, CopyAction copyAct) {
                    }
                    public void importUserData(UserDataContainer udc) {
                    }
                    public Iterator getUserDatumKeyIterator() {
                        return null;
                    }
                    public CopyAction getUserDatumCopyAction(Object key) {
                        return null;
                    }
                    public Object getUserDatum(Object key) {
                        return null;
                    }
                    public void setUserDatum(Object key, Object datum, CopyAction copyAct) {
                    }
                    public Object removeUserDatum(Object key) {
                        return null;
                    }
                    public boolean containsUserDatumKey(Object key) {
                        return false;
                    }
                }));
    }

    /**
     * Tests behavior of getLabel for DependencyEdge with no scopes suppressed.
     */
    @Test
    public void testGetLabelDependencyEdge() {
        String expectedLabel = "foo";
        String group = "g";
        String version = "v";
        String type = "jar";
        ArtifactVertex vertex1 = new ArtifactVertex(new DefaultArtifact(group, "a1", VersionRange.createFromVersion(version), expectedLabel, type, null, new DefaultArtifactHandler()), 0);
        ArtifactVertex vertex2 = new ArtifactVertex(new DefaultArtifact(group, "a2", VersionRange.createFromVersion(version), expectedLabel, type, null, new DefaultArtifactHandler()), 0);
        DirectedSparseGraph g = new DirectedSparseGraph();
        g.addVertex(vertex1);
        g.addVertex(vertex2);
        MyEdgeStringer underTest = new MyEdgeStringer(null);
        String label = underTest.getLabel(new DependencyEdge(vertex1, vertex2, expectedLabel));
        assertNotNull(label);
        assertEquals(expectedLabel, label);
    }

    /**
     * Test getLabel behavior on DependencyEdge with suppressed scope.
     */
    @Test
    public void testGetLabelSuppressedScope() {
        String suppressedScope = "bar";
        String group = "g";
        String version = "v";
        String type = "jar";
        ArtifactVertex vertex1 = new ArtifactVertex(new DefaultArtifact(group, "a1", VersionRange.createFromVersion(version), suppressedScope, type, null, new DefaultArtifactHandler()), 0);
        ArtifactVertex vertex2 = new ArtifactVertex(new DefaultArtifact(group, "a2", VersionRange.createFromVersion(version), suppressedScope, type, null, new DefaultArtifactHandler()), 0);
        DirectedSparseGraph g = new DirectedSparseGraph();
        g.addVertex(vertex1);
        g.addVertex(vertex2);
        MyEdgeStringer underTest = new MyEdgeStringer(suppressedScope);
        assertNull(underTest.getLabel(new DependencyEdge(vertex1, vertex2, suppressedScope)));
    }

    /**
     * Test getLabel behavior on DependencyEdge with not suppressed scope.
     */
    @Test
    public void testGetLabelNotSuppressedScope() {
        String expectedLabel = "foo";
        String suppressedScope = "bar";
        String group = "g";
        String version = "v";
        String type = "jar";
        ArtifactVertex vertex1 = new ArtifactVertex(new DefaultArtifact(group, "a1", VersionRange.createFromVersion(version), expectedLabel, type, null, new DefaultArtifactHandler()), 0);
        ArtifactVertex vertex2 = new ArtifactVertex(new DefaultArtifact(group, "a2", VersionRange.createFromVersion(version), expectedLabel, type, null, new DefaultArtifactHandler()), 0);
        DirectedSparseGraph g = new DirectedSparseGraph();
        g.addVertex(vertex1);
        g.addVertex(vertex2);
        MyEdgeStringer underTest = new MyEdgeStringer(suppressedScope);
        String label = underTest.getLabel(new DependencyEdge(vertex1, vertex2, expectedLabel));
        assertNotNull(label);
        assertEquals(expectedLabel, label);
    }
}
