package com.nadeem.app.atmosphere;

import org.atmosphere.websocket.WebSocketEventListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventListener extends WebSocketEventListenerAdapter {

	private final Logger LOGGER = LoggerFactory.getLogger(EventListener.class);

	@Override
	public void onMessage(WebSocketEvent event) {
		LOGGER.info("WebSocket message received from client " + event);
	}
}
