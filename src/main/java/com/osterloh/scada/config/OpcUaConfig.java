package com.osterloh.scada.config;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.client.DiscoveryClient;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Configuration
public class OpcUaConfig {

    @Value("${opcua.endpoint-url}")
    private String endpointUrl;

    @Bean
    public OpcUaClient opcUaClient() throws ExecutionException, InterruptedException, UaException {
        EndpointDescription[] endpoints = CompletableFuture.supplyAsync(() -> {
            try {
                return DiscoveryClient.getEndpoints(endpointUrl)
                        .get()
                        .toArray(new EndpointDescription[0]);
            } catch (Exception e) {
                throw new RuntimeException("Failed to get endpoints OPC-UA", e);
            }
        }).join();

        EndpointDescription endpointDescription = endpoints[0];

        OpcUaClient client = OpcUaClient.create(
                endpointUrl,
                eps -> eps.stream()
                        .filter(e -> e.getSecurityPolicyUri().equals(SecurityPolicy.None.getUri()))
                        .findFirst(),
                cfg -> cfg.setApplicationName(LocalizedText.english("Client Java OPC-UA"))
                        .build()
        );

        client.connect().get();

        System.out.println("Connected to OPC-UA server" + endpointUrl);

        return client;
    }
}
