
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ taglib prefix="security"
	   uri="http://www.springframework.org/security/tags" %>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Bigdata Ready Enterprise</title>
	<!-- Include one of jTable styles. -->

	<link href="../css/css/bootstrap.min.css" rel="stylesheet" type="text/css" />
	<link href="../css/jtables-bdre.css" rel="stylesheet" type="text/css" />
	<link href="../css/jquery-ui-1.10.3.custom.css" rel="stylesheet" type="text/css" />

	<!-- Include jTable script file. -->
	<script src="../js/jquery.min.js" type="text/javascript"></script>
	<script src="../js/jquery-ui-1.10.3.custom.js" type="text/javascript"></script>
	<script src="../js/jquery.jtable.js" type="text/javascript"></script>

	<script type="text/javascript">
		    $(document).ready(function () {
	    $('#Container').jtable({
	    title: 'Setup DQ Job',
		    paging: true,
		    pageSize: 10,
		    sorting: true,
		    actions: {

		    listAction: function(postData, jtParams) {
		    return $.Deferred(function($dfd) {
		    $.ajax({
		    url: '/mdrest/dqsetup?page=' + jtParams.jtStartIndex + '&size='+jtParams.jtPageSize,
			    type: 'GET',
			    data: postData,
			    dataType: 'json',
			    success: function(data) {
			    $dfd.resolve(data);
			    },
			    error: function() {
			    $dfd.reject();
			    }
		    }); ;
		    });
		    },
			    deleteAction: function(item) {
			    console.log("item is");
				    console.log(item);
				    return $.Deferred(function($dfd) {
				    $.ajax({
				    url: '/mdrest/dqsetup/' + item.subProcessId,
					    type: 'DELETE',
					    data: item,
					    dataType: 'json',
					    success: function(data) {
					    $dfd.resolve(data);
					    },
					    error: function() {
					    $dfd.reject();
					    }
				    });
				    });
			    },
	    <security:authorize access="hasAnyRole('ROLE_ADMIN','ROLE_USER')">

		    updateAction: function(postData) {
		    console.log(postData);
			    return $.Deferred(function($dfd) {
			    $.ajax({
			    url: '/mdrest/dqsetup',
				    type: 'POST',
				    data: postData,
				    dataType: 'json',
				    success: function(data) {
				    console.log(data);
					    $dfd.resolve(data);
				    },
				    error: function() {
				    $dfd.reject();
				    }
			    });
			    });
		    },
			    createAction: function(postData) {
			    console.log(postData);
				    return $.Deferred(function($dfd) {
				    $.ajax({
				    url: '/mdrest/dqsetup/',
					    type: 'PUT',
					    data: postData,
					    dataType: 'json',
					    success: function(data) {
					    $dfd.resolve(data);
					    },
					    error: function() {
					    $dfd.reject();
					    }
				    });
				    });
			    }</security:authorize>
		    },
		    fields: {

		    rulesUserNameValue: {
		    key : false,
			    list: false,
			    create:true,
			    edit: true,
			    title: 'Rules Username'
		    },
			    rulesPasswordValue: {
			    title: 'Rules Password'
			    },
			    rulesPackageValue: {
			    title: 'Rules Package',
				    key : false,
				    list: true,
				    create:true,
				    edit:true
			    },
			    fileDelimiterRegexValue: {
			    title: 'File Delimiter'
			    },
			    minPassThresholdPercentValue: {
			    title: 'Min pass threshold %',
				    width: '15%'
			    },
			    configGroup: {
			    title :'Config Group',
				    key : false,
				    list: true,
				    create:false,
				    edit: false,
			    },
			    busDomainId: {
			    title :'Application',
				    key : false,
				    list: false,
				    create:true,
				    edit: false,
			    },
			    canRecover: {
			    title :'can recover',
				    key : false,
				    list: false,
				    create:true,
				    edit: false,
			    },
			    enqId: {
			    title :'enq id',
				    key : false,
				    list: false,
				    create:true,
				    edit: false,
			    },
			    parentProcessId: {
			    key : false,
				    list: true,
				    create:false,
				    edit: false,
				    title: 'DQ Job ID'

			    },
			    subProcessId: {
			    key : true,
				    list: true,
				    create:false,
				    edit: false,
				    title: 'DQ Step'

			    },
			    description: {
			    key : false,
				    list: true,
				    create:true,
				    edit: true,
				    title: 'Description'

			    },
			    ProcessPageButton: {
			    sorting: false,
				    width: '12%',
				    title: 'Process page',
				    create: false,
				    edit: false,
				    listClass: 'bdre-jtable-button',
				    display: function(data) {
				    return '<img src="../css/metro/processpage.png" onclick="showProcessPage(' + data.record.parentProcessId + ')"></img> ';
				    },
			    },
		    }
	    });
		    $('#Container').jtable('load');
	    });</script>
	    <script>

			function showProcessPage(pid){
			console.log('entered function');
				console.log(${param.pid == null });
				location.href = '<c:url value="/pages/process.page?pid="/>' + pid;
			}

	</script>
    </head>
    <body>

    <section style="width:100%;text-align:center;">
	<div id="Container"></div>
    </section>


</body>
</html>​
