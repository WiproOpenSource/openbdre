<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<html>
    <body>
	<h1><spring:message code="403.page.http_error"/></h1>

	<c:choose>
	    <c:when test="${empty username}">
		<h2><spring:message code="403.page.error_desciption"/></h2>
	    </c:when>
	    <c:otherwise>
		<h2>Username : ${username} <br/><spring:message code="403.page.error_desciption"/></h2>
		</c:otherwise>
	    </c:choose>

    </body>
</html>