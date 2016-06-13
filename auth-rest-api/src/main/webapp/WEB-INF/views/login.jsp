<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@page session="true"%>
<html>
    <head>
	<link href="../../css/bootstrap.min.css" rel="stylesheet" type="text/css" />
	<title><spring:message code="login.page.title"/></title>
	<style>
@font-face {
	font-family: 'RobotoRegular';
	src: url('../../fonts/Roboto-Regular-webfont.eot');
	src: url('../../fonts/Roboto-Regular-webfont.eot?#iefix')
		format('embedded-opentype'),
		url('../../fonts/Roboto-Regular-webfont.woff') format('woff'),
		url('../../fonts/Roboto-Regular-webfont.ttf') format('truetype'),
		url('../../fonts/Roboto-Regular-webfont.svg#RobotoRegular')
		format('svg');
	font-weight: normal;
	font-style: normal;
}

body.blue-bg {
	background: url(../../css/images/BDRE_BG.jpg) no-repeat center 0 #012B38;
	background-size: 100% auto;
}

.footerWrapper {
	width: 100%;
	float: left;
	padding-top: 10px;
	height: 80px;
	font-size: 0.6rem;
	background-color: #8C9DA9 /*rgba(180, 180, 180,0.7)*/;
	/*border-top:3px solid #333; color:#FFF;*/
}

.footerWrapper a {
	text-decoration: none;
	padding: 0 3px;
	font-size: 0.7rem;
	color: #FFF;
}

.footerWrapper span {
	color: #555;
}

.footerWrapper span {
	float: left;
	color: #36BDBE;
}

#container {
	min-height: 100%;
	position: relative;
}

#body {
	padding: 10px;
	padding-bottom: 90px; /* Height of the footer */
}

.topWrapper2 {
	background-color: transparent; /*height:70px;*/
	/*text-shadow: -1px -1px 1px #ccc;*/ /*box-shadow: 0 1px 3px #333;*/
}

.topWrapper2 h1 {
	line-height: 2.5em;
	font-size: 1.6rem;
	color: #1d619c;
}

.topWrapper2 a img {
	padding: 0;
}

.topWrapper2 a {
	padding: 0 12px;
	border-left: 1px dotted #176080; /*border-right:1px solid #000;*/
	font-size: 14px;
	color: #36BDBE;
	text-decoration: none;
}

.bodyWrapper {
	margin-top: 10px;
	margin-bottom: 10px; /* color:#ADCCE6;*/
	color: rgba(255, 255, 255, 1); /*font-size:0.8rem;*/
	font-size: 12px;
}

.topWrapper2 a:hover {
	color: #333;
}

.topWrapper2 a:first-child {
	border-left: none;
}

.topWrapper2 a:last-child {
	border-right: none;
}

.topWrapper2 h1 {
	font-size: 18px;
	margin-top: 12px;
	color: #FFF;
	font-weight: bold
}

.topWrapper2 #search {
	color: #36BDBE;
	border-left: 1px dotted #176080;
}

.blue-bg .footerWrapper {
	background-color: transparent;
}

#footer {
	position: absolute;
	bottom: 0;
	width: 100%;
	height: 90px; /* Height of the footer */
	/*line-height:3.5em;*/
	font-size: 12px;
	color: #CCC;
	font-size: 11px;
}

.form-control::-webkit-input-placeholder, .form-control textarea::-webkit-input-placeholder
	{
	color: #D7E4F1 !important;
}

.form-control:-moz-placeholder, .form-control textarea:-moz-placeholder
	{
	color: #D7E4F1 !important;
}

.form-control::-moz-placeholder, .form-control textarea::-moz-placeholder
	{
	color: #D7E4F1 !important;
}

.form-control:-ms-input-placeholder, .form-control textarea:-ms-input-placeholder
	{
	color: #D7E4F1 !important;
}

.form-control::-webkit-input-placeholder {
	color: #D7E4F1
}

.loginBox2 a {
	color: #E4BE12;
}

.loginBox2 {
	margin: auto;
	width: 32%;
	margin: 0 auto;
	padding: 3em 6em; /*text-align:center;*/
	position: absolute;
	top: 50%;
	left: 50%;
	transform: translate(-50%, -50%);
}

.loginBox2 .has-success .form-control {
	border-color: transparent transparent #FFF;
	box-shadow: none;
	background-color: transparent;
}

.loginBox2 .has-success .input-group-addon {
	background-color: transparent;
	border-color: transparent transparent #FFF;
}

.loginBox2 .has-success .input-group-addon {
	color: #FFF;
}

