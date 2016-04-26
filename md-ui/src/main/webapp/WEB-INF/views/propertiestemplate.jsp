<%@ taglib prefix="security"
	   uri="http://www.springframework.org/security/tags" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
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
	    title: 'Property Template List',
		    paging: true,
		    pageSize: 10,
		    sorting: true,
		    actions: {
		    listAction: function (postData, jtParams) {
		    console.log(postData);
			    return $.Deferred(function ($dfd) {
			    $.ajax({
			    url: '/mdrest/propertiestemplate?page=' + jtParams.jtStartIndex + '&size='+jtParams.jtPageSize,
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
			    url: '/mdrest/propertiestemplate',
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
			    deleteAction: function(item) {
			    console.log(item);
				    return $.Deferred(function($dfd) {
				    $.ajax({
				    url: '/mdrest/propertiestemplate/' + item.processTemplateId,
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
			    }</security:authorize>
		    },
		    fields: {

		    PropertiesTemplate: {
		    title: 'Click to expand',
			    width: '5%',
			    sorting: false,
			    edit: false,
			    create: false,
			    listClass: 'bdre-jtable-button',
			    display: function(item) {                         //Create an image that will be used to open child table

			    var $img = $('<img src="../css/images/three-bar.png" title="Properties Template info" />'); //Open child table when user clicks the image

				    $img.click(function() {
				    $('#Container').jtable('openChildTable',
					    $img.closest('tr'), {
				    title: ' Properties of ' + item.record.processTemplateId,
					    paging: true,
					    pageSize: 10,
					    actions: {
					    listAction: function(postData) {
					    return $.Deferred(function($dfd) {
					    console.log(item);
						    $.ajax({
						    url: '/mdrest/propertiestemplate/' + item.record.processTemplateId,
							    type: 'GET',
							    data: item,
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
						    deleteAction: function(postData) {
						    console.log(postData.processTemplateId);
							    return $.Deferred(function($dfd) {
							    $.ajax({
							    url: '/mdrest/propertiestemplate/' + item.record.processTemplateId + '/' + postData.key,
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
						    updateAction: function(postData) {
						    console.log(postData);
							    return $.Deferred(function($dfd) {
							    $.ajax({
							    url: '/mdrest/propertiestemplate',
								    type: 'POST',
								    data: postData + '&processTemplateId=' + item.record.processTemplateId,
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
							    url: '/mdrest/propertiestemplate',
								    type: 'PUT',
								    data: postData + '&processTemplateId=' + item.record.processTemplateId,
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

					    processTemplateId: {
					    key : true,
						    list: false,
						    create:false,
						    edit: true,
						    title: 'Process',
						    defaultValue: item.record.processTemplateId,
					    },
						    configGroup: {
						    title: 'Config Group',
							    defaultValue: item.record.configGroup,
						    },
						    key: {
						    title: 'Key',
							    key : true,
							    list: true,
							    create:true,
							    edit:true,
							    defaultValue: item.record.key,
						    },
						    value: {
						    title: 'Value',
							    defaultValue: item.record.value,
						    },
						    description: {
						    title: 'Description',
							    defaultValue: item.record.description,
						    },
					    }
				    },
					    function(data) { //opened handler

					    data.childTable.jtable('load');
					    });
				    }); //Return image to show on the person row

				    return $img;
			    }
		    },
			    configGroup: {
			    title :'Config Group',
				    key : true,
				    list: false,
				    create:true,
				    edit: false,
				    defaultValue:"NewConfig"
			    }, key: {
		    title: 'Key',
			    key : true,
			    list: false,
			    create:true,
			    edit: false,
			    defaultValue:"NewKey"

		    }, value: {
		    title: 'Value',
			    key : true,
			    list : false,
			    create : true,
			    edit : false,
			    defaultValue: "NewValue"

		    },
			    description: {
			    title: 'Description',
				    list : false,
				    create : true,
				    edit : false,
				    key : true,
				    defaultValue:"NewDescription"
			    },
			    processTemplateId: {
			    key : true,
				    list: true,
				    create:true,
				    edit: false,
				    title: 'ProcessTemplate'

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
</html>​
