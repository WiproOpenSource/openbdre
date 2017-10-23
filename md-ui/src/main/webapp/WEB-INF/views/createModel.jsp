<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="security"
	   uri="http://www.springframework.org/security/tags" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link rel="stylesheet" href="../css/jquery.steps.css" />
        <link rel="stylesheet" href="../css/jquery.steps.custom.css" />
        <link href="../css/css/bootstrap.min.css" rel="stylesheet" type="text/css" />
        <link href="../css/jtables-bdre.css" rel="stylesheet" type="text/css" />
        <link href="../css/jquery-ui-1.10.3.custom.css" rel="stylesheet" type="text/css" />
        <link href="../css/bootstrap.custom.css" rel="stylesheet" />
        <link href="../StreamAnalytix_files/materialdesignicons.min.css" media="all" rel="stylesheet" type="text/css">
        <link href="../StreamAnalytix_files/bootstrap.min.css" rel="stylesheet">
        <link href="../StreamAnalytix_files/bootstrap-material-design.min.css" rel="stylesheet">
        <link href="../StreamAnalytix_files/ripples.min.css" rel="stylesheet">
        <link href="../StreamAnalytix_files/sax-fonts.css" class="include" rel="stylesheet" type="text/css">
        <link href="../StreamAnalytix_files/toastr.min.css" rel="stylesheet">
        <link href="../StreamAnalytix_files/datatables.min.css" rel="stylesheet">
        <link href="../StreamAnalytix_files/theme.css" rel="stylesheet" type="text/css">
        <link href="../StreamAnalytix_files/style.css" rel="stylesheet" type="text/css">
        <link href="../StreamAnalytix_files/select2.4.0.css" rel="stylesheet">
        <link href="../StreamAnalytix_files/select2-bootstrap.css" rel="stylesheet">
        <script src="../js/jquery.min.js"></script>
        <script src="../js/jquery-ui-1.10.3.custom.js"></script>
        <script src="../js/jquery.steps.min.js"></script>
        <script src="../js/jquery.jtable.js" type="text/javascript"></script>
        <script src="../js/bootstrap.js" type="text/javascript"></script>
        <script src="../js/angular.min.js" type="text/javascript"></script>
    </head>

    <script>
    		var insert=1;
         var wizard = null;
         var finalJson;
         wizard = $(document).ready(function() {

    	$("#bdre-data-load").steps({
    		headerTag: "h3",
    		bodyTag: "section",
    		transitionEffect: "slide",
    		enableCancelButton: true,
    		onStepChanging: function(event, currentIndex, newIndex) {
    			console.log(currentIndex + 'current ' + newIndex );
    	  /*	    if(currentIndex == 0 && newIndex == 1) {
    			console.log(document.getElementById('fileFormat').elements[1].value);

    			console.log(document.getElementById('fileFormat'));
               if((document.getElementById('fileformat').value=="Json" || document.getElementById('fileformat').value=="XML") && insert==1 && document.getElementById('isDefaultTemplate').value=="No"){
               var content1="";
               content1=content1+'<div class="form-group">';
               content1=content1+'<label for = "fileUpload" >File Upload</label >';
               content1=content1+'<input name = "regFile" id = "regFile" type = "file" class = "form-control" style="opacity: 100; position: inherit;" /></div>';
               content1=content1+'<div class="form-group">';
                content1=content1+'<div class="clearfix"></div>';
                var format = document.getElementById('fileformat').value;
               content1=content1+'<button class = "btn btn-default  btn-success" style="margin-top: 30px;background: lightsteelblue;" type = "button" onClick = "uploadFile(\''+format+'\')" href = "#" >Upload File</button >';
               $('#bdre-data-load').steps('insert', 1, { title: "File Upload", content: content1 });
               insert=insert+1;
               }

               if(insert==2 && document.getElementById('fileformat').value !="Json" && document.getElementById('fileformat').value !="XML" )
               {
               $('#bdre-data-load').steps('remove',1);
               insert=insert-1;
               }
    			}*/
    			return true;
    		},
    		onStepChanged: function(event, currentIndex, priorIndex) {
    			        console.log(currentIndex + " " + priorIndex);
    			       // if(insert==1 && priorIndex==0 && currentIndex==1)
                      //  $('#rawTableColumnDetails').jtable('load');
                     //   if(insert==2 && priorIndex==1 && currentIndex==2)
                      //  $('#rawTableColumnDetails').jtable('load');
    		},
    		onFinished: function(event, currentIndex) {
    		                      /*   formIntoMap('fileformat_', 'fileFormat');
                                     jtableIntoMap('rawtablecolumn_', 'rawTableColumnDetails');
                                     console.log("$scope.connectionName is "+con_name);
                                     map["fileformat_connectionName"]=con_name;
                                     console.log(map);
            						$.ajax({
            							type: "POST",
            							url: "/mdrest/message/createjobs",
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
            											    $('#Container').jtable('load');
            												$(this).dialog("close");
            												location.href = '<c:url value="/pages/premessageconfig.page"/>';
            											}
            										}
            									}).html('<p><span class="jtable-confirm-message">Message successfully created </span></p>');
            								}

            							else{
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
                                        }).html('<p><span class="jtable-confirm-message">Message is not created</span></p>');

            								}
            							}

            						});
                                return false;  */
    		},
    		onCanceled: function(event) {
    			location.href = '<c:url value="/pages/createModel.page"/>';
    		}
    	});
    });

    		</script>


  <div  id="preModelDetails">


   <div >
  	<div id="bdre-data-load">
  			<h3>Model Details</h3>
              			<section>
              <form class="form-horizontal" role="form" id="modelDetail">


                      <!-- btn-group -->
                      <div id="rawTablDetailsDB">

                      <div class="form-group" >
                      <label class="control-label col-sm-2" for="modelType">Model Type</label>
                      <div class="col-sm-10">
                          <select name="modelType">
                            <option value="linearRegression">Linear Regression</option>
                            <option value="logisticRegression">Logistic Regression</option>

                          </select>
                      </div>
                  </div>


                      <div class="form-group" >
                          <label class="control-label col-sm-2" for="messageName">Message Name</label>
                          <div class="col-sm-10">
                              <input type="text" class="form-control"  id="messageName" name="messageName" placeholder="message name" value="" required>
                              <select>

                              </select>
                          </div>
                      </div>

                         <div class="form-group" >
                                                   <label class="control-label col-sm-2" for="continuousFeatures">Continuous Features</label>
                                                   <div class="col-sm-10">
                                                       <input type="text" class="form-control"  id="continuousFeatures"  >
                                                   </div>
                                               </div>
                    <div class="form-group" >
                   <label class="control-label col-sm-2" for="categoryFeatures">Category Features</label>
                   <div class="col-sm-10">
                       <input type="text" class="form-control"  id="categoryFeatures"  >
                   </div>
               </div>





                      <div class="clearfix"></div>
                      </div>

                      <!-- /btn-group -->

                  </form>
              			</section>



  			<h3>Model Parameters</h3>
  			<section>
                          <form class="form-horizontal" role="form" id="modelParameter">


                                  <!-- btn-group -->
                                  <div id="rawTablDetailsDB">

                                  <div class="form-group" >
                                  <label class="control-label col-sm-2" for="elasticNetParam">Elastic Net Param</label>
                                  <div class="col-sm-10">
                                      <input type="text" class="form-control"  id="elastcNetParam">
                                  </div>
                              </div>


                                  <div class="form-group" >
                                      <label class="control-label col-sm-2" for="maxIter">Maximum Iterations</label>
                                      <div class="col-sm-10">
                                          <input type="text" class="form-control"  id="maxIter" >
                                      </div>
                                  </div>

                                     <div class="form-group" >
                                                               <label class="control-label col-sm-2" for="regParam">Regularization Parameter</label>
                                                               <div class="col-sm-10">
                                                                   <input type="text" class="form-control"  id="regParam"  >
                                                               </div>
                                                           </div>
                                <div class="form-group" >
                               <label class="control-label col-sm-2" for="labelColumn">Label Column</label>
                               <div class="col-sm-10">
                                   <input type="text" class="form-control"  id="labelColumn"  >
                               </div>
                           </div>





                                  <div class="clearfix"></div>
                                  </div>

                                  <!-- /btn-group -->

                              </form>
                          			</section>


                  <h3>Model Data</h3>
                    			<section>
                                            <form class="form-horizontal" role="form" id="modelData">


                                                    <!-- btn-group -->
                                                    <div id="rawTablDetailsDB">

                                                    <div class="form-group" >
                                                    <label class="control-label col-sm-2" for="dataFile">Data File</label>
                                                    <div class="col-sm-10">
                                                        <input type="text" class="form-control"  id="dataFile">
                                                    </div>
                                                </div>


                                                    <div class="form-group" >
                                                        <label class="control-label col-sm-2" for="modelName">Model Name</label>
                                                        <div class="col-sm-10">
                                                            <input type="text" class="form-control"  id="modelName" >
                                                        </div>
                                                    </div>





                                                    <div class="clearfix"></div>
                                                    </div>

                                                    <!-- /btn-group -->

                                                </form>
                                            			</section>
  		</div>
          </div>

  </div>

