<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@page session="true"%>
<html>
    <head>
	<link href="../../css/bootstrap.min.css" rel="stylesheet" type="text/css" />
	<title><spring:message code="login.page.title"/></title>
	<style>
	     body{background-image: url("../../css/images/BDRE_BG.png");width: 100%;height: 100%;background-size: cover;overflow: hidden;}
		.login-box{width: 425px;height: 315px;margin: auto;border: 1px solid #e4e4e4;background-color: #e4e4e4;border-radius: 5px;position: absolute;top: 55%;bottom: 50%;left:0;right:0;}
		.logo{width: 110px;top: -51px;position: absolute;left: 0;right:0;}
		.btn-signin{background-color: #005352;height: 37px;width: 111px;padding-top: 0px;padding-bottom: 0px;float:right;margin-bottom:20px}
		.form-group-pdiv .form-control{height: 38px;width: 95%;margin:0 auto;padding: 6px 38px;}
		.form-group-pdiv .form-group {position: relative;margin-bottom:30px;margin-left:13px;}
		.loginForm{margin: 24% 20px 0px 20px;}
		.icon-circle{width: 40px;height: 40px;border-radius: 80px;background: #005352 no-repeat center;}
	    .pwordicon{background-image:  url("../../css/images/password-icon.png")  ;background-size: 65% 65%;position: absolute;left: -5px;}
	    .usericon{background-image: url("../../css/images/user.png");background-size: 55% 55%;position: absolute;left: -5px;}
	    .login-links{width: 95%;margin: 0 auto;}
	    .login-fp{float: left;color: #000000;font-family: sans-serif;font-weight: 500; margin-left:13px;margin-top: 2%;}
	    .text-info{color: #FFFFFF;font-size:60px;border-radius: 5px;padding-top: 4%;padding-bottom: 4%;}
	    .login-alert-danger{border: none;background: none;position: absolute;top: 18%;margin-left:16px}
	</style>
	<script>
	// Break out of an iframe
    // Passing `this` and re-aliasing as `window` ensures
    // that the window object hasn't been overwritten.
	(function(window) {
      if (window.location !== window.top.location) {
        window.top.location = window.location;
      }
    })(this);
	</script>
    </head>
    <body onload='document.loginForm.username.focus();'>
	<div class="text-center text-info"><spring:message code="login.page.title_bdre"/></div>
	<div id="login-box" class="center-block login-box">
	<img id="logo" class="center-block img-responsive logo" src="../../css/images/logo.png"/>
		<c:if test="${not empty error}">
		<div class="alert alert-danger login-alert-danger " role="alert">
		    <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span>
		    <span class="sr-only"><spring:message code="login.page.error"/></span>
		    ${error}
		</div>
		</c:if>
	    <c:if test="${not empty msg}">
		<div class="alert alert-info " role="alert">
		    <span class="glyphicon glyphicon-info-sign" aria-hidden="true"></span>
		    <span class="sr-only"><spring:message code="login.page.info"/></span>
		    ${msg}
		</div>
	    </c:if>
		<form name='loginForm' class="loginForm"
		  action="<c:url value='/j_spring_security_check' />" method='POST'>
		<div class="form-group-pdiv">
			<div class="form-group">
			    <div class="icon-circle usericon"></div><input type="text" class="form-control" id="InputEmail" name='username' placeholder=<spring:message code="login.page.username"/> >
			</div>
			<div class="form-group">
			    <div class="icon-circle pwordicon"></div><input type="password" class="form-control" id="password" name='password' placeholder=<spring:message code="login.page.password"/> >
			</div>
		</div>
		<div class="login-links">
			<div class="login-fp"><spring:message code="login.page.forgot_password"/></div>
			<button type="submit" class="btn btn-default btn-lg btn-primary btn-signin"><span id="sizing-addon2"><spring:message code="login.page.sign_button"/></span></button>
			<div class="clearfix"></div>
		</div>
		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
 		</form>
	</div>
    </body>
</html>