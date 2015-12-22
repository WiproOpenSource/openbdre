<%@ taglib prefix="security"
       uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
     pageEncoding="ISO-8859-1"%>
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

  <div class="container container-table">
      <div class="row vertical-center-row">
          <div class="text-center col-md-8 col-md-offset-2">
<h2>File Monitor Process Creation </h2>
  <form class="form-horizontal" role="form" id="propertiesFieldsForm">

                          <div class="form-group">
                              <label class="control-label col-sm-4">Monitored Dir Name:</label>
                              <div class="col-sm-8">
                                  <input required type="text" class="form-control" name="monitoredDirName" placeholder="Enter the dir name to be monitored" value=<%=System.getProperty("user.home")%>>
                              </div>
                          </div>
                          <div class="form-group">
                              <label class="control-label col-sm-4">File Pattern:</label>
                              <div class="col-sm-8">
                                  <input required type="text" class="form-control" name="filePattern" placeholder="Enter the file pattern" value="*" >
                              </div>
                          </div>
                          <div class="form-group">
                              <label class="control-label col-sm-4" >Delete Copied Source:</label>
                              <div class="col-sm-8">
                                  <select class="form-control" id="sel1" name="deleteCopiedSource">
                                  <option value="true">Source Delete </option>
                                  <option value="false">Move to Archive Dir</option>
                                  </select>
                              </div>
                          </div>
                          <div class="form-group">
                              <label class="control-label col-sm-4" >HDFS Upload Dir:</label>
                              <div class="col-sm-8">
                                  <input type="text" required class="form-control" name="hdfsUploadDir" placeholder="Enter hdfs upload directory" >
                              </div>
                          </div>
                          <div class="form-group">
                              <label class="control-label col-sm-4" >SleepTime:</label>
                              <div class="col-sm-8">
                                  <input type="number" required class="form-control" placeholder="in milliseconds" name="sleepTime" value="500">
                              </div>
                          </div>



                      <!-- /btn-group -->

                        <div class="form-group">

                                                      <div class="col-sm-2 col-sm-offset-4">

                                <button type="submit" ng-click="createJob()" class="btn btn-default form-control">submit</button>
                             </div>
                             </div>



                  </form>
            </div>
                  </div>
              </div>



               <script>
                        var createJobResult;
                        var app = angular.module('myApp', []);
                        app.controller('myCtrl', function($scope) {

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
                                                                            $(this).dialog("close");
                                                                        }
                                                                    }
                                                                }).text("Jobs successfully created.");
                                                                createJobResult = data;
                                                                displayProcess(createJobResult);
                                                                $('#createProcess').hide();
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
                           title: 'Data Ingestion Processes',
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
                                       return '<span class="label label-primary" onclick="fetchPipelineInfo(' + data.record.processId + ')">Display</span> ';
                                   },
                               }
                           }
                       });
                       $('#Process').jtable('load');

                   }

                           </script>



  </body>