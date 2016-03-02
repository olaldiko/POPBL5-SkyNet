var map, marker;
var lat = 0, lng = 0;
var zoom = 11;
var markers = [];
var resources = [];
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
function deleteNU() {
	for (var i = 0; i < markers.length; i++) {
		markers[i].setMap(null);
	}
	markers = [];
	console.log("MARKER: All markers deleted");
}
function deleteNUR() {
	for (var i = 0; i < resources.length; i++) {
		resources[i].setMap(null);
	}
	resources = [];
	console.log("MARKER: All markers deleted");
}
function createMarkerNU(lat, lng, text, i) {
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
	console.log("MARKER: Maker " + i + " added.");
	markers.push(m);
}
function createMarkerNUR(lat, lng, text, i) {
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
	console.log("MARKER: Maker " + i + " added.");
	resources.push(m);
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
		center: new google.maps.LatLng(43.276639, -1.987208),
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