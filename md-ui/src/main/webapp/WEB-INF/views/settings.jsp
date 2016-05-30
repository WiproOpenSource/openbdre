<%@ taglib prefix="security"
	   uri="http://www.springframework.org/security/tags" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title><spring:message code="common.page.title_bdre_1"/></title>
	<style>
	#Settings{
		background-color: #F8F9FB;
		padding-top: 4%;
	}
	.btn-default{
		width: 20%;
		margin: 8px 0 16px 6px !important;
		padding-top: 6px 6px !important;;
		padding-bottom: 6px !important;
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
	<link rel="stylesheet" href="../css/jquery.steps.css" />
	<link rel="stylesheet" href="../css/jquery.steps.custom.css" />
	<link href="../css/bootstrap.custom.css" rel="stylesheet" type="text/css" />
	<!-- Include jTable script file. -->
	<script src="../js/jquery.min.js" type="text/javascript"></script>
    <script src="../js/bootstrap.js" type="text/javascript"></script>
	<script src="../js/jquery-ui-1.10.3.custom.js" type="text/javascript"></script>
	<script src="../js/jquery.jtable.js" type="text/javascript"></script>
    <script src="../js/jquery.steps.min.js"></script>
    <script src="../js/angular.min.js" type="text/javascript"></script>

	<script type="text/javascript">
	
	

	$(document).ready(function () {
		$('#Container').jtable({
        	title: 'General Configuration',
           		    paging: true,
           		    pageSize: 10,
           		    sorting: true,
           		    actions: {
           		    listAction: function (postData, jtParams) {
           		        console.log(postData);
           			    return $.Deferred(function ($dfd) {
           			    $.ajax({
           			    url: "/mdrest/genconfig",
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
           		    },
           		    fields: {
           		    	Groups: {
           		    		title: 'Click to expand',
           			    	width: '5%',
           			    	sorting: false,
           			    	edit: false,
           			    	create: false,
           			    	listClass: 'bdre-jtable-button',
           			    	display: function(item) {                         //Create an image that will be used to open child table

           			    	var $img = $('<img src="../css/images/three-bar.png" title="Clusters info" />'); //Open child table when user clicks the image
							$img.click(function() {
           				    	if( item.record.configGroup=='cluster.hive-address'){
           				      		console.log(item.record.configGroup);
           				            $('#Container').jtable('openChildTable',
            						  $img.closest('tr'),{
           		       	          		title: 'Clusters List',
           		       		      		paging: true,
           		       		      		pageSize: 10,
           		       		      		sorting: true,
           		       		      		actions: {
           		       		        		listAction: function (postData, jtParams) {
           		       		          			console.log(postData);
           		       			      			return $.Deferred(function ($dfd) {
           		       			        			$.ajax({
           		       			          				url: "/mdrest/genconfig/" + item.record.configGroup + "/?required=1",
           		       				      				type: 'GET',
           		       				      				data: postData,
           		       				      				dataType: 'json',
           		       				      				success: function (data) {
           		       				        				console.log(postData);
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
           		       			      				url: '/mdrest/hivemigration/insertcluster',
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
           		          				</security:authorize>
           		       				},
           		       				fields: {
           		       		    		Clusters: {
           		       		        		title: 'Click to expand',
           		       			    		width: '5%',
           		       			    		sorting: false,
           		       			    		edit: false,
           		       			    		create: false,
           		       			    		listClass: 'bdre-jtable-button',
           		       						display: function(cluster_item) {                         //Create an image that will be used to open child table

           	           			    			var $cluster_img = $('<img src="../css/images/three-bar.png" title="Clusters info" />'); //Open child table when user clicks the image

           	           				    		$cluster_img.click(function() {
           	           				    			$('#Container').jtable('openChildTable',
           	            							  $cluster_img.closest('tr'),{
           	           		       	          			title: 'Details of '+cluster_item.record.description,
           	           		       		      			paging: true,
           	           		       		      			pageSize: 10,
           	           		       		      			sorting: true,
           	           		       		      			actions: {        	           		       		    	  
           	           		       		 					listAction: function(postData) {
           	         					    					return $.Deferred(function($dfd) {
           	         					    						console.log(item.record.description);
           	         						    					$.ajax({
           	         						    						url: '/mdrest/hivemigration/cluster/' + cluster_item.record.description,
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
           	         						    		updateAction: function(postData) {
           	         						    			console.log(postData);
           	         							    		return $.Deferred(function($dfd) {
           	         							    			$.ajax({
           	         							    			url: '/mdrest/hivemigration/updatecluster',
           	         								    		type: 'POST',
           	         								    		data: postData + '&description=' + cluster_item.record.description,
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
           	           		       		    	  },
           	           		       		      fields:{
           	           		       		      	key: {
           	       						    		title: 'Type',
           	       						    		edit: true,
           	       									defaultValue: cluster_item.record.key,
           	       						  		},
           	       						    	defaultVal: {
           	       						    		title: 'Address',
           	       						    		edit: true,
           	       									defaultValue: cluster_item.record.defaultVal,
           	       						    	},
           	           		       		    }
           	           				  	},
           	           			 		function(data) { //opened handler
                  					    	data.childTable.jtable('load');
                  					    }
           	           				 );
           	           				  
   	           				    });
           	           			return $cluster_img;
           		       		}		    
           		       	},
           		       		    
           		       	nameNodeHostName: {
         					   	   title :'namenode hostname',
         						   key : true,
         						   list: false,
         						   create:true,
         						   edit: false,
         					   }, 
         					   nameNodePort: {
         							title: ' namenode port',
         								key : true,
         								list: false,
         								create:true,
         								edit: false,
         							}, 
         						jobTrackerHostName: {
         							title: 'jobtracker hostname',
         								key : true,
         								list : false,
         								create : true,
         								edit : false,
         							},
         						jobTrackerPort: {
         								title: 'jobtracker port',
         									list : false,
         									create : true,
         									edit : false,
         									key : true,
         								},
         						hiveHostName: {
         									title: 'hive hostname',
         									list: false,
         									create:true,
         									edit: false,
         									key : true,
         								},
         						clusterName: {
         								   title: 'cluster name',
         								   list: false,
         								   create:true,
         								   edit: false,
         								   key : true,
         								   },
           		       		    description: {
               			    	    key : true,
               				        list: true,
               				        create:false,
               				        edit: false,
               				        title: 'Clusters'
               			        }
               		  }
               	  },
           		  function(data) { //opened handler
        			 data.childTable.jtable('load');
        		  }
           		);          				    
           				    
           		}else
           				    $('#Container').jtable('openChildTable',
           					    $img.closest('tr'), {
           				    title: ' Details of ' + item.record.configGroup,
           					    paging: true,
           					    pageSize: 10,
           					    actions: {
           					    listAction: function(postData) {
           					    return $.Deferred(function($dfd) {
           					    console.log(item.record.description);
           						    $.ajax({
           						    url: '/mdrest/genconfig/'+item.record.configGroup,
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

           						    updateAction: function(postData) {
           						    console.log(postData);
           							    return $.Deferred(function($dfd) {
           							    $.ajax({
           							    url: '/mdrest/genconfig/admin/update',
           								    type: 'POST',
           								    data: postData + '&configGroup=' + item.record.configGroup,
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

           						    <security:authorize access="hasAnyRole('ROLE_ADMIN','ROLE_USER')">
                                               		    createAction:function (postData) {
                                               		    console.log(postData);
                                               			    return $.Deferred(function ($dfd) {
                                               			    $.ajax({
                                               			    url: '/mdrest/genconfig/admin/add',
                                               				    type: 'PUT',
                                               				    data: postData+'&configGroup=' + item.record.configGroup,
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
           					    formCreated: function (event, data)
                                			{
                                				if(data.formType=='edit') {
                                					$('#Edit-key').prop('readonly', true);
                                				}
                                			},
           					    fields: {
           						    key: {
           						    	title: 'Key',
           						    	edit: true,
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
                                   }
           					    }
           				    },
           					    function(data) { //opened handler

           					    data.childTable.jtable('load');
           					    }
           					);
           				    }); //Return image to show on the person row

           				    return $img;
           			    }
           		    },
           		    configGroup: {
           			    	key : true,
           				    list: true,
           				    create:false,
           				    edit: false,
           				    title: 'Config Group'

           			    }
           		    }
           	    });
           		    $('#Container').jtable('load');


});



	</script>

	</head>
    <body ng-controller="myCtrl">
    		<div class="page-header"><spring:message code="settings.page.panel_heading"/></div>
    		<section>
    			<section style="width:100%;text-align:center;">
                    	<div id="Container" ></div>
                </section>
    		</section>
    		<div id="div-dialog-warning"/>
</body>
</html>