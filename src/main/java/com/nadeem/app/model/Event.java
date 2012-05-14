package com.nadeem.app.model;

public class Event {
	
	private double lat;
	private double lng;
	private String message;
	
	public Event() {}
	
	public Event(double lat, double lng) {
		this(lat, lng, "This is message");
	}
	
	public Event(double lat, double lng, String message) {
		this.lat = lat;
		this.lng = lng;
		this.message = message;
	}
	
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLng() {
		return lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return String.format("Event[lat=%s , lng=%s, message=%s]", lat, lng, message);
	}
}
