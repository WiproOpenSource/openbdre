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
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>


    		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    		<script src="../js/jquery.min.js"></script>
    		<link href="../css/jquery-ui-1.10.3.custom.css" rel="stylesheet">
    		<link href="../css/css/bootstrap.min.css" rel="stylesheet" />
    		<script src="../js/jquery-ui-1.10.3.custom.js"></script>
    		<script src="../js/jquery.steps.min.js"></script>
    		<link rel="stylesheet" href="../css/jquery.steps.css" />
    		<link rel="stylesheet" href="../css/jquery.steps.custom.css" />
    		<link href="../css/bootstrap.custom.css" rel="stylesheet" type="text/css" />
    		<script src="../js/bootstrap.js" type="text/javascript"></script>
            <script src = "../js/jquery.fancytree.js" ></script >
            <link rel = "stylesheet" href = "../css/ui.fancytree.css" />
            <script src = "../js/jquery.fancytree.gridnav.js" type = "text/javascript" ></script >
            <script src = "../js/jquery.fancytree.table.js" type = "text/javascript" ></script >
    		<script src="../js/jquery.jtable.js" type="text/javascript"></script>
    		<script src="../js/angular.min.js" type="text/javascript"></script>
    		<link href="../css/jtables-bdre.css" rel="stylesheet" type="text/css" />

	<script>
    	  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
    	  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
    	  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
    	  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');
    	  //Please replace with your own analytics id
    	  ga('create', 'UA-72345517-1', 'auto');
    	  ga('send', 'pageview');
    	</script>

	<script >
		function fetchPipelineInfo(pid){
			location.href = '<c:url value="/pages/lineage.page?pid="/>' + pid;
		}
		</script >


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
     var databases;
     var srcEnvi;
     var srcName;
     var srcDatabase;
     var srcTable;
     var destEnvi;
     var destDatabase;
     var str;
     var destName;
     function srcEnvVar(){

         str = $('#srcEnv').val();
          console.log("Str "+str);
           var pos = str.indexOf(',"-%%-",');
           srcEnvi = str.slice(0,pos);
          srcName = str.slice(pos+8);
          console.log("SrcEnvi is " +srcEnvi);
          console.log("SrcName is " +srcName);
          $('#showSrcEnv').val(srcEnvi);
          }
     function srcDBVar(){
               srcDatabase = $('#srcDB').val();
               $('#showSrcDB').val(srcDatabase);
               }
     function srcTableVar(){
               srcTable = checkedTables;
               $('#showSrcTables').val(srcTable);
               }
     function destEnvVar(){
               var str = $('#destEnv').val();
               console.log("Str "+str);
               var pos = str.indexOf(',"-%%-",');
               destEnvi = str.slice(0,pos);
               destName = str.slice(pos+8);
               $('#showDestEnv').val(destEnvi);
               }
    function destDBVar(){
              destDatabase = $('#destDB').val();
              $('#showDestDB').val(destDatabase);
              }

    function dbs(){

    }



         </script>
  <script>
