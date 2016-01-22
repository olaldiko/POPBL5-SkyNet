<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"  %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"   %>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<title>Incidencias actuales - Skynet</title>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<script type="text/javascript" charset="UTF-8" src="https://maps.googleapis.com/maps/api/js"></script>
		<script type="text/javascript" src="<%= request.getContextPath() %>/js/maps.js"></script>
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css" integrity="sha384-1q8mTJOASx8j1Au+a5WDVnPi2lkFfwwEAa8hDDdjZlpLegxhjVME1fgjWPGmkzs7" crossorigin="anonymous">
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap-theme.min.css" integrity="sha384-fLW2N01lMqjakBkx3l/M9EahuwpSfeNvV63J5ezn3uZzapT0u7EYsXMjQV+0En5r" crossorigin="anonymous">
		<link rel="stylesheet" href="<%=request.getContextPath()%>/css/main.css" />
		<link rel="stylesheet" href="<%=request.getContextPath()%>/css/maps.css" />
	</head>
	<body>
		<jsp:include page="../includes/header.jsp"/>
		<% request.getSession().setAttribute("page", "/Mapa"); %>
		<div id="wrapper">
			<div id ="main">
				<section id="two">
					<div class="container">
						<header class="major">
							<h2>Incidencias actuales</h2>
						</header>
					</div>
					<div id="map-canvas" class="container" style="height: 500px; width: 100%;"></div>
				</section>
			</div>
		</div>
		<div id="exe"></div>
		<jsp:include page="../includes/footer.jsp"/>
		<script type="text/javascript">
			$(document).ready(setInterval(function() {
				$("#exe").load('<%=request.getContextPath()%>/Ajax');
				eval(document.getElementById('cont'));
			}, 5000));
		</script>
	</body>
</html>