var map, marker;
var lat = 0, lng = 0;
var zoom = 11;
var markers = [], j = 0;
var resources = [], h = 0;
function getLocation() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(showPosition);
    }
}
function showPosition(position) {
	lat = position.coords.latitude;
	lng = position.coords.longitude;
	if (marker == null) createMarker(lat, lng);
	else changePositionMarker(lat, lng);
	updateMap(lat, lng);
	setVars(lat, lng);
}
function createMarker(lat, lng) {
	marker = new google.maps.Marker({
		position: new google.maps.LatLng(lat, lng),
		map: map,
		title: 'Mi posicion'
	});
	marker.setMap(map);
	updateMap(lat, lng);
}
function createMarkerNU(lat, lng, text, i) {
	var exists = 1, h = 0;
	if (j != 0) {
		for (h = 0; h < j; h++) {
			var aux = markers[h];
			if (aux.get("id") != i) exists = 0;
		}
	} else {
		exists = 0;
	}
	if (exists == 0) {
		var image = '../Skynet/img/warning.png';
		var m = new google.maps.Marker({
			position: new google.maps.LatLng(lat, lng),
			map: map,
			icon: image
		});
		var infowindow = new google.maps.InfoWindow({
		    content: text
		});
		m.addListener('click', function() {
		    infowindow.open(map, m);
		});
		m.set("id", i);
		markers[j] = m;
		j = j + 1;
	} else {
		console.log("No añadimos marcador repetido.");
	}
}
function createMarkerNUR(lat, lng, text, i) {
	var exists = 1, h = 0;
	if (h != 0) {
		for (j = 0; j < h; j++) {
			var aux = resources[j];
			if (aux.get("id") != i) exists = 0;
		}
	} else {
		exists = 0;
	}
	if (exists == 0) {
		var image = '../Skynet/img/ambulance.png';
		var m = new google.maps.Marker({
			position: new google.maps.LatLng(lat, lng),
			map: map,
			icon: image
		});
		var infowindow = new google.maps.InfoWindow({
		    content: text
		});
		m.addListener('click', function() {
		    infowindow.open(map, m);
		});
		m.set("id", i);
		resources[h] = m;
		h = h + 1;
	} else {
		console.log("Actualizamos marcador repetido.");
		for (j = 0; j < h; j++) {
			var aux = resources[j];
			if (aux.get("id") == i) {
				aux.setPosition(new google.maps.LatLng(lat, lng));
			}
		}
	}
}
function changePositionMarker(lat, lng) {
	marker.setPosition(new google.maps.LatLng(lat, lng));
	updateMap(lat, lng);
}
function updateMap(lat, lng) {
	map.panTo(new google.maps.LatLng(lat, lng));
}
function setVars(lat, lng) {
	document.getElementById('lat').value = lat;
	document.getElementById('lng').value = lng;
}
function loadMap() {
	var mapOptions = {
		center: new google.maps.LatLng(43.062991, -2.506180),
		scrollwheel: false,
		zoom: zoom
	};
	map = new google.maps.Map(document.getElementById("map-canvas"), mapOptions);
	google.maps.event.addListener(map, 'click', function(event) {
		lat = event.latLng.lat();
		lng = event.latLng.lng();
		if (marker == null) createMarker(lat, lng);
		else changePositionMarker(lat, lng);
		updateMap(lat, lng);
		setVars(lat, lng);
	});
}
google.maps.event.addDomListener(window, 'load', loadMap);