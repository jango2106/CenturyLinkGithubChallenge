package com.test.github.challenge;

import java.io.IOException;
import java.net.URI;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

/**
 *
 * @author Dustin Roan (dustin.a.roan@gmail.com)
 *
 */
public class CenturyLinkApplication {
	public static final String BASE_URL = "http://localhost:8080/service/";

	public static HttpServer startServer() {
		final ResourceConfig config = new ResourceConfig().packages("com.test.github.challenge.resource");
		return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URL), config);
	}

	public static void main(final String[] args) throws IOException {
		final HttpServer server = startServer();
		System.out.println("Rest application has started. Hit enter to stop.");
		System.in.read();
		server.shutdown();
	}
}
