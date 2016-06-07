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
	    title: 'General configuration List',
		    paging: true,
		    pageSize: 10,
		    sorting: true,
		    actions: {
		    listAction: function (postData, jtParams) {
		    console.log(postData);
			    return $.Deferred(function ($dfd) {
			    $.ajax({
			    url: '/mdrest/genconfig?page=' + jtParams.jtStartIndex + '&size='+jtParams.jtPageSize,
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
			    url: '/mdrest/genconfig/admin/add/',
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
				    url: '/mdrest/genconfig/' + item.configGroup,
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
				    title:  ' Details of ' + item.record.configGroup,
					    paging: true,
					    pageSize: 10,
					    actions: {
					    listAction: function(postData) {
					    return $.Deferred(function($dfd) {
					    console.log(item);
						    $.ajax({
						    url: '/mdrest/genconfig/list/' + item.record.configGroup,
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
							    url: '/mdrest/genconfig/' + item.record.configGroup + '/' + postData.key +'/',
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
							    url: '/mdrest/genconfig/admin/update',
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
							    url: '/mdrest/genconfig/admin/add/',
								    type: 'PUT',
								    data: postData + '&configGroup=' + item.record.configGroup,
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
                                configGroup: {
                                                    title :'Config Group',
                                                    key : true,
                                                    list: false,
                                                    create:false,
                                                    edit: false,
                                                },
                                    key: {
                                        title: 'Key',
                                        key : true,
                                        list: true,
                                        create:true,
                                        edit:false,
                                        defaultValue: item.record.key,
                                    },
                                    defaultVal: {
                                        title: 'Default Value',
                                        edit: true,
                                    },
                                    value: {
                                       title: 'Value',
                                       edit: true,
                                    },
                                    description: {
                                       title: 'Description',
                                       edit: true,
                                    },
                                    type: {
                                        title: 'Type',
                                        edit: true,
                                    },
                                    enabled: {
                                       title: 'IsEnabled?',
                                       edit: true,
                                  },
                                  required: {
                                     title: 'Required?',
                                     edit: true,
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

			    key: {
			        key : true,
			        list: false,
                    title: 'Key',
                    edit: true,
                   create:true
                },
                defaultVal: {
                    key : true,
                    list: false,
                    title: 'Default Value',
                    edit: true,
                    create:true
                },
                value: {
                   key : true,
                   list: false,
                   title: 'Value',
                   edit: true,
                   create:true
                },
                description: {
                   key : true,
                   list: false,
                   title: 'Description',
                   edit: true,
                    create:true

                },
                type: {
                    key : true,
                    list: false,
                    title: 'Type',
                    edit: true,
                    create:true

                },
                enabled: {
                   key : true,
                   list: false,
                   title: 'IsEnabled?',
                   create:true,
                   edit: true
               },
               required: {
                     key : true,
                    list: false,
                    title: 'Required?',
                    edit: true,
                    create:true

               },
               configGroup: {
                    title :'Config Group',
                    key : true,
                    list: true,
                    create:true,
                    edit: false
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
