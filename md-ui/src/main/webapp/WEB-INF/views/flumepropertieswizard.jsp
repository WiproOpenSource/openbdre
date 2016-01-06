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
		<script src="../js/angular.min.js" type="text/javascript"></script>
		<script src="../js/bootstrap.js" type="text/javascript"></script>
		<script src="../js/jquery.jtable.js" type="text/javascript"></script>
		<link href="../css/jtables-bdre.css" rel="stylesheet" type="text/css" />
		<script >
                function fetchPipelineInfo(pid){
        			location.href = '<c:url value="/pages/lineage.page?pid="/>' + pid;
                }
        		</script >
		<script>
var selectedSourceType = '';
var selectedChannelType = '';
var selectedSinkType = '';
var selectedprocessFieldsFormType='';
var jsonObj = {
	"Result": "OK"
}
var map = new Object();
var createJobResult;
var requiredProperties;
var sourceFlag;
var created = 0;

		</script>


		<script type="text/javascript">
function addDataToJson(properties) {
	console.log(properties);
	var id = properties.id;
	console.log(id);
}

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
	$("#bdre-flume-ingestion").steps({
		headerTag: "h3",
		bodyTag: "section",
		transitionEffect: "slideLeft",
		stepsOrientation: "vertical",
		enableCancelButton: true,
		onStepChanging: function(event, currentIndex, newIndex) {
			console.log(currentIndex + 'current ' + newIndex);
			if(currentIndex == 0 && newIndex == 1 && selectedSourceType == "") {
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
				}).text("Please Select Some Value");
				return false;
			}
			if(currentIndex == 3 && newIndex == 4 && selectedChannelType == "") {
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
				}).text("Please Select Some Value");
				return false;
			}
			if(currentIndex == 6 && newIndex == 7 && selectedSinkType == "") {
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
				}).text("Please Select Some Value");
				return false;
			}
			if(currentIndex == 1 && newIndex == 2) {
				testNullValues('sourceRequiredFieldsForm');
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
			if(currentIndex == 4 && newIndex == 5) {
				testNullValues('channelRequiredFieldsForm');
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

			if(currentIndex == 7 && newIndex == 8) {
				testNullValues('sinkRequiredFieldsForm');
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
					formIntoMap('process_','processFieldsForm');
					map['source_type'] = selectedSourceType;
					map['channel_type'] = selectedChannelType;
					map['sink_type'] = selectedSinkType;
					map['process_type']=selectedprocessFieldsFormType;

					$('#createjobs').on('click', function(e) {

						console.log(selectedSourceType);
						console.log(selectedChannelType);
						console.log(selectedSinkType);
						console.log(selectedprocessFieldsFormType);

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


	</head>

	<body ng-app="myApp" ng-controller="myCtrl">

		<div id="bdre-flume-ingestion" ng-controller="myCtrl">
			<h3>Select Source Type</h3>
			<section>

				<div id="dropdownSource">
					<div class="alert alert-info" role="alert">
						Application requires source type which depends on how you are getting your data
					</div>
					<!-- btn-group -->
					<div class="btn-group">
						<button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="false" aria-expanded="true" id="srcDropdown">
							<span>Select Source</span><span class="caret"></span>
						</button>
						<ul class="dropdown-menu" aria-labelledby="srcDropdown">
							<li>
								<a href="#"></a>
							</li>
						</ul>
					</div>
					<!-- /btn-group -->
				</div>
			</section>
			<h3>Required Source Properties</h3>
			<section>
				<div class="alert alert-info" role="alert">
					Form contains required configuration properties related with your selected source type.
				</div>
				<div id="sourceRequiredFields"></div>
			</section>

			<h3>Advanced Source Properties</h3>
			<section>
				<div class="alert alert-info" role="alert">
					Table contains advanced configuration properties related with your selected source type. Use edit and delete button to change or delete properties.
				</div>
				<div id='sourceAdvancedFields'></div>

			</section>


			<h3>Select Channel Type</h3>
			<section>

				<div id="dropdownChannel">
					<div class="alert alert-info" role="alert">
						Application requires channel type which depends on how you want to transfer your data from source to sink
					</div>
					<!-- btn-group -->
					<div class="btn-group">
						<button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="false" aria-expanded="true" id="chanDropdown"><span>Select Channel</span><span class="caret"></span></button>
						<ul class="dropdown-menu" aria-labelledby="chanDropdown">
							<li>
								<a href="#"></a>
							</li>
						</ul>
					</div>
					<!-- /btn-group -->
				</div>
			</section>

			<h3>Required Channel Properties</h3>
			<section>
				<div class="alert alert-info" role="alert">
					Form contains required configuration properties related with your selected channel type.
				</div>
				<div id='channelRequiredFields'></div>

			</section>

			<h3>Advanced Channel Properties</h3>
			<section>
				<div class="alert alert-info" role="alert">
					Table contains advanced configuration properties related with your selected channel type. Use edit and delete button to change or delete properties.
				</div>
				<div id='channelAdvancedFields'></div>

			</section>

			<h3>Select Sink Type</h3>
			<section>

				<div id="dropdownSink">
					<div class="alert alert-info" role="alert">
						Application requires sink type which depends on where you want to dump your data
					</div>
					<!-- btn-group -->
					<div class="btn-group">
						<button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="false" aria-expanded="true" id="sinkDropdown"><span>Select Sink</span><span class="caret"></span></button>
						<ul class="dropdown-menu" aria-labelledby="sinkDropdown">
							<li>
								<a href="#"></a>
							</li>
						</ul>
					</div>
					<!-- /btn-group -->
				</div>

			</section>

			<h3>Required Sink Properties</h3>
			<section>
				<div class="alert alert-info" role="alert">
					Form contains required configuration properties related with your selected sink type.
				</div>
				<div id='sinkRequiredFields'></div>

			</section>

			<h3>Advanced Sink Properties</h3>
			<section>
				<div class="alert alert-info" role="alert">
					Table contains advanced configuration properties related with your selected sink type. Use edit and delete button to change or delete properties.
				</div>
				<div id='sinkAdvancedFields'></div>

			</section>
 						<h3>Process Details</h3>
                             <section>
                                 <form class="form-horizontal" role="form" id="processFieldsForm">
                                     <div id="processDetails">
                                         <div class="alert alert-info" role="alert">
                                             Application requires process details to create process entries in metadata
                                         </div>
                                         <!-- btn-group -->
                                         <div id="processFields">

                                             <div class="form-group">
                                                 <label class="control-label col-sm-2" for="processName">Process Name:</label>
                                                 <div class="col-sm-10">
                                                     <input type="text" class="form-control"  id="processName" name="processName" placeholder="Enter Process Name" required>
                                                 </div>
                                             </div>
                                             <div class="form-group">
                                                 <label class="control-label col-sm-2" for="processDescription">Process Description:</label>
                                                 <div class="col-sm-10">
                                                     <input type="text" class="form-control" id="processDescription" name="processDescription" placeholder="Enter Process Description"  required>
                                                 </div>
                                             </div>
                                             <div class="form-group">
                                                 <label class="control-label col-sm-2" for="busDomainId">Bus Domain Id:</label>
                                                 <div class="col-sm-10">
                                                     <select class="form-control" id="busDomainId" name="busDomainId">
                                                         <option ng-repeat="busDomain in busDomains.Options" value="{{busDomain.Value}}" name="busDomainId">{{busDomain.DisplayText}} </option>

                                                     </select>
                                                 </div>
                                             </div>
                                         </div>
                                         <!-- /btn-group -->
                                     </div>
                                     </form>
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
			<script>
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
                                    });
            </script>

		<script type="text/javascript">
function loadJTable(typeValue, typeOf, typeDiv) {
	console.log('type value' + typeValue + 'type of ' + typeOf + 'type Div' + typeDiv);
	var div = '';
	div = document.getElementById(typeDiv);
	$(div).jtable({
		title: 'Additional Configurations For ' + typeValue,
		paging: false,
		sorting: false,
		create: false,
		edit: false,
		actions: {
			listAction: function(postData, jtParams) {
				return $.Deferred(function($dfd) {
					$.ajax({
						url: '/mdrest/genconfig/' + typeOf + '/?required=0',
						type: 'GET',
						data: postData,
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
			createAction: function(postData) {
				console.log(postData);
				return $.Deferred(function($dfd) {
					$.ajax({
						url: '/mdrest/flumeproperties',
						type: 'PUT',
						data: postData,
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

			description: {
				title: 'Description',
				width: '50%',
				edit: false
			},
			key: {
				key: true,
				create: true,
				title: 'Configuration'
			},
			defaultVal: {
				title: 'Value'
			},
			configGroup: {
				type: 'hidden',
				create: true,
				edit: false,
				title: 'Config Group'
			},
			required: {
				type: 'hidden',
				defaultVal: '0'
			}
		}
	});

	$(div).jtable('load');

};

		</script>
		<script>
function buildForm(typeOf, typeDiv) {
	console.log('inside the function');

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
				formHTML = formHTML + '<span class="glyphicon glyphicon-question-sign" title="' + v.description + '"></span>';
				formHTML = formHTML + '<input name="' + v.key + '" value="' + v.defaultVal + '" placeholder="' + v.description + '" type="' + v.key + '" class="form-control" id="' + v.key + '"></div>';
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
