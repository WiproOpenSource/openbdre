<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>
<html>
    <head>
	<link href="../../css/bootstrap.min.css" rel="stylesheet" type="text/css" />
	<title>Login Page</title>
	<style>
	     body{background-image: url("../../css/images/BDRE_BG.png");}
		.login-box {width: 470px;height: 361px;border: 1px solid #e4e4e4;background-color: #e4e4e4;border-radius:5px;}
		.logo{width:100px;top: 51px;position: relative;}
		.bdre-signin{background-color: #005352;height: 37px;width: 111px;padding-top: 0px;padding-bottom: 0px;}
		.bdre-form-group .form-control{height: 50px;width: 95%;margin:0 auto;padding: 6px 38px;}
		.bdre-loginForm{margin: 24% 20px 0px 20px; 	   	}
		.bdre-form-group .form-group {position: relative;}
	    .bdre-circle{background-color: #005352;width: 38px;height: 38px;border-radius: 80px;background-repeat: no-repeat;background-position: center;position: absolute;top:11%;left: -5px;}
	    .bdre-pwordicon{background-image: url("../../css/images/password-icon.png");background-size: 65% 65%;}
	    .bdre-usericon{background-image: url("../../css/images/user.png");background-size: 55% 55%;}
	    .bdre-btn{text-align: right;right: 36px;position: relative;}
	    .bdre-links{width: 95%;margin: 0 auto;}
	    .bdre-fp{float: left;color: #000000;font-family: sans-serif;font-weight: 500;margin-top: 2%}
	    .bdre-signindiv{float:right;margin-right: 20px;}
	    .text-info{color: #FFFFFF;position: relative;top: 6%;}
	</style>
	<script>
	// Break out of an iframe
    //
    // Passing `this` and re-aliasing as `window` ensures
    // that the window object hasn't been overwritten.
    //

    (function(window) {
      if (window.location !== window.top.location) {
        window.top.location = window.location;
      }
    })(this);
	
    </script>
    </head>
    <body onload='document.loginForm.username.focus();'>


	<h1 class="text-center text-info">Big Data Ready Enterprise</h1>
	<img id="logo" class="center-block img-responsive logo" src="../../css/images/logo.png"/>
	<div id="login-box" class="center-block login-box">
		<!-- <p class="lead text-info">Please login </p> -->
		<c:if test="${not empty error}">
		<div class="alert alert-danger " role="alert">
		    <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span>
		    <span class="sr-only">Error:</span>
		    ${error}
		</div>

	    </c:if>
	    <c:if test="${not empty msg}">
		<div class="alert alert-info " role="alert">
		    <span class="glyphicon glyphicon-info-sign" aria-hidden="true"></span>
		    <span class="sr-only">Info:</span>
		    ${msg}
		</div>
	    </c:if>

	    <form name='loginForm' class="bdre-loginForm"
		  action="<c:url value='/j_spring_security_check' />" method='POST'>

		<div class="bdre-form-group">
			<div class="form-group">
			    <div class="bdre-circle bdre-usericon"></div><input type="text" class="form-control" id="InputEmail" name='username' placeholder="Username">
			</div>
			<div class="form-group">
			    <div class="bdre-circle bdre-pwordicon"></div><input type="password" class="form-control" id="password" name='password' placeholder="Password">
			</div>
		</div>

		<!--<table>
		<div class="input-group">
		  
		  <input class="form-control" placeholder="Password" type='password' name='password' aria-describedby="sizing-addon2">
		  <span class="input-group-addon" id="sizing-addon2"><span class="glyphicon glyphicon-lock" aria-hidden="true"></span></span>
		</div>
		<div class="input-group">
		  <input type="text" class="form-control" name='username' placeholder="Username" aria-describedby="sizing-addon2">
		  <span class="input-group-addon" id="sizing-addon2"><span class="glyphicon glyphicon-user" aria-hidden="true"></span></span>
		</div>
			<tr>
				<td>User:</td>
				<td><input type='text' name='username'></td>
			</tr>
			<tr>
				<td>Password:</td>
				<td><input type='password' name='password' /></td>
			</tr>
			<tr>
				<td colspan='2'><input name="submit" type="submit"
					value="submit" /></td>
			</tr>
			<input class="btn btn-lg btn-default" name="submit" type="submit" value="Sign In" />
		</table> -->
		<div class="bdre-links">
		<div class="bdre-fp">Forgot Password?</div>
		<div class="bdre-signindiv"><button type="submit" class="btn btn-default btn-lg btn-primary bdre-signin"><span id="sizing-addon2">Sign in</button></div>
		<div class="clearfix"></div>
		</div>
		<!-- *************below are the buttons with different css***********
		<button type="submit" class="btn btn-default btn-lg btn-info"><span id="sizing-addon2"><span class="glyphicon glyphicon-arrow-right" aria-hidden="true"></span> Sign in </button>
		<button type="submit" class="btn btn-default btn-lg btn-warning"><span id="sizing-addon2"><span class="glyphicon glyphicon-arrow-right" aria-hidden="true"></span> Sign in </button>
		<button type="submit" class="btn btn-default btn-lg btn-danger"><span id="sizing-addon2"><span class="glyphicon glyphicon-arrow-right" aria-hidden="true"></span> Sign in </button>
		<button type="submit" class="btn btn-default btn-lg btn-block"><span id="sizing-addon2"><span class="glyphicon glyphicon-arrow-right" aria-hidden="true"></span> Sign in </button>
		<button type="submit" class="btn btn-default btn-lg"><span id="sizing-addon2"><span class="glyphicon glyphicon-arrow-right" aria-hidden="true"></span> Sign in </button> -->
		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
 </form>
	</div>

    </body>
</html>