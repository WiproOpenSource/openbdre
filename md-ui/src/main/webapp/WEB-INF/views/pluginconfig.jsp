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
	    title: 'Plugin Config List',
		    paging: true,
		    pageSize: 10,
		    sorting: true,
		    actions: {
		    listAction: function (postData, jtParams) {
		    console.log(postData);
			    return $.Deferred(function ($dfd) {
			    $.ajax({
			    url: '/mdrest/pluginconfig?page=' + jtParams.jtStartIndex + '&size='+jtParams.jtPageSize,
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
			    url: '/mdrest/pluginconfig',
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
				    url: '/mdrest/pluginconfig/' + item.pluginUniqueId,
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
		    title: 'Click to expand',
			    width: '5%',
			    sorting: false,
			    edit: false,
			    create: false,
			    listClass: 'bdre-jtable-button',
			    display: function(item) {                         //Create an image that will be used to open child table

			    var $img = $('<img src="../css/images/three-bar.png" title="Properties info" />'); //Open child table when user clicks the image

				    $img.click(function() {
				    $('#Container').jtable('openChildTable',
					    $img.closest('tr'), {
				    title: ' Plugin Config of ' + item.record.pluginUniqueId,
					    paging: true,
					    pageSize: 10,
					    actions: {
					    listAction: function(postData) {
					    return $.Deferred(function($dfd) {
					    console.log(item);
						    $.ajax({
						    url: '/mdrest/pluginconfig/' + item.record.pluginUniqueId,
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
						    console.log(postData);
							    return $.Deferred(function($dfd) {
							    $.ajax({
							    url: '/mdrest/pluginconfig/' + item.record.pluginUniqueId + '/' + postData.pluginKey +'/',
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
							    url: '/mdrest/pluginconfig',
								    type: 'POST',
								    data: postData + '&processId=' + item.record.pluginUniqueId,
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
						    createAction:function(postData) {
                                                    console.log(postData);
                                                        return $.Deferred(function($dfd) {
                                                        $.ajax({
                                                        url: '/mdrest/pluginconfig',
                                                            type: 'PUT',
                                                            data: postData + '&processId=' + item.record.pluginUniqueId,
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
					    pluginUniqueId: {
					        key : false,
						    list: true,
						    create:true,
						    edit: true,
						    title: 'pluginUniqueId',
						    defaultValue: item.record.pluginUniqueId,
					    },
						    configGroup: {
						    title: 'Config Group',
							    defaultValue: item.record.configGroup,
						    },
						    pluginKey: {
						      key : ture,
						       title: 'Key',
							    list: true,
							    create:true,
							    edit:false,
							    defaultValue: item.record.configKey,
						    },
						    pluginValue: {
						    title: 'Value',
							    defaultValue: item.record.configValue,
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
			    },
			     pluginKey: {
		        title: 'Key',
			    key : true,
			    list: false,
			    create:true,
			    edit: false,

		    },
		     pluginValue: {
		    title: 'Value',
			    key : true,
			    list : false,
			    create : true,
			    edit : false,

		    },
			    pluginUniqueId: {
			    title: 'plugin Unique Id',
				    list : true,
				    create : true,
				    edit : false,
				    key : true,
				    defaultValue:"pluginUniqueId"
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
