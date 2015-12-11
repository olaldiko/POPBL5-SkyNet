<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"  %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"   %>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>Skynet</title>
	</head>
	<body>
		<jsp:include page="./includes/header.jsp"/>
		<%request.getSession().setAttribute("page", "/Avisos"); %>
		<h2>Reportar Incidencia</h2>
		<form method="post" action="<%=request.getContextPath()%>/Avisos">
			<table>
				<tr>
					<td><h4>Tipo de incidencia:</h4></td>
					<td>
						<select required placeholder="Tipo Incidencia" id="tipo" class="inputText" name="tipo">
							<option value="volvo">Ambulancia</option>
						</select>
					</td>
				</tr>
				<tr>
					<td><h4>Nº de personas afectadas:</h4></td>
					<td><input required type="number" placeholder="Nº Personas" id="personas" class="inputText" name="personas" size="30" /> </td>
				</tr>
				<tr>
					<td><h4>Gravedad:</h4></td>
					<td><input required type="text" placeholder="Gravedad" id="gravedad" class="inputText" name="gravedad" size="30" /> </td>
				</tr>
				<tr>
					<td><h4>Tu nombre:</h4></td>
					<td><input required type="text" placeholder="Nombre" id="nombre" class="inputText" name="nombre" size="30" /> </td>
				</tr>
				<tr>
					<td><h4>Teléfono:</h4></td>
					<td><input required type="text" placeholder="Teléfono" id="telefono" class="inputText" name="telefono" size="30" /> </td>
				</tr>
				<tr>
					<td><h4>Observaciones:</h4></td>
				</tr>
				<tr>
					<td colspan="2"><textarea placeholder="Observaciones" id="observaciones" class="inputText" name="observaciones" rows=8 cols=57></textarea> </td>
				</tr>
				<tr>
					<td colspan="2" align="center"><input type="submit" class="ayudaButton" name="action" value="AYUDA"/> </td>
				</tr>
			</table>
		</form>
		<jsp:include page="./includes/footer.jsp"/>
	</body>
</html>

