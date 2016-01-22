<%@ page import="domain.User" %>
<%
User u = null;
HttpSession s = request.getSession(false);
if (s != null) {
	u = (User) s.getAttribute("user");
	request.setAttribute("s", s);
}
%>
<script src="<%= request.getContextPath() %>/js/jquery.min.js"></script>
<script src="<%= request.getContextPath() %>/js/jquery.scrollzer.min.js"></script>
<script src="<%= request.getContextPath() %>/js/jquery.scrolly.min.js"></script>
<script src="<%= request.getContextPath() %>/js/skel.min.js"></script>
<script src="<%= request.getContextPath() %>/js/util.js"></script>
<script src="<%= request.getContextPath() %>/js/main.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js" integrity="sha384-0mSbJDEHialfmuBBQP6A4Qrprq5OVfW37PRR3j5ELqxss1yVqOtnepnHVP9aJ7xS" crossorigin="anonymous"></script>
<section id="header">
	<header>
		<span class="image avatar"><img src="" alt="" /></span>
		<h1 id="logo"><a href="#">Skynet</a></h1>
		<p></p>
	</header>
	<nav id="nav">
		<ul>
			<li><a href="<%= request.getContextPath() %>/Avisos">Reportar incidencia</a></li>
			<li><a href="<%= request.getContextPath() %>/Mapa">Ver mapa</a></li>
			<% if ((u != null) && (u.getUsername() != null)) { %>
				<li><a href="<%= request.getContextPath() %>/Editar">Hola, <%= u.getNombre() %></a></li>
				<form method="post" action="<%= request.getContextPath() %>/Logout">
				    <li><input type="submit" value="Logout"></li>
				</form>
			<% } else { %>
				<li><a href="#">Iniciar sesion</a></li>
				<form id="loginForm" method="post" action="<%= request.getContextPath() %>/Login">
					<li><input required type="text" placeholder="Usuario" id="user" class="loginText" name="user" size="30" /></li>
					<li><input required type="password" placeholder="Contraseña" id="password" class="loginText" name="password" size="30" /></li>
					<li><input type="submit" class="loginButton" name="action" value="Entrar"/></li>
				</form>
				<li><a href="<%= request.getContextPath() %>/Registrarse">¿Aun no eres usuario? Registrate</a></li>
			<% } %>
		</ul>
	</nav>
</section>
				
	