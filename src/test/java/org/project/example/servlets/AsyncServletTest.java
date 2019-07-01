package org.project.example.servlets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.HttpURLConnection;
import java.net.URI;

import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class AsyncServletTest
{
    private static Server server;
    private static URI serverUri;
    private static Logger log = LogManager.getLogger(AsyncServletTest.class);
	

    @BeforeClass
    public static void startJetty() throws Exception
    {
        // Create Server
        server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(0); // auto-bind to available port
		server.addConnector(connector);

        // Add Servlets
        ServletHandler servletHandler = new ServletHandler();
        servletHandler.addServletWithMapping(AsyncServlet.class, "/async");
        server.setHandler(servletHandler);


        // Start Server
        server.start();

        // Determine Base URI for Server
        String host = connector.getHost();
        if (host == null)
        {
            host = "localhost";
        }
        int port = connector.getLocalPort();
        serverUri = new URI(String.format("http://%s:%d",host,port));
    }

    @AfterClass
    public static void stopJetty()
    {
        try
        {
            server.stop();
        }
        catch (Exception e)
        {
            log.error(e);
        }
    }

    @Test
    public void testGet() throws Exception
    {
      
        Response output = ClientBuilder.newClient()
                            .target(serverUri).path("/async")
                            .request().get();
      
        assertEquals("Should return status 200", 200, output.getStatus());
        assertTrue("Should return string async on reponse", output.readEntity(String.class).contains("async"));
        
    }
}