var ips= new Object();;
var nameNodeIp;
var jobTrackerIp;
var destnameNodeIp;
var destjobTrackerIp;
  var created = 0;
   var checkedTables = [];
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
            if(currentIndex == 0 && newIndex == 1 && document.getElementById('processName').value == "" && document.getElementById('processDesc').value == "") {
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
            				}).text("Please Enter Process Name and Description");
            				return false;
            			}
  			return true;
        },
  		onStepChanged: function(event, currentIndex, priorIndex) {
  			console.log(currentIndex + " " + priorIndex);

  			if(currentIndex == 1 && priorIndex == 0) {
  			 formIntoMap('srcEnv_', 'processDetailsForm');
            srcEnvVar();

            var nameNode =  getGenConfigMap('cluster.nn-address');
            for(var i in nameNode){
                if(i.valueOf() == srcName.valueOf())
                    nameNodeIp = nameNode[i].defaultVal;
            }

             var jobTracker =  getGenConfigMap('cluster.jt-address');
                        for(var i in jobTracker){
                            if(i.valueOf() == srcName.valueOf())
                                jobTrackerIp = jobTracker[i].defaultVal;
                        }
                console.log("namenode "+nameNodeIp);
                console.log("jobTrac "+jobTrackerIp);

                        console.log(srcEnvi);
                        var urlEnv = "/mdrest/hivemigration/databases/"+srcEnvi;
                        console.log("url:"+urlEnv);
                              var  databases=[];
                                $.ajax({
                                     url: urlEnv,
                                         type: 'GET',
                                         dataType: 'json',
                                         async: false,
                                         success: function (data) {
                                         databases=data;
                                         },
                                         error: function () {
                                             alert(' src database danger');
                                         }

                                     });

                                     var str;
                                         var x = document.getElementById("srcDB");
                                     for( str in databases["Options"]){
                                         var option = document.createElement("option");
                                         option.text = databases["Options"][str].Value;
            							 option.value = databases["Options"][str].Value;
                                         x.appendChild(option);
                                     }

            }
				 if(currentIndex == 2 && priorIndex == 1) {
				 formIntoMap('srcDB_', 'srcDBForm');
				 srcDBVar();

				   				var tables= [];
                                          $.ajax({
                                               url: '/mdrest/hivemigration/tables/'+srcEnvi+"/"+srcDatabase,
                                                   type: 'GET',
                                                   dataType: 'json',
                                                   async: false,
                                                   success: function (data) {
                                                      tables = data;
                                                   },
                                                   error: function () {
                                                       alert('table danger');
                                                   }
                                               });
                                 var a;
										 var objDiv = document.getElementById("srctables");

                                        for(a in tables["Options"]){
                                                var item = document.createElement("input");
                                                item.type = "checkbox";
                                                item.name = "tablesGrp";
                                                item.id = "tablesDiv";
                                                item.value = tables["Options"][a].Value;

                                                var objTextNode1 = document.createTextNode(tables["Options"][a].Value);

                                                var objLabel = document.createElement("label");
                                                objLabel.htmlFor = item.id;
                                                objLabel.appendChild(item);
                                                objLabel.appendChild(objTextNode1);

                                                objDiv.appendChild(objLabel);
                                                objDiv.appendChild(document.createElement('br'));
                                        }
				 }

				 if(currentIndex == 3 && priorIndex == 2) {
                     var inputs = document.forms["tablesForm"].elements;
                     var cbs = [];

                     var i;

                     for ( i = 0; i < inputs.length; i++) {
                       if (inputs[i].type == "checkbox") {
                         cbs.push(inputs[i]);
                         if (inputs[i].checked) {
                           checkedTables.push(inputs[i].value);
                            }
                         }
                       }
                     var nbCbs = cbs.length;
                     var nbChecked = checkedTables.length;

				 srcTableVar();
				 }

				 if(currentIndex == 4 && priorIndex == 3) {
                 formIntoMap('destEnv_','destEnvForm');
                 destEnvVar();

                  var destnameNode =  getGenConfigMap('cluster.nn-address');
                             for(var i in destnameNode){
                                 if(i.valueOf() == destName.valueOf())
                                     destnameNodeIp = destnameNode[i].defaultVal;
                             }

                              var destjobTracker =  getGenConfigMap('cluster.jt-address');
                                         for(var i in destjobTracker){
                                             if(i.valueOf() == destName.valueOf())
                                                 destjobTrackerIp = destjobTracker[i].defaultVal;
                                         }
                                 console.log("namenode "+nameNodeIp);
                                 console.log("jobTrac "+jobTrackerIp);

                 		 var destdatabases= [];
						  $.ajax({
							   url: '/mdrest/hivemigration/destdatabases/'+destEnvi,
								   type: 'GET',
								   dataType: 'json',
								   async: false,
								   success: function (data) {
									   destdatabases = data;
								   },
								   error: function () {
									   alert('dest database danger');
								   }
							   });
						 var str;
                             var x = document.getElementById("destDB");
                         for( str in destdatabases["Options"]){
                             var option = document.createElement("option");
                             option.text = destdatabases["Options"][str].Value;
                             option.value = destdatabases["Options"][str].Value;
                             x.appendChild(option);
                         }
                 }

				if(currentIndex == 5 && priorIndex == 4) {
				formIntoMap('destDB_', 'destDBForm');
				destDBVar();

                map["scrNameNode"] = nameNodeIp;
                map["srcJobTracker"]=jobTrackerIp;
                map["destNameNode"] = destnameNodeIp;
                map["destjobTracker"]=destjobTrackerIp;

            	$('#createjobs').on('click', function(e) {
            	    console.log("checked tables"+checkedTables);
                        $.ajax({
                            type: "POST",
                            url: "/mdrest/hivemigration/createjobs/"+checkedTables,
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
                 }
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
                      $scope.srcEnvs= getGenConfigMap('cluster.hive-address');

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


    <body ng-app="myApp">

    <div class="page-header"><spring:message code="hivetablemigration.page.panel_heading"/></div>
    <div class="alert alert-info" role="alert">
           <spring:message code="hivetablemigration.page.alert_info_outer_heading"/>
    </div>
   	


        <div id="bdre-data-migration">
        <h3><div class="number-circular">1</div><spring:message code="hivetablemigration.page.h3_div_1"/></h3>

                            <section>
                              <form class="form-horizontal" role="form" id="processDetailsForm">
                                  <div id="processDetails" ng-controller="myCtrl">

                                            <!-- btn-group -->
                                            <div id="process">
                                            <div class="form-group">
                                                            <label class="control-label col-sm-2" for="processName"><spring:message code="hivetablemigration.page.form_procname"/></label>
                                                            <div class="col-sm-10">
                                                                <input type="text" class="form-control"  id="processName" name="processName" placeholder="Enter Process Name" value="" required>
                                                            </div>
                                                        </div>
																<div class="form-group">
                                                                    <label class="control-label col-sm-2" for="processDesc"><spring:message code="hivetablemigration.page.form_procdesc"/></label>
                                                                    <div class="col-sm-10">
                                                                        <input type="text" class="form-control"  id="processDesc" name="processDesc" placeholder="Enter Process Description" value="" required>
                                                                    </div>

                                                               </div>

                                                                <div class="form-group">
                                                               <label class="control-label col-sm-2" for="srcEnv"><spring:message code="hivetablemigration.page.form_src_env"/></label>
                                                               <div class="col-sm-10">
                                                                   <select class="form-control" id="srcEnv" name="srcEnv" >
                                                                       <option ng-repeat="srcEnv in srcEnvs" value='{{srcEnv.defaultVal}},"-%%-",{{srcEnv.description}}'  label="{{srcEnv.description}} ">{{srcEnv.description}}</option>

                                                                   </select>
                                                               </div>
                                                           </div>


                                                    <div class="form-group">
                                                            <label class="control-label col-sm-2" for="busDomainId"><spring:message code="hivetablemigration.page.form_bus_domain_id"/></label>
                                                            <div class="col-sm-10">
                                                                <select class="form-control" id="busDomainId" name="busDomainId">
                                                                    <option ng-repeat="busDomain in busDomains.Options" value="{{busDomain.Value}}" name="busDomainId">{{busDomain.DisplayText}}</option>

                                                                </select>
                                                            </div>
                                                        </div>
                                                        <div class="clearfix"></div>

                                           
                                            <!-- /btn-group -->
                                        </div>
                                        </form>
                            </section>
              <h3><div class="number-circular">2</div><spring:message code="hivetablemigration.page.h3_div_2"/></h3>

              <section>

                        <form class="form-horizontal" role="form" id="srcDBForm">
                            <div id="srcDBDiv">
                               <div class="form-group">
                                  <label class="control-label col-sm-2" for="srcDB"><spring:message code="hivetablemigration.page.form_src_db"/></label>
                                    <div class="col-sm-10">
                                      <select class="form-control" id="srcDB" name="srcDB" >
                                      </select>
                                    </div>
                                </div>
                                <div class="clearfix"></div>

                        </form>
              </section>

	    <h3><div class="number-circular">3</div><spring:message code="hivetablemigration.page.h3_div_3"/></h3>
			<section>
			 <label class="control-label col-sm-2" for="tabl"><spring:message code="hivetablemigration.page.form_src_tables"/></label>
			  <form class="form-horizontal"  id="tablesForm">
                          <div id ="srctables" class="col-sm-10">

                          </div>

              </form>

             </section>

         <h3><div class="number-circular">4</div><spring:message code="hivetablemigration.page.h3_div_4"/></h3>
             <section>
             <form class="form-horizontal" role="form" id="destEnvForm">

				   <div id="fileFormatDiv" ng-controller="myCtrl">
								 <div class="form-group">
									 <label class="control-label col-sm-2" for="destEnv"><spring:message code="hivetablemigration.page.form_dest_env"/></label>
									 <div class="col-sm-10">
										 <select class="form-control" id="destEnv" name="destEnv" >
                                               <option ng-repeat="destEnv in srcEnvs" value='{{destEnv.defaultVal}},"-%%-",{{destEnv.description}}'  label="{{destEnv.description}} ">{{destEnv.description}}</option>
										 </select>
								     </div>
						 </div>
						 <div class="clearfix"></div>

						  <div class="form-group">
                                                             <label class="control-label col-sm-2" for="instexecId"><spring:message code="hivetablemigration.page.form_inst_exec"/></label>
                                                                  <div class="col-sm-10">
                                                                      <input type="text" class="form-control"  id="instexecId" name="instexecId"  value="instanceExecId" required>
                                                                  </div>
                                                                  </div>
                                                                  <div class="clearfix"></div>

				</form>

              </section>
         <h3><div class="number-circular">5</div><spring:message code="hivetablemigration.page.h3_div_5"/></h3>
                  <section>
                            <form class="form-horizontal" role="form" id="destDBForm">
                                <div id="destDBDiv">
                                   <div class="form-group">
                                      <label class="control-label col-sm-2" for="destDB"><spring:message code="hivetablemigration.page.form_dest_db"/>:</label>
                                        <div class="col-sm-10">
                                          <select class="form-control" id="destDB" name="destDB" >
                                          </select>
                                        </div>
                                    </div>
                                    <div class="clearfix"></div>

                            </form>
                  </section>
          <h3><div class="number-circular">6</div><spring:message code="hivetablemigration.page.h3_div_6"/></h3>
             <section>


               	<div class="form-group">
                    <label class="control-label col-sm-4" for="showSrcEnv"><spring:message code="hivetablemigration.page.h3_div_1"/></label>
                    <div class="col-sm-4">
                        <input type="text" class="form-control"  id="showSrcEnv" name="showSrcEnv"  disabled="disabled" >
                    </div>
                </div>
                


                <div class="form-group">
                    <label class="control-label col-sm-4" for="showSrcDB"><spring:message code="hivetablemigration.page.h3_div_2"/></label>
                    <div class="col-sm-4">
                        <input type="text" class="form-control"  id="showSrcDB" name="showSrcDB"  disabled="disabled" >
                    </div>
                </div>
                

               <div class="form-group">
                    <label class="control-label col-sm-4" for="showSrcTables"><spring:message code="hivetablemigration.page.h3_div_3"/></label>
                    <div class="col-sm-4">
                        <input type="text" class="form-control"  id="showSrcTables" name="showSrcTables"  disabled="disabled" >
                    </div>
                </div>
                

                <div class="form-group">
                    <label class="control-label col-sm-4" for="showDestEnv"><spring:message code="hivetablemigration.page.h3_div_4"/></label>
                    <div class="col-sm-4">
                        <input type="text" class="form-control"  id="showDestEnv" name="showDestEnv"  disabled="disabled" >
                    </div>
                </div>
                

                 <div class="form-group">
                    <label class="control-label col-sm-4" for="showDestDB"><spring:message code="hivetablemigration.page.h3_div_5"/></label>
                    <div class="col-sm-4">
                        <input type="text" class="form-control"  id="showDestDB" name="showDestDB"  disabled="disabled" >
                    </div>
                </div>
                <div class="clearfix"></div>


    </section>

    <h3><div class="number-circular">7</div><spring:message code="hivetablemigration.page.h3_div_7"/></h3>
                 <section>
                 <div id="Process">
                <button id="createjobs" type="button" class="btn btn-primary btn-lg"><spring:message code="hivetablemigration.page.create_jobs"/></button>
                </div>


                <div style="display:none" id="div-dialog-warning">
                <p><span class="ui-icon ui-icon-alert" style="float:left;"></span></p>
                </div>
                 </section>
    </body>

</html>