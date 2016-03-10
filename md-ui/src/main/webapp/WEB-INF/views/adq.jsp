<%@ taglib prefix="security"
	   uri="http://www.springframework.org/security/tags" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	 pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Bigdata Ready Enterprise</title>
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
	    title: 'App Deployment Queue List',
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
			    url: '/mdrest/adq?page=' + jtParams.jtStartIndex + '&size='+jtParams.jtPageSize,
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
        				    url: '/mdrest/adq',
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
				    url: '/mdrest/adq/' + item.deploymentId,
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
            appDeploymentQueueId: {
            title: 'Deploy ID',
            key: true,
                list: true,
                edit:false
            },
            processId: {
			title: 'Process ID',
			edit:false,

			},
		    appDeploymentQueueStatus: {
		    title: 'App Deploy Status',
		    edit:false,


		    },
            userName: {
           title: 'User Name',
           edit:false
           },
		   appDomain: {
		   title: 'Application Domain',
		   edit:false
			 },
             appName: {
             title: 'Application Name',
             edit:false
             },
		   mergeButton: {

			 sorting: false,
			 width: '2%',
			 listClass: 'bdre-jtable-button',
			 create: false,
			 edit: false,
			 display: function(data) {
				 return '<span class="label label-primary" onclick="mergeApp(' + data.record.processId + ')">Merge</span> ';
			 },
		 },
		 rejectButton: {

         			 sorting: false,
         			 width: '2%',
         			 listClass: 'bdre-jtable-button',
         			 create: false,
         			 edit: false,
         			 display: function(data) {
         				 return '<span class="label label-primary" onclick="rejectApp(' + data.record.processId + ')">Reject</span> ';
         			 },
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