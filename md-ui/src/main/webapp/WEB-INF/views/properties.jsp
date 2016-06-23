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
	    title: '<spring:message code="properties.page.title_list"/>',
		    paging: true,
		    pageSize: 10,
		    sorting: true,
		    actions: {
		    listAction: function (postData, jtParams) {
		    console.log(postData);
			    return $.Deferred(function ($dfd) {
			    $.ajax({
			    url: '/mdrest/properties?page=' + jtParams.jtStartIndex + '&size='+jtParams.jtPageSize,
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
			    url: '/mdrest/properties',
				    type: 'PUT',
				    data: postData,
				    dataType: 'json',
				   success: function(data) {
                       if(data.Result == "OK") {
                           $dfd.resolve(data);
                       }
                       else
                       {
                        if(data.Message == "ACCESS DENIED")
                        {
                        alert(data.Message);
                        data.Result="OK";
                        $dfd.resolve(data);
                        }
                        else
                        $dfd.resolve(data);
                       }
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
				    url: '/mdrest/properties/' + item.processId,
					    type: 'DELETE',
					    data: item,
					    dataType: 'json',
					    success: function(data) {
                                    if(data.Result == "OK") {
                                        $dfd.resolve(data);
                                    }
                                    else
                                    {
                                     if(data.Message == "ACCESS DENIED")
                                     {
                                     alert(data.Message);
                                     data.Result="OK";
                                     $dfd.resolve(data);
                                     }
                                     else
                                     $dfd.resolve(data);
                                    }
                                },
					    error: function() {
					    $dfd.reject();
					    }

				    });
				    });
			    }</security:authorize>
		    },
		    fields: {

		    Properties: {
		    title: '<spring:message code="properties.page.title_img_click"/>',
			    width: '5%',
			    sorting: false,
			    edit: false,
			    create: false,
			    listClass: 'bdre-jtable-button',
			    display: function(item) {                         //Create an image that will be used to open child table

			    var $img = $('<img src="../css/images/three-bar.png" title=<spring:message code="properties.page.title_img"/> />'); //Open child table when user clicks the image

				    $img.click(function() {
				    $('#Container').jtable('openChildTable',
					    $img.closest('tr'), {
				    title:  '<spring:message code="properties.page.title_details"/>'+' ' + item.record.configGroup,
					    paging: true,
					    pageSize: 10,
					    actions: {
					    listAction: function(postData) {
					    return $.Deferred(function($dfd) {
					    console.log(item);
						    $.ajax({
						    url: '/mdrest/properties/' + item.record.processId,
							    type: 'GET',
							    data: item,
							    dataType: 'json',
							    success: function(data) {
                                            if(data.Result == "OK") {
                                                $dfd.resolve(data);
                                            }
                                            else
                                            {
                                             if(data.Message == "ACCESS DENIED")
                                             {
                                             alert(data.Message);
                                             data.Result="OK";
                                             $dfd.resolve(data);
                                             }
                                             else
                                             $dfd.resolve(data);
                                            }
                                        },
							    error: function() {
							    $dfd.reject();
							    }
						    }); ;
					    });
					    },
						    deleteAction: function(postData) {
						    console.log(postData.processId);
							    return $.Deferred(function($dfd) {
							    $.ajax({
							    url: '/mdrest/properties/' + item.record.processId + '/' + postData.key +'/',
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
							    url: '/mdrest/properties',
								    type: 'POST',
								    data: postData + '&processId=' + item.record.processId,
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
							    url: '/mdrest/properties',
								    type: 'PUT',
								    data: postData + '&processId=' + item.record.processId,
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

					    processId: {
					    key : true,
						    list: false,
						    create:false,
						    edit: true,
						    title: '<spring:message code="properties.page.title_process"/>',
						    defaultValue: item.record.processId,
					    },
						    configGroup: {
						    title: '<spring:message code="properties.page.title_cg"/>',
							    defaultValue: item.record.configGroup,
						    },
						    key: {
						    title: '<spring:message code="properties.page.title_key"/>',
							    key : true,
							    list: true,
							    create:true,
							    edit:false,
							    defaultValue: item.record.key,
						    },
						    value: {
						    title: '<spring:message code="properties.page.title_value"/>',
							    defaultValue: item.record.value,
						    },
						    description: {
						    title: '<spring:message code="properties.page.title_desc"/>',
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
			    title :'<spring:message code="properties.page.title_cg"/>',
				    key : true,
				    list: false,
				    create:true,
				    edit: false,
				    defaultValue:'<spring:message code="properties.page.devault_val_cg"/>'
			    }, key: {
		    title: '<spring:message code="properties.page.title_key"/>',
			    key : true,
			    list: false,
			    create:true,
			    edit: false,
			    defaultValue:'<spring:message code="properties.page.devault_val_key"/>'

		    }, value: {
		    title: '<spring:message code="properties.page.title_value"/>',
			    key : true,
			    list : false,
			    create : true,
			    edit : false,
			    defaultValue: '<spring:message code="properties.page.devault_val_value"/>'

		    },
			    description: {
			    title: '<spring:message code="properties.page.title_desc"/>',
				    list : false,
				    create : true,
				    edit : false,
				    key : true,
				    defaultValue:'<spring:message code="properties.page.devault_val_desc"/>'
			    },
			    processId: {
			    key : true,
				    list: true,
				    create:true,
				    edit: false,
				    title: '<spring:message code="properties.page.title_process"/>'

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
