<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/security/tags"
	prefix="security"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>

<c:url value="/" var="contextPath" />

<tags:pageTemplate titulo="Home">
<main class="container">
	<h5 style="color: #33cc28; text-align: center">${message}</h5>
		<!-- Parte que é liberada somente para o administrador, onde ele faz os cadastros de conta, etc -->
<security:authorize access="hasRole('ROLE_ADMIN')">
	<div class="nova-conta">
		<div class="links-criacao">
			<a href="${s:mvcUrl('CDC#formulario').build() }" class="btn btn-primary">Cadastro	de contas</a> 
			<a href="${s:mvcUrl('UC#formulario').build() }" class="btn btn-primary">Cadastro de colaborador</a> 
			<a href="${s:mvcUrl('CDC#listarContas').build() }" class="btn btn-primary">Listar contas</a>
		</div>
		<br />
	</div>
</security:authorize>

				<!-- Parte dos filtros das contas. Disponível para qualquer usuario -->	
<h5 class="titulo-filtro">Filtrar por:</h5>
	<form id="formBusca" class="form-inline my-2 my-lg-0" action="${s:mvcUrl('CDC#filtrar').build() }" method="get">
			<div class="form-group">
				<label>Cliente:</label> 
				<select class="form-control form-control-sm" id="select-cliente" name="cliente">
				<option value="" disabled selected hidden>Cliente</option>
				<c:forEach items="${clientes }" var="cliente">
					<option value="${ cliente.nome }">${ cliente.nome }</option>
				</c:forEach>
				</select>
			</div>
			<security:authorize access="hasRole('ROLE_ADMIN')">
			<div class="form-group">
				<label class="label-filtro">Colaborador:</label> 
				<select class="form-control form-control-sm"  id="select-usuario"	name="usuario">
					<option value="" disabled selected hidden>Colaborador</option>
					<c:forEach items="${usuarios }" var="usuario">
						<option value="${ usuario.nome }">${ usuario.nome }</option>
					</c:forEach>
				</select>
			</div>
			</security:authorize>
			<div class="form-group">
				<label class="label-filtro">Situação: </label> 
				<select class="form-control form-control-sm"  name="situacao" form="formBusca">
				<option value="" disabled selected hidden>Sit</option>
				<option value="ATIVA">ATIVA</option>
				<option value="INATIVA">INATIVA</option>
				</select>
			</div>
			<div class="form-group">
				<label class="label-filtro">Data início: </label> 
				<input class="form-control form-control-sm" type="text" placeholder="dd/mm/aaaa" name="dataInicio"/>
			</div>
			<div class="form-group">
				<label class="label-filtro">Data fim: </label> 
				<input class="form-control form-control-sm"  type="text" placeholder="dd/mm/aaaa" name="dataFinal"/>
			</div>
				<button style="margin: .2em; clear: left;" class="btn btn-primary" type="submit">Filtrar</button>
		</form><!-- Fim do form de busca/filtro -->

							<!-- Inicio da seção da listagem de contas -->
<div class="contas">
	<c:forEach items="${contas}" var="conta">
		<div class="card border-primary mb-3">
			<div class="card-header">
				<small class="data-inicio"> <fmt:formatDate	value="${ conta.dataInicio.time }" pattern="dd/MM/yyyy" /></small> 
				<small class="data-fim"><fmt:formatDate	value="${ conta.dataFim.time }" pattern="dd/MM/yyyy" /></small>
			</div>
			<div class="card-body">
				<div class="card-body-header">
					<h4 class="card-title">Colaborador: ${ conta.usuario.nome }</h4>
					<small>${ conta.situacao }</small>
				</div>
			<h5 class="card-title cliente">Cliente: ${ conta.cliente.nome }</h5>
				<small>
						<fmt:formatNumber value="${ saldos.get( conta.id ) }"
										minFractionDigits="2" type="currency" />
						</small>
				<div class="movimentacoes">
					
					<h5 class="card-title">Opções:</h5>
					<div style="display: flex" class="botoes-conta">
						<form action="${s:mvcUrl('MCC#listar').build() }" method="get">
							<input name="id" type="hidden" value="${ conta.id }" />
							<button class="btn btn-primary my-2 my-sm-0 mr-1" type="submit">Detalhes</button>
						</form>
						<c:if test="${conta.situacao != 'INATIVA'}">
							<form action="${s:mvcUrl('MCC#formulario').build() }" method="post">
								<input name="id" type="hidden" value="${ conta.id }" />
								<button class="btn btn-primary my-2 my-sm-0 mr-1" type="submit">Movimentar</button>
							</form>
						</c:if>
							<form action="${s:mvcUrl('MCC#editar').build() }" method="get">
								<input name="id" type="hidden" value="${ conta.id }" />
								<button class="btn btn-primary my-2 my-sm-0 mr-1" type="submit">Editar</button>
							</form>
					</div>
				</div>
			</div>
		</div>
	</c:forEach>
</div>
</main>
</tags:pageTemplate>