package com.nadeem.app.atmosphere;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.atmosphere.cpr.Broadcaster;

import com.nadeem.app.model.Event;

public class EventGenerator {
	
	private Broadcaster broadcaster;
	private int interval;
	private Thread generator;
	private AtomicInteger currentIndex;
	
	private static List<Event> events = new ArrayList<Event>();
	
	static {
		
		events.add(new Event(41.246766314966735, -95.83972984374998,"This is message One"));
		events.add(new Event(41.288055098900614, -95.79578453124998,"This is message Two"));
		events.add(new Event(41.325192679004296,-95.71338707031248,"This is message Three"));
		events.add(new Event(41.44059651953827, -95.64197593749998,"This is message Four"));
		events.add(new Event(41.44883181337414, -95.62549644531248,"This is message Five"));
		events.add(new Event(41.485877700512404, -95.57056480468748,"This is message Six"));
		events.add(new Event(41.510563198216495, -95.32337242187498,"This is message Seven"));
		events.add(new Event(41.510563198216495, -94.74659019531248,"This is message Eight"));
		events.add(new Event(41.506449602008004, -94.29065757812498,"This is message Nine"));
		
		events.add(new Event(41.53112725855296, -94.07642417968748,"This is message Ten"));
		events.add(new Event(41.59278022771459, -93.84571128906248,"This is message Eleven"));
		events.add(new Event(41.60044000000001, -93.60819000000001,"This is message Twelve"));

	}
	
	/**
	 * EventGenerator constructor.
	 * @param broadcaster The Broadcaster used to push generated events
	 * @param interval The interval time in ms between each event generation
	 */
	public EventGenerator(Broadcaster broadcaster, int interval) {
		this.broadcaster = broadcaster;
		this.interval = interval;
		this.currentIndex = new AtomicInteger(0);
	}
	
	/**
	 * Starts the generation of random events. Each event is broadcasted
	 * using the provided Broadcaster.
	 */
	public void start() {
		if (generator == null) {
			generator = new Thread(new Generator() , "GeneratorThread");
			this.currentIndex = new AtomicInteger(0);
			generator.start();
		}
	}
	
	/**
	 * Stops the generation of random events.
	 */
	public void stop() {
		if (generator != null) {
			generator.interrupt();
			generator = null;
		}
	}
	
	private class Generator implements Runnable {
		
		@Override
		public void run() {
			while(true) {
				try {
					
					broadcaster.broadcast(events.get(currentIndex.getAndIncrement()));
					Thread.sleep(interval);
					if (currentIndex.get() == events.size()-1) {
						stop();
					}
				} catch (InterruptedException e) {break;}
			}
		}
	}
}
