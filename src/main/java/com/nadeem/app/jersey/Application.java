package com.nadeem.app.jersey;

import javax.annotation.PostConstruct;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterConfig;
import org.atmosphere.cpr.BroadcasterFactory;
import org.atmosphere.cpr.DefaultBroadcaster;
import org.atmosphere.jersey.SuspendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nadeem.app.atmosphere.EventGenerator;
import com.nadeem.app.atmosphere.EventListener;
import com.nadeem.app.atmosphere.EventsLogger;
import com.nadeem.app.model.Event;
import com.sun.jersey.spi.resource.Singleton;

@Path("/")
@Singleton
public class Application {
	
	private final Logger LOGGER = LoggerFactory.getLogger(Application.class);
	
	private EventListener listener;
	private EventGenerator generator;
	private EventsLogger eventsLogger;

	private @Context BroadcasterFactory broadcasterFactory;
	
	@PostConstruct
	public void init() {
		LOGGER.info("Initializing EventResource");
		
		BroadcasterConfig config = getBroadcaster().getBroadcasterConfig();
		config.addFilter(new JsonFilter());

		listener 		= new EventListener();
		eventsLogger	= new EventsLogger();
		generator		= new EventGenerator(getBroadcaster(), 2000);
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public SuspendResponse<String> connect(@Context AtmosphereResource res) {
		return new SuspendResponse.SuspendResponseBuilder<String>()
				.broadcaster(getBroadcaster())
				.outputComments(true)
				.addListener(listener)
				.addListener(eventsLogger)
				.build();
	}
	
	@POST
	@Path("event")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response broadcastEvent(Event event) {
		LOGGER.info("New event : ", event);
		getBroadcaster().broadcast(event);
		return Response.ok().build();
	}

	@GET
	@Path("start")
	public Response start() {
		LOGGER.info("Starting EventGenerator");
		generator.start();
		return Response.ok().build();
	}

	@GET
	@Path("stop")
	public Response stop() {
		LOGGER.info("Stopping EventGenerator");
		generator.stop();
		return Response.ok().build();
	}

	private Broadcaster getBroadcaster() {
		return broadcasterFactory.lookup(DefaultBroadcaster.class, "pushMap", true);
	}
}
