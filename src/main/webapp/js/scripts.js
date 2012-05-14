var appUrl = document.location.toString() + "app";

$(function () {
	initializeUIComponents();
	initializeMap();
});

function initializeUIComponents() {
	// IE compatibility
	console = (!window.console) ? {} : window.console;
	console.log = (!window.console.log) ? function() {} : window.console.log;

	$("#button-fireworks").buttonset().change(function() {
		var selected = $("#button-fireworks input[type='radio']:checked")[0];
		if ($("#button-start").is(selected)) {
			$.ajax({type: "GET", url: appUrl + "/start"});
		} else {
			$.ajax({type: "GET", url: appUrl + "/stop"});
		}
	});
	$("#button-start").button({icons: {primary:'ui-icon-play'}});
	$("#button-stop").button({icons: {primary:'ui-icon-pause'}});

	$("#button-connection").buttonset().change(function() {
		var selected = $("#button-connection input[type='radio']:checked")[0];
		if ($("#button-connect").is(selected)) {
			console.log("Connecting...");
			 wsApi.subscribe();
		} else {
			console.log("Disconnecting...");
			wsApi.unsubscribe();
		}		
	});
	$("#button-connect").button({icons: {primary:'ui-icon-link'}});
	$("#button-disconnect").button({icons: {primary:'ui-icon-cancel'}});
}

function initializeMap() {

	var end		= new google.maps.LatLng(41.60044000000001, -93.60819000000001);
	var start	= new google.maps.LatLng(41.222470, -96.00603000000001);

	// Init the map
	var mapOptions = {
			zoom: 2,
			center: start,
			mapTypeId: google.maps.MapTypeId.ROADMAP,
			panControl: false
	};

	mapsApi.map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);

	mapsApi.directionsDisplay = new google.maps.DirectionsRenderer();	
	mapsApi.directionsDisplay.setMap(mapsApi.map);

	mapsApi.showRoute(start, end);	
}

var mapsApi = {
		map: null,
		directionsDisplay: null,
		directionsService: new google.maps.DirectionsService(),
		markersArray: [],
		infoWindow : new google.maps.InfoWindow(),
		"showRoute" : function(start, end) {
			var request = {
					origin:start,
					destination:end,
					provideRouteAlternatives: false,
					optimizeWaypoints: true,
					travelMode: google.maps.DirectionsTravelMode.DRIVING
			};
			this.directionsService.route(request, function(result, status) {
				if (status == google.maps.DirectionsStatus.OK) {
					mapsApi.directionsDisplay.setDirections(result);
				} else {
					console.log("Error Response received from Google  status= " + status + ": result :" + result);					
				}

			});
		},		
		"addMarkder": function(json) {
			var marker = new google.maps.Marker({
				map: this.map,
				animation: google.maps.Animation.DROP,
				position: new google.maps.LatLng(json.lat, json.lng)
			});
			google.maps.event.addListener(marker, 'click', function() {
				mapsApi.showHelpWindow(json.message, marker);
			});
			this.markersArray.push(marker);			

		},
		"showHelpWindow" : function(message, marker) {
			mapsApi.infoWindow.setContent(message);
			mapsApi.infoWindow.open(this.map, marker);
		},
		"clearOverlays" : function() { // Removes the overlays from the map, but keeps them in the array
			if (this.markersArray) {
				for (i in this.markersArray) {
					this.markersArray[i].setMap(null);
				}
			}		
		},
		"showOverlays" : function() { // Shows any overlays currently in the array
			if (this.markersArray) {
				for (i in this.markersArray) {
					this.markersArray[i].setMap(this.map);
				}
			}

		},
		"deleteOverlays" : function() { // Deletes all markers in the array by removing references to them
			if (this.markersArray) {
				for (i in this.markersArray) {
					this.markersArray[i].setMap(null);
				}
				this.markersArray.length = 0;
			}
		}
};

var wsApi = {
	connectedEndpoint:null,
	callbackAdded:false,
	subscribe:function () {          

		var request = {
				url: appUrl,
				logLevel : 'info',
				transport: 'websocket', /* websocket, jsonp, long-polling, polling, streaming */
				fallbackTransport: 'streaming',
				attachHeadersAsQueryString: true,
				onOpen : function(response) {
					console.log("Connected to realtime endpoint using " + response.transport);
				},
				onClose : function(response) {
					console.log("Disconnected from realtime endpoint");
				},
				onMessage : function (response) {
					if (response.transport != 'polling' && response.state == 'messageReceived') {
						if (response.status == 200) {
							var data = response.responseBody;
							if (data.length > 0) {

								console.log("Message Received using " + response.transport + ": " + data);
								var json = JSON.parse(data);
								mapsApi.addMarkder(json);
							}
						}
					}
				}
		};

		this.connectedEndpoint = $.atmosphere.subscribe(request);
		callbackAdded = true;
	},

	send:function (message) {
		console.log("Sending message");
		console.log(message);
		this.connectedEndpoint.push(JSON.stringify(message));
	},

	unsubscribe:function () {
		$.atmosphere.unsubscribe();
	}
};

