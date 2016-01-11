
<%@ page import="domain.User" %>
<%
User u = null;
HttpSession s = request.getSession(false);
if(s!=null) u = (User)s.getAttribute("user");
%>

<link href="<%=request.getContextPath()%>/Templates/css/Skynet.css" rel="stylesheet" type="text/css" media="screen" />
<script type="text/javascript" charset="UTF-8" src="<%=request.getContextPath()%>/js/login.js"></script>
<div id="header">
	<h1>Skynet</h1>
	<form method="post" action="<%=request.getContextPath()%>/Avisos">
	    <input type="submit" value="Reportar Incidencia">
	</form>
	<form method="post" action="<%=request.getContextPath()%>/Mapa">
	    <input type="submit" value="Ver Mapa">
	</form>
	<%if(u!=null && u.getUsername()!=null){%>
		<div id="logged">
			<input type="button" value="<%=u.getUsername()%>" onclick="showUserMenu()"/>
			<div id="userMenu" style="display: none;">
				<table>
					<tr>
						<td>
							<form method="post" action="<%=request.getContextPath()%>/Logout">
							    <input type="submit" value="Logout">
							</form>
						</td>
					</tr>
				</table>
			</div>
		</div>
	<%} else {%>
		<div id="notlogged">
			<input type="button" value="Login" onclick="showLogin()"/>
			<form method="post" action="<%=request.getContextPath()%>/Registrarse">
			    <input type="submit" value="Registrarse">
			</form>
			<div id="login" style="display: none;">
				<form method="post" action="<%=request.getContextPath()%>/Login">
					<table>
						<tr>
							<td><h4>Usuario:</h4></td>
							<td><input required type="text" placeholder="Usuario" id="user" class="loginText" name="user" size="30" /> </td>
						</tr>
						<tr>
							<td><h4>Contraseña:</h4></td>
							<td><input required type="password" placeholder="Contraseña" id="password" class="loginText" name="password" size="30" /> </td>
						</tr>
						<tr>
							<td colspan="2" align="center"><input type="submit" class="loginButton" name="action" value="Entrar"/> </td>
						</tr>
					</table>
				</form>
			</div>
			<%if (request.getSession().getAttribute("loginError")!=null) { %>
				<h4><%=(String)request.getSession().getAttribute("loginError") %></h4>
			<%request.getSession().setAttribute("loginError",null);} %>
		</div>
	<%}%>
</div>
	
	