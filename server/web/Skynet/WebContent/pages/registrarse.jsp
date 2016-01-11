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
			<h2>Mapa</h2>
			<div id="registro">
				<form method="post" action="<%=request.getContextPath()%>/Registrarse">
					<table>
						<tr>
							<td><h4>Usuario:</h4></td>
							<td><input required type="text" placeholder="Usuario" id="user" class="registerText" name="user" size="30" /> </td>
						</tr>
						<tr>
							<td><h4>Contraseña:</h4></td>
							<td><input required type="password" placeholder="Contraseña" id="password" class="registerText" name="password" size="30" /> </td>
						</tr>
						<tr>
							<td><h4>Repetir contraseña:</h4></td>
							<td><input required type="password" placeholder="Repetir Contraseña" id="password2" class="registerText" name="password2" size="30" /> </td>
						</tr>
						<tr>
							<td><h4>Nombre:</h4></td>
							<td><input required type="text" placeholder="Nombre" id="nombre" class="registerText" name="nombre" size="30" /> </td>
						</tr>
						<tr>
							<td><h4>Apellido:</h4></td>
							<td><input required type="text" placeholder="Apellido" id="apellido" class="registerText" name="apellido" size="30" /> </td>
						</tr>
						<tr>
							<td><h4>Dirección:</h4></td>
							<td><input required type="text" placeholder="Dirección" id="direccion" class="registerText" name="direccion" size="30" /> </td>
						</tr>
						<tr>
							<td><h4>Teléfono:</h4></td>
							<td><input required type="text" placeholder="Teléfono" id="telefono" class="registerText" name="telefono" size="30" /> </td>
						</tr>
						<tr>
							<td><h4>DNI:</h4></td>
							<td><input required type="text" placeholder="DNI" id="DNI" class="registerText" name="DNI" size="30" /> </td>
						</tr>
						<tr>
							<td><h4>Notas:</h4></td>
						</tr>
						<tr>
							<td colspan="2"><textarea placeholder="Notas" id="notas" class="registerText" name="notas" rows=8 cols=57></textarea> </td>
						</tr>
						<tr>
							<td colspan="2" align="center"><input type="submit" class="registerButton" name="action" value="Registrarse"/> </td>
						</tr>
					</table>
				</form>
			</div>
		<jsp:include page="../includes/footer.jsp"/>
	</body>
</html>