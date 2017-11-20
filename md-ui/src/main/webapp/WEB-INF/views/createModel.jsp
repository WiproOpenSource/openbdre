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
        <script src="../js/jquery.min.js" type="text/javascript"></script>
        <script src="../js/jquery-ui-1.10.3.custom.js" type="text/javascript"></script>
        <script src="../js/jquery.steps.min.js" type="text/javascript"></script>
        <script src="../js/jquery.jtable.js" type="text/javascript"></script>
        <script src="../js/bootstrap.js" type="text/javascript"></script>
        <script src="../js/angular.min.js" type="text/javascript"></script>

         <link href="../css/select2.min.css" rel="stylesheet" />
                        <script src="../js/select2.min.js"></script>


    </head>

    <script>
    		var insert=1;
         var wizard = null;
         var finalJson;
         wizard = $(document).ready(function() {

    	$("#bdre-data-load").steps({
    		headerTag: "h2",
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
    			       // if(insert==1 && priorIndex==0 && currentIndex==1)
                      //  $('#rawTableColumnDetails').jtable('load');
                     //   if(insert==2 && priorIndex==1 && currentIndex==2)
                      //  $('#rawTableColumnDetails').jtable('load');
    		},
    		onFinished: function(event, currentIndex) {
    		                         /* formIntoMap('fileformat_', 'fileFormat');
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
                                return false; */
    		},
    		onCanceled: function(event) {
    			//location.href = '<c:url value="/pages/createModel.page"/>';
    		}
    	});
    });

    		</script>
  <script>
  var map = new Object();
  function formIntoMap(typeProp, typeOf) {
  	var x = '';
  	x = document.getElementById(typeOf);
  	console.log(x);
  	var text = "";
  	var i;
  	for(i = 0; i < x.length; i++) {
  		map[typeProp + x.elements[i].name] = x.elements[i].value;
  		//console.log(map[typeProp + x.elements[i].name]);
  		//console.log(x.elements[i].value);
  	}

  }

  function messageChange()
                                 {

                                 console.log("function messageChange is being called");
                                 var message=document.getElementById("messageName").value;

                                 console.log("value of messageName is "+message);
                               angular.element(document.getElementById('preMessageDetails')).scope().change(message);



                                 }
  </script>
  <script>
                  var app = angular.module('app', []);
                     app.controller('myCtrl',function($scope) {

                      $scope.modelList={};

                        $scope.processId=86;

                     $.ajax({

                         url: "/mdrest/processtype/options_analytics/"+$scope.processId,
                             type: 'POST',
                             dataType: 'json',
                             async: false,
                             success: function (data) {

                                 $scope.modelList = data.Options;
                                 console.log($scope.modelList);

                             },
                             error: function () {
                                 alert('danger');
                             }
                         });
                       $scope.change=function(message){
                             $scope.continuousColumnList=[];
                             $scope.categoryColumnList=[];
                         $.ajax({

                               url: '/mdrest/sparkstreaming/getmessagecolumns/'+ message,
                                   type: 'GET',
                                   dataType: 'json',
                                   async: false,
                                   success: function (data) {

                                       $scope.columnList = data.Options;
                                       console.log("message name  is ", message);
                                       console.log("column list is ", data.Options);

                                       for(i=0;i<$scope.columnList.length;i++){



                                          var tmp=$scope.columnList[i].Value.split(":");


                                          if(tmp[1]=="Integer" || tmp[1]=="Long" || tmp[1]=="Short" || tmp[1]=="Byte" || tmp[1]=="Float" || tmp[1]=="Double" || tmp[1]=="Decimal"){
                                          console.log(tmp);
                                            $scope.continuousColumnList.push($scope.columnList[i]);

                                            }
                                            else{
                                            console.log(tmp);
                                            $scope.categoryColumnList.push($scope.columnList[i]);
                                            }

                                       }
                                       console.log($scope.continuousColumnList);
                                       console.log($scope.categoryColumnList);
                                       $('#continuousFeatures').find('option').remove();
                                       $('#categoryFeatures').find('option').remove();
                                       $('#labelColumn').find('option').remove();

                                        var option=new Option("prem","prem");
                                   $.each($scope.continuousColumnList, function (i, v) {
                                   var option=new Option("prem","prem");
                                                           $('#continuousFeatures').append($('<option>', {
                                                               value: v.Value,
                                                               text : v.DisplayText,
                                                           }));
                                                       });
                                   $.each($scope.categoryColumnList, function (i, v) {
                          var option=new Option("prem","prem");
                                                  $('#categoryFeatures').append($('<option>', {
                                                      value: v.Value,
                                                      text : v.DisplayText,
                                                  }));
                                              });
                                          $.each($scope.columnList, function (i, v) {
                        var option=new Option("prem","prem");
                                                $('#labelColumn').append($('<option>', {
                                                    value: v.Value,
                                                    text : v.DisplayText,
                                                }));
                                            });

                                   },
                                   error: function () {
                                       alert('danger');
                                   }
                               });
                           }




                  });
                   function saveModelProperties(){

                                             console.log("saveModelProperties is being called");
                                             var value1=$(".js-example-basic-multiple1").select2("val");
                                             console.log("value1 ",value1);
                                             var continuousValue=value1[0];
                                             for(i=1;i<value1.length;i++)
                                             {
                                                  continuousValue=continuousValue.concat(",");
                                                  var s=value1[i];
                                                  continuousValue=continuousValue.concat(s);
                                             }
                                             var value2=$(".js-example-basic-multiple2").select2("val");
                                             var categoryValue=value2[0];
                                             for(i=1;i<value2.length;i++)
                                             {
                                                  categoryValue=categoryValue.concat(",");
                                                  var s=value2[i];
                                                  categoryValue=categoryValue.concat(s);
                                             }
                                             formIntoMap("Model_","modelDetail");
                                             formIntoMap("ModelProperties_","modelRequiredFieldsForm");
                                             console.log(continuousValue);
                                             console.log("hiiii");
                                             //map["ModelProperties_filePath"]=document.getElementById("regFile").value;
                                             map["Model_modelName"]=document.getElementById("modelName").value;
                                             map["Model_continuousFeatures"]=continuousValue;
                                             map["Model_categoryFeatures"]=categoryValue;
                                             console.log(map);
                                             $.ajax({
                                                         type: "POST",
                                                         url: "/mdrest/models/createModels",
                                                         data: jQuery.param(map),
                                                         success: function(data) {
                                                             if(data.Result == "OK") {

                                                                 alert("Model Created Successfully");

                                               }

                                                             else{
                                                             alert("warning","Error occured");
                                                             }
                                                         }

                                                     });
                                             }

                    function uploadZip (subDir,fileId){
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
                                                                  $("#div-dialog-warning").dialog({
                                                                                  title: "",
                                                                                  resizable: false,
                                                                                  height: 'auto',
                                                                                  modal: true,
                                                                                  buttons: {
                                                                                      "Ok" : function () {
                                                                                          $(this).dialog("close");
                                                                                      }
                                                                                  }
                                                                  }).html('<p><span class="jtable-confirm-message"><spring:message code="processimportwizard.page.upload_success"/>'+' ' + uploadedFileName + '</span></p>');
                                                                  return false;
                                  							},
                                  						  error: function () {
                                  							    $("#div-dialog-warning").dialog({
                                                                              title: "",
                                                                              resizable: false,
                                                                              height: 'auto',
                                                                              modal: true,
                                                                              buttons: {
                                                                                  "Ok" : function () {
                                                                                      $(this).dialog("close");
                                                                                  }
                                                                              }
                                                              }).html('<p><span class="jtable-confirm-message"><spring:message code="processimportwizard.page.upload_error"/></span></p>');
                                                              return false;
                                  							}
                                  						 });

                                  }





          </script>
          <script>
          function loadModelProperties(loadMethod) {
              console.log(loadMethod);
                      if(loadMethod=="serializedModel" || loadMethod=="pmmlFile"){
                      var div = document.getElementById('modelRequiredFields');
                      var formHTML='';
                      formHTML=formHTML+'<form class="form-horizontal" role="form" id="modelData">';
                      formHTML=formHTML+'<div id="rawTablDetailsDB">';
                      formHTML=formHTML+'<div class="form-group" >';
                      formHTML=formHTML+'<label class="control-label col-sm-2" for="regFile">Model File</label>';
                      formHTML=formHTML+'<div class="col-sm-10">';
                      formHTML=formHTML+'<input name = "regFile" id = "regFile" type = "file" class = "form-control" style="opacity: 100; position: inherit;" />';
                      formHTML=formHTML+'</div>';
                      formHTML=formHTML+'</div>';
                      formHTML=formHTML+'<button class = "btn btn-default  btn-success" style="margin-top: 30px;background: lightsteelblue;" type = "button" onClick = "uploadZip(\''+"model"+'\',\''+"regFile"+'\')" href = "#" >Upload File</button >';
                      formHTML=formHTML+'<div class="clearfix"></div>';
                      formHTML=formHTML+'</div>';
                      formHTML=formHTML+'</form>';
                      div.innerHTML = formHTML;
                      }
                      else{
                      buildForm(loadMethod, 'modelRequiredFields');}
                      }
          </script>


  <div  ng-app="app" id="preMessageDetails" ng-controller="myCtrl">




  	<div id="bdre-data-load">

                  <h2><div class="number-circular">4</div>Model Type</h2>
                  <section>
                  <form class="form-horizontal" role="form" id="modelDetail">

                  <div class="form-group" >
                                <label class="control-label col-sm-2" for="modelType">Model Type</label>
                                <div class="col-sm-10">
                                 <select class="form-control" id="modelType" name="modelType" ng-model = "modelName" onchange="loadModelOptions(this.value);" ng-options = "val.DisplayText as val.DisplayText for (file,val) in modelList track by val.DisplayText"   >
                                             <option  value="">Select option</option>
                                         </select>
                                </div>
                            </div>

                            <div class="form-group" >
                                <label class="control-label col-sm-2" for="loadOptions">Load Method</label>
                                <div class="col-sm-10">
                                 <select class="form-control" id="loadOptions" name="loadOptions" onchange="loadModelProperties(this.value);" >
                                             <option  value="">Select option</option>
                                         </select>
                                </div>
                            </div>

                  <div class="clearfix"></div>
                  </form>
                  </section>

                  <h2><div class="number-circular">5</div>Model Configuration</h2>
                  <section>
                        <div id="modelRequiredFields"></div>
                  </section>

                  <h2><div class="number-circular">6</div>Create Model</h2>
                  <section>
                  <form class="form-horizontal" role="form" id="modelConfirmation">
                  <div class="form-group" >
                          <label class="control-label col-sm-2" for="modelName">Model Name</label>
                          <div class="col-sm-10">
                           <input type="text" class="form-control"  id="modelName" >
                          </div>
                      </div>

                   <div class="form-group" >
                         <label class="control-label col-sm-2" for="modelDescription">Model Description</label>
                         <div class="col-sm-10">
                          <input type="text" class="form-control"  id="modelDescription" >
                         </div>
                       </div>
                  <button class = "btn btn-default  btn-success" style="margin-top: 30px;background: lightsteelblue;" type = "button" onClick = "saveModelProperties()"  >Create Job</button >
                  </form>
                  </section>


                          			<div style = "display:none" id = "div-dialog-warning" >
                                                    				<p ><span class = "ui-icon ui-icon-alert" style = "float:left;" ></span >

                                                    				</p>
                                                    				</div >

  		</div>

  </div>


  <script>
  var i=0;
  </script>
                <script>
                function loadModelTypes()
                {
                    if(i==0){
                    var processId=41;
                    $.ajax({
                        type: "POST",
                        url: "/mdrest/processtype/options_analytics/"+processId,
                        dataType: 'json',
                        success: function(data) {
                        console.log(data);
                        $.each(data.Options, function (i, v) {
                            $('#modelType').append($('<option>', {
                                value: v.value,
                                text : v.DisplayText,
                            }));
                        });
                        },
                    });
                    i=i+1;
                    }
                }


                </script>


        <script>
        function buildForm(typeOf, typeDiv) {
        	$.ajax({
        		type: "GET",
        		url: "/mdrest/genconfig/" + typeOf + "/?required=1",
        		dataType: 'json',
        		success: function(data) {
        			var root = 'Records';
        			var div = document.getElementById(typeDiv);
        			var formHTML = '';
        			formHTML = formHTML + '<form role="form" id = "' + typeDiv + 'Form">';
        			console.log(data[root]);
        			$.each(data[root], function(i, v) {
        				formHTML = formHTML + '<div class="form-group"> <label for="' + v.key + '">' + v.value + '</label>';
        				<!-- formHTML = formHTML + '<span class="glyphicon glyphicon-question-sign" title="' + v.description + '"></span>'; -->
        				formHTML = formHTML + '<input name="' + v.key + '" value="' + v.defaultVal + '" placeholder="' + v.description + '" type="' + v.type + '" class="form-control" id="' + v.key + '"></div>';
        			});
        			formHTML = formHTML + '</form>';
        			div.innerHTML = formHTML;
        			console.log(div);
        		}
        	});
        	return true;
        }
        </script>

        <script>

        function loadModelOptions(modelType){
        console.log(modelType);
        var model = modelType+"_Model";
        $.ajax({
            type: "GET",
            url: "/mdrest/genconfig/" + model + "/?required=1",
            dataType: 'json',
            success: function(data) {
            console.log(data);
            $('#loadOptions').find('option').remove();
            $.each(data.Records, function (i, v) {
                $('#loadOptions').append($('<option>', {
                    value: v.key,
                    text : v.value,
                }));
            });
            },
        });

        }

        </script>