package com.agilejava.maven.plugins.overview.render;

import com.agilejava.maven.plugins.overview.vo.DependencyEdge;
import edu.uci.ics.jung.graph.ArchetypeEdge;
import edu.uci.ics.jung.graph.decorators.EdgeStringer;

public class MyEdgeStringer implements EdgeStringer {
    private String suppressedScopes;

    public MyEdgeStringer(String suppressedScopes) {
        this.suppressedScopes = suppressedScopes == null ? "" : suppressedScopes;
    }

    public String getLabel(ArchetypeEdge archetypeEdge) {
        if (archetypeEdge instanceof DependencyEdge) {
            DependencyEdge edge = (DependencyEdge) archetypeEdge;
            String scope = edge.toString();
            if (!suppressedScopes.contains(scope)) {
                return scope;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
