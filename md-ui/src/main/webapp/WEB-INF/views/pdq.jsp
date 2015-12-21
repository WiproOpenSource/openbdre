<%@ taglib prefix="security"
	   uri="http://www.springframework.org/security/tags" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	 pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
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
	    title: 'Process Deployment Queue List',
		    paging: true,
		    edit: false,
		    create: false,
		    pageSize: 10,
		    sorting: true,
		    actions: {
		    listAction: function (postData, jtParams) {
		    console.log(postData);
			    return $.Deferred(function ($dfd) {
			    $.ajax({
			    url: '/mdrest/pdq?page=' + jtParams.jtStartIndex + '&size='+jtParams.jtPageSize,
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
	   			 updateAction: function(item) {
        			    console.log(item);
        				    return $.Deferred(function($dfd) {
        				    $.ajax({
        				    url: '/mdrest/pdq',
        					    type: 'POST',
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
        			    },
			    deleteAction: function (item) {
			    console.log(item);
				    return $.Deferred(function ($dfd) {
				    $.ajax({
				    url: '/mdrest/pdq/' + item.deploymentId,
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
            deploymentId: {
            title: 'Deploy ID',
            key: true,
                list: true,
                edit:false
            },
            processId: {
			title: 'Process ID',
			edit:false,

			},
		    deployStatusId: {
		    title: 'Deploy Status',
		    edit:false,
		    type: 'combobox',
		    options: '/mdrest/deploystatus/options'

		    },
		    tableInsertTs: {
		    title: 'Insert Time',
		    edit:false
		    },
		    tableStartTs: {
		    title: 'Start Time',
		    edit:false

		    },
		    tableEndTs: {
		    title: 'End Time',
		    edit:false
		    },
		    deployScriptLocation: {
            title: 'Script Location',
            edit:false
            },
            userName: {
           title: 'User Name',
           edit:false
           },
            processTypeId: {
		   title: 'Process Type',
		   edit:false,
		   type: 'combobox',
           options: '/mdrest/processtype/optionslist/'
		   },
		   busDomainId: {
		   title: 'App',
		   type: 'combobox',
           options: '/mdrest/busdomain/options/',
		   edit:false
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