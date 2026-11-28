
package com.railmate.ai.config;

import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.http.StreamableHttpMcpTransport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpClientConfig {

    @Bean
    public McpClient mcpClient() {

        // 1. Create transport to communicate with the MCP server
        StreamableHttpMcpTransport transport =
                StreamableHttpMcpTransport.builder()
                        .url("http://localhost:8081/mcp")
                        .logRequests(true)
                        .logResponses(true)
                        .build();

        // 2. Create and initialize the MCP client
        return DefaultMcpClient.builder()
                .key("railmate-backend")
                .transport(transport)
                .build();
    }

    @Bean
    public McpToolProvider mcpToolProvider(McpClient mcpClient) {

        // 3. Discover MCP tools and expose them to LangChain4j
        return McpToolProvider.builder()
                .mcpClients(mcpClient)
                .build();
    }
}
