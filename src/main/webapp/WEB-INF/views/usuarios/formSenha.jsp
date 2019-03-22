<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>

<c:url value="/" var="contextPath" />

<tags:pageTemplate titulo="Alteração de senha">

		<div class="container container-login">

		<h2 class="titulo-login">Alterar senha</h2>
		<h5 style="color:red" class="color-red">${ message }</h5>

		<form:form action="${s:mvcUrl('UC#alterarSenha').build() }"
			method="post" commandName="usuario">
			<div class="form-group">
				<label>Nova senha: </label>
				<form:password cssClass="form-control" path="senha" />
			</div>
			<div class="form-group">
				<label>Repita a senha: </label>
				<form:password cssClass="form-control" path="senhaRepetida" />
			</div>
			<form:hidden path="id" />
			<button type="submit" class="btn btn-primary">Alterar</button>
		</form:form>
	</div>
</tags:pageTemplate>