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
    <body>

    <script>
    var aggregationFinal ="";
                   var intercept="";
           var columns =[];
           var map2=new Map();
    		var insert=1;
    		var hiveInsert=0;
    		var databaseList={};
         var wizard = null;
         var finalJson;
         wizard = $(document).ready(function() {
        var created = 0;
    	$("#bdre-data-load").steps({
    		headerTag: "h2",
    		bodyTag: "section",
    		transitionEffect: "slideLeft",
    		stepsOrientation: "vertical",
    		enableCancelButton: true,
    		onStepChanging: function(event, currentIndex, newIndex) {
    			console.log(currentIndex + 'current ' + newIndex );
    			if(currentIndex==0 && newIndex==1){
               if(hiveInsert==1 && document.getElementById("persistentName").value !="Hive")
                      {
                      $('#bdre-data-load').steps('remove',2);
                      hiveInsert=0;
                      }
                        }
    			if(currentIndex==1 && newIndex==2){
    			var source = document.getElementById("persistentName").value;
    			console.log(source);
    			if(source=="HDFS" && insert==1){
    			var fileformat = $('[ng-controller="myCtrl"]').scope().fileformats;
    			var formHTML='';
    			formHTML=formHTML+'<section>';
    			formHTML=formHTML+'<form class="form-horizontal" role="form" id="sourceDataSchema">';
    			formHTML=formHTML+'<div class="form-group">';
    			formHTML=formHTML+'<label class="control-label col-sm-2"  for="fileformat">Message Format</label>';
    			formHTML=formHTML+'<div class="col-sm-10">';
    			formHTML=formHTML+'<select class="form-control" id="fileformat" name="fileformat" onchange="changeme(this.value)" >';
    			formHTML=formHTML+'<option  value="">Select the option</option>';
    			for (var key in fileformat) {
                   if (fileformat.hasOwnProperty(key)) {
                   formHTML=formHTML+'<option value="'+ key + '">' + fileformat[key].value + '</option>';
                   }
               }
    			formHTML=formHTML+'</select>';
    			formHTML=formHTML+'</div>';
    			formHTML=formHTML+'</div>';
    			formHTML=formHTML+'</form>';
    			formHTML=formHTML+'</section>';
    			$('#bdre-data-load').steps('insert', 2, { title: 'Data Format', content: formHTML });
    			insert=insert+1;
    			}
    			if(insert==2 && document.getElementById("persistentName").value !="HDFS")
                   {
                   $('#bdre-data-load').steps('remove',2);
                   insert=insert-1;
                   }
                   if(source=="Hive" && hiveInsert==0){
                      console.log("Adding new section for databse and table");
                            $.ajax({
                                                   type: "GET",
                                                   url: "/mdrest/hivemigration/databases/localhost:10000",
                                                   dataType: 'json',
                                                   async: false,
                                                   success: function (data) {
                                                       databaseList = data.Options;
                                                       console.log(databaseList);
                                                   },
                                                   error: function () {
                                                       alert('danger');
                                                   }
                                               });


                  var formHTML="";
                  formHTML=formHTML+'<section>';
                  formHTML=formHTML+'<form class="form-horizontal" role="form" id="hiveDBTable">';
                  formHTML=formHTML+'<div class="form-group" >';

               formHTML = formHTML + '<label class="control-label col-sm-2" for="hive-db">Database Name</label>';
                   formHTML = formHTML +  '<div class="col-md-4">' ;
               formHTML = formHTML + '  <select class="form-control" id="hive-db" name="hive-db" onchange="loadTableOptions(this.value)">';
               //
               formHTML=formHTML+'<option value="select">Select Option</option>';
               for(var k=0;k<databaseList.length;k++){

                                       formHTML=formHTML+'<option value="'+ databaseList[k].Value + '">' + databaseList[k].DisplayText + '</option>';


               }

               formHTML = formHTML + '</select>';
               formHTML = formHTML +  '</div>' ;
               formHTML = formHTML +  '</div>' ;
               formHTML=formHTML+'<div class="form-group" >';
               formHTML = formHTML + '<label class="control-label col-sm-2" for="hive-table">Table Name</label>';
                 formHTML = formHTML +  '<div class="col-md-4">' ;

                          formHTML = formHTML + '  <select class="form-control" id="hive-table" name="hive-table" >';

                          formHTML = formHTML + '</select>';
                          formHTML = formHTML +  '</div>' ;
                          formHTML = formHTML +  '</div>' ;
               formHTML = formHTML +  '</form>' ;
               formHTML = formHTML +  '</section>' ;
               $('#bdre-data-load').steps('insert', 2, { title: 'Database and Table', content: formHTML });
                      hiveInsert=1;
                   }
            }

    		return true;
    		},
    		onStepChanged: function(event, currentIndex, priorIndex) {
    			        console.log(currentIndex + " " + priorIndex);
    			       if(priorIndex==2 && currentIndex==3 && document.getElementById("persistentName").value=="HDFS"){
    			       console.log("Printing the fileformat");
    			       console.log(document.getElementById("fileformat").value);

                            	$('#rawTableColumnDetails').jtable({});
                                $('#rawTableColumnDetails').jtable('destroy');
                                    $('#rawTableColumnDetails').jtable({
                                        title: 'Schema column details',
                                        paging: false,
                                        sorting: false,
                                        create: false,
                                        edit: false,
                                        actions: {
                                            listAction: function(postData){
                                            return $.Deferred(function ($dfd) {
                                            if(document.getElementById("fileformat").value=="Json" || document.getElementById("fileformat").value=="XML"){
                                            console.log("Entered the if block");
                                            console.log(restWrapper);
                                            for(var i=0;i<restWrapper.Record.length;i++){
                                            restWrapper.Record[i].dataType="Double";
                                            restWrapper.Records[i].dataType="Double";
                                            }
                                            $dfd.resolve(restWrapper);}
                                            else{
                                            console.log("Entered the else block");
                                            var jsonEmptyObj = {"Result":"OK","Records":[],"Message":null,"TotalRecordCount":0,"Record":[]};
                                            $dfd.resolve(jsonEmptyObj);}
                                            });
                                            },
                                            createAction: function(postData) {
                                                console.log(postData);
                                                var serialnumber = 1;
                                                var rawSplitedPostData = postData.split("&");
                                                var rawJSONedPostData = '{';
                                                rawJSONedPostData += '"serialNumber":"';
                                                rawJSONedPostData += serialnumber;
                                                serialnumber += 1;
                                                rawJSONedPostData += '"';
                                                rawJSONedPostData += ',';
                                                for (i=0; i < rawSplitedPostData.length ; i++)
                                                {
                                                    console.log("data is " + rawSplitedPostData[i]);
                                                    rawJSONedPostData += '"';
                                                    rawJSONedPostData += rawSplitedPostData[i].split("=")[0];
                                                    rawJSONedPostData += '"';
                                                    rawJSONedPostData += ":";
                                                    rawJSONedPostData += '"';
                                                    rawJSONedPostData += rawSplitedPostData[i].split("=")[1];
                                                    rawJSONedPostData += '"';
                                                    rawJSONedPostData += ',';
                                                    console.log("json is" + rawJSONedPostData);
                                                }
                                                var rawLastIndex = rawJSONedPostData.lastIndexOf(",");
                                                rawJSONedPostData = rawJSONedPostData.substring(0,rawLastIndex);
                                                rawJSONedPostData +=  "}";
                                                console.log(rawJSONedPostData);
                                               var rawReturnObj='{"Result":"OK","Record":' + rawJSONedPostData + '}';
                                               var rawJSONedReturn = $.parseJSON(rawReturnObj);
                                               return $.Deferred(function($dfd) {
                                                                console.log(rawJSONedReturn);
                                                                $dfd.resolve(rawJSONedReturn);
                                                            });
                                                },
                                               updateAction: function(postData) {

                                                return $.Deferred(function($dfd) {
                                                    console.log(postData);
                                                    $dfd.resolve(jsonObj);
                                                });
                                                },
                                                deleteAction: function(item) {
                                                    console.log(item.key);
                                                    return $.Deferred(function($dfd) {
                                                        $dfd.resolve(jsonObj);
                                                    });
                                                }

                                            },
                                        fields: {
                                            serialNumber:{
                                                key : true,
                                                list:false,
                                                create : false,
                                                edit:false
                                            },
                                            columnName: {
                                                title: '<spring:message code="dataload.page.title_col_name"/>',
                                                width: '50%',
                                                edit: true,
                                                create:true
                                            },
                                            dataType: {
                                                create: true,
                                                title: 'Data Type',
                                                edit: true,
                                                options:{ 'String':'String',
                                                          'Integer':'Integer',
                                                          'Long':'Long',
                                                          'Short':'Short',
                                                          'Byte':'Byte',
                                                          'Float':'Float',
                                                          'Double':'Double',
                                                          'Decimal':'Decimal',
                                                          'Boolean':'Boolean',
                                                          'Decimal':'Decimal',
                                                          'Binary' : 'Binary',
                                                          'Date':'Date',
                                                          'TimeStamp':'TimeStamp'
                                                          }
                                            }
                                        }
                                    });
                                    console.log($('#rawTableColumnDetails'));
                                    $('#rawTableColumnDetails').jtable('load');
                                    console.log($('#rawTableColumnDetails'));
                                }
                                if(priorIndex==2 && currentIndex==3 && document.getElementById("persistentName").value=="Hive")
                                {
                                $('#rawTableColumnDetails').jtable({});
                                     $('#rawTableColumnDetails').jtable('destroy');
                                     var srcDb=document.getElementById("hive-db").value;
                                     var tbl=document.getElementById("hive-table").value;
                                     var srenv="localhost:10000";
                                     console.log(srcDb);
                                     console.log(tbl);
                                     $('#rawTableColumnDetails').jtable({
                                     title: 'Hive column details',
                                     actions: {
                                       listAction: function(postData){
                                       return $.Deferred(function ($dfd) {
                                       $.ajax({
                                         url: "/mdrest/ml/columns/" + srenv + '/' + srcDb + '/' + tbl,
                                             type: 'GET',
                                             dataType: 'json',
                                             async: false,
                                             success: function (data) {
                                                 var hiveColumns=data.Records;
                                                 console.log(hiveColumns);
                                                 for(var k in hiveColumns) {
                                                             console.log(hiveColumns[k].columnName);
                                                             columns.push(hiveColumns[k].columnName);
                                                     }
                                                 $dfd.resolve(data);
                                             },
                                             error: function () {
                                                 alert('danger');
                                             }
                                         });
                                         });
                                      },
                                      deleteAction: function(postData) {
                                   return $.Deferred(function ($dfd) {
                                          $.ajax({
                                            url: "/mdrest/ml/columns/" + srenv + '/' + srcDb + '/' + tbl,
                                                type: 'GET',
                                                dataType: 'json',
                                                async: false,
                                                success: function (data) {

                                                    $dfd.resolve(data);
                                                },
                                                error: function () {
                                                    alert('danger');
                                                }
                                            });
                                            });
                                       },

                                     updateAction: function(postData) {
                                                              }
                                                              },
                        fields: {
                                            columnName: {
                                                title: '<spring:message code="dataload.page.title_col_name"/>',
                                                width: '50%',
                                                edit: true,
                                                create:true,
                                                list:true,
                                                key:true
                                            },
                                            dataType: {
                                                create: true,
                                                title: 'Data Type',
                                                edit: true,
                                                list:true,
                                                key:false
                                            }
                                        }
                                     });
                                     $('#rawTableColumnDetails').jtable('load');
                                }
                                if(priorIndex==1 && currentIndex==2 && document.getElementById("persistentName").value=="HBase")
                                {
                                $('#rawTableColumnDetails').jtable({});
                                   $('#rawTableColumnDetails').jtable('destroy');
                                   $('#rawTableColumnDetails').jtable({
                                    title: 'HBase column details',
                                    });
                                    console.log($('#rawTableColumnDetails'));
                                    //$('#rawTableColumnDetails').jtable('load');
                                    console.log($('#rawTableColumnDetails'));
                                                        }
    		},
    		onFinished: function(event, currentIndex) {
                location.href = '<c:url value="/pages/ViewModel.page"/>';
    		},
    		onCanceled: function(event) {
    		}
    	});
    });
    		</script>
