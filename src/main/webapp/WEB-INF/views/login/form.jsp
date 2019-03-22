<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>

<tags:pageTemplate titulo="Login ">
	<div class="container container-login">
		<h2 class="titulo-login">Controle de despesas Safetech - Login</h2>
		<form:form cssClass="p-2" servletRelativeAction="/login" method="post">
			<div class="form-group">
				<label>Login</label> 
				<input type="text" name="username"	class="form-control" />
			</div>
			<div class="form-group">
				<label>Senha</label> 
				<input type="password" name="password" class="form-control" />
			</div>
			<button type="submit" class="btn btn-primary">Logar</button>
		</form:form>
	</div>
</tags:pageTemplate>