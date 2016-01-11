<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="security"
	   uri="http://www.springframework.org/security/tags" %>
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

		<script src="../js/bootstrap.js" type="text/javascript"></script>
		<script src="../js/jquery.jtable.js" type="text/javascript"></script>
		<script src="../js/angular.min.js" type="text/javascript"></script>
		<link href="../css/jtables-bdre.css" rel="stylesheet" type="text/css" />
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
		<script type="text/javascript">
function addDataToJson(properties) {
	console.log(properties);
	var id = properties.id;
	console.log(id);
}

		</script>
			<script type="text/javascript">
        		function copyForm() {
        		    var newform2=$('#rawTableColumn').clone(); //Clone form 1
                    newform2.filter('form').prop('id', 'baseTableColumn'); //Update formID
                    newform2.children("#rawDescription").children("#rawFormGroup1").attr('id', 'baseFormGroup1');
                    newform2.children("#rawDescription").children("#rawFormGroup1").children('#rawColName.1').attr('id', 'baseColName.1');
                    newform2.children("#rawDescription").children("#rawFormGroup1").children('#rawDataType.1').attr('id', 'baseDataType.1');
                    newform2.children("#rawDescription").children("#rawFormGroup1").children('#rawRemove1').attr('id', 'baseRemove1');
                    newform2.children("#rawDeleteDiv").attr('id', 'baseDeleteDiv');
                    newform2.children("#baseDeleteDiv").children("#rawButton1").attr('id', 'baseButton1');
                    $('#baseTableColumn').replaceWith(newform2);
        		}
        		</script>
		<script>
