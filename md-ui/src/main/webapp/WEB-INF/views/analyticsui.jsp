

                    <%@ taglib prefix="security"
       uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
     pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<script>
	  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
	  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
	  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
	  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');
	  //Please replace with your own analytics id
	  ga('create', 'UA-72345517-1', 'auto');
	  ga('send', 'pageview');
	</script>

   <script src="../js/jquery.min.js"></script>
       <link href="../css/jquery-ui-1.10.3.custom.css" rel="stylesheet">
       <link href="../css/css/bootstrap.min.css" rel="stylesheet" />
       <link href="../css/bootstrap.custom.css" rel="stylesheet" type="text/css" />
       <script src="../js/jquery-ui-1.10.3.custom.js"></script>
       <script src="../js/jquery.steps.min.js"></script>
       <link rel="stylesheet" href="../css/jquery.steps.css" />
       <link rel="stylesheet" href="../css/jquery.steps.custom.css" />
   	    <script src="../js/bootstrap.js" type="text/javascript"></script>
       <script src="../js/jquery.jtable.js" type="text/javascript"></script>
       <link href="../css/jtables-bdre.css" rel="stylesheet" type="text/css" />

       <script src="../js/angular.min.js" type="text/javascript"></script>
    <style>
    html, body, .container-table {
        height: 100%;
    }
    .container-table {
        display: table;
    }
    .vertical-center-row {
        display: table-cell;
        horizontal-align: middle;
        padding-top: 2cm;
    }
    </style>

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

  </head>

  <body ng-app="myApp" ng-controller="myCtrl">
                        <div class="page-header"><spring:message code="analyticsui.page.panel_heading"/></div>
                        <div class="row">&nbsp;</div>
                        <div class="row bdre-process-creation-form">
                            <div class="col-md-3"> </div>
                            <div class="col-md-6" id="divEncloseHeading">
                                <div class="panel panel-primary">
                                    <div class="panel-body">
                                        <form role="form" id="propertiesFieldsForm">
                                            <div class="form-group">
                                                <label ><spring:message code="analyticsui.page.industry.name"/></label>
                                                    <select class="form-control" id="industry" name="industry" >
                                                        <option ng-repeat="industry in industries" value="{{industry.defaultVal}}" name="industry">{{industry.value}}</option>
                                                    </select>
                                              </div>

                                            <div class="form-group">
                                                <label ><spring:message code="analyticsui.page.category"/> </label>
                                                <input type="text" class="form-control" name="category" id="category" placeholder="category" required>
                                             </div>

                                            <div class="form-group">
                                                <label ><spring:message code="analyticsui.page.app.name"/></label>
                                                <input type="text" class="form-control" name="appname" id="appname" placeholder="App Name" required>
                                            </div>

                                             <div class="form-group">
                                                <label ><spring:message code="analyticsui.page.app.description"/></label>
                                                <input type="text" class="form-control" name="appdesc" id="appdesc" placeholder="App Description" required>
                                            </div>

                                              <div class="form-group">
                                                 <label ><spring:message code="analyticsui.page.questions.json"/></label>
                                                 <input type="text" class="form-control" name="questionsjson" id="questionsjson" placeholder="Questions Json" required>
                                             </div>

                                              <div class="form-group">
                                                 <label ><spring:message code="analyticsui.page.dashboard.url"/></label>
                                                 <input type="text" class="form-control" name="dashboardurl" id="dashboardurl" placeholder="Dashboard URL" required>
                                             </div>

                                             <div class="form-group">
                                                  <label ><spring:message code="analyticsui.page.ddp.url"/></label>
                                                  <input type="text" class="form-control" name="ddpurl" id="ddpurl" placeholder="DDP URL" required>
                                              </div>

                                             <div class="form-group">
                                                <label ><spring:message code="analyticsui.page.app.image"/></label>
                                                <input type="file" class="form-control" name="appimage" id="appimage"  required>
                                            </div>

                                              <div class="form-group">
                                                 <label><spring:message code="analyticsui.page.process.name"/></label>
                                                 <input type="text" class="form-control"  id="processName" name="processName" placeholder="Enter Process Name" required>
                                             </div>
                                             <div class="form-group">
                                                 <label><spring:message code="analyticsui.page.process.description"/></label>
                                                  <input type="text" class="form-control" id="processDescription" name="processDescription" placeholder="Enter Process Description" required>
                                             </div>
                                             <div class="form-group">
                                                  <label><spring:message code="analyticsui.page.bus.domain.id"/></label>
                                                   <select class="form-control" id="busDomainId" name="busDomainId">
                                                    <option ng-repeat="busDomain in busDomains.Options" value="{{busDomain.Value}}" name="busDomainId">{{busDomain.DisplayText}}</option>
                                                    </select>
                                             </div>
                                               <div class="actions text-center pull-right">
                                            <input type="submit" id="createJobButton" class="btn btn-primary" ng-click="createJob()"/>
                                            </div>


                                            </form>
                                    </div>
                                </div>
                            </div>
                         </div>
                            <div class="col-md-3"> </div>
			<div class="row">&nbsp;</div>
                    <div class="row">
                        <div class="col-md-3"> </div>
                        <div class="col-md-6 ">
                        <div class="panel panel-success">
                            <div class="panel-heading" name="successHeader" id="successHeader"><spring:message code="analyticsui.page.create_success"/></div>
                            <div id="Process"></div>
                        </div>
                        </div>
                    </div>


               <script>

               $("#successHeader").hide();
                        var createJobResult;
                        var app = angular.module('myApp', []);
                        app.controller('myCtrl', function($scope) {

                        $scope.uploadedFileName ="";
                        $scope.imgstatus="";
                        $scope.industries= getGenConfigMap('industry_name');
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




                      $scope.createJob =function (){

                      var appImage = document.getElementById("appimage").value;
                       formIntoMap('appproperties_','propertiesFieldsForm');
                       map["appImage"] = appImage;
                                 $.ajax({

                                                         type: "POST",
                                                         url: "/mdrest/analyticsapp/createjobs",
                                                         data: jQuery.param(map),
                                                         success: function(data) {
                                                             if(data.Result == "OK") {
                                                              console.log("data passed successfully");
                                                                 created = 1;
                                                                 $("#div-dialog-warning").dialog({
                                                                     title: "Success",
                                                                     resizable: false,
                                                                     height: 'auto',
                                                                     modal: true,
                                                                     buttons: {
                                                                         "Ok": function() {
                                                                         $("#divEncloseHeading").hide();
                                                                         $("#successHeader").show();
                                                                          createJobResult = data;
                                                                          console.log(createJobResult.Records[0].processId);
                                                                          uploadImg(createJobResult.Records[0].processId,"appimage");
                                                                          displayProcess(createJobResult);
                                                                          $(this).dialog('<spring:message code="analyticsui.page.close"/>');
                                                                         }
                                                                     }
                                                                 }).text('<spring:message code="analyticsui.page.create_success"/>');

                                                             }
                                                             else{
                                                             console.log("data not passed successfully");
                                                                 $("#div-dialog-warning").dialog({
                                                                     title: "Error",
                                                                     resizable: false,
                                                                     height: 'auto',
                                                                     modal: true,
                                                                     buttons: {
                                                                         "Ok": function() {
                                                                             $(this).dialog('<spring:message code="analyticsui.page.close"/>');
                                                                         }
                                                                     }
                                                                 }).html(data.Message);
                                                             }
                                                             console.log(createJobResult);
                                                         }

                                                     });
                             }
                         });
                    </script>

                    <script>
                                                                     var uploadedFileName ="";
                                                                     var imgstatus="";
                                                                   function uploadImg (subDir,fileId){
                                                                  var arg= [subDir,fileId];
                                                                    var fd = new FormData();
                                                                   		                var fileObj = $("#"+arg[1])[0].files[0];
                                                                                           var fileName=fileObj.name;
                                                                                           fd.append("file", fileObj);
                                                                                           fd.append("name", fileName);
                                                                                           $.ajax({
                                                                                             url: '/mdrest/filehandler/uploadzip/'+arg[0],
                                                                                             type: "POST",
                                                                                             data: fd,
                                                                                             async: false,
                                                                                             enctype: 'multipart/form-data',
                                                                                             processData: false,  // tell jQuery not to process the data
                                                                                             contentType: false,  // tell jQuery not to set contentType
                                                                                             success:function (data) {
                                                                                                   uploadedFileName=data.Record.fileName;
                                                                                                   console.log( data );
                                                                                                   imgstatus="uploaded";
                                                                                                   return false;
                                                                   							},
                                                                   						  error: function () {
                                                                   							   imgstatus="failed";
                                                                                               return false;
                                                                   							}
                                                                   						 });

                                                                   }



                                                                   </script>



                <script>
                function displayProcess(records) {
                	$('#Process').jtable({
                		title: '<spring:message code="analyticsui.page.title_jtable"/>',
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
                				title: '<spring:message code="analyticsui.page.title_id"/>'
                			},
                			Properties: {
                				title: '<spring:message code="analyticsui.page.title_properties"/>',
                				width: '5%',
                				sorting: false,
                				edit: false,
                				create: false,
                				listClass: 'bdre-jtable-button',
                				display: function(item) { //Create an image that will be used to open child table

                					var $img = $('<span class="label label-primary"><spring:message code="analyticsui.page.img_show"/></span>'); //Open child table when user clicks the image

                					$img.click(function() {
                						$('#Process').jtable('openChildTable',
                							$img.closest('tr'), {
                								title: '<spring:message code="analyticsui.page.img_properties"/>'+' ' + item.record.processId,
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
                										title: '<spring:message code="analyticsui.page.title_process"/>',
                										defaultValue: item.record.processId,
                									},
                									configGroup: {
                										title: '<spring:message code="analyticsui.page.title_cg"/>',
                										defaultValue: item.record.configGroup,
                									},
                									key: {
                										title: '<spring:message code="analyticsui.page.title_key"/>',
                										key: true,
                										list: true,
                										create: true,
                										edit: false,
                										defaultValue: item.record.key,
                									},
                									value: {
                										title: '<spring:message code="analyticsui.page.title_value"/>',
                										defaultValue: item.record.value,
                									},
                									description: {
                										title: '<spring:message code="analyticsui.page.title_desc"/>',
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
                				title: '<spring:message code="analyticsui.page.title_name"/>'
                			},
                			tableAddTS: {
                				title: '<spring:message code="analyticsui.page.title_add_ts"/>',
                				create: false,
                				edit: true,
                				list: false,
                				type: 'hidden'
                			},
                			description: {
                				title: '<spring:message code="analyticsui.page.title_desc"/>',
                			},
                			batchPattern: {
                				title: '<spring:message code="analyticsui.page.title_batch_mark"/>',
                				list: false,
                				create: false,
                				edit: true,
                				type: 'hidden'

                			},
                			parentProcessId: {
                				title: '<spring:message code="analyticsui.page.title_parent"/>',
                				edit: true,
                				create: false,
                				list: false,
                				type: 'hidden'
                			},
                			canRecover: {
                				title: '<spring:message code="analyticsui.page.title_restorable"/>',
                				type: 'hidden',
                				list: false,
                				edit: true,
                			},
                			nextProcessIds: {
                				title: '<spring:message code="analyticsui.page.title_next"/>',
                				list: false,
                				edit: true,
                				type: 'hidden'

                			},
                			enqProcessId: {
                				title: '<spring:message code="analyticsui.page.title_enque"/>',
                				list: false,
                				edit: true,
                				type: 'hidden',
                			},
                			busDomainId: {
                				title: '<spring:message code="analyticsui.page.title_app"/>',
                				list: false,
                				edit: true,
                				type: 'combobox',
                				options: '/mdrest/busdomain/options/',
                			},
                			processTypeId: {
                				title: '<spring:message code="analyticsui.page.title_type"/>',
                				edit: true,
                				type: 'hidden',
                				options: '/mdrest/processtype/optionslist'

                			},
                			ProcessPipelineButton: {
                				title: '<spring:message code="analyticsui.page.title_pipeline"/>',
                				sorting: false,
                				width: '2%',
                				listClass: 'bdre-jtable-button',
                				create: false,
                				edit: false,
                				display: function(data) {
                					return '<span class="label label-primary" onclick="fetchPipelineInfo(' + data.record.processId + ')"><spring:message code="analyticsui.page.display"/></span> ';
                				},
                			}
                		}
                	});
                	$('#Process').jtable('load');

                }

                		</script>

<div style="display:none" id="div-dialog-warning">
			<p><span class="ui-icon ui-icon-alert" style="float:left;"></span></p>
		</div>
  </body>
  </html>