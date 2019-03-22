<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>

<tags:pageTemplate titulo="Controle de despesas - Safetech">
	<section id="index-section" class="container middle">
		<h1>Sem movimentações nesta conta</h1>
		<a class="navbar-brand" href="${s:mvcUrl('HC#index').build() }">Controle de despesas - Safetech</a>
	</section>
</tags:pageTemplate>
