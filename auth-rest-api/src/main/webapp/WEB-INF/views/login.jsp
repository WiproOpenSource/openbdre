<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@page session="true"%>
<META Http-Equiv="Cache-Control" Content="no-cache">
    <META Http-Equiv="Pragma" Content="no-cache">
    <META Http-Equiv="Expires" Content="0">
<html>
    <head>
     <script src="../js/jquery.min.js" type="text/javascript"></script>
             <script src="../js/jquery-ui-1.10.3.custom.js" type="text/javascript"></script>
             <script src="../js/jquery.steps.min.js" type="text/javascript"></script>
             <script src="../js/jquery.jtable.js" type="text/javascript"></script>
	<link href="../../css/bootstrap.min.css" rel="stylesheet" type="text/css" />
	<link rel="shortcut icon" href="../../css/images/favicon.ico" type="image/x-icon" />
	<title><spring:message code="login.page.title"/></title>
	<style>
	     body{background-image: url("../../css/images/BDRE_BG.jpg");width: 100%;height: 100%;background-size: cover;overflow: hidden;}
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
	    .text-info{color: #FFFFFF;font-size:60px;border-radius: 5px;padding-top: 4%;padding-bottom: 0%;}
	    .text-footer{color: #FFFFFF;font-size:30px;border-radius: 5px;padding-top: 0%;padding-bottom: 2%;}
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
	<div class="text-center text-footer"><spring:message code="login.page.footer_bdre"/></div>
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

          action="<c:url value='/j_spring_security_check' />" onsubmit="return validateForm()" method='POST'>
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
        <script>
           function validateForm() {
                   var username = document.forms["loginForm"]["username"].value;
                   var letterNumber = /^[0-9a-zA-Z]+$/;
                   if (username.match(letterNumber)) {
                       var password=document.forms["loginForm"]["password"].value;
                       document.forms["loginForm"]["password"].value = SHA1(password);
                       return true;
                   }
                   else{
                     alert("Please enter valid characters");
                                    return false;
                   }
               }

               function SHA1(msg) {
                 function rotate_left(n,s) {
                   var t4 = ( n<<s ) | (n>>>(32-s));
                   return t4;
                 };
                 function lsb_hex(val) {
                   var str="";
                   var i;
                   var vh;
                   var vl;
                   for( i=0; i<=6; i+=2 ) {
                     vh = (val>>>(i*4+4))&0x0f;
                     vl = (val>>>(i*4))&0x0f;
                     str += vh.toString(16) + vl.toString(16);
                   }
                   return str;
                 };
                 function cvt_hex(val) {
                   var str="";
                   var i;
                   var v;
                   for( i=7; i>=0; i-- ) {
                     v = (val>>>(i*4))&0x0f;
                     str += v.toString(16);
                   }
                   return str;
                 };
                 function Utf8Encode(string) {
                   string = string.replace(/\r\n/g,"\n");
                   var utftext = "";
                   for (var n = 0; n < string.length; n++) {
                     var c = string.charCodeAt(n);
                     if (c < 128) {
                       utftext += String.fromCharCode(c);
                     }
                     else if((c > 127) && (c < 2048)) {
                       utftext += String.fromCharCode((c >> 6) | 192);
                       utftext += String.fromCharCode((c & 63) | 128);
                     }
                     else {
                       utftext += String.fromCharCode((c >> 12) | 224);
                       utftext += String.fromCharCode(((c >> 6) & 63) | 128);
                       utftext += String.fromCharCode((c & 63) | 128);
                     }
                   }
                   return utftext;
                 };
                 var blockstart;
                 var i, j;
                 var W = new Array(80);
                 var H0 = 0x67452301;
                 var H1 = 0xEFCDAB89;
                 var H2 = 0x98BADCFE;
                 var H3 = 0x10325476;
                 var H4 = 0xC3D2E1F0;
                 var A, B, C, D, E;
                 var temp;
                 msg = Utf8Encode(msg);
                 var msg_len = msg.length;
                 var word_array = new Array();
                 for( i=0; i<msg_len-3; i+=4 ) {
                   j = msg.charCodeAt(i)<<24 | msg.charCodeAt(i+1)<<16 |
                   msg.charCodeAt(i+2)<<8 | msg.charCodeAt(i+3);
                   word_array.push( j );
                 }
                 switch( msg_len % 4 ) {
                   case 0:
                     i = 0x080000000;
                   break;
                   case 1:
                     i = msg.charCodeAt(msg_len-1)<<24 | 0x0800000;
                   break;
                   case 2:
                     i = msg.charCodeAt(msg_len-2)<<24 | msg.charCodeAt(msg_len-1)<<16 | 0x08000;
                   break;
                   case 3:
                     i = msg.charCodeAt(msg_len-3)<<24 | msg.charCodeAt(msg_len-2)<<16 | msg.charCodeAt(msg_len-1)<<8  | 0x80;
                   break;
                 }
                 word_array.push( i );
                 while( (word_array.length % 16) != 14 ) word_array.push( 0 );
                 word_array.push( msg_len>>>29 );
                 word_array.push( (msg_len<<3)&0x0ffffffff );
                 for ( blockstart=0; blockstart<word_array.length; blockstart+=16 ) {
                   for( i=0; i<16; i++ ) W[i] = word_array[blockstart+i];
                   for( i=16; i<=79; i++ ) W[i] = rotate_left(W[i-3] ^ W[i-8] ^ W[i-14] ^ W[i-16], 1);
                   A = H0;
                   B = H1;
                   C = H2;
                   D = H3;
                   E = H4;
                   for( i= 0; i<=19; i++ ) {
                     temp = (rotate_left(A,5) + ((B&C) | (~B&D)) + E + W[i] + 0x5A827999) & 0x0ffffffff;
                     E = D;
                     D = C;
                     C = rotate_left(B,30);
                     B = A;
                     A = temp;
                   }
                   for( i=20; i<=39; i++ ) {
                     temp = (rotate_left(A,5) + (B ^ C ^ D) + E + W[i] + 0x6ED9EBA1) & 0x0ffffffff;
                     E = D;
                     D = C;
                     C = rotate_left(B,30);
                     B = A;
                     A = temp;
                   }
                   for( i=40; i<=59; i++ ) {
                     temp = (rotate_left(A,5) + ((B&C) | (B&D) | (C&D)) + E + W[i] + 0x8F1BBCDC) & 0x0ffffffff;
                     E = D;
                     D = C;
                     C = rotate_left(B,30);
                     B = A;
                     A = temp;
                   }
                   for( i=60; i<=79; i++ ) {
                     temp = (rotate_left(A,5) + (B ^ C ^ D) + E + W[i] + 0xCA62C1D6) & 0x0ffffffff;
                     E = D;
                     D = C;
                     C = rotate_left(B,30);
                     B = A;
                     A = temp;
                   }
                   H0 = (H0 + A) & 0x0ffffffff;
                   H1 = (H1 + B) & 0x0ffffffff;
                   H2 = (H2 + C) & 0x0ffffffff;
                   H3 = (H3 + D) & 0x0ffffffff;
                   H4 = (H4 + E) & 0x0ffffffff;
                 }
                 var temp = cvt_hex(H0) + cvt_hex(H1) + cvt_hex(H2) + cvt_hex(H3) + cvt_hex(H4);

                 return temp.toLowerCase();
               }


        </script>
    </body>
</html>