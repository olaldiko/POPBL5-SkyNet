<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"  %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"   %>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="domain.User" %>
<%@ page import="domain.IncidenciaFacade" %>
<%
User u = null;
HttpSession s = request.getSession(false);
if(s!=null) u = (User)s.getAttribute("user");
//
IncidenciaFacade ifacade = new IncidenciaFacade();
String[][] tiposIncidencia = ifacade.tiposIncidencia();
%>
    
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
							<%for(int i = 0 ; i < tiposIncidencia.length ; i++) { %>
								<option value="<%=tiposIncidencia[i][0]%>"><%=tiposIncidencia[i][1]%></option>
							<%}%>
						</select>
					</td>
				</tr>
				<tr>
					<td><h4>Nº de personas afectadas:</h4></td>
					<td><input required type="number" placeholder="Nº Personas" id="personas" class="inputText" name="personas" size="30" /> </td>
				</tr>
				<tr>
					<td><h4>Gravedad:</h4></td>
					<td>
						<select required placeholder="Gravedad" id="gravedad" class="inputText" name="gravedad">
							<option value="4">Muy Grave</option>
							<option value="3">Grave</option>
							<option value="2">Media</option>
							<option value="1">Leve</option>
						</select>
					</td>
				</tr>
				<tr>
					<td><h4>Lugar:</h4></td>
					<td><input required type="text" placeholder="Lugar del accidente" id="lugar" class="inputText" name="lugar" size="30" /> </td>
				</tr>
				<tr>
					<td><h4>Teléfono:</h4></td>
					<td><input required type="text" placeholder="Teléfono" id="telefono" class="inputText" name="telefono" size="30" value="<%=u!=null ? u.getTelefono():"" %>" /> </td>
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

