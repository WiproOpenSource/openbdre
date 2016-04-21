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
	<link rel="stylesheet" href="../css/jquery.steps.css" />

	<!-- Include jTable script file. -->
	<script src="../js/jquery.min.js" type="text/javascript"></script>
    <script src="../js/bootstrap.js" type="text/javascript"></script>
	<script src="../js/jquery-ui-1.10.3.custom.js" type="text/javascript"></script>
	<script src="../js/jquery.jtable.js" type="text/javascript"></script>
    <script src="../js/jquery.steps.min.js"></script>
     <script src="../js/angular.min.js" type="text/javascript"></script>


	<script type="text/javascript">
         var map = new Object();
		var configGroupVal = '';
            var updateSettings = function (){

						$.ajax({
	        			                    type: "POST",
                                            url: "/mdrest/genconfig/admin",
                                            data: $('#SettingsForm').serialize()+"&configGroup="+configGroupVal,
                                            success: function(data) {
                                                if(data.Result == "OK") {
                                                    created = 1;
                                                    $("#div-dialog-warning").dialog({
                                                        title: "Success",
                                                        resizable: false,
                                                        height: 'auto',
                                                        modal: true,
                                                        buttons: {
                                                            "Ok": function() {
                                                                $(this).dialog("close");
                                                            }
                                                        }
                                                    }).text("Saved successfully.");

                                                }
                                                else{
                                                    $("#div-dialog-warning").dialog({
                                                        title: "Error",
                                                        resizable: false,
                                                        height: 'auto',
                                                        modal: true,
                                                        buttons: {
                                                            "Ok": function() {
                                                                $(this).dialog("close");
                                                            }
                                                        }
                                                    }).html(data.Message);
                                                }

                                            }
                    		});
                	}


	$(document).ready(function () {
    $('#configDropdown').change(function() {
		console.log($(this).val());
		var config=$(this).val();
		if(config == "cluster"){
		$("#clusterDiv").show();
            var cfgGrp = 'cluster';
                		$(document).ready(function () {
                	    $('#clusterDiv').jtable({
                	     title: 'Clusters',
                	      messages: {
                                 addNewRecord: 'Add new cluster'
                             },

                		    actions: {
                		    listAction: function (postData, jtParams) {
                		    console.log(postData);
                			    return $.Deferred(function ($dfd) {
                			    $.ajax({
                			    url:'/mdrest/genconfig/likegc/'+cfgGrp+'/?required=2',
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

                		    createAction:function (postData) {
                		    console.log(postData);
                			    return $.Deferred(function ($dfd) {
                			    $.ajax({
                			    url: '/mdrest/genconfig/',
                				    type: 'PUT',
                				    data: postData,
                				    dataType: 'json',
                				    success: function (data) {
                                    window.location.reload();
                				    $dfd.resolve(data);
                				    },
                				    error: function () {
                				    $dfd.reject();
                				    }
                			    });
                			    });

                		    }


                		    },fields: {


                                    nameNodeHostName: {
                                    title :'name_node_hostname',
                                        key : true,
                                        list: false,
                                        create:true,
                                        edit: false,

                                    }, nameNodePort: {
                              		    title: ' name_node_port',
                              			    key : true,
                              			    list: false,
                              			    create:true,
                              			    edit: false,

                              		    }, jobTrackerHostName: {
                              		    title: 'job_tracker_hostname',
                              			    key : true,
                              			    list : false,
                              			    create : true,
                              			    edit : false,


                              		    },
                              			    jobTrackerPort: {
                              			    title: 'job_tracker_port',
                              				    list : false,
                              				    create : true,
                              				    edit : false,
                              				    key : true,

                              			    },
                              			    hiveHostName: {
                              			        title: 'hive_server_hostname',
                              				    list: false,
                              				    create:true,
                              				    edit: false,
                              				    key : true,

                              			    },
                              			    clusterName: {
                                                title: 'cluster_name',
                                                list: false,
                                                create:true,
                                                edit: false,
                                                key : true,

                                            },
                              		    }
                		     });
                            		    $('#clusterDiv').jtable('load');
                            	    });




		configGroupVal = config;
		$.ajax({
        		type: "GET",
        		url: "/mdrest/genconfig/likegc/" + config + "/?required=1",
        		dataType: 'json',
        		success: function(data) {
        			var root = 'Records';
        			var div = document.getElementById('Settings');
        			var formHTML = '';
        			formHTML = formHTML + '<form role="form" id = "' + 'Settings' + 'Form" >';
        			console.log(data[root]);
        			$.each(data[root], function(i, v) {
        				formHTML = formHTML + '<div class="form-group" > <label for="' + v.key + '">' + v.key + '</label>';
        				formHTML = formHTML + '<span class="glyphicon glyphicon-question-sign" title="' + v.description + '"></span>';
        				formHTML = formHTML + '<input name="'+v.key+'" value="' + v.defaultVal + '" class="form-control" id="' + v.key + '"></div>';
        			});

        			    formHTML = formHTML + '<div id="editSettings"><button onclick="updateSettings()" id="editMdSetting" type="button" class="btn btn-primary">save</button></div>';
        			formHTML = formHTML + '</form>';
        			div.innerHTML = formHTML;
        			console.log(div);
        		}
        	});

        }
		else{
        $("#clusterDiv").hide();
		buildFormDisplay(config,'Settings');
		}
	});
    });

function buildFormDisplay(configGroup, typeDiv) {
	console.log('inside the function');
	$.ajax({
		type: "GET",
		url: "/mdrest/genconfig/" + configGroup + "/?required=1",
		dataType: 'json',
		success: function(data) {
			var root = 'Records';
			var div = document.getElementById(typeDiv);
			var formHTML = '';
			formHTML = formHTML + '<form role="form" id = "' + typeDiv + 'Form" >';
			console.log(data[root]);
			$.each(data[root], function(i, v) {
				formHTML = formHTML + '<div class="form-group" > <label for="' + v.key + '">' + v.key + '</label>';
				formHTML = formHTML + '<span class="glyphicon glyphicon-question-sign" title="' + v.description + '"></span>';
				formHTML = formHTML + '<input name="'+v.key+'" value="' + v.defaultVal + '" class="form-control" id="' + v.key + '"></div>';
			});

			    formHTML = formHTML + '<div id="editSettings"><button onclick="updateSettings()" id="editMdSetting" type="button" class="btn btn-primary">save</button></div>';
			formHTML = formHTML + '</form>';
			div.innerHTML = formHTML;
			console.log(div);
		}
	});
	return true;
}
	</script>

	</head>
    <body ng-controller="myCtrl">
    		<section>
    			<div id="config">
    				<div style="display: inline-block;" id="configDiv" align="center">
					<form id="configForm" >
					<span>Select Configuration</span>
    					<select id="configDropdown" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="false" aria-expanded="true">
    						<option value="" disabled selected>Select your option</option>
    						<option value="mdconfig">mdconfig</option>
    						<option value="imconfig">imconfig</option>
    						<option value="scripts_config">scriptsconfig</option>
    						<option value="cluster">cluster hive address</option>
						</select>
					</form>
					</div>
    			</div>
    			<div class="alert alert-info" role="alert" align="center" style="margin-top:20px" >
                                   <spring:message code="settings.page.configuration_alert"/>
                </div>

				<div id="clusterDiv" ></div>
				<div id="Settings" ></div>
    </section>
    <div id="div-dialog-warning"/>
</body>
</html>