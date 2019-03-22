<nav class="navbar navbar-expand-sm navbar-dark bg-primary cabecalho">
<a href="${s:mvcUrl('HC#index').build() }"> <img class="m-1"
		style="filter: brightness(0) invert(1); height: 70px"
		src="${ contextPath }resources/imagens/logo_safe.png" alt="Safetech">
	</a>	

	<button class="navbar-toggler" type="button" data-toggle="collapse"
		data-target="#navbarColor01" aria-controls="navbarColor01"
		aria-expanded="false" aria-label="Toggle navigation">
		<span class="navbar-toggler-icon"></span>
	</button>

	<div class="collapse navbar-collapse" id="navbarColor01">
		<security:authorize access="isAuthenticated()">
			<security:authentication property="principal" var="usuario" />
			<h6>Bem vindo, ${usuario.nome }</h6>
		</security:authorize>
		<security:authorize access="isAuthenticated()">
			<a style="color: white" class="nav-link" href="<c:url value="/logout" />">Sair</a>
			<a style="color: white" class="nav-link" href="${s:mvcUrl('UC#formSenha').build() }">Alterar Senha</a>
		</security:authorize>
		<security:authorize access="!isAuthenticated()">
			<a style="color: white" class="nav-link" href="${s:mvcUrl('LC#form').build() }">Login</a>
		</security:authorize>
	</div>
</nav>