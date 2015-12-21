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
	    title: 'Process Log List',
		    paging: true,
		    pageSize: 10,
		    sorting: true,
		    actions: {
		    listAction: function (postData, jtParams) {
		    console.log(postData);
			    return $.Deferred(function ($dfd) {
			    $.ajax({
	    <c:if test="${param.pid==null}">
			    url: '/mdrest/processlog?page=' + jtParams.jtStartIndex + '&size='+jtParams.jtPageSize,
	    </c:if>
	    <c:if test="${param.pid!=null}">
			    url: '/mdrest/process?pid=${param.pid}&page=' + jtParams.jtStartIndex + '&size='+jtParams.jtPageSize,
	    </c:if>
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
		    }
		    },
		    fields: {

		    SubProcess: {
		    title: 'Click to get list of Sub Processes of Process',
			    width: '5%',
			    sorting: false,
			    edit: false,
			    create: false,
			    listClass: 'bdre-jtable-button',
			    display: function(item) {                         //Create an image that will be used to open child table

			    var $img = $('<span class="label label-primary">Sub Process</span>'); //Open child table when user clicks the image

				    $img.click(function() {
				    $('#Container').jtable('openChildTable',
					    $img.closest('tr'), {
				    title: ' SubProcess of ' + item.record.parentProcessId + ' having logs ',
					    paging: true,
					    pageSize: 10,
					    actions: {
					    listAction: function(postData) {
					    return $.Deferred(function($dfd) {
					    console.log(item);
						    console.log (item.record.parentProcessId);
						    console.log (item.record.processId);
						    $.ajax({
						    url: '/mdrest/subprocess/' + item.record.processId,
							    type: 'GET',
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
					    }
					    },
					    fields: {
					    Log: {
					    title: 'Click to get logs related sub process',
						    width: '5%',
						    sorting: false,
						    edit: false,
						    create: false,
						    listClass: 'bdre-jtable-button',
						    display: function(item) {                         //Create an image that will be used to open child table

						    var $img = $('<span class="label label-primary">Logs</span>'); //Open child table when user clicks the image

							    $img.click(function() {
							    $('#Container').jtable('openChildTable',
								    $img.closest('tr'), {
							    title: ' Logs of process id ' + item.record.processId,
								    paging: true,
								    pageSize: 10,
								    actions: {
								    listAction: function(postData) {
								    return $.Deferred(function($dfd) {
								    console.log(item);
									    $.ajax({
									    url: '/mdrest/processlog/' + item.record.processId,
										    type: 'GET',
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
								    }
								    },
								    fields :{
								    processId: {
								    key : true,
									    list: false,
									    create:false,
									    edit: true,
									    title: 'Process Id',
									    defaultValue: item.record.processId,
								    },
									    addTs: {
									    title: 'add ts'
									    },
									    logCategory: {
									    title: 'log category'

									    },
									    message: {
									    title: 'message'

									    },
									    messageId: {
									    title: 'message id',
									    },
									    instanceRef: {

									    title: 'instance ref',
										    create:true,
										    edit :true
									    }

								    }

							    },
								    function(data) { //opened handler

								    data.childTable.jtable('load');
								    });
							    }); //Return image to show on the person row

							    return $img;
						    }
					    },
						    processId: {
						    key : true,
							    list: true,
							    create:true,
							    edit: false,
							    title: 'Process'

						    }
					    }
				    },
					    function(data) { //opened handler

					    data.childTable.jtable('load');
					    });
				    }); //Return image to show on the person row

				    return $img;
			    }

		    },
			    parentProcessId: {
			    key : true,
				    list: false,
				    create:true,
				    edit: false,
				    title: 'Process'

			    },
			    processId: {
			    key : true,
				    list: true,
				    create:true,
				    edit: false,
				    title: 'Process'

			    }
		    }
	    });
		    $('#Container').jtable('load');
	    });</script>
	<script>

		    function showProcessPage(pid){
		    console.log('entered function');
			    console.log(${param.pid == null });
			    location.href = '<c:url value="/pages/processlog.page?pid="/>' + pid;
		    }

	</script>
    </head>
    <body>
    <section style="width:100%;text-align:center;">
	<div id="Container"></div>
    </section>
    <div id="input-box-button" >
	<form onsubmit="showProcessPage(jQuery('#pid').val()); return false;">
	    <div class="input-group">
		<input class="form-control" type="number" name="pid" id="pid" value ="" placeholder="Filter by processid"/>
		<!-- <button  class="btn btn-default btn-lg btn-primary"><span id="sizing-addon2"><span class="glyphicon glyphicon-arrow-right" aria-hidden="true"></span> Show Lineage </button> -->
		<span class="input-group-btn">
		    <button class="btn btn-default  btn-primary" type="submit" onClick="showProcessPage(jQuery('#pid').val())"><span id="sizing-addon2"><span class="glyphicon glyphicon-arrow-right" aria-hidden="true"></span>&nbsp;</button>
		</span>
	    </div>
	</form>
    </div>

</body>
</html>​
