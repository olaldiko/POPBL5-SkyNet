<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"  %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"   %>

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Skynet</title>
	</head>
	<body>
		<jsp:include page="../includes/header.jsp"/>
		<%if(request.getAttribute("registerMsg")!=null){%>
			<h2><%=(String)request.getAttribute("registerMsg") %></h2>
		<%} %>
		<jsp:include page="../includes/footer.jsp"/>
	</body>
</html>