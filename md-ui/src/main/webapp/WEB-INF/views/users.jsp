<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8" %>
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
		    title: 'Users List',
		    paging: true,
		    pageSize: 10,
		    sorting: true,
		    selecting: true, //Enable selecting
		    multiselect: false, //Allow multiple selecting
		    selectOnRowClick: true, //Enable this to only select using checkboxes
		    openChildAsAccordion: true,
		    actions: {
			listAction: function (postData, jtParams) {
			    console.log(postData);
			    return $.Deferred(function ($dfd) {
				$.ajax({
				    url: '/mdrest/users?page=' + jtParams.jtStartIndex + '&size='+jtParams.jtPageSize,
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
			createAction: function (postData) {
			    console.log(postData);
			    return $.Deferred(function ($dfd) {
				$.ajax({
				    url: '/mdrest/users',
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
				    url: '/mdrest/users',
				    type: 'POST',
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
			deleteAction: function (item) {
			    console.log(item);
			    return $.Deferred(function ($dfd) {
				$.ajax({
				    url: '/mdrest/users/' + item.username,
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

		    },
		    fields: {
			UserRoles: {                    

			    width: '5%',
			    sorting: false,
			    edit: false,
			    create: false,
			    listClass: 'bdre-jtable-button',
			                        display: function (item) {                         //Create an image that will be used to open child table
                                                        
				var $img = $('<img src="../css/images/three-bar.png" title="User roles info" />');                         //Open child table when user clicks the image
                                                        
				$img.click(function () {                            
				    $('#Container').jtable('openChildTable',                                     
					    $img.closest('tr'),                                      {                                        
					title: ' Roles of ' + item.record.username,
					                                        actions: {                                        
					    listAction: function (postData) {
						return $.Deferred(function ($dfd) {
						    console.log(item);

						    $.ajax({
							url: '/mdrest/userroles/' + item.record.username,
							type: 'GET',
							data: item,
							dataType: 'json',
							success: function (data) {
							    $dfd.resolve(data);
							},
							error: function () {
							    $dfd.reject();
							}
						    });
						    ;
						});
					    },
					        deleteAction: function (postData) {
						console.log(postData.processId);
						return $.Deferred(function ($dfd) {
						    $.ajax({
							url: '/mdrest/userroles/' + postData.userRoleId,
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
					    },
					    updateAction: function (postData) {
						console.log(postData);
						return $.Deferred(function ($dfd) {
						    $.ajax({
							url: '/mdrest/userroles',
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
					    createAction: function (postData) {
						console.log(postData);
						return $.Deferred(function ($dfd) {
						    $.ajax({
							url: '/mdrest/userroles',
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
					    }
					},
					    fields: {    
                                                                                                                       
					    userRoleId: {
						key: true,
						width: '5%',
						list: false,
						create: false,
						title: 'User Role Id'
					    },
					    username: {
						type: 'hidden',
						defaultValue: item.record.username,
					    },
					    role: {
						title: 'Role',
						type: 'combobox',
						options: {'ROLE_ADMIN': 'Administrator', 'ROLE_USER': 'Application Developer', 'ROLE_READONLY': 'Readonly User'}

					    },
					}
				    },
				    function (data) { //opened handler
                                                                                    
					data.childTable.jtable('load');                                    
				    });                        
				});                         //Return image to show on the person row
                                                        
				return $img;                    
			    }                
			},
			username: {
			    key: true,
			    list: true,
			    create: true,
			    edit: false,
			    title: 'Username'
			},
			password: {
				list: false,
				create: true,
				edit: true,
				type: 'password',
				title: 'Password'
			},
			enabled: {
			    title: 'Enabled',
			    type: "radiobutton",
			    edit: true,
			    options: {
				"1": "true",
				"0": "false"
			    },
			}
		    },
		    selectionChanged: function () {
			//Get all selected rows
			var $selectedRows = $('#Container').jtable('selectedRows');

			//    $selectedRows.each(function() {
			//        var record = $(this).data('record');
			//        alert('Username: ' + record.username);
			//    });
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