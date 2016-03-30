<!--
  ~ Copyright (c) 2014 Wipro Limited
  ~ All Rights Reserved
  ~
  ~ This code is protected by copyright and distributed under
  ~ licenses restricting copying, distribution and decompilation.
  -->
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="security"
	   uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>

	<script>
	  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
	  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
	  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
	  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');
	  //Please replace with your own analytics id
	  ga('create', 'UA-72345517-1', 'auto');
	  ga('send', 'pageview');
	</script>

    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    		<script src="../js/jquery.min.js"></script>
    		<link href="../css/jquery-ui-1.10.3.custom.css" rel="stylesheet">
    		<link href="../css/css/bootstrap.min.css" rel="stylesheet" />
    		<script src="../js/jquery-ui-1.10.3.custom.js"></script>
    		<script src="../js/jquery.steps.min.js"></script>
    		<link rel="stylesheet" href="../css/jquery.steps.css" />
    		<script src="../js/bootstrap.js" type="text/javascript"></script>
            <script src = "../js/jquery.fancytree.js" ></script >
            <link rel = "stylesheet" href = "../css/ui.fancytree.css" />
            <script src = "../js/jquery.fancytree.gridnav.js" type = "text/javascript" ></script >
            <script src = "../js/jquery.fancytree.table.js" type = "text/javascript" ></script >
    		<script src="../js/jquery.jtable.js" type="text/javascript"></script>
    		<script src="../js/angular.min.js" type="text/javascript"></script>
    		<link href="../css/jtables-bdre.css" rel="stylesheet" type="text/css" />


   	<script>
   var jsonObj = {"Result":"OK","Records":[],"Message":null,"TotalRecordCount":0,"Record":[]};
   var map = new Object();
   var createJobResult;
   var requiredProperties;
   var sourceFlag;
   var created = 0;

   var getGenConfigMap = function(cfgGrp){
       var map = new Object();
       $.ajax({
           type: "GET",
           url: "/mdrest/genconfig/"+cfgGrp+"/?required=2",
           dataType: 'json',
           async: false,
           success: function(data) {

               var root = 'Records';
               $.each(data[root], function(i, v) {
                   map[v.key] = v;
               });

           },
           error : function(data){
               console.log(data);
           }

       });
   return map;

   };
   </script>

   <script>
   function displayProcess(records) {
   	$('#Process').jtable({
   		title: 'Cluster to Cluster Hive Table Migration Processes',
   		paging: false,
   		sorting: false,
   		create: false,
   		edit: true,
   		actions: {
   			listAction: function() {
   				return records;
   			},
   			updateAction: function(postData) {

   				return $.Deferred(function($dfd) {
   					$.ajax({
   						url: '/mdrest/process',
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
   			}
   		},
   		fields: {
   			processId: {
   				key: true,
   				list: true,
   				create: false,
   				edit: false,
   				title: 'Id'
   			},
   			Properties: {
   				title: 'Properties',
   				width: '5%',
   				sorting: false,
   				edit: false,
   				create: false,
   				listClass: 'bdre-jtable-button',
   				display: function(item) { //Create an image that will be used to open child table

   					var $img = $('<span class="label label-primary">Show</span>'); //Open child table when user clicks the image

   					$img.click(function() {
   						$('#Process').jtable('openChildTable',
   							$img.closest('tr'), {
   								title: ' Properties of ' + item.record.processId,
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
   													$dfd.resolve(data);
   												},
   												error: function() {
   													$dfd.reject();
   												}
   											});;
   										});
   									},
   									deleteAction: function(postData) {
   										console.log(postData.processId);
   										return $.Deferred(function($dfd) {
   											$.ajax({
   												url: '/mdrest/properties/' + item.record.processId + '/' + postData.key,
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
   										key: true,
   										list: false,
   										create: false,
   										edit: true,
   										title: 'Process',
   										defaultValue: item.record.processId,
   									},
   									configGroup: {
   										title: 'Config Group',
   										defaultValue: item.record.configGroup,
   									},
   									key: {
   										title: 'Key',
   										key: true,
   										list: true,
   										create: true,
   										edit: false,
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
   			processName: {
   				title: 'Name'
   			},
   			tableAddTS: {
   				title: 'Add TS',
   				create: false,
   				edit: true,
   				list: false,
   				type: 'hidden'
   			},
   			description: {
   				title: 'Description',
   			},
   			batchPattern: {
   				title: 'Batch Mark',
   				list: false,
   				create: false,
   				edit: true,
   				type: 'hidden'

   			},
   			parentProcessId: {
   				title: 'Parent',
   				edit: true,
   				create: false,
   				list: false,
   				type: 'hidden'
   			},
   			canRecover: {
   				title: 'Restorable',
   				type: 'hidden',
   				list: false,
   				edit: true,
   			},
   			nextProcessIds: {
   				title: 'Next',
   				list: false,
   				edit: true,
   				type: 'hidden'

   			},
   			enqProcessId: {
   				title: 'Enqueuer',
   				list: false,
   				edit: true,
   				type: 'hidden',
   			},
   			busDomainId: {
   				title: 'Application',
   				list: false,
   				edit: true,
   				type: 'combobox',
   				options: '/mdrest/busdomain/options/',
   			},
   			processTypeId: {
   				title: 'Type',
   				edit: true,
   				type: 'hidden',
   				options: '/mdrest/processtype/optionslist'

   			},
   			ProcessPipelineButton: {
   				title: 'Pipeline',
   				sorting: false,
   				width: '2%',
   				listClass: 'bdre-jtable-button',
   				create: false,
   				edit: false,
   				display: function(data) {
   					return '<span class="label label-primary" onclick="fetchPipelineInfo(' + data.record.processId + ')">Display</span> ';
   				},
   			}
   		}
   	});
   	$('#Process').jtable('load');
  }
  </script>

  <script>
  var created = 0;
  var wizard = null;
  wizard = $(document).ready(function() {
  	$("#bdre-data-migration").steps({
  		headerTag: "h3",
  		bodyTag: "section",
  		transitionEffect: "slideLeft",
  		stepsOrientation: "vertical",
  		enableCancelButton: true,
  		onStepChanging: function(event, currentIndex, newIndex) {
  			console.log(currentIndex + 'current ' + newIndex );

  			return true;
        },
  		onStepChanged: function(event, currentIndex, priorIndex) {


  			console.log(currentIndex + " " + priorIndex);

                formIntoMap('srcEnv_', 'processDetailsForm');

				 formIntoMap('srcDB_', 'srcDBForm');

				 formIntoMap('tables_','tablesForm');

				 formIntoMap('destEnv_','destEnvForm');

            	$('#createjobs').on('click', function(e) {
                        $.ajax({
                            type: "POST",
                            url: "/mdrest/hivemigration/createjob",
                            data: jQuery.param(map),
                            success: function(data) {
                                if(data.Result == "OK") {
                                    created = 1;
                                    $("#div-dialog-warning").dialog({
                                        title: "",
                                        resizable: false,
                                        height: 'auto',
                                        modal: true,
                                        buttons: {
                                            "Ok": function() {
                                                $(this).dialog("close");
                                            }
                                        }
                                    }).text("Jobs successfully created.");
                                    createJobResult = data;
                                    displayProcess(createJobResult);
                                }
                                console.log(createJobResult);
                            }

                        });
                    return false;
                    });
  			return true;
  		},
  		onFinished: function(event, currentIndex) {
  			if(created == 1) {
  				location.href = '<c:url value="/pages/process.page"/>';
  			} else {
  				$("#div-dialog-warning").dialog({
  					title: "",
  					resizable: false,
  					height: 'auto',
  					modal: true,
  					buttons: {
  						"Ok": function() {
  							$(this).dialog("close");
  						}
  					}
  				}).text("Jobs have not been created.");
  			}
  		},
  		onCanceled: function(event) {
  			location.href = '<c:url value="/pages/hivetablemigration.page"/>';
  		}
  	});
  });


  </script>

  		<script>
                  var app = angular.module('myApp', []);
                  app.controller('myCtrl', function($scope) {
                      $scope.srcEnvs= getGenConfigMap('src_Env');
                      $scope.databases= {};
                       $.ajax({
                            url: '/mdrest/hivemigration/databases/',
                                type: 'GET',
                                dataType: 'json',
                                async: false,
                                success: function (data) {
                                    $scope.databases = data;
                                },
                                error: function () {
                                    alert('database danger');
                                }
                            });
                       $scope.tables= {};
                         $.ajax({
                              url: '/mdrest/hivemigration/tables/',
                                  type: 'GET',
                                  dataType: 'json',
                                  async: false,
                                  success: function (data) {
                                      $scope.tables = data;
                                  },
                                  error: function () {
                                      alert('table danger');
                                  }
                              });
                      $scope.formatMap=null;
                      $scope.busDomains = {};
                      $.ajax({
                      url: '/mdrest/busdomain/options/',
                          type: 'POST',
                          dataType: 'json',
                          async: false,
                          success: function (data) {
                              $scope.busDomains = data;
                          },
                          error: function () {
                              alert('danger');
                          }
                      });
                  });
          </script>

        <script>
                       function formIntoMap(typeProp, typeOf) {
                       	var x = '';
                       	x = document.getElementById(typeOf);
                       	console.log(x);
                       	var text = "";
                       	var i;
                       	for(i = 0; i < x.length; i++) {
                       		map[typeProp + x.elements[i].name] = x.elements[i].value;
                       	}
                       }
        </script>

    </head>


    <body ng-app="myApp" ng-controller="myCtrl">
        <div id="bdre-data-migration" ng-controller="myCtrl">
                <h3>Source Environment</h3>
                            <section>
                              <form class="form-horizontal" role="form" id="processDetailsForm">
                                  <div id="processDetails">

                                            <!-- btn-group -->
                                            <div id="process">
                                            <div class="form-group">
                                                            <label class="control-label col-sm-2" for="processName">Process Name:</label>
                                                            <div class="col-sm-10">
                                                                <input type="text" class="form-control"  id="processName" name="processName" placeholder="Enter Process Name" value="" required>
                                                            </div>
                                                        </div>
                                                <div class="form-group">
                                                    <label class="control-label col-sm-2" for="srcEnv">Source Environment:</label>
                                                    <div class="col-sm-10">
                                                        <select class="form-control" id="srcEnv" name="srcEnv" >
                                                            <option ng-repeat="srcEnv in srcEnvs" value="{{srcEnv.defaultVal}}" name="srcEnv">{{srcEnv.value}}</option>

                                                        </select>
                                                    </div>
                                                </div>
                                            </div>
                                            <!-- /btn-group -->
                                        </div>
                                        </form>
                            </section>
                 <h3>Source Database</h3>
                <section>
                          <form class="form-horizontal" role="form" id="srcDBForm">
                              <div id="srcDBDiv">
                                 <div class="form-group">
                                    <label class="control-label col-sm-2" for="srcDB">Select a database:</label>
                                      <div class="col-sm-10">
                                        <select class="form-control" id="srcDB" name="srcDB" >
                                           <option ng-repeat="srcDB in databases.Options" value="{{srcDB.Value}}" name="srcDB">{{srcDB.DisplayText}}</option>
                                        </select>
                                      </div>
                                  </div>

                          </form>
                </section>
			<h3>Tables</h3>
			<section>
			  <form class="form-horizontal" role="form" id="tablesForm">
                  <div id="tablesDiv">
                     <div class="form-group">
                        <label class="control-label col-sm-2" for="tabl">Select Table:</label>
                          <div class="col-sm-10">
                            <select class="form-control" id="tabl" name="tabl" >
                               <option ng-repeat="tabl in tables.Options" value="{{tabl.Value}}" name="tabl">{{tabl.DisplayText}}</option>
                            </select>
                          </div>
                      </div>

              </form>

             </section>

             <h3>Destination Environment</h3>
             <section>
             <form class="form-horizontal" role="form" id="destEnvForm">
				   <div id="fileFormatDiv">
								 <div class="form-group">
									 <label class="control-label col-sm-2" for="destEnv">Select Destination Environment:</label>
									 <div class="col-sm-10">
										 <select class="form-control" id="destEnv" name="destEnv" >
											 <option ng-repeat="destEnv in srcEnvs" value="{{destEnv.defaultVal}}" name="destEnv">{{destEnv.value}}</option>
										 </select>
								     </div>
						 </div>
				</form>

              </section>
            <h3>Confirm</h3>
             <section>
               <div id="Process">
               		<button id="createjobs" type="button" class="btn btn-primary btn-lg">Create Jobs</button>
               	</div>
             </section>

             <div style="display:none" id="div-dialog-warning">
             			<p><span class="ui-icon ui-icon-alert" style="float:left;"></span></p>
             		</div>

    </body>
</html>