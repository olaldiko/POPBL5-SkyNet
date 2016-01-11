<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"  %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"   %>
<%@ page import="domain.QueryResult" %>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<link href="<%=request.getContextPath()%>/Templates/css/postgre.css" rel="stylesheet" type="text/css" media="screen" />
		<title>Skynet</title>
	</head>
	<body>
		<form method="post" action="<%=request.getContextPath()%>/DatabaseTest">
			<table>
				<tr>
					<td><h4>Database:</h4></td>
					<td><input type="text" id="queryDB" class="queryText" name="pgdb" size="30" /> </td>
				</tr>
				<tr>
					<td><h4>User:</h4></td>
					<td><input type="text" id="queryUser" class="queryText" name="pguser" size="30" /> </td>
				</tr>
				<tr>
					<td><h4>Password:</h4></td>
					<td><input type="password" id="queryPass" class="queryText" name="pgpass" size="30" /> </td>
				</tr>
			</table> 
			<table>
				<tr>
					<td><input type="text" id="queryText" class="queryText" name="query" size="80" /> </td>
					<td></td>
					<td colspan="2" align="center"><input type="submit" class="queryButton" name="action" value="query"/> </td>
				</tr>
				<tr><td/><td/><td/></tr>
			</table>
		</form>
		<%if(request.getAttribute("result")!=null) {
			QueryResult r = (QueryResult)request.getAttribute("result");
		%>
		<table class="queryResult" border=1>
			<tr>
				<%for(int j = 0 ; j < r.getColumns() ; j++) {%>
					<td>
						<%=r.getColumnName(j)%>
					</td>
				<%}%>
			</tr>
			<% for(int i = 0 ; i < r.getRows() ; i++) {%>
				<tr>
					<%for(int j = 0 ; j < r.getColumns() ; j++) {%>
						<td>
							<%=r.getResult(i, j)%>
						</td>
					<%}%>
				</tr>
			<%}%>
		</table>
		<%}
		else if(request.getAttribute("error")!=null) {%>
			<h2 class="error"><%=(String)request.getAttribute("error")%></h2>
		<%}%>
	</body>
</html>