<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="security"
	   uri="http://www.springframework.org/security/tags" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
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
		<link rel="stylesheet" href="../css/jquery.steps.custom.css" />
		<link href="../css/bootstrap.custom.css" rel="stylesheet" />
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
			    jTableForBaseColumns();
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
			console.log(document.getElementById('fileFormat').elements[1].value);
			    buildForm(document.getElementById('fileFormat').elements[1].value);
			}
			return true;
		},
		onStepChanged: function(event, currentIndex, priorIndex) {
			console.log(currentIndex + " " + priorIndex);
			if(currentIndex == 8 && priorIndex == 7) {
				{

					formIntoMap('process_', 'processFieldsForm1');
					formIntoMap('fileformat_', 'fileFormat');
					jtableIntoMap('rawtablecolumn_', 'rawTableColumnDetails');
					formIntoMap('fileformatdetails_', 'formatFields');
					formIntoMap('serdeproperties_', 'serdeProperties');
					formIntoMap('tableproperties_', 'tableProperties');
					formIntoMap('basetable_', 'baseTableDetails');
					jtableIntoMapForBase('baseTableColumnDetails');

					$('#createjobs').on('click', function(e) {

						$.ajax({
							type: "POST",
							url: "/mdrest/dataload/createjobs",
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
                    return false;
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
			location.href = '<c:url value="/pages/dataload.page"/>';
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
<body ng-app="myApp" ng-controller="myCtrl" >
	<div class="page-header"><spring:message code="dataload.page.panel_heading"/></div>
	<div class="alert alert-info" role="alert">
		<spring:message code="dataload.page.alert_info_outer_heading" />
	</div>
	<div id="bdre-data-load" ng-controller="myCtrl">
		
			<h3><div class="number-circular">1</div><spring:message code="dataload.page.h3_div"/></h3>
			<section>
			<form class="form-horizontal" role="form" id="processFieldsForm1">
                        <div id="processDetails">
                           <!-- btn-group -->
                            <div id="processFields">
								<div class="form-group">
                                    <label class="control-label col-sm-6" for="processName" ><spring:message code="dataload.page.form_left_procname"/></label>
                                    <div class="col-sm-8">
                                        <input type="text" class="form-control"  id="processName" name="processName" placeholder=<spring:message code="dataload.page.form_left_procname_placeholder"/> value="" required>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label class="control-label col-sm-6 " for="processDescription"><spring:message code="dataload.page.form_left_procdesc"/></label>
									 <div class="col-sm-8">
                                        <input type="text" class="form-control" id="processDescription" name="processDescription" placeholder=<spring:message code="dataload.page.form_left_procdesc_placeholder"/> value="" required>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label class="control-label col-sm-6" for="busDomainId"><spring:message code="dataload.page.form_left_bus_domain_id"/></label>
									<div class="col-sm-8">
                                        <select class="form-control" id="busDomainId" name="busDomainId">
                                            <option ng-repeat="busDomain in busDomains.Options" value="{{busDomain.Value}}" name="busDomainId">{{busDomain.DisplayText}}</option>

                                        </select>
                                    </div>
                                </div>
                                 <div class="form-group">
                                    <label class="control-label col-sm-6" for="enqueueId"><spring:message code="dataload.page.form_right_enqueing_id"/></label>
                                    <div class="col-sm-8">
                                        <input type="text" class="form-control" id="enqueueId" name="enqueueId" placeholder=<spring:message code="dataload.page.form_right_enqueing_id_placeholder"/> value="" required>
                                    </div>
                                </div>
                                <div class="clearfix"></div>
                            </div>
                            <!-- /btn-group -->
                        </div>
                        </form>
			</section>
			<h3><div class="number-circular">2</div><spring:message code="dataload.page.h3_div_2"/></h3>
            			<section>
            <form class="form-horizontal" role="form" id="fileFormat">
                                    <div id="fileFormatDiv">
                                        <div class="alert alert-info" role="alert">
                                          <spring:message code="dataload.page.alert_info_form"/>
                                        </div>
                                        <!-- btn-group -->
                                        <div id="rawTablDetailsDB">
                                        <div class="form-group" >
                                            <label class="control-label col-sm-2" for="rawDBName"><spring:message code="dataload.page.raw_db_name"/></label>
                                            <div class="col-sm-10">
                                                <input type="text" class="form-control"  id="rawDBName" name="rawDBName" placeholder=<spring:message code="dataload.page.raw_db_name_placeholder"/>value="" required>
                                            </div>
                                        </div>
                                        <div class="form-group">
                                            <label class="control-label col-sm-2" for="fileformat"><spring:message code="dataload.page.file_format"/></label>
                                            <div class="col-sm-10">
                                                <select class="form-control" id="fileformat" name="fileformat" >
                                                    <option ng-repeat="fileformat in fileformats" value="{{fileformat.defaultVal}}" name="fileformat">{{fileformat.value}}</option>

                                                </select>
                                            </div>
                                        </div>
                                        <div class="clearfix"></div>
                                        </div>
                                        
                                        <!-- /btn-group -->
                                    </div>
                                    </form>
            			</section>
			<h3><div class="number-circular">3</div><spring:message code="dataload.page.raw_table_props"/></h3>
			<section>
			    <div id="rawTableColumnDetails"></div>

            </section>

			<h3><div class="number-circular">4</div><spring:message code="dataload.page.formats"/></h3>
            <section>
                  <div id="fileFormatDetails"></div>
            </section>


			<h3><div class="number-circular">5</div><spring:message code="dataload.page.provide_props"/></h3>
			<section>
                                    <form class="form-horizontal" role="form" id="serdeProperties">
                                        <div id="serdePropertiesDiv">
                                            <div class="alert alert-info" role="alert">
                                                <b style="font-size:24px;"><spring:message code="dataload.page.how_to"/></b>
                                                <b><spring:message code="dataload.page.enter_props"/></b>
                                                <br>
											</div>

                                            <!-- btn-group -->
                                            <div class="form-group" id="formGroupSerde1">
                                                <div class="col-md-3">
                                                    <input type="text" class="form-control input-sm" id="serdePropKey.1" value="" name="serdePropKey.1" placeholder=<spring:message code="dataload.page.serde_key_placeholder"/> />
                                                </div>
                                                <div class="col-md-3">
                                                    <input type="text" class="form-control input-sm" id="serdePropValue.1" value="" name="serdePropValue.1" placeholder=<spring:message code="dataload.page.serde_property_placeholder"/>/>
                                                </div>
                                                <button id="removeserde1" class="btn btn-danger remove-me"><span class="glyphicon glyphicon-trash"></span></button>


                                            </div>
                                            <!-- /btn-group -->
                                        </div>
                                        <div class="col-md-2" id="serdePropDiv">
                                                    <button id="serdeButton1" class="btn btn-primary add-more">
                                                        <span class="glyphicon glyphicon-plus" style="font-size:large"></span>
                                                    </button>
                                        </div>

                                    </form>

                                    </section>

			<h3><div class="number-circular">6</div><spring:message code="dataload.page.provide_table_props"/></h3>
            			<section>
                                                <form class="form-horizontal" role="form" id="tableProperties">
                                                    <div id="tablePropertiesDiv">
                                                        <div class="alert alert-info" role="alert">
                                                            <b style="font-size:24px;"><spring:message code="dataload.page.how_to"/></b>
                                                            <b><spring:message code="dataload.page.enter_table_key_value"/></b>
                                                            <br>

                                                        </div>

                                                        <!-- btn-group -->
                                                        <div class="form-group" id="formGroupTable1">
                                                            <div class="col-md-3">
                                                                <input type="text" class="form-control input-sm" id="tablePropKey.1" value="" name="tablePropKey.1" placeholder=<spring:message code="dataload.page.table_prop_key_placeholder"/> />
                                                            </div>
                                                            <div class="col-md-3">
                                                                <input type="text" class="form-control input-sm" id="tablePropValue.1" value="" name="tablePropValue.1" placeholder=<spring:message code="dataload.page.table_property_placeholder"/>/>
                                                            </div>
                                                            <button id="removetable1" class="btn btn-danger remove-me"><span class="glyphicon glyphicon-trash"></span></button>


                                                        </div>
                                                        <div class="clearfix"></div>
                                                        <!-- /btn-group -->
                                                    </div>
                                                    <div class="col-md-2" id="tablePropDiv">
                                                                <button id="tableButton1" class="btn btn-primary add-more">
                                                                    <span class="glyphicon glyphicon-plus" style="font-size:large"></span>
                                                                </button>
                                                    </div>

                                                </form>

                                                </section>

		<h3><div class="number-circular">7</div><spring:message code="dataload.page.base_table_name"/></h3>
                			<section>
                <form class="form-horizontal" role="form" id="baseTableDetails">
                                        <div id="baseTableDetailsDiv">
                                            <div class="alert alert-info" role="alert">
                                                <spring:message code="dataload.page.alert_info_base_table"/>
                                            </div>
                                            <!-- btn-group -->
                                            <div id="baseTablDetailsDB">
                                                <div class="form-group">
                                                    <label class="control-label col-sm-2" for="baseDBName"><spring:message code="dataload.page.base_db_name"/></label>
                                                    <div class="col-sm-10">
                                                        <input type="text" class="form-control"  id="baseDBName" name="baseDBName" placeholder=<spring:message code="dataload.page.base_db_name_placeholder"/> required>
                                                    </div>
                                                </div>
                                                <div class="form-group">
                                                    <label class="control-label col-sm-2" for="baseTableName"><spring:message code="dataload.page.base_db_name"/></label>
                                                    <div class="col-sm-10">
                                                        <input type="text" class="form-control" id="baseTableName" name="baseTableName" placeholder=<spring:message code="dataload.page.enter_base_table_name_placeholder"/> required>
                                                    </div>
                                                </div>
                                                <div class="clearfix"></div>

                                            </div>
                                            <!-- /btn-group -->
                                        </div>
                                        </form>
                			</section>
                <h3><div class="number-circular">8</div><spring:message code="dataload.page.base_table_details"/></h3>
                            <section>
                                <div id="baseTableColumnDetails"> </div>
                            </section>
			<h3><div class="number-circular">9</div><spring:message code="dataload.page.confirm"/></h3>
			<section>
				<div id="Process">
					<button id="createjobs" type="button" class="btn btn-primary btn-lg"><spring:message code="dataload.page.create_jobs"/></button>
				</div>
			</section>
		</div>
		
		<div style="display:none" id="div-dialog-warning">
			<p><span class="ui-icon ui-icon-alert" style="float:left;"></span></p>
		</div>

		<script type="text/javascript">
	$(document).ready(function () {
	$('#rawTableColumnDetails').jtable({
		title: 'Raw Table Column ',
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
				title: 'Column Name',
				width: '50%',
				edit: true,
				create:true
			},
			dataType: {

				create: true,
				title: 'Data Type',
				edit: true,
				options:{ 'BigInt':'BigInt',
                          'SmallInt':'SmallInt',
                          'Float':'Float',
                          'Double':'Double',
                          'Decimal':'Decimal',
                          'Timestamp':'Timestamp',
                          'Date':'Date',
                          'String':'String'}
			}
		},

        recordAdded: function(event, data){
                //after record insertion, reload the records
                console.log("inserting data into base table "+ {record: JSON.stringify(data.record) });

                $('#baseTableColumnDetails').jtable('addRecord', {
                record: data.record
                });
        },
        recordUpdated: function(event, data){
                //after record insertion, reload the records
                console.log("updating data into base table "+ {record: JSON.stringify(data.record) });

                $('#baseTableColumnDetails').jtable('updateRecord', {
                record: data.record,
                clientOnly:true
                });
        },
        recordDeleted: function(event, data){
                //after record insertion, reload the records
                console.log("inserting data into base table "+ {record: JSON.stringify(data.record) });

                $('#baseTableColumnDetails').jtable('deleteRecord', {
                    key: data.record.serialNumber,
                    clientOnly:true
                });
        }

	});



	$('#rawTableColumnDetails').jtable('load');

});

		</script>
<script type="text/javascript">


	 $(document).ready(function () {
        	    $('#baseTableColumnDetails').jtable({
        	    title: 'Base Table Column',
        		    paging: false,
                    sorting: false,
                    create: false,
                    edit: false,
        		    actions: {
                        listAction: function (postData, jtParams) {
                            return jsonObj;
                        },

                        createAction:function (postData, jtParams) {
                            console.log(postData);
                            var baseSplitedPostData = postData.split("&");
                            var baseJSONedPostData = '{';
                            for (i=0; i < baseSplitedPostData.length ; i++)
                            {
                                console.log("data is " + baseSplitedPostData[i]);
                                baseJSONedPostData += '"';
                                baseJSONedPostData += baseSplitedPostData[i].split("=")[0];
                                baseJSONedPostData += '"';
                                baseJSONedPostData += ":";
                                baseJSONedPostData += '"';
                                baseJSONedPostData += baseSplitedPostData[i].split("=")[1];
                                baseJSONedPostData += '"';
                                baseJSONedPostData += ',';
                                console.log("json is" + baseJSONedPostData);
                            }
                            if (baseJSONedPostData.indexOf("transformations") == -1){
                                baseJSONedPostData +=  '"transformations":"'+baseSplitedPostData[1].split("=")[1]+'"}';
                            }else{
                                var baseLastIndex = baseJSONedPostData.lastIndexOf(",");
                                baseJSONedPostData = baseJSONedPostData.substring(0,baseLastIndex);
                                baseJSONedPostData +=  "}";
                            }
                            console.log(baseJSONedPostData);


                           var baseReturnObj='{"Result":"OK","Record":' + baseJSONedPostData + '}';
                           var baseJSONedReturn = $.parseJSON(baseReturnObj);

                           return $.Deferred(function($dfd) {
                                console.log(baseJSONedReturn);
                                $dfd.resolve(baseJSONedReturn);
                            });
        		    },
        			   	updateAction: function(postData) {
                        console.log(postData);
                                                                                var baseUpdateSplitedPostData = postData.split("&");
                                                                                var baseUpdateJSONedPostData = '{';
                                                                                for (i=0; i < baseUpdateSplitedPostData.length ; i++)
                                                                                {
                                                                                    console.log("data is " + baseUpdateSplitedPostData[i]);
                                                                                    baseUpdateJSONedPostData += '"';
                                                                                    baseUpdateJSONedPostData += baseUpdateSplitedPostData[i].split("=")[0];
                                                                                    baseUpdateJSONedPostData += '"';
                                                                                    baseUpdateJSONedPostData += ":";
                                                                                    baseUpdateJSONedPostData += '"';
                                                                                    baseUpdateJSONedPostData += baseUpdateSplitedPostData[i].split("=")[1];
                                                                                    baseUpdateJSONedPostData += '"';
                                                                                    baseUpdateJSONedPostData += ',';
                                                                                    console.log("json is" + baseUpdateJSONedPostData);
                                                                                }
                                                                                if (baseUpdateJSONedPostData.indexOf("transformation") == -1){
                                                                                    baseUpdateJSONedPostData +=  '"transformations":"'+baseUpdateSplitedPostData[1].split("=")[1]+'"}';
                                                                                }else{
                                                                                    var baseUpdateLastIndex = baseUpdateJSONedPostData.lastIndexOf(",");
                                                                                    baseUpdateJSONedPostData = baseUpdateJSONedPostData.substring(0,baseUpdateLastIndex);
                                                                                    baseUpdateJSONedPostData +=  "}";
                                                                                }
                                                                                console.log(baseUpdateJSONedPostData);


                                                                               var baseUpdateReturnObj='{"Result":"OK","Record":' + baseUpdateJSONedPostData + '}';
                                                                               var baseupdateJSONedReturn = $.parseJSON(baseUpdateReturnObj);

                                                                           return $.Deferred(function($dfd) {
                                                                           					console.log(baseUpdateReturnObj);
                                                                           					$dfd.resolve(baseupdateJSONedReturn);
                                                                           				});

                        },
                        deleteAction: function() {
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
                            list: true,
                            create:true,
                            edit: true,
                            title: 'Column Name'
                        },
                        dataType: {

                            create: true,
                            title: 'Data Type',
                            edit: true,
                            options:{ 'BigInt':'BigInt',
									  'SmallInt':'SmallInt',
									  'Float':'Float',
									  'Double':'Double',
									  'Decimal':'Decimal',
									  'Timestamp':'Timestamp',
									  'Date':'Date',
									  'String':'String'}

                        },
                         transformations: {
                            title: 'Source',
                            create: true,
                            edit: true
                        },
                        partition: {
                                                    title: 'Partition Column',
                                                    create: true,
                                                    edit: true
                                                }
        		    }
        	    });
        		    $('#baseTableColumnDetails').jtable('load');
        	    });

        	</script>



		<script>
function buildForm(fileformat) {
	console.log('inside the function');

	$.ajax({
		type: "GET",
		url: "/mdrest/genconfig/" + fileformat + "/?required=2",
		dataType: 'json',
		success: function(data) {
			var root = 'Records';
			var div = document.getElementById('fileFormatDetails');
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
		var keys = typeProp + record.columnName;
		console.log(keys);
		map[keys] = record.dataType;
		console.log(map);
	});
	$('.jtable-row-selected').removeClass('jtable-row-selected');
}

		</script>
				<script>
        function jtableIntoMapForBase(typeDiv) {
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
        		console.log(record.columnName);
        		map["transform_"+record.columnName] = record.transformations;
        		map["stagedatatype_"+record.columnName] = record.dataType;
        		map["baseaction_"+record.columnName] = record.dataType;
        		map["partition_"+record.columnName] = record.partition;
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
