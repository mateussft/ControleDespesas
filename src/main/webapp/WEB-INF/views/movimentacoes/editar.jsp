<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>

<c:url value="/" var="contextPath" />

<tags:pageTemplate titulo="Controle de despesas - Safetech">

	<div class="container">
		<a href="${s:mvcUrl('HC#index').build()}" class="btn btn-primary">Home</a>
		<h2 style="text-align: center;">Movimentações</h2>
		<h5>Colaborador: ${ movimentacoes[0].conta.usuario.nome }</h5>
		<h5>Cliente: ${ movimentacoes[0].conta.cliente.nome }</h5>
		<h5 style="color: #33cc28">${ message }</h5>
<br/>
		<table>
			<thead>
				<tr class="table-primary">
				<security:authorize access="hasRole('ROLE_ADMIN')">
					<th>Responsável</th>
				</security:authorize>
					<th>Descrição</th>
					<th>Valor</th>
					<th>Tipo</th>
					<th>Conciliada</th>
				<security:authorize access="hasRole('ROLE_ADMIN')">
					<th>Condizente</th>
				</security:authorize>
				<security:authorize access="hasRole('ROLE_ADMIN')">
					<th>Remover?</th>
				</security:authorize>
				<security:authorize access="hasRole('ROLE_USER')">
				<c:if test="${movimentacoes[0].conta.situacao != 'INATIVA'}">
					<th>Remover?</th>
				</c:if>
				</security:authorize>
				</tr>
			</thead>
			<tbody>

<c:forEach items="${ movimentacoes }" var="movimentacao">
	<tr>
		<security:authorize access="hasRole('ROLE_ADMIN')">
			
			<td>
							<form class="m-0 p-0" style="text-align: center"
								action="${s:mvcUrl('MCC#atualizar').build() }"
								method="post">
								<input name="id" type="hidden" value="${ movimentacao.id }" /> <select
									onchange="this.form.submit()" class="m-0 form-control"
									name="responsavel">
									
									<option value="${ movimentacao.responsavel }">${ movimentacao.responsavel }</option>

									<security:authorize access="hasRole('ROLE_ADMIN')">
										<c:forEach var="i" begin="0" end="2">
											<c:if test="${ movimentacao.responsavel != responsaveis[i] }">
											
												<option value="${ responsaveis[i] }">${ responsaveis[i] }</option>
											</c:if>
										</c:forEach>
									</security:authorize>

								</select> <input name="conta" type="hidden"
									value="${ movimentacoes[0].conta.id }" />
							</form>
						</td>
			
			
			
			
			<%-- <td>	
				<form style="text-align: center" action="${s:mvcUrl('MCC#atualizar').build() }" method="post">
				<input name="id" type="hidden" value="${ movimentacao.id}" />
				<select class="form-control m-0" name="responsavel"  onchange="this.form.submit()">
					<option>SELECIONE</option>
					<c:forEach items="${responsavel}" var="responsavel">
						<c:if test="${movimentacao.responsavel== responsavel}">
							<option value="${responsavel}" selected>${responsavel}</option>
						</c:if>	
						 <option value="${responsavel}">${responsavel}</option>
					</c:forEach> 
				</select>
				<input name="conta"	type="hidden" value="${movimentacoes[0].conta.id}" />
				</form>	
			</td>  --%>
		</security:authorize>
			<td>${movimentacao.descricao}</td>
			<td><fmt:formatNumber value="${movimentacao.valor}" type="currency"/></td>
			<td>${movimentacao.tipo}</td>
			<td>${movimentacao.conciliada}</td>
			<security:authorize access="hasRole('ROLE_ADMIN')">
			<td class="td-concilia">
				<c:if test="${movimentacao.conciliada == 'NAO' or movimentacao.conciliada != 'SIM' and movimentacao.conciliada != 'NAO'}">
				<form class="p-0" style="text-align: center" action="${s:mvcUrl('MCC#conciliar').build() }" method="post">
					<input name="id" type="hidden" value="${movimentacao.id}" /> 
					<input name="tipo" type="hidden" value="SIM" /> 
					<input name="conta"	type="hidden" value="${movimentacoes[0].conta.id}" />
					<button type="submit"><img src="${contextPath}resources/imagens/checked.svg" alt="checked"></button>
				</form>
				</c:if>
				<c:if test="${movimentacao.conciliada == 'SIM'}">
				<form class="p-0" style="text-align: center" action="${s:mvcUrl('MCC#conciliar').build()}" method="post">
					<input name="id" type="hidden" value="${ movimentacao.id }" />
					<input name="tipo" type="hidden" value="NAO" /> 
					<input name="conta" type="hidden" value="${movimentacoes[0].conta.id}" />
					<button type="submit"> <img src="${contextPath}resources/imagens/cancel.svg" alt="checked"></button>
				</form>
				</c:if>
			</td><!-- fim da td de conciliar a despesa -->
			</security:authorize>
			
			<security:authorize access="hasRole('ROLE_ADMIN')">
			<td class="td-remover">
				<form class="p-0" style="text-align: center" action="${s:mvcUrl('MCC#remover').build()}" method="post">
					<input name="id" type="hidden" value="${movimentacao.id}" />
					<input name="conta" type="hidden" value="${movimentacoes[0].conta.id}" />
					<button class="btn btn-danger" type="submit">Remover</button>	
				</form>
			</td>
			</security:authorize>
			
			<security:authorize access="hasRole('ROLE_USER')">
			<c:if test="${movimentacoes[0].conta.situacao != 'INATIVA'}">
			<td class="td-remover">
				<form class="p-0" style="text-align: center" onsubmit="return confirm('Deseja remover?');" action="${s:mvcUrl('MCC#remover').build()}" method="post">
					<input name="id" type="hidden" value="${movimentacao.id}" />
					<input name="conta" type="hidden" value="${movimentacoes[0].conta.id}" />
					<c:if test="${movimentacao.conciliada != 'SIM'}">
					<button class="btn btn-danger" type="submit">Remover</button>
					</c:if>
				</form>
			</td>
			</c:if>
			</security:authorize>
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
		<c:if test="${movimentacoes[0].conta.situacao != 'INATIVA'}">
				<form class="p-0 p-sm-1" onsubmit="return confirm('Deseja encerrar?');"	style="text-align: center"
					action="${s:mvcUrl('CDC#encerrarConta').build() }" method="post">
						<input name="id" type="hidden" value="${ movimentacoes[0].conta.id}" />
						<button class="btn btn-danger mb-2" type="submit">Encerrar conta</button>
						</form>		
		</c:if>
	</security:authorize>
	
	
			<c:if test="${movimentacoes[0].conta.devolucao == null}">
	<security:authorize access="hasRole('ROLE_ADMIN')">
		<c:if test="${movimentacoes[0].conta.situacao == 'INATIVA'}">
			<form style="text-align: center" onsubmit="return confirm('Confirma que deseja receber o restante em vale?');" action="${s:mvcUrl('CDC#vale').build()}" method="post">
				<input name="id" type="hidden" value="${movimentacoes[0].conta.id}" />
				<button class="btn btn-danger mb-2" type="submit">Vale</button>
			</form>
			
			<form style="text-align: center" onsubmit="return confirm('Confirma que deseja devolver para a empresa o restante?');" action="${s:mvcUrl('CDC#devolucao').build()}" method="post">
				<input name="id" type="hidden" value="${movimentacoes[0].conta.id}" />
				<button class="btn btn-danger mb-2" type="submit">Devolução</button>
			</form>
			
			
		</c:if>
	</security:authorize>	
	</c:if>	
		
	</div>

</tags:pageTemplate>