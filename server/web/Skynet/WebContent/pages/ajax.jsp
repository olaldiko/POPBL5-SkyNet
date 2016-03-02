<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<script type="text/javascript">
<% if (request.getSession().getAttribute("arrayIncidencias") != null) {
	String [] incidencias = ((String) request.getSession().getAttribute("arrayIncidencias")).split("<");
	for (int j = 0; j < incidencias.length; j++) {
		String [] data = incidencias[j].split(">"); %> 
		var lat = "<%= data[0] %>";
		var lng = "<%= data[1] %>";
		var incidenciaID = "<%= data[2] %>";
		var fechaNotificacion = "<%= data[3] %>";
		var nivel = "<%= data[4] %>";
		var informacion = "<%= data[5] %>";
		var numAfectados = "<%= data[6] %>";
		var fechaResolucion = "<%= data[7] %>";
		var contentString =   "<div id='content'>"+
							       "<div id='siteNotice'>"+
							       "</div>"+
							       "<h1 id='firstHeading' class='firstHeading'>Incidencia numero "+incidenciaID+"</h1>"+
							       "<div id='bodyContent'>"+
								       "<p><b>Fecha de notificacion</b>: "+fechaNotificacion+"</br>"+
								       "<b>Informacion</b>: "+informacion+"</br>"+
								       "<b>Nivel</b>: "+nivel+"</br>"+
								       "<b>Numero de afectados</b>: "+numAfectados+"</br>"+
								       "<b>Fecha de resolucion</b>: "+fechaResolucion+"</p>"+
							       "</div>"+
						       "</div>";
		deleteNU();
		createMarkerNU(lat, lng, contentString, incidenciaID); <%
	}
} %>
<% if (request.getSession().getAttribute("arrayRecursos") != null) {
	String [] recursos = ((String) request.getSession().getAttribute("arrayRecursos")).split("<");
	for (int j = 0; j < recursos.length; j++) {
		String [] data = recursos[j].split(">"); %>
		var contentString = "<div id='content'>"+
							       "<div id='siteNotice'>"+
							       "</div>"+
							       "<h1 id='firstHeading' class='firstHeading'>Recurso numero <%= data[0] %></h1>"+
							       "<div id='bodyContent'>"+
							       	   "<p><b>Fecha de actualizacion</b>: <%= data[1] %></p>"+
						       "</div>"+
						       "</div>";
		deleteNUR();
		createMarkerNUR(<%= data[2] %>, <%= data[3] %>, contentString, <%= data[0] %>); <%
	}
} %>
</script>
