
package com.railmate.railmate_mcp_server.config;

import com.railmate.railmate_mcp_server.tool.TrainSearchMcpTool;
import io.modelcontextprotocol.json.McpJsonDefaults;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.HttpServletStreamableServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema.ServerCapabilities;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.railmate.railmate_mcp_server.tool.SeatAvailabilityMcpTool;
import com.railmate.railmate_mcp_server.tool.BookingDetailsMcpTool;
import com.railmate.railmate_mcp_server.tool.UserBookingsMcpTool;
import com.railmate.railmate_mcp_server.tool.BookingPreparationMcpTool;
import com.railmate.railmate_mcp_server.tool.BookingCancellationMcpTool;

@Configuration
public class McpServerConfig {

    @Bean
    public HttpServletStreamableServerTransportProvider transportProvider() {

        return HttpServletStreamableServerTransportProvider.builder()
                .jsonMapper(McpJsonDefaults.getMapper())
                .mcpEndpoint("/mcp")
                .build();
    }

    @Bean
    public ServletRegistrationBean<?> mcpServlet(
            HttpServletStreamableServerTransportProvider transportProvider) {

        return new ServletRegistrationBean<>(transportProvider);
    }


    @Bean
    public McpSyncServer mcpSyncServer(
            HttpServletStreamableServerTransportProvider transportProvider,
            TrainSearchMcpTool trainSearchMcpTool,
            SeatAvailabilityMcpTool seatAvailabilityMcpTool,
            BookingDetailsMcpTool bookingDetailsMcpTool,
            UserBookingsMcpTool userBookingsMcpTool,
            BookingPreparationMcpTool bookingPreparationMcpTool,
            BookingCancellationMcpTool bookingCancellationMcpTool
    ) {

        McpSyncServer server = McpServer.sync(transportProvider)
                .serverInfo("railmate-mcp-server", "1.0.0")
                .capabilities(
                        ServerCapabilities.builder()
                                .tools(true)
                                .build()
                )
                .build();

        // Register our first MCP tool
        server.addTool(trainSearchMcpTool.specification());
        server.addTool(seatAvailabilityMcpTool.specification());
        server.addTool(bookingDetailsMcpTool.specification());
        server.addTool(userBookingsMcpTool.specification());
        server.addTool(bookingPreparationMcpTool.specification());
        server.addTool(bookingCancellationMcpTool.specification());

        return server;
    }

}
