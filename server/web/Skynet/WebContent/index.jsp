<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"  %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"   %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="domain.User" %>
<%@ page import="domain.IncidenciaFacade" %>
<%
User u = null;
HttpSession s = request.getSession(false);
if (s != null) u = (User) s.getAttribute("user");
IncidenciaFacade ifacade = new IncidenciaFacade();
String [][] tiposIncidencia = ifacade.tiposIncidencia();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<title>Reportar Incidencia - Skynet</title>
		<meta charset="utf-8" />
		<meta name="viewport" content="width=device-width, initial-scale=1" />
		<script type="text/javascript" src="http://maps.googleapis.com/maps/api/js"></script>
		<script type="text/javascript" src="<%= request.getContextPath() %>/js/maps.js"></script>
		<script type="text/javascript" src="<%= request.getContextPath() %>/js/check.js"></script>
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css" integrity="sha384-1q8mTJOASx8j1Au+a5WDVnPi2lkFfwwEAa8hDDdjZlpLegxhjVME1fgjWPGmkzs7" crossorigin="anonymous">
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap-theme.min.css" integrity="sha384-fLW2N01lMqjakBkx3l/M9EahuwpSfeNvV63J5ezn3uZzapT0u7EYsXMjQV+0En5r" crossorigin="anonymous">
		<link rel="stylesheet" href="<%=request.getContextPath()%>/css/main.css" />
	</head>
	<body onload="document.getElementById('alertLatLng').style.display = 'none';">
		<jsp:include page="./includes/header.jsp"/>
		<% request.getSession().setAttribute("page", "/Avisos"); %>
		<div id="wrapper">
			<div id="main">
				<section id="two">
					<div class="container">
						<header class="major">
							<h2>Reportar Incidencia</h2>
							<p>Rellena este formulario para reportar<br />
							una incidencia.</p>
						</header>
						<div id="alertLatLng">
							<div class="alert alert-info" role="alert"><button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>Debes definir una localizacion</div>
						</div>
						<% if (request.getAttribute("mensaje") != null) { %>
							<div class="alert alert-info" role="alert"><button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button><%= (String) request.getAttribute("mensaje") %></div>
						<% request.setAttribute("mensaje", null);
						} %>
						<form method="post" id="formAviso" action="<%=request.getContextPath()%>/Avisos">
							<h4>Tipo de incidencia</h4>
							<select required placeholder="Tipo Incidencia" id="tipo" class="inputText" name="tipo">
								<% for (int i = 0; i < tiposIncidencia.length; i++) { %>
									<option value="<%= tiposIncidencia[i][0] %>"><%= tiposIncidencia[i][1] %></option>
								<% } %>
							</select>
							<br />
							<h4>Numero de personas afectadas</h4>
							<input required type="number" placeholder="Nº Personas" id="personas" class="inputText" name="personas" size="30" min="1" value="1" />
							<br /><br />
							<h4>Gravedad</h4>
							<select required placeholder="Gravedad" id="gravedad" class="inputText" name="gravedad">
								<option value="4">Muy Grave</option>
								<option value="3">Grave</option>
								<option value="2">Media</option>
								<option value="1">Leve</option>
							</select>
							<br />
							<h4>Lugar</h4>
							<p><button type="button" onclick="getLocation();return false;">Obtener mi posicion</button> o indicar lugar en el mapa
							<div id="map-canvas" class="container" style="height: 300px;"></div>
							</p>
							<h4>Telefono</h4>
							<input pattern="[0-9]{9}" required="true" title="El numero de telefono no es valido" type="text" placeholder="Teléfono" id="telefono" class="inputText" name="telefono" size="30" value="<%=u != null ? u.getTelefono(): "" %>" />
							<br /><br />
							<h4>Observaciones</h4>
							<textarea placeholder="Observaciones" id="observaciones" class="inputText" name="observaciones" rows=8 cols=57 maxlength="350"></textarea>
							<br />
							<div id="alertLatLng">
								<div class="alert alert-info" role="alert"><button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>Debes definir una localizacion</div>
							</div>
							<input type="submit" class="ayudaButton" name="action" value="AYUDA"/>
							<input type="hidden" id="lat" name="lat" />
							<input type="hidden" id="lng" name="lng" />
							<script type="text/javascript">
							var form = document.getElementById("formAviso");
							form.addEventListener("submit", function (e) {
							      if (lat == 0) {
							    	  document.getElementById('alertLatLng').style.display = 'block';
							          e.preventDefault();
							      }
							 });
							</script>
						</form>
					</div>
				</section>
			</div>
		</div>
		<jsp:include page="./includes/footer.jsp"/>
	</body>
</html>

