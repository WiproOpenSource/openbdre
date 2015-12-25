<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>
<html>
    <head>
	<link href="../../css/bootstrap.min.css" rel="stylesheet" type="text/css" />
	<title>Login Page</title>
	<style>

	    body{
		color: #514B4B;
		background: #FaFaFa;
	    }

	    #login-box {
		width: 370px;
		padding: 20px;
		margin-top:20px;
		border-top: 1px solid #e4e4e4;
		border-bottom: 1px solid #e4e4e4;
		border-radius:1px;
	    }
	    .hide{
		display:none;
	    }
	    #logo{
		width:100px;
	    }
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


	<h1 class="text-center text-info">  Welcome to Big Data Ready Enterprise  </h1>
	<img id="logo" class="center-block img-responsive" src="../../css/images/bdre-logo.png"/>
	<div id="login-box" class="center-block">

	    <p class="lead text-info">Please login </p>

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

	    <form name='loginForm'
		  action="<c:url value='/j_spring_security_check' />" method='POST'>

		<div class="form-group">
		    <label for="username">Username</label>
		    <input type="text" class="form-control" id="InputEmail" name='username' placeholder="Username">
		</div>
		<div class="form-group">
		    <label for="password">Password</label>
		    <input type="password" class="form-control" id="password" name='password' placeholder="Password">
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

		<button type="submit" class="btn btn-default btn-lg btn-primary"><span id="sizing-addon2"><span class="glyphicon glyphicon-arrow-right" aria-hidden="true"></span> Sign in </button>
		<!-- *************below are the buttons with different css***********
		<button type="submit" class="btn btn-default btn-lg btn-info"><span id="sizing-addon2"><span class="glyphicon glyphicon-arrow-right" aria-hidden="true"></span> Sign in </button>
		<button type="submit" class="btn btn-default btn-lg btn-warning"><span id="sizing-addon2"><span class="glyphicon glyphicon-arrow-right" aria-hidden="true"></span> Sign in </button>
		<button type="submit" class="btn btn-default btn-lg btn-danger"><span id="sizing-addon2"><span class="glyphicon glyphicon-arrow-right" aria-hidden="true"></span> Sign in </button>
		<button type="submit" class="btn btn-default btn-lg btn-block"><span id="sizing-addon2"><span class="glyphicon glyphicon-arrow-right" aria-hidden="true"></span> Sign in </button>
		<button type="submit" class="btn btn-default btn-lg"><span id="sizing-addon2"><span class="glyphicon glyphicon-arrow-right" aria-hidden="true"></span> Sign in </button> -->
		<input type="hidden" name="${_csrf.parameterName}"
		       value="${_csrf.token}" />

	    </form>
	</div>

    </body>
</html>