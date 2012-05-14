package com.nadeem.app.atmosphere;

import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.cpr.AtmosphereResourceEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventsLogger implements AtmosphereResourceEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventsLogger.class);

    public EventsLogger() {

    }

    public void onSuspend(final AtmosphereResourceEvent event) {
        LOGGER.info("onSuspend(): {}:{}", event.getResource().getRequest().getRemoteAddr(),
                event.getResource().getRequest().getRemotePort());
    }

    public void onResume(AtmosphereResourceEvent event) {
        LOGGER.info("onResume(): {}:{}", event.getResource().getRequest().getRemoteAddr(),
                event.getResource().getRequest().getRemotePort());
    }

    public void onDisconnect(AtmosphereResourceEvent event) {
        LOGGER.info("onDisconnect(): {}:{}", event.getResource().getRequest().getRemoteAddr(),
                event.getResource().getRequest().getRemotePort());
    }

    public void onBroadcast(AtmosphereResourceEvent event) {
        LOGGER.info("onBroadcast(): {}", event.getMessage());
    }

    public void onThrowable(AtmosphereResourceEvent event) {
        LOGGER.warn("onThrowable(): {}", event);
    }
}