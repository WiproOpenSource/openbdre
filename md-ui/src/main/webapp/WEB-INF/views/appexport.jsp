
 <%@ taglib prefix="security"
       uri="http://www.springframework.org/security/tags" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
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
	<link rel="stylesheet" href="../css/bootstrap.custom.css" />
    <script src="../js/bootstrap.js" type="text/javascript"></script>
    <script src="../js/jquery.jtable.js" type="text/javascript"></script>
    <link href="../css/jtables-bdre.css" rel="stylesheet" type="text/css" />
    <link href="../css/select2.min.css" rel="stylesheet" />
    <script src="../js/select2.min.js"></script>

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

  <body>
  <script>
  var app = angular.module('myApp',[]);
                          app.controller('myCtrl',function($scope) {
                          $scope.parentProcessList={};
                          $.ajax({
                                url: "/mdrest/process/parentProcessList",
                                    type: 'GET',
                                    dataType: 'json',
                                    async: false,
                                    success: function(data){
                                        console.log(data.Records);
                                        for(var i=0;i<data.Records.length;i++){
                                        data.Records[i].processName=data.Records[i].processId+"_"+data.Records[i].processName;
                                        }
                                        $scope.parentProcessList=data.Records;
                                        console.log($scope.parentProcessList);
                                    },
                                    error: function () {
                                        alert('danger');
                                    }
                                });

                          });
  </script>
  <div class="page-header"><spring:message code="appexport.page.pannel_heading"/></div>
 <%
   String processId=request.getParameter("processId");
  %>
                         <div class="row">&nbsp;</div>
                         <div class="row">
                             <div class="col-md-3"> </div>
                             <div class="col-md-6" >
                                    <div  class="col-md-3"></div>
                                       <div class="col-md-3 ">
                                           <div class="row">&nbsp;</div>
                                           <button type="button" width="20px" onclick="downloadZip(<%=processId %>)" class="btn btn-primary btn-large  pull-right">Download Multiple Zip</button>
                                       </div>
                                       <div  class="col-md-3">
                                            <div class="row">&nbsp;</div>
                                            <button type="button" width="20px" onclick="showExportForm()" class="btn btn-primary btn-large pull-center"><spring:message code="appexport.page.export_to_appstore"/></button>
                                       </div>
<br>
                                                      <br>
                                                      <br>
                                                      <br>
                                       <div class="row"></div>
                                          <button type="button" width="20px" onclick="downloadCurrentZip(<%=processId %>)" class="btn btn-primary btn-large  pull-center">Download Current Process Zip</button>
                                      </div>

                             </div>
                         </div>



                        <div class="row">&nbsp;</div>
                        <div class="row">
                            <div class="col-md-3"> </div>
                            <div class="col-md-6" id="divEncloseHeading">


                                <div class="panel panel-primary" id="export">

                                    <div id="exportForm" class="panel-body">

                                        <form role="form" id="exportToAppStoreForm"  >
                                             <div class="form-group">
                                                <label ><spring:message code="appexport.page.application_name"/></label>
                                                <input type="text" class="form-control" name="appName"  placeholder= <spring:message code="appexport.page.appname_placeholder"/> required>
                                            </div>
                                            <div class="form-group">
                                                <label ><spring:message code="appexport.page.select_business_domain"/></label>
                                                <select class="form-control" name="appDomain">
                                                    <option value="banking">Banking</option>
                                                    <option value="retail"> Retail</option>
                                                    <option value="telecom">Telecom</option>
                                                    <option value="insurance">Insurance </option>
                                                    <option value="hc">Healthcare</option>
                                                    <option value="enu">Energy And Utilities </option>

                                                </select>
                                            </div>

                                            <div class="form-group">
                                                  <label ><spring:message code="appexport.page.upload_app_img"/></label>
                                                  <spring:message code="appexport.page.upload_app_img_placeholder" var="placeholder"/>
                                                  <input type="file" name="appImage" class="form-control" placeholder='${placeholder}' id="img-id" required>

                                            </div>
                                            <div class="form-group">
                                               <label></label>
                                               <input type="hidden" class="form-control" name="processId"  value="<%=processId %>" required>
                                            </div>
											<div class="actions text-center pull-right">
                                            	<input type="submit" id="submitButton" class="btn btn-primary" onclick="appstorePush();"/>
                                            </div>
                                        </form>

                                    </div>
                                </div>
                            </div>

                        </div>


                        <div id="batchProcesses" ng-app="myApp" ng-controller="myCtrl">
                           <label class="form-control" for="features" style="width:20%">Select Processes</label>
                           <select class="js-example-basic-multiple" id="processList" name="processList" multiple="multiple" style="width:20%" ng-model="processlist" ng-options = "parentProcess.processId as parentProcess.processName for parentProcess in parentProcessList track by parentProcess.processId">
                           <option value="">Select the option</option>
                           </select>
                           </div>


                        <script>
                        $(".js-example-basic-multiple").select2();
                        </script>

               <script>
               $("#export").hide();
               $("#successHeader").hide();
                               downloadZip =function(processId){

                                $.ajax({
                                      url: '/mdrest/process/export/' + processId,
                                       type: 'GET',
                                       dataType: 'json',
                                        success: function(data) {
                                        if (data.Result == "OK") {
                                        console.log(window.location.protocol);
                                        console.log(window.location.host);
                                        var selectedProcess = $(".js-example-basic-multiple").select2("val");
                                        console.log(selectedProcess);
                                        var processString="";
                                        processString=processString.concat(selectedProcess[0]);
                                        for(var i=1;i<selectedProcess.length;i++){
                                        processString=processString.concat("-");
                                        processString=processString.concat(selectedProcess[i]);
                                        }
                                       var url = (window.location.protocol + "//" + window.location.host + "/mdrest/process/zippedexportMultiple/" + processString);
                                        window.location.href = url;
                                     }
                                  if (data.Result == "ERROR")
                                    alert(data.Message);
                               },
                                error: function() {
                                alert('<spring:message code="appexport.page.alert_error_zip"/>');
                            }
                        });

                               }

                               downloadCurrentZip = function (processId){
                               var url = (window.location.protocol + "//" + window.location.host + "/mdrest/process/zippedexport/" + processId);
                               window.location.href = url;
                               }

                               appstorePush =function (){
                                  uploadImg(<%=processId %> ,'img-id');
                                   event.preventDefault();
                                $.ajax({
                                     url: '/mdrest/adq/',
                                      type: 'POST',
                                      data: $('#exportToAppStoreForm').serialize(),
                                      dataType: 'json',
                                       success: function(data) {
                                       if (data.Result == "OK") {
                                       console.log(data);
                                       if(imgstatus == "uploaded"){
                                            alert('<spring:message code="appexport.page.alert_success"/>');
                                             $("#export").hide();
                                             }
                                        if(imgstatus == "failed")
                                            alert('<spring:message code="appexport.page.alert_image_error"/>');

                                    }
                                         if (data.Result == "ERROR")
                                           alert(data.Message);
                                      },
                                       error: function() {
                                       console.log(imgstatus);
                                       alert('<spring:message code="appexport.page.alert_error"/>');
                                   }
                               });


                                }
                                 </script>

                                 <script>

                                      showExportForm=function (){
                                                                 $("#export").show();
                                              }
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


                                               <div style = "display:none" id = "div-dialog-warning" >
                                                               				<p ><span class = "ui-icon ui-icon-alert" style = "float:left;" ></span >
                                                               				<div />


  </body>
  </html>