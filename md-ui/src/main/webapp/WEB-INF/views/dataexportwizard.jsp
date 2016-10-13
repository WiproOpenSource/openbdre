<%@ taglib prefix="security"
       uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
     pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>

<head>
    <title><spring:message code="common.page.title_bdre_2"/></title>

	<script>
	  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
	  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
	  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
	  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');
	  //Please replace with your own analytics id
	  ga('create', 'UA-72345517-1', 'auto');
	  ga('send', 'pageview');
	</script>

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
    <link rel="stylesheet" href="../css/jquery.steps.custom.css" />
    <link href="../css/bootstrap.custom.css" rel="stylesheet" type="text/css" />
	<script src="../js/angular.min.js" type="text/javascript"></script>
    <script type="text/javascript">

    var map = new Object();
    var createJobResult;


    </script>
<script >
        function fetchPipelineInfo(pid){
			location.href = '<c:url value="/pages/lineage.page?pid="/>' + pid;
        }
</script >
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
</head>
<body ng-app="myApp" ng-controller="myCtrl">
	<div class="page-header"><spring:message code="dataexportwizard.page.panel_heading"/></div>

	<div class="alert alert-info" role="alert">
	   <spring:message code="dataexportwizard.page.alert_info_outer"/>
    </div>


    <div id="bdre-export"  >
      <h3><div class="number-circular">1</div>Output Table Details:</h3>
           <section >
              <form class="form-horizontal" role="form" id="DatabaseDetailsForm">

           					<div >

           						<div class="form-group">
           						<label for = "dbURL" ><spring:message code="dataexportwizard.page.db_url"/></label >
           						<input id = "dbURL"  name = "dbURL" type = "text" class = "form-control" />
           						</div>
           						<div class="form-group">
           						<label for = "dbUser" ><spring:message code="dataexportwizard.page.db_user"/></label >
           						<input id = "dbUser"  name = "dbUser" type = "text" class = "form-control"  />
           						</div>
           						<div class="form-group">
           						<label for = "dbPassword" ><spring:message code="dataexportwizard.page.db_psswd"/></label >
           						<input id = "dbPassword" name = "dbPassword" type = "password" class = "form-control"  />
           						</div>
           						<div class="form-group">
           						<label for = "dbDriver" ><spring:message code="dataexportwizard.page.db_driver"/></label >
           						<input id = "dbDriver" name = "dbDriver" type = "text" class = "form-control" />
           						</div>
           						<div class="form-group">
           						<label for = "table" ><spring:message code="dataexportwizard.page.db_table"/></label >
                                   <input id = "table"  name = "table" type = "text" class = "form-control" />
           						</div>
           						<div class="clearfix"></div>

           					</div >
                </form>
           	</section >




            <h3><div class="number-circular">2</div>Input Data Details:</h3>
            <section>
            <form class="form-horizontal" role="form" id="inputFieldsForm">
                <div id="InputDataDetails">
                    <div class="alert alert-info" role="alert">
						<spring:message code="dataexportwizard.page.alert_info_2"/>
                    </div>
                    <!-- btn-group -->
                    <div id="InputDataDetails2">
                        <div class="form-group">
                            <label class="control-label col-sm-2" for="inputHDFSDir"><spring:message code="dataexportwizard.page.export_dir"/></label>
                            <div class="col-sm-10">
                                <input type="text" class="form-control" name="inputHDFSDir"  >
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label col-sm-2" for="inputDataDelimiter"><spring:message code="dataexportwizard.page.delimiter"/></label>
                            <div class="col-sm-10">
                                <input type="text" class="form-control" name="inputDataDelimiter">
                            </div>
                        </div>

                    </div>
                    <!-- /btn-group -->
                </div>
                </form>
                </section>
                <h3><div class="number-circular">3</div>Process Details</h3>
                <section>
                    <form class="form-horizontal" role="form" id="processFieldsForm1">
                        <div id="processDetails">
                            <div class="alert alert-info" role="alert">
                                <spring:message code="dataexportwizard.page.alert_info_3"/>
                            </div>
                            <!-- btn-group -->
                            <div id="processFields">

                                <div class="form-group">
                                    <label class="control-label col-sm-2" for="processName"><spring:message code="dataexportwizard.page.proc_name"/></label>
                                    <div class="col-sm-10">
                                        <input type="text" class="form-control"  id="processName" name="processName" placeholder=<spring:message code="dataexportwizard.page.proc_name_placeholder"/> required>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label class="control-label col-sm-2" for="processDescription"><spring:message code="dataexportwizard.page.proc_desc"/></label>
                                    <div class="col-sm-10">
                                        <input type="text" class="form-control" id="processDescription" name="processDescription" placeholder=<spring:message code="dataexportwizard.page.proc_desc_placeholder"/> required>
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label class="control-label col-sm-2" for="busDomainId"><spring:message code="dataexportwizard.page.bus_domain_id"/></label>
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
                <h3><div class="number-circular">4</div>Create Job</h3>
                <section>
                <div id="createProcess">
                    <button ng-click="createJob()" id="createjobs" type="button" class="btn btn-primary"><spring:message code="dataexportwizard.page.create_export_job"/>

