<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"  %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"   %>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<script type="text/javascript" charset="UTF-8" src="https://maps.googleapis.com/maps/api/js"></script>
		<script type="text/javascript" charset="UTF-8" src="<%=request.getContextPath()%>/js/mapa.js"></script>
		<link href="<%=request.getContextPath()%>/Templates/css/mapa.css" rel="stylesheet" type="text/css" media="screen" />
		<title>Skynet</title>
	</head>
	<body>
		<jsp:include page="../includes/header.jsp"/>
			<%request.getSession().setAttribute("page", "/Mapa"); %>
			<h2>Mapa</h2>
			<div id="mapa" style="width:1000px;height:500px;"></div>
		<jsp:include page="../includes/footer.jsp"/>
	</body>
</html>