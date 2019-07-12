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
	 <style>
	    .refresh-icon {
                                background: #4A4B4B;
                                background: -webkit-linear-gradient(#4A4B4B 50%, #3A3B3B 50%);
                                background: -o-linear-gradient(#4A4B4B 50%, #3A3B3B 50%);
                                background: -moz-linear-gradient(#4A4B4B 50%, #3A3B3B 50%);
                                background: -ms-linear-gradient(#4A4B4B 50%, #3A3B3B 50%);
                                background: linear-gradient(#4A4B4B 50%, #3A3B3B 50%);
                                position: absolute;
                                top: 0;
                                color: white;
                                cursor: pointer
                            }
	 </style>
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
	    title: '<spring:message code="pdq.page.jtable_title"/>',
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
            title: '<spring:message code="pdq.page.title_deploy_id"/>',
            key: true,
                list: true,
                edit:false
            },
            processId: {
			title: '<spring:message code="pdq.page.title_process_id"/>',
			edit:false,

			},
		    deployStatusId: {
		    title: '<spring:message code="pdq.page.title_deploy_status"/>',
		    edit:false,
		    type: 'combobox',
		    options: '/mdrest/deploystatus/options'

		    },
		    tableInsertTs: {
		    title: '<spring:message code="pdq.page.title_insert_time"/>',
		    edit:false
		    },
		    tableStartTs: {
		    title: '<spring:message code="pdq.page.title_start_time"/>',
		    edit:false

		    },
		    tableEndTs: {
		    title: '<spring:message code="pdq.page.title_end_time"/>',
		    edit:false
		    },
		    deployScriptLocation: {
            title: '<spring:message code="pdq.page.title_location"/>',
            edit:false
            },
            userName: {
           title: '<spring:message code="pdq.page.title_username"/>',
           edit:false
           },
            processTypeId: {
		   title: '<spring:message code="pdq.page.title_process_type"/>',
		   edit:false,
		   type: 'combobox',
           options: '/mdrest/processtype/optionslist/'
		   },
		   busDomainId: {
		   title: '<spring:message code="pdq.page.title_app"/>',
		   type: 'combobox',
           options: '/mdrest/busdomain/options/',
		   edit:false
			 }
	    }
	    });
		    $('#Container').jtable('load');
	    });

	    function refreshPage(){

                      $('#Container').jtable('reload');
                    }
	</script>
    </head>
    <body>

    <section style="width:100%;text-align:center;">
	<div id="Container"></div>
    </section>

    <div id="refresh-icon" class="refresh-icon" style="left: 325px !important;">
        <button class="btn btn-default" type="submit" style="background-color: #c3beb5;" onClick="refreshPage()"><span class="glyphicon glyphicon-refresh" aria-hidden="true"></span> Refresh </button>
    </div>


</body>
</html>
