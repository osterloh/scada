package com.osterloh.scada.adapter.in.opcua;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.structured.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OpcUaNodeResolver {

    private final Map<String, NodeId> cache = new ConcurrentHashMap<>();

    public NodeId resolve(OpcUaClient client, String machineName, String browserName, int namespaceIndex) throws Exception {
        String key = machineName + "." + browserName;

        NodeId cached = cache.get(key);
        if (cached != null) return cached;

        RelativePathElement machineElement = element(namespaceIndex, machineName);

        RelativePathElement variableElement = element(namespaceIndex, browserName);

        RelativePath relativePath = new RelativePath(
                new RelativePathElement[]{machineElement, variableElement}
        );

        BrowsePath browserPath = new BrowsePath(Identifiers.ObjectsFolder, relativePath);

        TranslateBrowsePathsToNodeIdsResponse response = client.translateBrowsePaths(List.of(browserPath)).get();
        BrowsePathResult[] results = response.getResults();

        if (results == null || results.length == 0) {
            throw new RuntimeException("No results returned to: " + key);
        }

        BrowsePathResult result = results[0];

        if (result.getTargets() == null || result.getTargets().length == 0) {
            throw new RuntimeException("It's not possible solve the NodeId to: " + key +
                    " (statusCode=" + result.getStatusCode() + ")");
        }

        NodeId nodeId = result.getTargets()[0].getTargetId().toNodeId(client.getNamespaceTable())
                .orElseThrow(() -> new RuntimeException("Failure to convert ExpandedNodeId to NodeId: " + key));

        cache.put(key, nodeId);

        return nodeId;
    }

    private RelativePathElement element(int namespaceIndex, String name) {
        return new RelativePathElement(
                Identifiers.HierarchicalReferences,
                false,
                true,
                new QualifiedName(namespaceIndex, name)
        );
    }

}
