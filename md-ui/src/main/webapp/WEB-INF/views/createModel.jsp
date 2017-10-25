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

        <script>
        var selectedModelType = '';
        </script>
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
    			location.href = '<c:url value="/pages/createModel.page"/>';
    		}
    	});
    });

    		</script>


  <div  id="preModelDetails">
   <div >
  	<div id="bdre-data-load" >
  			<h3>Model Details</h3>
              			<section>
              <form class="form-horizontal" role="form" id="modelDetail">

              <div id="dropdownModel">
              <div class="form-group" >
                            <label class="control-label col-sm-2" for="modelType">Model Type</label>
                            <div class="col-sm-10">
                             <select class="form-control" id="modelType" name="modelType" onclick="loadModelTypes();" onchange = "loadProperties();">
                                         <option value="">Select an Option</option>
                                     </select>
                            </div>
                        </div>

                      <!-- btn-group -->


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

                      <!-- /btn-group -->

                  </form>
              			</section>



  			<h3>Model Parameters</h3>
  			<section>
                          <div id="modelRequiredFields"></div>
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

                function loadProperties() {
                    var text = $('#modelType option:selected').text();
                        buildForm(text + "_Model", 'modelRequiredFields');
                        console.log(text);
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