<script>
                var message_name="";
                var app = angular.module('app', []);
                   app.controller('myCtrl', function($scope) {

                    $scope.messageList={};

                    $scope.columnList={};
                   $.ajax({
                       url: '/mdrest/message/optionslist',
                           type: 'POST',
                           dataType: 'json',
                           async: false,
                           success: function (data) {
                               $scope.messageList = data.Options;
                           },
                           error: function () {
                               alert('danger');
                           }
                       });

                    $scope.change=function()
                    {
                    console.log("function change is being called");
                    console.log("value of messageName is "+$scope.messageName);
                    message_name=$scope.messageName;
                    console.log(message_name);
                    /*$.ajax({
                       url: '/mdrest/connections/'+$scope.connectionName+"/"+"topicName",
                           type: 'GET',
                           dataType: 'json',
                           async: false,
                           success: function (data) {
                                console.log("topic list is "+data.Options);
                                $scope.topicList = data.Options;
                           },
                           error: function () {
                               alert('danger');
                           }
                       });

                    $scope.IsVisible=true;*/
                    }


                     $scope.showPopup=function()
                        {
                        console.log("value of topicName is "+$scope.topicName);
                        $('#topicNameInForm').val($scope.topicName);
                        $('#connectionNameInform').val($scope.connectionName);
                        }
                });


        </script>