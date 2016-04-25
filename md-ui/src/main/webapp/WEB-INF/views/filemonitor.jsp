
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
  </head>

  <body ng-app="myApp" ng-controller="myCtrl">
  						<div class="page-heading"><spring:message code="filemonitor.page.panel_heading_file_monitoring_creation"/></div>
                        <div class="row">&nbsp;</div>
                        <div class="row basic-forms">
                            <div class="col-md-2"></div>
                            <div class="col-md-8" id="divEncloseHeading" >
                                <div class="panel panel-primary">

                                    <%-- <div class="panel-heading"><spring:message code="filemonitor.page.panel_heading_file_monitoring_creation"/></div> --%>
                                    <div class="panel-body">
                                        <form role="form" id="propertiesFieldsForm">
                                            <div class="form-group">
                                                <label><spring:message code="filemonitor.page.property_form_field_dir_name"/></label>
                                                <input type="text" class="form-control" name="monitoredDirName" placeholder=<spring:message code="filemonitor.page.property_form_field_dir_name_placeholder"/> value=<%=System.getProperty("user.home")+"/mondir"%> required>
                                            </div>
                                            <div class="form-group">
                                                <label ><spring:message code="filemonitor.page.property_form_field_file_pattern"/></label>
                                                <input type="text" class="form-control" name="filePattern" value=".+" placeholder=<spring:message code="filemonitor.page.property_form_field_file_pattern_placeholder"/>required>
                                            </div>
                                            <div class="form-group">
                                                <label><spring:message code="filemonitor.page.property_form_field_srcfile_action"/></label>
                                                <select class="form-control" name="deleteCopiedSource">
                                                    <option value="true">Source File Delete</option>
                                                    <option value="false">Move Archive Dir </option>

                                                </select>
                                            </div>
                                            <div class="form-group">
                                                <label ><spring:message code="filemonitor.page.property_form_field_hdfs_upload_dir"/></label>
                                                <input type="text" class="form-control" name="hdfsUploadDir" id="hdfsUploadDir" placeholder=<spring:message code="filemonitor.page.property_form_field_hdfs_upload_dir_placeholder"/> required>
                                            </div>

                                            <div class="form-group">
                                                <label><spring:message code="filemonitor.page.property_form_field_polling_interval"/></label>
                                                <input type="number" class="form-control" name="sleepTime" value="500" placeholder=<spring:message code="filemonitor.page.property_form_field_polling_interval_placeholder"/> required>
                                            </div>

                                             <div class="form-group">
                                                 <label><spring:message code="filemonitor.page.property_form_field_process_name"/></label>
                                                 <input type="text" class="form-control"  id="processName" name="processName" placeholder=<spring:message code="filemonitor.page.property_form_field_process_name_placeholder"/> required>
                                             </div>
                                             <div class="form-group">
                                                 <label><spring:message code="filemonitor.page.property_form_field_process_desc"/></label>
                                                  <input type="text" class="form-control" id="processDescription" name="processDescription" placeholder=<spring:message code="filemonitor.page.property_form_field_process_desc_placeholder"/> required>
                                             </div>
                                             <div class="form-group">
                                                  <label><spring:message code="filemonitor.page.property_form_field_bus_domain_id"/></label>
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
                            <div class="col-md-2"> </div>
                <div class="row">&nbsp;</div>
                    <div class="row">
                        <div class="col-md-3"> </div>
                        <div class="col-md-6 ">
                        <div class="panel panel-success">
                            <div class="panel-heading" name="successHeader" id="successHeader"><spring:message code="filemonitor.page.success_header"/></div>
                            <div id="Process"></div>
                        </div>
                        </div>
                    </div>
               <script>
               $("#successHeader").hide();
                        var createJobResult;
                        var app = angular.module('myApp', []);
                        app.controller('myCtrl', function($scope) {
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

                                $.ajax({

                                                        type: "POST",
                                                        url: "/mdrest/filemonitor",
                                                        data: $('#propertiesFieldsForm').serialize(),
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
                                                                         displayProcess(createJobResult);
                                                                         $(this).dialog("close");
                                                                        }
                                                                    }
                                                                }).text("Jobs successfully created.");

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
                                                                            $(this).dialog("close");
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
                   function displayProcess(records) {
                       $('#Process').jtable({
                           title: 'File Monitor Processes',
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
                                                   paging: false,
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
                                       return '<span class="label label-primary" onclick="fetchPipelineInfo(' + data.record.processId + ')"></span> ';
                                   },
                               }
                           }
                       });
                       $('#Process').jtable('load');

                   }

                           </script>


<div id="div-dialog-warning"/>
  </body>
  </html>