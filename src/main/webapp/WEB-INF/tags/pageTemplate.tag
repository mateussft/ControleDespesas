<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib uri="http://www.springframework.org/security/tags"
	prefix="security"%>

<%@ attribute name="titulo" required="true"%>

<c:url value="/" var="contextPath" />

<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
<meta name="viewport"
	content="width=device-width, initial-scale=1, maximum-scale=1" />
<title>${titulo } - Controle de despesas Safetech</title>
  <link rel="stylesheet" type="text/css" media="screen"
	href="https://bootswatch.com/4/flatly/bootstrap.min.css">
 <link rel="stylesheet" href="${contextPath}resources/css/main.css" />
<link rel="stylesheet" href="${contextPath}resources/css/footer.css" />
 <link rel="stylesheet" href="${contextPath}resources/css/header.css" />
 <link rel="stylesheet" href="${contextPath}resources/css/home.css" />
<link rel="stylesheet" href="${contextPath}resources/css/login.css" />
<link rel="stylesheet" href="${contextPath}resources/css/responsive-table.css" />  
</head>

<body>
	<div class="container-fluid">
		<%@ include file="/WEB-INF/views/componentes/header.jsp"%>
		<jsp:doBody />
	</div>
	<footer>
		<h6 style="text-align: center">Safetech</h6>
	</footer>

<script src="https://code.jquery.com/jquery-3.3.1.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery.mask/1.14.10/jquery.mask.js"></script>
		
	<script
		src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js"
		integrity="sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49"
		crossorigin="anonymous"></script>
	<script
		src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.min.js"
		integrity="sha384-ChfqqxuZUCnJSK3+MXmPNIyE6ZbWh2IMqE241rYiqJxyMiZ6OW/JmZQ5stwEULTy"
		crossorigin="anonymous"></script>
<script>
$(document).ready(function() {
	$('[name=dataInicio], [name=dataFinal] ').mask("99/99/9999",{placeholder:"dd/MM/yyyy"});
	});
	</script>
</body>
</html>