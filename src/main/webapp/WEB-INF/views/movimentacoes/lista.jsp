<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/security/tags"
	prefix="security"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>

<c:url value="/" var="contextPath" />

<tags:pageTemplate titulo="Lista de clientes">
	<div class="container">
		<a href="${s:mvcUrl('HC#index').build() }" class="btn btn-primary">Home</a>
		<h2 style="text-align: center;">Movimentações</h2>
		<h5>Colaborador: ${ movimentacoes[0].conta.usuario.nome }</h5>
		<h5>Cliente: ${ movimentacoes[0].conta.cliente.nome }</h5>
		<h5>Tipo de devolução: ${ movimentacoes[0].conta.devolucao}</h5>
		<h5 style="color: red">${ message }</h5>
<br/>
		<table>
			<thead>
				<tr class="table-primary">
					<th>Responsável</th>
					<th>Criado por: </th>
					<th>Descrição</th>
					<th>Valor R$</th>
					<th>Tipo</th>
					<th>Conciliada</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${ movimentacoes }" var="m">
					<tr>
						<td> ${m.responsavel } </td>
						<td>${ m.usuario.nome }</td>
						<td>${ m.descricao }</td>
						<td><fmt:formatNumber value="${m.valor}" type="currency"/></td>
						<td>${ m.tipo }</td>
						<td>${ m.conciliada }</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		<br />
		
		
		<h5>Crédito: <fmt:formatNumber value="${credito}" type="currency"/></h5>
		<h5>Débito: <fmt:formatNumber value="${debito}" type="currency"/></h5></h5>
		_______________________________
		<h5>Saldo Geral: <fmt:formatNumber value="${saldo}" type="currency"/></h5></h5>
		<h5>Saldo Liquido: <fmt:formatNumber value="${saldoLiquido}" type="currency"/></h5></h5>
		
		<br />
		
		<security:authorize access="hasRole('ROLE_ADMIN')">
			<div class="d-flex">
				<form class="p-0"
					action="${s:mvcUrl('CDC#gerarRelatorio').build() }" method="post">
					<input name="conta" type="hidden"
						value="${ movimentacoes[0].conta.id }" />
					<button class="btn btn-primary mr-1" type="submit">Relatório Empresa
						</button>
				</form>
				
				<form class="p-0"
					action="${s:mvcUrl('CDC#gerarRelatorio').build() }" method="post">
					<input name="conta" type="hidden"
						value="${ movimentacoes[0].conta.id }" /> <input
						name="pdfcliente" type="hidden" value="sim" />
					<button class="btn btn-primary mb-5" type="submit">Gerar
						PDF para o cliente</button>
				</form>
				
				
			</div>
		</security:authorize>
		
		
		
		
		
		
		
		
	</div>
</tags:pageTemplate>