function displayProcess(records) {
	$('#Process').jtable({
		title: 'Data Load Processes',
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
var wizard = null;
var finalJson;
wizard = $(document).ready(function() {
	$("#bdre-data-load").steps({
		headerTag: "h3",
		bodyTag: "section",
		transitionEffect: "slideLeft",
		stepsOrientation: "vertical",
		enableCancelButton: true,
		onStepChanging: function(event, currentIndex, newIndex) {
			console.log(currentIndex + 'current ' + newIndex );
			if(currentIndex == 0 && newIndex == 1 && document.getElementById('processFieldsForm1').elements[0].value == "" && document.getElementById('processFieldsForm1').elements[1].value == "") {
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
			if(currentIndex == 3 && newIndex == 4 ) {
				testNullValues('formatFields');
                				console.log('source flag is ' + sourceFlag);
                				if(sourceFlag == 1) {
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
                					}).text("Please Provide Value For Required Fields");
                					return false;
                				}
			}
			if(currentIndex == 6 && newIndex == 7) {
			copyForm();
			testNullValues('baseTableDetails');
            				if(sourceFlag == 1) {
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
            					}).text("Please Provide Value For Required Fields");
            					return false;
            				}
			}
			if(currentIndex == 1 && newIndex == 2) {
			console.log(document.getElementById('rawTableDetails').elements[0].value);
			    buildForm(document.getElementById('rawTableDetails').elements[0].value);
			    jTableForRawColumns('rawTableColumnDetails');
			}
			return true;
		},
		onStepChanged: function(event, currentIndex, priorIndex) {
			console.log(currentIndex + " " + priorIndex);
			if(currentIndex == 9 && priorIndex == 8) {
				{
					jtableIntoMap('source_', 'sourceAdvancedFields');
					jtableIntoMap('channel_', 'channelAdvancedFields');
					jtableIntoMap('sink_', 'sinkAdvancedFields');
					formIntoMap('source_', 'sourceRequiredFieldsForm');
					formIntoMap('channel_', 'channelRequiredFieldsForm');
					formIntoMap('sink_', 'sinkRequiredFieldsForm');
					map['source_type'] = selectedSourceType;
					map['channel_type'] = selectedChannelType;
					map['sink_type'] = selectedSinkType;

					$('#createjobs').on('click', function(e) {
						console.log(selectedSourceType);
						console.log(selectedChannelType);
						console.log(selectedSinkType);

						$.ajax({
							type: "POST",
							url: "/mdrest/flumeproperties/createjobs",
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

					});

				}
			}
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
			location.href = '<c:url value="/pages/flumepropertieswizard.page"/>';
		}
	});
});

		</script>
		<script type="text/javascript">
            $(document).ready(function(){
            var next = 1;
            $(".add-more").click(function(e){
                e.preventDefault();
                var addto = "#rawDeleteDiv";
                var addRemove = "#rawFormGroup" + (next);
                next = next + 1;
                var removeBtn = '<button id="rawRemove' + (next) + '" class="btn btn-danger remove-me" ><span class="glyphicon glyphicon-trash" ></span></button></div><div id="field">';
                var newIn = '';
                newIn = newIn +  '<div class="form-group" id="rawFormGroup' + next + '">' ;
                newIn = newIn +  '<div class="col-md-3">' ;
                newIn = newIn +  '<input type="text" class="form-control input-sm" id="rawColName.' + next + '" value="" name="rawColName.' + next + '" placeholder="Column Name" />' ;
                newIn = newIn +  '</div>' ;
                newIn = newIn +  '<div class="col-md-3">' ;
                newIn = newIn +  '<input type="text" class="form-control input-sm" id=rawDataType.' + next + '" value="" name="rawDataType.' + next + '" placeholder="Data Type" />' ;
                newIn = newIn +  '</div>' ;
                newIn = newIn + removeBtn;
                newIn = newIn +  '</div>' ;

                var newInput = $(newIn);
                var removeButton = $(removeBtn);
                $(addto).before(newInput);

                $("#formGroup" + next).attr('data-source',$(addto).attr('data-source'));
                $("#count").val(next);

                    $('.remove-me').click(function(e){
                        e.preventDefault();
                        var fieldNum = this.id.charAt(this.id.length-1);
                        var fieldID = "#rawFormGroup" + fieldNum;
                        console.log($(this));
                        //$(this).remove();
                        $(fieldID).remove();
                    });
            });
        });
        </script>
        	<script type="text/javascript">
                    $(document).ready(function(){
                    var next = 1;
                    $(".add-more").click(function(e){
                        e.preventDefault();
                        var addto = "#baseDeleteDiv";
                        var addRemove = "#baseFormGroup" + (next);
                        next = next + 1;
                        var removeBtn = '<button id="rawRemove' + (next) + '" class="btn btn-danger remove-me" ><span class="glyphicon glyphicon-trash" ></span></button></div><div id="field">';
                        var newIn = '';
                        newIn = newIn +  '<div class="form-group" id="baseFormGroup' + next + '">' ;
                        newIn = newIn +  '<div class="col-md-3">' ;
                        newIn = newIn +  '<input type="text" class="form-control input-sm" id="baseColName.' + next + '" value="" name="baseColName.' + next + '" placeholder="Column Name" />' ;
                        newIn = newIn +  '</div>' ;
                        newIn = newIn +  '<div class="col-md-3">' ;
                        newIn = newIn +  '<input type="text" class="form-control input-sm" id=baseDataType.' + next + '" value="" name="baseDataType.' + next + '" placeholder="Data Type" />' ;
                        newIn = newIn +  '</div>' ;
                        newIn = newIn + removeBtn;
                        newIn = newIn +  '</div>' ;

                        var newInput = $(newIn);
                        var removeButton = $(removeBtn);
                        $(addto).before(newInput);

                        $("#formGroup" + next).attr('data-source',$(addto).attr('data-source'));
                        $("#count").val(next);

                            $('.remove-me').click(function(e){
                                e.preventDefault();
                                var fieldNum = this.id.charAt(this.id.length-1);
                                var fieldID = "#baseFormGroup" + fieldNum;
                                console.log($(this));
                                //$(this).remove();
                                $(fieldID).remove();
                            });
                    });
                });
                </script>
        <script type="text/javascript">
                    $(document).ready(function(){
                    var next = 1;
                    $(".add-more").click(function(e){
                        e.preventDefault();
                        var addto = "#serdePropDiv";
                        var addRemove = "#formGroupSerde" + (next);
                        next = next + 1;
                        var removeBtn = '<button id="removeserde' + (next) + '" class="btn btn-danger remove-me" ><span class="glyphicon glyphicon-trash" ></span></button></div><div id="field">';
                        var newIn = '';
                        newIn = newIn +  '<div class="form-group" id="formGroupSerde' + next + '">' ;
                        newIn = newIn +  '<div class="col-md-3">' ;
                        newIn = newIn +  '<input type="text" class="form-control input-sm" id="serdePropKey.' + next + '" value="" name="serdePropKey.' + next + '" placeholder="Serde Key" />' ;
                        newIn = newIn +  '</div>' ;
                        newIn = newIn +  '<div class="col-md-3">' ;
                        newIn = newIn +  '<input type="text" class="form-control input-sm" id="serdePropValue.' + next + '" value="" name="serdePropValue.' + next + '" placeholder="Serde Property" />' ;
                        newIn = newIn +  '</div>' ;
                        newIn = newIn + removeBtn;
                        newIn = newIn +  '</div>' ;

                        var newInput = $(newIn);
                        var removeButton = $(removeBtn);
                        $(addto).before(newInput);

                        $("#formGroupSerde" + next).attr('data-source',$(addto).attr('data-source'));
                        $("#count").val(next);

                            $('.remove-me').click(function(e){
                                e.preventDefault();
                                var fieldNum = this.id.charAt(this.id.length-1);
                                var fieldID = "#formGroupSerde" + fieldNum;
                                console.log($(this));
                                //$(this).remove();
                                $(fieldID).remove();
                            });
                    });
                });
                </script>
                      <script type="text/javascript">
                                    $(document).ready(function(){
                                    var next = 1;
                                    $(".add-more").click(function(e){
                                        e.preventDefault();
                                        var addto = "#tablePropDiv";
                                        var addRemove = "#formGroupTable" + (next);
                                        next = next + 1;
                                        var removeBtn = '<button id="removetable' + (next) + '" class="btn btn-danger remove-me" ><span class="glyphicon glyphicon-trash" ></span></button></div><div id="field">';
                                        var newIn = '';
                                        newIn = newIn +  '<div class="form-group" id="formGroupTable' + next + '">' ;
                                        newIn = newIn +  '<div class="col-md-3">' ;
                                        newIn = newIn +  '<input type="text" class="form-control input-sm" id="tablePropKey.' + next + '" value="" name="tablePropKey.' + next + '" placeholder="Table Prop Key" />' ;
                                        newIn = newIn +  '</div>' ;
                                        newIn = newIn +  '<div class="col-md-3">' ;
                                        newIn = newIn +  '<input type="text" class="form-control input-sm" id="tablePropValue.' + next + '" value="" name="tablePropValue.' + next + '" placeholder="Table Property" />' ;
                                        newIn = newIn +  '</div>' ;
                                        newIn = newIn + removeBtn;
                                        newIn = newIn +  '</div>' ;

                                        var newInput = $(newIn);
                                        var removeButton = $(removeBtn);
                                        $(addto).before(newInput);

                                        $("#formGroupTable" + next).attr('data-source',$(addto).attr('data-source'));
                                        $("#count").val(next);

                                            $('.remove-me').click(function(e){
                                                e.preventDefault();
                                                var fieldNum = this.id.charAt(this.id.length-1);
                                                var fieldID = "#formGroupTable" + fieldNum;
                                                console.log($(this));
                                                //$(this).remove();
                                                $(fieldID).remove();
                                            });
                                    });
                                });
                                </script>
		<script>
                var app = angular.module('myApp', []);
                app.controller('myCtrl', function($scope) {
                    $scope.fileformats= getGenConfigMap('file_format');
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


	</head>
<body ng-app="myApp" ng-controller="myCtrl">

		<div id="bdre-data-load" ng-controller="myCtrl">
			<h3>Provide Process Details</h3>
			<section>
<form class="form-horizontal" role="form" id="processFieldsForm1">
                        <div id="processDetails">
                            <div class="alert alert-info" role="alert">
                                Application requires process details to create process entries in metadata
                            </div>
                            <!-- btn-group -->
                            <div id="processFields">

                                <div class="form-group">
                                    <label class="control-label col-sm-2" for="processName">Process Name:</label>
                                    <div class="col-sm-10">
                                        <input type="text" class="form-control"  id="processName" name="processName" placeholder="Enter Process Name" value="remove me" required>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label class="control-label col-sm-2" for="processDescription">Process Description:</label>
                                    <div class="col-sm-10">
                                        <input type="text" class="form-control" id="processDescription" name="processDescription" placeholder="Enter Process Description" value="remove me" required>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label class="control-label col-sm-2" for="busDomainId">Bus Domain Id:</label>
                                    <div class="col-sm-10">
                                        <select class="form-control" id="busDomainId" name="busDomainId">
                                            <option ng-repeat="busDomain in busDomains.Options" value="{{busDomain.Value}}" name="busDomainId">{{busDomain.DisplayText}}</option>

                                        </select>
                                    </div>
                                </div>
                            </div>
                            <!-- /btn-group -->
                        </div>
                        </form>
			</section>
			<h3>Raw Table Details</h3>
            			<section>
            <form class="form-horizontal" role="form" id="rawTableDetails">
                                    <div id="rawTableDetailsDiv">
                                        <div class="alert alert-info" role="alert">
                                            Type of file you want to load in hive
                                        </div>
                                        <!-- btn-group -->
                                        <div id="rawTablDetailsDB">
                                            <div class="form-group">
                                                <label class="control-label col-sm-2" for="fileformat">File Format:</label>
                                                <div class="col-sm-10">
                                                    <select class="form-control" id="fileformat" name="fileformat" >
                                                        <option ng-repeat="fileformat in fileformats" value="{{fileformat.defaultVal}}" name="fileformat">{{fileformat.value}}</option>

                                                    </select>
                                                </div>
                                            </div>
                                        </div>
                                        <!-- /btn-group -->
                                    </div>
                                    </form>
            			</section>
			<h3>Raw Table Properties</h3>
			<section>
			    <div id="rawTableColumnDetails"></div>
            </section>

			<h3>Serde, OutPut and Input Format</h3>
            <section>
                  <div id="serde"></div>
            </section>


			<h3>Provide Serde Properties</h3>
			<section>
                                    <form class="form-horizontal" role="form" id="serdeProperties">
                                        <div id="serdePropertiesDiv">
                                            <div class="alert alert-info" role="alert">
                                                <div style="font-size:24px;" ><b>How To:</b> </div>
                                                <b>Enter Serde Properties key and value</b>
                                                <br>

                                            </div>

                                            <!-- btn-group -->
                                            <div class="form-group" id="formGroupSerde1">
                                                <div class="col-md-3">
                                                    <input type="text" class="form-control input-sm" id="serdePropKey.1" value="" name="serdePropKey.1" placeholder="Serde Key" />
                                                </div>
                                                <div class="col-md-3">
                                                    <input type="text" class="form-control input-sm" id="serdePropValue.1" value="" name="serdePropValue.1" placeholder="Serde Property" />
                                                </div>
                                                <button id="removeserde1" class="btn btn-danger remove-me"><span class="glyphicon glyphicon-trash"></span></button>


                                            </div>
                                            <!-- /btn-group -->
                                        </div>
                                        <div class="col-md-2" id="serdePropDiv">
                                                    <button id="b2" class="btn btn-primary add-more">
                                                        <span class="glyphicon glyphicon-plus" style="font-size:large"></span>
                                                    </button>
                                        </div>

                                    </form>

                                    </section>

			<h3>Provide Table Properties</h3>
            			<section>
                                                <form class="form-horizontal" role="form" id="tableProperties">
                                                    <div id="tablePropertiesDiv">
                                                        <div class="alert alert-info" role="alert">
                                                            <div style="font-size:24px;" ><b>How To:</b> </div>
                                                            <b>Enter Table Properties key and value</b>
                                                            <br>

                                                        </div>

                                                        <!-- btn-group -->
                                                        <div class="form-group" id="formGroupTable1">
                                                            <div class="col-md-3">
                                                                <input type="text" class="form-control input-sm" id="tablePropKey.1" value="" name="tablePropKey.1" placeholder="Table Prop Key" />
                                                            </div>
                                                            <div class="col-md-3">
                                                                <input type="text" class="form-control input-sm" id="tablePropValue.1" value="" name="tablePropValue.1" placeholder="Table Property" />
                                                            </div>
                                                            <button id="removetable1" class="btn btn-danger remove-me"><span class="glyphicon glyphicon-trash"></span></button>


                                                        </div>
                                                        <!-- /btn-group -->
                                                    </div>
                                                    <div class="col-md-2" id="tablePropDiv">
                                                                <button id="b2" class="btn btn-primary add-more">
                                                                    <span class="glyphicon glyphicon-plus" style="font-size:large"></span>
                                                                </button>
                                                    </div>

                                                </form>

                                                </section>

		<h3>Base Table Name AND DB</h3>
                			<section>
                <form class="form-horizontal" role="form" id="baseTableDetails">
                                        <div id="baseTableDetailsDiv">
                                            <div class="alert alert-info" role="alert">
                                                Application requires process details to create process entries in metadata
                                            </div>
                                            <!-- btn-group -->
                                            <div id="baseTablDetailsDB">

                                                <div class="form-group">
                                                    <label class="control-label col-sm-2" for="baseDBName">BASE DB Name:</label>
                                                    <div class="col-sm-10">
                                                        <input type="text" class="form-control"  id="baseDBName" name="baseDBName" placeholder="Enter BASE DB Name" required>
                                                    </div>
                                                </div>
                                                <div class="form-group">
                                                    <label class="control-label col-sm-2" for="baseTableName">Base Table Name:</label>
                                                    <div class="col-sm-10">
                                                        <input type="text" class="form-control" id="baseTableName" name="baseTableName" placeholder="Enter BASE TABLE NAME" required>
                                                    </div>
                                                </div>

                                            </div>
                                            <!-- /btn-group -->
                                        </div>
                                        </form>
                			</section>
                <h3>Base Table Details</h3>
                            <section>
                                  <div id="baseTableColumnDetails">
                                    <form id="baseTableColumn">
                                    </form>
                                   </div>
                            </section>
			<h3>Confirm</h3>
			<section>
				<div id="Process">
					<button id="createjobs" type="button" class="btn btn-primary btn-lg">Create Jobs</button>
				</div>
			</section>
		</div>
		<div style="display:none" id="div-dialog-warning">
			<p><span class="ui-icon ui-icon-alert" style="float:left;"></span></p>
		</div>
		<script>
$(document).ready(function() {
	$('#dropdownSink').on('show.bs.dropdown', function() {
		$.ajax({
			type: "GET",
			url: "/mdrest/genconfig/Sink_Type/?required=2",
			dataType: 'json',
			success: function(data) {
				var root = 'Records';
				var ul = $('#sinkDropdown').parent().find($("ul"));
				$(ul).empty();
				$.each(data[root], function(i, v) {
					$(ul).append('<li><a href="#">' + v.value + '</a></li>');
					var li = $(ul).children()[i];
					$(li).data(v);
				});
			},
		});

	});

	$('#dropdownSink').on('click', 'a', function() {

		var car = $(this).parent();
		console.log($(car));
		var cardata = $(car).data();
		console.log($(cardata));
		$('#sinkDropdown').html(cardata.value + '<span class="caret"></span>');
		if(selectedSinkType != '') {
			$('#sinkAdvancedFields').jtable('destroy');
		}
		selectedSinkType = cardata.key;
		buildForm(selectedSinkType + "_Sink", 'sinkRequiredFields');
		console.log(selectedSinkType);
		loadJTable(selectedSinkType, selectedSinkType + "_Sink", 'sinkAdvancedFields');
		console.log(selectedSinkType);
	});
	console.log('here');
	$('#dropdownChannel').on('show.bs.dropdown', function() {
		$.ajax({
			type: "GET",
			url: "/mdrest/genconfig/Channel_Type/?required=2",
			dataType: 'json',
			success: function(data) {
				var root = 'Records';
				var ul = $('#chanDropdown').parent().find($("ul"));
				$(ul).empty();
				$.each(data[root], function(i, v) {
					$(ul).append('<li><a href="#">' + v.value + '</a></li>');
					var li = $(ul).children()[i];
					$(li).data(v);
				});
			},
		});

	});
	$('#dropdownChannel').on('click', 'a', function() {

		var car = $(this).parent();
		console.log($(car));
		var cardata = $(car).data();
		console.log($(cardata));
		$('#chanDropdown').html(cardata.value + '<span class="caret"></span>');
		if(selectedChannelType != '') {
			$('#channelAdvancedFields').jtable('destroy');
		}
		selectedChannelType = cardata.key;
		buildForm(selectedChannelType + "_Channel", 'channelRequiredFields');
		console.log(selectedChannelType);
		loadJTable(selectedChannelType, selectedChannelType + "_Channel", 'channelAdvancedFields');
		console.log(selectedChannelType);
	});

	$('#dropdownSource').on('show.bs.dropdown', function() {
		console.log(' i dont know');
		$.ajax({
			type: "GET",
			url: "/mdrest/genconfig/Source_Type/?required=2",
			dataType: 'json',
			success: function(data) {
				var root = 'Records';
				var ul = $('#srcDropdown').parent().find($("ul"));
				$(ul).empty();
				$.each(data[root], function(i, v) {
					$(ul).append('<li><a href="#">' + v.value + '</a></li>');
					var li = $(ul).children()[i];
					$(li).data(v);
				});
			},
		});

	});
	$('#dropdownSource').on('click', 'a', function() {

		var car = $(this).parent();
		console.log($(car));
		var cardata = $(car).data();
		console.log($(cardata));
		$('#srcDropdown').html(cardata.value + '<span class="caret"></span>');
		if(selectedSourceType != '') {
			$('#sourceAdvancedFields').jtable('destroy');
		}
		selectedSourceType = cardata.key;
		buildForm(selectedSourceType + "_Source", 'sourceRequiredFields');
		console.log(selectedSourceType);
		loadJTable(selectedSourceType, selectedSourceType + "_Source", 'sourceAdvancedFields');
		console.log(selectedSourceType);
	});
});

		</script>
		<script type="text/javascript">
function jTableForRawColumns(divName) {
	console.log('div value' + divName);
	var div = '';
	div = document.getElementById(divName);
	$(div).jtable({
		title: 'Enter  Column and data type ',
		paging: false,
		sorting: false,
		create: false,
		edit: false,
		actions: {
			listAction: function(postData, jtParams) {
				return jsonObj;
			},
			createAction: function(postData) {
			console.log(postData);
			var splitedPostData = postData.split("&");
			var jsonedPostData = '[{';
            			for (i=0; i < splitedPostData.length ; i++)
            			{
                            console.log("data is " + splitedPostData[i]);
                            jsonedPostData += '"';
            			    jsonedPostData += splitedPostData[i].split("=")[0];
            			    jsonedPostData += '"';
            			    jsonedPostData += ":";
            			    jsonedPostData += '"';
            			    jsonedPostData += splitedPostData[i].split("=")[1];
            			    jsonedPostData += '"';
            			    jsonedPostData += ',';
            			    console.log("json is" + jsonedPostData);
            			}
            			var lastIndex = jsonedPostData.lastIndexOf(",");
            			jsonedPostData = jsonedPostData.substring(0,lastIndex);
            			jsonedPostData +=  "}";
            			jsonedPostData +=  "]";
            			console.log(jsonedPostData);


            			var returnObj={
                            "Result": "OK",
                            "Record": jsonedPostData
                       }
                       return returnObj;

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

			columnName: {
				title: 'Column Name',
				width: '50%',
				edit: false
			},
			dataType: {
				key: true,
				create: true,
				title: 'Data Type'
			}
		}
	});

	$(div).jtable('load');

};

		</script>




		<script>
function buildForm(fileformat) {
	console.log('inside the function');

	$.ajax({
		type: "GET",
		url: "/mdrest/genconfig/" + fileformat + "/?required=1",
		dataType: 'json',
		success: function(data) {
			var root = 'Records';
			var div = document.getElementById('serde');
			var formHTML = '';
			formHTML = formHTML + '<form class="form-horizontal" role="form" id = "formatFields">';
			formHTML = formHTML + '<div id="Serde, OutPut and Input Format">';
			formHTML = formHTML + '<div class="alert alert-info" role="alert">Application requires serde class and input/output format details to be entered</div>';

			console.log(data[root].length);
			if (data[root].length == 0){

			        formHTML = formHTML + '<div class="form-group"> <label class="control-label col-sm-3" for="inputFormat">Input Format:</label>';
                    formHTML = formHTML + '<div class="col-sm-9">';
                    formHTML = formHTML + '<input name="inputFormat" value="" placeholder="input format to be used" type="text" class="form-control" id="inputFormat"></div>';
                    formHTML = formHTML + '</div>';
                    formHTML = formHTML + '<div class="form-group"> <label class="control-label col-sm-3" for="outputFormat">Output Format:</label>';
                    formHTML = formHTML + '<div class="col-sm-9">';
                    formHTML = formHTML + '<input name="outputFormat" value="" placeholder="output format to be used" type="text" class="form-control" id="outputFormat"></div>';
                    formHTML = formHTML + '</div>';
                    formHTML = formHTML + '<div class="form-group"> <label class="control-label col-sm-3" for="serdeClass">Serde Class:</label>';
                    formHTML = formHTML + '<div class="col-sm-9">';
                    formHTML = formHTML + '<input name="serdeClass" value="" placeholder="serde class to be used" type="text" class="form-control" id="serdeClass"></div>';
                    formHTML = formHTML + '</div>';

			}else{
			$.each(data[root], function(i, v) {
				formHTML = formHTML + '<div class="form-group"> <label class="control-label col-sm-3" for="' + v.key + '">' + v.value +':</label>';
				formHTML = formHTML + '<div class="col-sm-9">';
				formHTML = formHTML + '<input name="' + v.key + '" value="' + v.defaultVal + '" placeholder="' + v.description + '" type="' + v.type + '" class="form-control" id="' + v.key + '"></div>';
				formHTML = formHTML + '</div>';
			});
			}
			formHTML = formHTML + '</form>';
			div.innerHTML = formHTML;
			console.log(div);
		}
	});
	return true;
}

		</script>

		<script>
function testNullValues(typeOf) {
	var x = '';
	console.log('type Of ' + typeOf);
	x = document.getElementById(typeOf);
	console.log(x.length);
	var text = "";
	sourceFlag = 0;
	var i;
	for(i = 0; i < x.length; i++) {
		console.log('value for element is ' + x.elements[i].value);
		if(x.elements[i].value == '' || x.elements[i].value == null) {
			sourceFlag = 1;
		}
	}
}



		</script>

		<script>
function jtableIntoMap(typeProp, typeDiv) {
	var div = '';
	div = document.getElementById(typeDiv);
	$('div .jtable-data-row').each(function() {
		console.log(this);
		$(this).addClass('jtable-row-selected');
		$(this).addClass('ui-state-highlight');
	});

	var $selectedRows = $(div).jtable('selectedRows');
	$selectedRows.each(function() {
		var record = $(this).data('record');
		var keys = typeProp + record.key;
		console.log(keys);
		map[keys] = record.defaultVal;
		console.log(map);
	});
	$('.jtable-row-selected').removeClass('jtable-row-selected');
}

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


	</body>

</html>