</button>
                </div>
                <div id="Process"></div>
                </section>

    </div>


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

             $scope.workflowTypes = {};
                                $.ajax({
                                url: '/mdrest/workflowtype/optionslist',
                                    type: 'POST',
                                    dataType: 'json',
                                    async: false,
                                    success: function (data) {
                                        $scope.workflowTypes = data;
                                    },
                                    error: function () {
                                        alert('danger');
                                    }
                                });


            $scope.createJob =function (){

                $.ajax({

                                        type: "POST",
                                        url: "/mdrest/dataexport/createjobs/",
                                        data: jQuery.param(map),
                                        success: function(data) {
                                            if(data.Result == "OK") {
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
                                                }).html('<p><span class="jtable-confirm-message"><spring:message code="crawler.page.success_msg"/></span></p>');
                                                createJobResult = data;
                                                displayProcess(createJobResult);
                                                $('#createProcess').hide();
                                            }
                                            else{
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
                                                }).html("<p><span class=\"jtable-confirm-message\">"+ data.Message+ "</span></p>");
                                            }
                                            console.log(createJobResult);
                                        }

                });
            }
        });
    </script>
    <script type="text/javascript">
    var wizard = null;

        $("#bdre-export").steps({
            headerTag: "h3",
            bodyTag: "section",
            transitionEffect: "slideLeft",
            stepsOrientation: "horizontal",
            enableCancelButton: true,
            onStepChanging: function(event, currentIndex, newIndex) {
            			console.log(currentIndex + 'current ' + newIndex + 'process Name');

            			if(currentIndex == 2 && newIndex == 3 && document.getElementById('processFieldsForm1').elements[0].value == "" && document.getElementById('processFieldsForm1').elements[1].value == "") {
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
            				}).html('<p><span class="jtable-confirm-message"><spring:message code="crawler.page.enter_details"/></span></p>');
            				return false;
            			}
            			return true;
            },
            onStepChanged: function(event, currentIndex, priorIndex) {
              			console.log(currentIndex + " " + priorIndex);

              			if(currentIndex == 1 && priorIndex == 0) {
              			 formIntoMap('dbDetails_', 'DatabaseDetailsForm');
              			    }

              			if(currentIndex == 2 && priorIndex == 1) {
                         formIntoMap('inputData_', 'inputFieldsForm');
                            }

                        if(currentIndex == 3 && priorIndex == 2) {
                         formIntoMap('processFields_', 'processFieldsForm1');
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
                                    }).html('<p><span class="jtable-confirm-message"><spring:message code="crawler.page.failed_msg"/></span></p>');
                                }
                },
            onCanceled: function(event) {
            location.href = '<c:url value="/pages/dataexportwizard.page"/>';
            }

        });

</script>
<script>
function displayProcess(records) {
    $('#Process').jtable({
        title: '<spring:message code="crawler.page.title_jtable"/>',
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
                title: '<spring:message code="crawler.page.title_id"/>'
            },
            Properties: {
                title: '<spring:message code="crawler.page.title_properties"/>',
                width: '5%',
                sorting: false,
                edit: false,
                create: false,
                listClass: 'bdre-jtable-button',
                display: function(item) { //Create an image that will be used to open child table

                    var $img = $('<span class="label label-primary"><spring:message code="crawler.page.img_show"/></span>'); //Open child table when user clicks the image

                    $img.click(function() {
                        $('#Process').jtable('openChildTable',
                            $img.closest('tr'), {
                                title: '<spring:message code="crawler.page.img_title"/>'+' ' + item.record.processId,
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
                                        title: '<spring:message code="crawler.page.title_process"/>',
                                        defaultValue: item.record.processId,
                                    },
                                    configGroup: {
                                        title: '<spring:message code="crawler.page.title_cg"/>',
                                        defaultValue: item.record.configGroup,
                                    },
                                    key: {
                                        title: '<spring:message code="crawler.page.title_key"/>',
                                        key: true,
                                        list: true,
                                        create: true,
                                        edit: false,
                                        defaultValue: item.record.key,
                                    },
                                    value: {
                                        title: '<spring:message code="crawler.page.title_value"/>',
                                        defaultValue: item.record.value,
                                    },
                                    description: {
                                        title: '<spring:message code="crawler.page.title_desc"/>',
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
                title: '<spring:message code="crawler.page.title_name"/>'
            },
            tableAddTS: {
                title: '<spring:message code="crawler.page.title_add_ts"/>',
                create: false,
                edit: true,
                list: false,
                type: 'hidden'
            },
            description: {
                title: '<spring:message code="crawler.page.title_desc"/>',
            },
            batchPattern: {
                title: '<spring:message code="crawler.page.title_batch_mark"/>',
                list: false,
                create: false,
                edit: true,
                type: 'hidden'

            },
            parentProcessId: {
                title: '<spring:message code="crawler.page.title_parent"/>',
                edit: true,
                create: false,
                list: false,
                type: 'hidden'
            },
            canRecover: {
                title: '<spring:message code="crawler.page.title_restorable"/>',
                type: 'hidden',
                list: false,
                edit: true,
            },
            nextProcessIds: {
                title: '<spring:message code="crawler.page.title_next"/>',
                list: false,
                edit: true,
                type: 'hidden'

            },
            enqProcessId: {
                title: '<spring:message code="crawler.page.title_enque"/>',
                list: false,
                edit: true,
                type: 'hidden',
            },
            busDomainId: {
                title: '<spring:message code="crawler.page.title_app"/>',
                list: false,
                edit: true,
                type: 'combobox',
                options: '/mdrest/busdomain/options/',
            },
            processTypeId: {
                title: '<spring:message code="crawler.page.title_type"/>',
                edit: true,
                type: 'hidden',
                options: '/mdrest/processtype/optionslist'

            },
            ProcessPipelineButton: {
                title: '<spring:message code="crawler.page.title_pipeline"/>',
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
<div id="div-dialog-warning"/>
</body>

</html>

