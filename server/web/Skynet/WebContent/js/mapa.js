
google.maps.event.addDomListener(window, 'load', initialize);

function initialize() {
  var mapProp = {
    center: {lat: 43.086548, lng: -2.291148},
    zoom: 10,
    mapTypeId:google.maps.MapTypeId.ROADMAP
  };
  var map=new google.maps.Map(document.getElementById("mapa"),mapProp);
}