.loginBox2 .btn {
	border-radius: 4px;
	box-shadow: -2px 0 9px rgba(0, 0, 0, 0.6);
	height: auto;
	line-height: 1.8em;
	padding: 7px 15px 7px 20px;
	text-align: left;
	white-space: normal;
}

.loginBox2 .form-control {
	color: #6CA8BA;
}

.loginBox2 .btn-primary {
	background-color: #608595;
	font-size: 15px;
	padding: 2px 10px;
	border-color: #567786;
	text-align: center;
	text-shadow: 0px 1px 0px rgba(0, 0, 0, 0.8);
}

.loginBox2 .btn-primary:hover {
	background-color: #567786;
	color: #151706 !important;
	text-shadow: 0px 1px 0px rgba(255, 255, 255, 0.3);
}

.btn {
	font-size: 1.0rem;
	border-width: 3px;
}

.footerWrapper span {
	float: left;
	color: #36BDBE;
}

.blue-bg a:hover {
	color: #FFF /*#36BDBE*/;
}
/*---------------Login2 End---------------------------*/
.blue-bg .glyphicon {
	text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.4);
}

.glyphicon {
	top: 0;
}

#navigation-bar .glyphicon {
	float: left;
}

#navigation-bar {
	position: relative;
	height: 21px;
	padding-left: 21px;
}

.input-group-addon:first-child {
	border-right: 0 none;
}

.has-success .input-group-addon {
	color: #053a4f;
}

.has-success .input-group-addon {
	background-color: #dff0d8;
	border-color: #509eba;
	color: #144770;
}

.iconStrip {
	padding-top: 23px;
}

.DDPTitle h1 a {
	font-size: 19px;
}

html, body {
	margin: 0;
	padding: 0;
	height: 100%;
}

body {
	position: relative;
	margin: 0 !important;
	padding: 0 !important;
}

body {
	background: url(../images/bg.jpg) no-repeat center center fixed #133c5d;
	background-size: 100% 100%;
	font-family: 'RobotoRegular', Arial, Helvetica, sans-serif;
	line-height: 125%;
	-webkit-background-size: cover;
	-moz-background-size: cover;
	-o-background-size: cover;
	background-size: cover;
}
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
    <body onload='document.loginForm.username.focus();' class="blue-bg">
	<%-- <div class="text-center text-info"><spring:message code="login.page.title_bdre"/></div> --%>
	<div id="container">
	<div class="topWrapper2">
		<div class="container">
			<div class="pull-left DDPTitle"><h1><a><img src="../../css/images/ddplogo.png" width="256" alt="Data Discovery Platform" /></a></h1></div>
				<div class="pull-right iconStrip">
				<nav> 
				<div id="navigation-bar" class="clearfix">
				<img src="../../css/images/Wipro-logo.png" width="65" alt="Data Discovery Platform" />
				
				</div>
				</nav>
				
				</div>
			<div class="clearfix"></div>
		</div>

	</div>
	<div class="container" id="body">
		<div class="bodyWrapper">
	<div id="login-box" class="center-block loginbox2">
	<!-- <img id="logo" class="center-block img-responsive logo" src="../../css/images/Wipro-logo.png"/> -->
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
		  action="<c:url value='/j_spring_security_check' />" method='POST' autocomplete="off">
		    <div class="form-group has-success has-feedback">
		        <div class="input-group">
		        <span class="input-group-addon "><i class="glyphicon glyphicon-user"></i></span>
		        	<input id="InputEmail" name='username' placeholder=<spring:message code="login.page.username"/> type="text" class="form-control">
		        </div>
        	</div>
		  	<div class="form-group has-success has-feedback">
		        <div class="input-group">
		        <span class="input-group-addon"><i class="glyphicon glyphicon-lock"></i></span>
		        <input input type="password" class="form-control" id="password" name='password' placeholder=<spring:message code="login.page.password"/>>
		        </div>
	        </div>
		<div class="login-links">
			<%-- <div class="login-fp"><spring:message code="login.page.forgot_password"/></div> --%>
			<button type="submit" class="btn btn-lg btn-primary btn-block btn-signin">SIGN IN</button>
			<div class="clearfix"></div>
		</div>
		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
 		</form>
	</div>
	</div>
	</div>
	<!--Footer Start-->
	<div class="footerWrapper" id="footer">
		<div class="container">
			<span>&copy;  Wipro Limited 2016.</span>
		</div>
	</div>
<!--Footer End-->
</div>
	
    </body>
</html>