package com.agilejava.maven.plugins.overview.render;

import edu.uci.ics.jung.graph.decorators.EdgeStringer;
import edu.uci.ics.jung.graph.ArchetypeEdge;
import com.agilejava.maven.plugins.overview.vo.DependencyEdge;

import java.util.List;

public class MyEdgeStringer implements EdgeStringer {
    private List<String> suppressedScopes;

    public MyEdgeStringer(List<String> suppressedScopes) {
        this.suppressedScopes = suppressedScopes;
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