<script>
 function loadTableOptions(dbName){
            $.ajax({
                type: "GET",
                url: "/mdrest/hivemigration/tables/localhost:10000/" + dbName ,
                dataType: 'json',
                async: false,
                success: function (data) {
                    console.log(data);
                    $('#hive-table').find('option').remove();
                                $('#hive-table').append('<option  value="">Select option</option>');
                                $.each(data.Options, function (i, v) {
                                    $('#hive-table').append($('<option>', {
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
  </script>
  <script>
                  var app = angular.module('app', []);
                     app.controller('myCtrl',function($scope) {
                      $scope.modelList={};
                      $scope.persistentList={};
                      $scope.fileformats= getGenConfigMap('file_format');
                      $scope.columnList={};
                        $scope.processId=86;
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
                        $scope.model="ML_Model";
                     $.ajax({
                             type: "GET",
                             url: "/mdrest/genconfig/" + $scope.model + "/?required=1",
                             dataType: 'json',
                             async: false,
                             success: function (data) {
                                 $scope.modelList = data.Records;
                                 console.log($scope.modelList);
                             },
                             error: function () {
                                 alert('danger');
                             }
                         });
                         $scope.srenv="localhost:10000";


                         $.ajax({
                         url: "/mdrest/genconfig/PersistentStores_Connection_Type/?required=2",
                         type: 'POST',
                          dataType: 'json',
                          async: false,
                          success: function (data) {
                          $scope.persistentList = data.Records;
                                                           console.log("hiiii");
                                                           console.log($scope.persistentList);
                                                           console.log("hiiii");
                          },
                                                       error: function () {
                                                           alert('danger');
                                                       }
                         });
                  });
                   function saveModelProperties(){
                                             console.log("saveModelProperties is being called");
                                           formIntoMap("","modelConfirmation");
                                           formIntoMap("","persistentStore");
                                           formIntoMap("","persistentFieldsForm");
                                           formIntoMap("","modelDetail");
                                           formIntoMap("","hiveDBTable");
                                           var text2 = $('#loadOptions option:selected').text();
                                           var model_Type=document.getElementById("modelType").value;
                                           console.log(text2);
                                           if(text2=="PMML File" || text2=="Serialized Model"){
                                           //formIntoMap("","modelData");
                                           var fileName=document.getElementById("regFile").value;
                                           console.log(fileName.split("\\")[2]);
                                           map["pmml-file-path"]=fileName.split("\\")[2];
                                           }
                                           else if (model_Type=="LogisticRegression" || model_Type=="LinearRegression"){
                                                intercept=document.getElementById("Intercept.1").value;

                                                  var text=document.getElementById("Column.1").value;

                                                  text=text.concat(":");
                                                 text=text.concat(document.getElementById("Coefficient.1").value);
                                                  for(i=2;i<=count;i++){

                                                     text=text.concat(",");
                                                    s1=document.getElementById("Column." + i).value;

                                                      text=text.concat(s1);
                                                      text=text.concat(":");
                                                      text=text.concat(document.getElementById("Coefficient." + i).value);

                                                  }
                                                  console.log("hello");
                                                  console.log(text);

                                                  map["intercept"]=intercept;
                                                  map["coefficients"]=text;
                                           }
                                           else{
                                           var value1=$(".js-example-basic-multiple").select2("val");
                                           console.log("value1 ",value1);
                                           var continuousValue=value1[0];
                                           for(var l=1;l<value1.length;l++)
                                           {
                                                continuousValue=continuousValue.concat(",");
                                                var s=value1[l];
                                                continuousValue=continuousValue.concat(s);
                                           }
                                           var text=document.getElementById("Information.1").value;
                                           for(i=2;i<=count1;i++){
                                           text=text.concat(":");
                                           text=text.concat(document.getElementById("Information."+i).value);
                                           }
                                           map["clusters"]=text;
                                           map["features"]=continuousValue;
                                           }

                                           jtableIntoMap("", "rawTableColumnDetails");
                                            var columns="";
                                            var i=0;

                                           jtableIntoMap("", "rawTableColumnDetails");
                                           console.log("Printing the jtable map");
                                           console.log(map1);

                                            var attribute="";
                                            var i=1;

                                           for (var key in map1) {
                                               if (map1.hasOwnProperty(key)) {
                                               attribute=attribute.concat(key);

                                               attribute=attribute.concat(":");
                                               attribute=attribute.concat(map1[key].charAt(0).toUpperCase()+map1[key].slice(1));

                                               if(i<Object.keys(map1).length){
                                               attribute=attribute.concat(",");}

                                               }
                                               i=i+1;
                                           }
                                           console.log(attribute);

                                           map["schema"]=attribute;

                                           if(document.getElementById("persistentName").value=="HDFS"){
                                           map["fileformat"]=document.getElementById("fileformat").value;
                                           if(document.getElementById("fileformat").value=="Delimited"){map["Delimiter"]=document.getElementById("delimiter").value;}
                                           else{map["schema-file-path"]=document.getElementById("formatFile").value;}
                                           }


                                           console.log("Printing the map");
                                           console.log(map);
                                             $.ajax({
                                                         type: "POST",
                                                         url: "/mdrest/ml/createjobs/",
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
                                            				}).html('<p><span class="jtable-confirm-message">Model Created Successfully</span></p>');




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
                                                            }).html('<p><span class="jtable-confirm-message">Model Created Successfully</span></p>');

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
                                    var restWrapper=new Object();
                                             var uploadedFileName ="";
                                              function uploadFile(msgformat){
                                             var arg= ["formatFile"];
                                               var fd = new FormData();
                                              var fileObj = $("#"+arg[0])[0].files[0];
                                              var fileName=fileObj.name;
                                              fd.append("file", fileObj);
                                              fd.append("name", fileName);
                                              console.log("message format : "+msgformat);
                                              $.ajax({
                                                url: '/mdrest/filehandler/uploadFile/'+msgformat,
                                                type: "POST",
                                                data: fd,
                                                async: false,
                                                enctype: 'multipart/form-data',
                                                processData: false,  // tell jQuery not to process the data
                                                contentType: false,  // tell jQuery not to set contentType
                                                success:function (data) {
                                                      uploadedFileName=data.Record.fileName;
                                                      console.log( data );
                                                      restWrapper=data.Record.restWrapper;
                                                      console.log(restWrapper);
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
          var count=0;
          var count1=0;
          </script>
          <script>
          function loadModelProperties(loadMethod) {
          jtableIntoMap("", "rawTableColumnDetails");
          var columnNames = [];
          for (var key in map1) {
             if (map1.hasOwnProperty(key)) {
             columnNames.push(key);
             }
          }
          if(count1>0)
          var selected=$(".js-example-basic-multiple").select2("val");
          var inter=0;
          var prevCoeffValues=[];
          var prevClusterValues=[];
          var prevColumnNames=[];
          if(count>0){
             inter=document.getElementById("Intercept.1").value;
             for(var j=1;j<=count;j++){
                prevCoeffValues.push(document.getElementById("Coefficient."+ j).value);
                prevColumnNames.push(document.getElementById("Column."+ j).value);
             }
          }
          if(count1>0){

                       for(var j=1;j<=count1;j++){
                          prevClusterValues.push(document.getElementById("Information."+ j).value);
                       }
                    }


              console.log(loadMethod);
              var div = document.getElementById('modelRequiredFields');
                      if(loadMethod=="serializedModel" || loadMethod=="pmmlFile"){
                      count=0;
                      count1=0;
                      var formHTML='';
                      formHTML=formHTML+'<form class="form-horizontal" role="form" id="modelData">';
                      formHTML=formHTML+'<div id="rawTablDetailsDB">';
                      formHTML=formHTML+'<div class="form-group" >';
                      formHTML=formHTML+'<label class="control-label col-sm-2" for="regFile">Model File</label>';
                      formHTML=formHTML+'<div class="col-sm-10">';
                      formHTML=formHTML+'<input name = "pmml-file-path" id = "regFile" type = "file" class = "form-control" style="opacity: 100; position: inherit;" />';
                      formHTML=formHTML+'</div>';
                      formHTML=formHTML+'</div>';
                      formHTML=formHTML+'<button class = "btn btn-default  btn-success" style="margin-top: 30px;background: lightsteelblue;" type = "button" onClick = "uploadZip(\''+"model"+'\',\''+"regFile"+'\')" href = "#" >Upload File</button >';
                      formHTML=formHTML+'<div class="clearfix"></div>';
                      formHTML=formHTML+'</div>';
                      formHTML=formHTML+'</form>';
                      div.innerHTML = formHTML;
                      }
                      else if(loadMethod=='ModelInformation'){
                      var model = document.getElementById("modelType").value;
                      console.log(model);
                      if(model=="LogisticRegression" || model=="LinearRegression"){
                      count1=0;

                      console.log("Enter ModelInformation");
                          //console.log(coefficients);
                          var formHTML='';

                          var next=1;
                          var column;
                            formHTML=formHTML+'<div class="col-md-12" >';
                        formHTML=formHTML+'<div class="col-md-4">Column </div>';
                        formHTML=formHTML+'<div class="col-md-4">Coefficient</div>';
                        formHTML=formHTML+'<div class="col-md-4">Intercept</div>';

                        formHTML=formHTML+'</div>';


                        for(var t=0;t<=count;t++){


                        formHTML=formHTML+'<div class="col-md-12" >';
                        formHTML = formHTML +  '<div class="col-md-4">' ;

                        formHTML = formHTML + '  <select class="form-control" id="Column.' + next + '" name="Column.' + next + '" >';


                        for(var k=0;k<columnNames.length;k++){
                         if(columnNames[k]==prevColumnNames[t]){
                                                formHTML=formHTML+'<option value="'+ columnNames[k] + '" selected>' + columnNames[k] + '</option>';
                                                }
                                                else{
                                                formHTML=formHTML+'<option value="'+ columnNames[k] + '">' + columnNames[k] + '</option>';}
                        }

                        formHTML = formHTML + '</select>';
                        formHTML = formHTML +  '</div>' ;
                        formHTML = formHTML +  '<div class="col-md-4">' ;
                        if(t==count){
                        formHTML = formHTML +  '<input class="form-control" id="Coefficient.' + next + '"value='+ 0 +' name="Coefficient.' + next + '">' ;}
                        else{
                        formHTML = formHTML +  '<input class="form-control" id="Coefficient.' + next + '"value='+ prevCoeffValues[t] +' name="Coefficient.' + next + '">' ;
                        }
                        formHTML = formHTML +  '</input>' ;
                        formHTML = formHTML +  '</div>' ;

                        if(t==0){
                        formHTML = formHTML +  '<div class="col-md-4">' ;
                        formHTML = formHTML +  '<input class="form-control" id="Intercept.' + next + '"value='+ inter +' name="Intercept.' + next + '">' ;
                        formHTML = formHTML +  '</input>' ;
                        formHTML = formHTML +  '</div>' ;
                        formHTML=formHTML+'</div>';
                        }
                        else
                        {
                        console.log("Wht's up");
                        formHTML = formHTML +  '<div class="col-md-4">' ;

                        formHTML = formHTML +  '</div>' ;

                        formHTML=formHTML+'</div>';

                        }
                        next++;

                        }


                        count++;
                        formHTML=formHTML+'<div id="count" value="' + count + '"></div>';


                        if(count<columnNames.length)

                        formHTML=formHTML+'<button class = "btn btn-default  btn-success" style="margin-top: 30px;background: lightsteelblue;" type = "button" onClick = loadModelProperties("ModelInformation")  >Add Column</button >';

                        div.innerHTML = formHTML;}
                        else{
                        count=0;
                        var formHTML='';
                        if(count1==0){
                        formHTML=formHTML+'<div id="clusterFeatures"';
                        formHTML=formHTML + ' <label class="form-control" for="features">Select Features</label>';
                       formHTML=formHTML + ' <select class="js-example-basic-multiple" id="features" name="features" multiple="multiple">';
                       for(var k=0;k<columnNames.length;k++){
                           formHTML=formHTML+'<option value="'+ columnNames[k] + '">' + columnNames[k] + '</option>';
                           }
                           formHTML=formHTML+"</select>";
                           formHTML=formHTML+'</div>';
                           }
                           else{
                                formHTML=formHTML+'<div id="clusterFeatures"';
                                formHTML=formHTML + ' <label class="form-control" for="features">Select Features</label>';
                               formHTML=formHTML + ' <select class="js-example-basic-multiple" id="features" name="features" multiple="multiple">';
                               for(var k=0;k<columnNames.length;k++){
                                   formHTML=formHTML+'<option value="'+ columnNames[k] + '">' + columnNames[k] + '</option>';
                                   }
                                   formHTML=formHTML+"</select>";
                                   formHTML=formHTML+'</div>';

                                               }

                              var next=1;
                              var column;
                              formHTML=formHTML+'<br>';
                              formHTML=formHTML+'<br>';
                              formHTML=formHTML+'<br>';
                              formHTML=formHTML+'<br>';
                              formHTML=formHTML+'<br>';
                              formHTML=formHTML+'<div id="clusterCentres"';
                                formHTML=formHTML+'<div class="col-md-12" >';
                            formHTML=formHTML+'<div class="col-md-4">Cluster No</div>';
                            formHTML=formHTML+'<div class="col-md-4">Cluster Centre</div>';

                            formHTML=formHTML+'</div>';


                            for(var t=0;t<=count1;t++){


                            formHTML=formHTML+'<div class="col-md-12" >';
                            formHTML = formHTML +  '<div class="col-md-4">' ;
                            formHTML = formHTML +  '<input class="form-control" id="Cluster.' + next + '"value='+ next +' name="Cluster.' + next + '">' ;
                            formHTML = formHTML +  '</input>' ;
                            formHTML = formHTML +  '</div>' ;
                            formHTML = formHTML +  '<div class="col-md-4">' ;
                            if(t==count1)
                            formHTML = formHTML +  '<input class="form-control" id="Information.' + next + '"value='+ 0 +' name="Information.' + next + '">' ;
                            else
                            formHTML = formHTML +  '<input class="form-control" id="Information.' + next + '"value='+ prevClusterValues[t] +' name="Information.' + next + '">' ;
                            formHTML = formHTML +  '</input>' ;
                            formHTML = formHTML +  '</div>' ;
                            formHTML=formHTML+'</div>';

                            next++;

                            }

                            count1++;

                            formHTML=formHTML+'<button class = "btn btn-default  btn-success" style="margin-top: 30px;background: lightsteelblue;" type = "button" onClick = loadModelProperties("ModelInformation")  >Add Cluster</button >';
                            formHTML=formHTML+'</div>';
                            div.innerHTML = formHTML;

                            console.log("hhahahhahaha");
                            $(".js-example-basic-multiple").select2();
                            console.log("hhahahhahaha");
                            if(count1>1){
                            $('#features').val(selected);

                            $('#features').trigger('change');
                            }
                        }

                                  }
                                  else{
                                  count=0;
                                  count1=0;
                                  var formHTML='';
                                  div.innerHTML = formHTML;
                                  }
                      }
          </script>



  <div  ng-app="app" id="preModelDetails" ng-controller="myCtrl">




  	<div id="bdre-data-load">

<h2><div class="number-circular">1</div>Select Source</h2>
                        			<section>
                    <form class="form-horizontal" role="form" id="persistentStore">
                    <div class="form-group" >
                                  <label class="control-label col-sm-2" for="persistentName">Source</label>
                                  <div class="col-sm-10">
                                   <select class="form-control" id="persistentName" name="source"  ng-model="persistentName"  onchange="loadProperties();" ng-options = "val.columnName as val.columnName for (file, val) in persistentList track by val.columnName"  >
                                               <option  value="">Select the option</option>
                                           </select>
                                  </div>
                              </div>

                              </form>
                              </section>

  <h2><div class="number-circular">2</div>Source Configuration</h2>
                          			<section>

                       <div id="persistentFields"></div>
<div class="clearfix"></div>




                                        </section>
                                        <h2><div class="number-circular">3</div>Data Schema</h2>
                                        <section>
                                        <form class="form-horizontal" role="form" id="schema">
                                                        <div id="rawTableColumnDetails"></div>
                                                        <div class="clearfix"></div>
                                                        </form>
                                                        </section>

                                                         </section>

                  <h2><div class="number-circular">4</div>Model Type</h2>
                  <section>
                  <form class="form-horizontal" role="form" id="modelDetail">

                  <div class="form-group" >
                                <label class="control-label col-sm-2" for="modelType">Model Type</label>
                                <div class="col-sm-10">
                                 <select class="form-control" id="modelType" name="ml-algo" ng-model = "modelName" onchange="loadModelOptions(this.value);" ng-options = "val.key as val.value for (file,val) in modelList track by val.key" >
                                             <option  value="">Select option</option>
                                         </select>
                                </div>
                            </div>

                            <div class="form-group" >
                                <label class="control-label col-sm-2" for="loadOptions">Load Method</label>
                                <div class="col-sm-10">
                                 <select class="form-control" id="loadOptions" name="model-input-method" onchange="loadModelProperties(this.value);" >
                                             //<option  value="">Select option</option>
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
                           <input type="text" class="form-control col-sm-2"  id="modelName" name="modelName">
                          </div>
                      </div>

                   <div class="form-group" >
                         <label class="control-label col-sm-2" for="modelDescription">Model Description</label>
                         <div class="col-sm-10">
                          <input type="text" class="form-control col-sm-2"  id="modelDescription" name="modelDescription">
                         </div>
                       </div>

                       <div class="form-group">
                       <label class="control-label col-sm-2" for="modelBusDomain"><spring:message code="hivetablemigration.page.form_bus_domain_id"/></label>
                       <div class="col-sm-10">
                           <select class="form-control" id="modelBusDomain" name="modelBusDomain">
                               <option ng-repeat="busDomain in busDomains.Options" value="{{busDomain.Value}}" name="modelBusDomain">{{busDomain.DisplayText}}</option>

                           </select>
                       </div>
                   </div>
                  </form>
                  <button class="btn btn-default  btn-success" style="margin-top: 200px;background: #F2B30B !important;padding-left: 0px;margin-left: 0px;left: -400px;" type="button" onclick="saveModelProperties()">Create Model</button>

                  </section>


                          			<div style = "display:none" id = "div-dialog-warning" >
                                                    				<p ><span class = "ui-icon ui-icon-alert" style = "float:left;" ></span >

                                                    				</p>
                                                    				</div >

  		</div>

  </div>


<div style="display:none" id="div-dialog-warning">
			<p><span class="ui-icon ui-icon-alert" style="float:left;"></span></p>
		</div>

  <script>
  var i=0;
  </script>
                <script>

                function loadProperties() {
                    var text = $('#persistentName option:selected').text();
                        buildForm(text + "_Model_Connection", "persistentFields");
                        console.log("This is the div");
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
            $('#loadOptions').append('<option  value="">Select option</option>');
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

        <script>
        var map1 = new Object();
        function jtableIntoMap(typeProp, typeDiv) {
        	var div = '';
        	div = document.getElementById(typeDiv);
        	$('div .jtable-data-row').each(function() {
        		//console.log(this);
        		$(this).addClass('jtable-row-selected');
        		$(this).addClass('ui-state-highlight');
        	});
        	var $selectedRows = $(div).jtable('selectedRows');
        	$selectedRows.each(function() {
        		var record = $(this).data('record');
        		var keys = typeProp + record.columnName;
        		//console.log(keys);
        		map1[keys] = record.dataType;
        		//console.log(map1);
        	});
        	$('.jtable-row-selected').removeClass('jtable-row-selected');
        }
        		</script>

        		<script>
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

                function changeme(fileformat){
                console.log(fileformat);
                var div = document.getElementById("sourceDataSchema");
                var formatList = $('[ng-controller="myCtrl"]').scope().fileformats;
                formHTML='';
                formHTML=formHTML+'<div class="form-group">';
                formHTML=formHTML+'<label class="control-label col-sm-2"  for="fileformat">Message Format</label>';
                formHTML=formHTML+'<div class="col-sm-10">';
                formHTML=formHTML+'<select class="form-control" id="fileformat" name="fileformat" onchange="changeme(this.value)" >';
                formHTML=formHTML+'<option  value="">Select the option</option>';
                for (var key in formatList) {
                   if (formatList.hasOwnProperty(key)) {
                   if(key==fileformat){formHTML=formHTML+'<option value="'+ key + '" selected>' + formatList[key].value + '</option>';}
                   else
                   {formHTML=formHTML+'<option value="'+ key + '">' + formatList[key].value + '</option>';}
                   }
                }
                formHTML=formHTML+'</select>';
                formHTML=formHTML+'</div>';
                formHTML=formHTML+'</div>';
                if(fileformat=="Delimited"){
                formHTML=formHTML+'<div class="form-group" id="delimiteddiv">';
                formHTML=formHTML+'<label class="control-label col-sm-2" for="delimiter">Delimiter</label>';
                formHTML=formHTML+'<div class="col-sm-10">';
                formHTML=formHTML+'<input type="text" class="form-control"  id="delimiter" name="delimiter" value="" required>';
                formHTML=formHTML+'</div>';
                formHTML=formHTML+'</div>';
                }
                else{
                formHTML=formHTML+'<div class="form-group">';
                formHTML=formHTML+'<label for = "fileUpload" >File Upload</label >';
                formHTML=formHTML+'<input name = "formatFile" id = "formatFile" type = "file" class = "form-control" style="opacity: 100; position: inherit;" /></div>';
                formHTML=formHTML+'<div class="form-group">';
                //formHTML=formHTML+'<div class="clearfix"></div>';
                var format = document.getElementById('fileformat').value;
                formHTML=formHTML+'<button class = "btn btn-default  btn-success" style="margin-top: 30px;background: lightsteelblue;" type = "button" onClick = "uploadFile(\''+format+'\')" href = "#" >Upload File</button >';
                }
                div.innerHTML=formHTML;
                }

        		</script>
</body>
