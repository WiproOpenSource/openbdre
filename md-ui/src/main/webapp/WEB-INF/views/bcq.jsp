<%@ taglib prefix="security"
	   uri="http://www.springframework.org/security/tags" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	 pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title><spring:message code="common.page.title_bdre_1"/></title>
	<script>
	  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
	  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
	  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
	  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');
	  //Please replace with your own analytics id
	  ga('create', 'UA-72345517-1', 'auto');
	  ga('send', 'pageview');
	</script>

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
	    title: '<spring:message code="bcq.page.title_jtable"/>',
		    paging: true,
		    pageSize: 10,
		    sorting: true,
		    actions: {
		    listAction: function (postData, jtParams) {
		    console.log(postData);
			    return $.Deferred(function ($dfd) {
			    $.ajax({
			    url: '/mdrest/bcq?page=' + jtParams.jtStartIndex  + '&size='+jtParams.jtPageSize,
				    type: 'GET',
				    data: postData,
				    dataType: 'json',
				    success: function (data) {
				    $dfd.resolve(data);
				    },
				    error: function () {
				    $dfd.reject();
				    }
			    });
			    });
		    },
	    <security:authorize access="hasAnyRole('ROLE_ADMIN','ROLE_USER')">
		    createAction:function (postData) {
		    console.log(postData);
			    return $.Deferred(function ($dfd) {
			    $.ajax({
			    url: '/mdrest/bcq',
				    type: 'PUT',
				    data: postData,
				    dataType: 'json',
				    success: function (data) {
				    $dfd.resolve(data);
				    },
				    error: function () {
				    $dfd.reject();
				    }
			    });
			    });
		    },
			    updateAction: function (postData) {
			    console.log(postData);
				    return $.Deferred(function ($dfd) {
				    $.ajax({
				    url: '/mdrest/bcq',
					    type: 'POST',
					    data: postData,
					    dataType: 'json',
					    success: function (data) {
					    console.log(data);
						    $dfd.resolve(data);
					    },
					    error: function () {
					    $dfd.reject();
					    }
				    });
				    });
			    },
			    deleteAction: function (item) {
			    console.log(item);
				    return $.Deferred(function ($dfd) {
				    $.ajax({
				    url: '/mdrest/bcq/' + item.queueId,
					    type: 'DELETE',
					    data: item,
					    dataType: 'json',
					    success: function (data) {
					    $dfd.resolve(data);
					    },
					    error: function () {
					    $dfd.reject();
					    }
				    });
				    });
			    }
	    </security:authorize>
		    },
		    fields:
	    {
	    queueId: {
	    key: true,
		    list: true,
		    create: false,
		    edit: false
	    },
		    sourceBatchId: {
		    title: '<spring:message code="bcq.page.title_src_batch_id"/>'
		    },
		    targetBatchId: {
		    title: '<spring:message code="bcq.page.title_target_batch_id"/>'

		    },
		    tableInsertTS: {
		    title: '<spring:message code="bcq.page.title_insert_time"/>',
			    create: false,
			    edit: true


		    },
		    sourceProcessId: {
		    title: '<spring:message code="bcq.page.title_src_id"/>',
		    },
		    tableStartTS: {
		    title: '<spring:message code="bcq.page.title_start_time"/>',
			    create: true,
			    edit: true

		    },
		    tableEndTS: {
		    title: '<spring:message code="bcq.page.title_end_time"/>',
			    create: true,
			    edit: true
		    },
		    batchState: {
		    title: '<spring:message code="bcq.page.title_batch_state"/>',
			    edit: true,
			    type: 'combobox',
			    options: '/mdrest/batchstatus/options',
			    defaultValue: "0"
		    },
		    batchMarking: {
		    title: '<spring:message code="bcq.page.title_batch_marking"/>'
		    },
		    processId: {
		    title: '<spring:message code="bcq.page.title_pid"/> '
		    }
	    }
	    });
		    $('#Container').jtable('load');
	    });
	</script>
    </head>
    <body>

    <section style="width:100%;text-align:center;">
	<div id="Container"></div>
    </section>


</body>
</html>