<%@ taglib prefix="security"
	   uri="http://www.springframework.org/security/tags" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
        <%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	 pageEncoding="ISO-8859-1"%>
            <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

            <html>

            <head>
                <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
                <title><spring:message code="common.page.title_bdre_1"/></title>
            <script>
              (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
              (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
              m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
              })(window,document,'script','//www.google-analytics.com/analytics.js','ga');
              //Please replace with your own analytics id
              ga('create', 'UA-72345517-1', 'auto');
              ga('send', 'pageview');
            </script>

                <!-- Include one of jTable styles. -->
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
                <link href="../css/css/bootstrap.min.css" rel="stylesheet" type="text/css" />
                <link href="../css/jtables-bdre.css" rel="stylesheet" type="text/css" />
                <link rel="stylesheet" href="../css/jquery.steps.custom.css" />
                <link href="../css/bootstrap.custom.css" rel="stylesheet" type="text/css" />
                <link href="../css/jquery-ui-1.10.3.custom.css" rel="stylesheet" type="text/css" />
                <link href="../css/select2.min.css" rel="stylesheet" />

                <!-- Include jTable script file. -->
                <script src="../js/jquery.min.js" type="text/javascript"></script>
                <script src="../js/bootstrap.js" type="text/javascript"></script>
                <script src="../js/jquery-ui-1.10.3.custom.js" type="text/javascript"></script>
                <script src="../js/select2.min.js"></script>

                <script type="text/javascript">
                    $(".js-example-placeholder-multiple").select2();
                </script>


                <link rel="stylesheet" type="text/css" href="../js/fc/wf.css">

                <!-- Library code. -->

                <script src="../js/angular.min.js" type="text/javascript"></script>

                <!-- Flowchart code. -->
                <script src="../js/svgutil.js" type="text/javascript"></script>
                <script src="../js/fc/app2.js" type="text/javascript"></script>
                <script src="../js/fc/debug.js" type="text/javascript"></script>
                <script src="../js/fc/svg_class.js" type="text/javascript"></script>
                <script src="../js/fc/mouse_capture_service.js" type="text/javascript"></script>
                <script src="../js/fc/dragging_service.js" type="text/javascript"></script>
                <script src="../js/fc/flowchart_viewmodel2.js" type="text/javascript"></script>
                <script src="../js/fc/flowchart_directive2.js" type="text/javascript"></script>


                <!--Ajax calls Code. -->
                <script type="text/javascript" src="../js/fc/wfd-ac2.js"></script>
                <script type="text/javascript" src="../js/fc/cachejs.js"></script>

                <!--Utilities -->
                <script src="../js/nanobar.min.js" type="text/javascript"></script>

                <style>


                      .usericon {
                            display: block;
                            width: 30px;
                            height: 30px;
                            border-radius: 80px;
                            background: #FFF no-repeat center;
                            background-image: url("../css/images/user_icon.png");
                            background-size: 65% 65%;
                        }

                     .side-container{
                                            padding-left:75px;
                                    }
                     .modelwindow {
                               display: none; /* Hidden by default */
                               position: fixed; /* Stay in place */
                               z-index: 1; /* Sit on top */
                               padding-top: 60px; /* Location of the box */
                               left: 0;
                               top: 0;
                               width: 100%; /* Full width */
                               height: 100%; /* Full height */
                               overflow: auto; /* Enable scroll if needed */
                               background-color: rgb(0,0,0); /* Fallback color */
                               background-color: rgba(0,0,0,0.4); /* Black w/ opacity */
                           }

                           .remove-me{
                           margin-top: 0px;

                           }
                           .remove-me-broadcast{
                               margin-top: 0%;
                               margin-bottom:1%;
                           }

                           .remove-me-filter{
                                  margin-top: 0%;
                                  margin-bottom:1%;
                              }

                           .remove-me-enricher{
                                    margin-bottom: 0%;
                                    margin-top: 1.5%;
                             }

                           #deletediv{
                           padding-left: 0px;
                           }

                           /* Modal Content */
                           .modal-content {
                               background-color: #fefefe;
                               margin: auto;
                               padding: 20px;
                               border: 1px solid #888;
                               width: 60%;
                           }

                           /* The Close Button */
                           .closemodal {
                               color: #aaaaaa;
                               float: right;
                               font-size: 28px;
                               font-weight: bold;
                               margin-top: -25px;
                               margin-right: -15px;

                           }

                           .close:hover,
                           .close:focus {
                               color: #000;
                               text-decoration: none;
                               cursor: pointer;
                           }





                    /* make sidebar nav vertical */

                    .sidebar-nav {
                        min-height: 700px;
                    }

                    .alert {
                        padding: 5px;
                    }

                    .fixed-height {
                        max-height: 750px;
                    }

                    .level2 {
                        opacity: .6;
                        -moz-opacity: 0.6;
                        z-index: 2;
                    }

                    .level2:hover {
                        opacity: 1;
                        -moz-opacity: .99;
                    }

                    .foldablearrow:after {
                        font-family: "Glyphicons Halflings";
                        content: "\e114";
                        float: right;
                        margin-left: 15px;
                    }

                    .foldablearrow.collapsed:after {
                        content: "\e080";
                    }

                    #makescrollable {
                        max-height: 250px;
                        overflow: auto;
                        overflow-x: hidden;
                    }

                    #modalCover {
                        left: 0;
                        top: 0;
                        width: 100%;
                        height: 800px;
                        position: fixed;
                        background-color: black;
                        z-index: 9999;
                        opacity: 0.4;
                        filter: alpha(opacity=40);
                        /* For IE8 and earlier */
                    }
                    .panel-body, .panel-primary>.panel-heading {
					    background-color: transparent;
					}
					.label-info{
						background-color: #FABD1A;
					    padding: 7px 14px;
					    color: #000;
					}
					.label-default{
						background-color: #FABD1A;
					    padding: 7px 14px;
					    color: #000;
					}
					.form-group-file{
						width: 100% !important;
					}
					.panel-body{
						padding: 15px;
					}
			</style>

            </head>

            <script type="text/javascript">
                         var workspace="";
                         function findWorkspace() {
                             var location=window.location.href;
                             console.log(window.location.href);
                             var res = location.split("/");
                             for (var i in res) {
                              if(res[i].includes("mdui")==true)
                                 {
                                  workspace=res[i];
                                  console.log(workspace);
                                 }
                             }

                             if(workspace!="mdui")
                             $('#logout').append(" from "+workspace.substring(5,workspace.length));
                         }
                         window.onload = findWorkspace;
                         </script>

            <div class="page-header"><spring:message code="wfdesigner.page.create_new_workflow"/></div>
            <c:choose>
                <c:when test="${not empty param.processId}">

                    <body ng-app="app" data-ng-init="init(${param.processId})" ng-controller="AppCtrl" mouse-capture ng-keydown="keyDown($event)" ng-keyup="keyUp($event)">
                        <div id="nanobardiv"></div>
                        <div class="row">
                            <div class="col-md-3">&nbsp;</div>
                        </div>






                        <div class="row" style="background-color: #F8F9FB;padding-top: 2%;border-radius:5px;">

                         <div id="myModal" class="modelwindow">

                                          <!-- Modal content -->
                                            <div class="modal-content">
                                              <span class="closemodal">&times;</span>
                            <div>
                                <div class="panel panel-default" ng-if="chartViewModel.selectedProcess.processName == null" class="animate-if">
                                    <div class="panel-heading" data-toggle="collapse" data-parent="#accordion"><spring:message code="wfdesigner.page.panel_heading"/></div>
                                    <div class="panel-body"><spring:message code="wfdesigner.page.panel_body"/></div>
                                </div>
                                <div class="panel-group" id="accordion" ng-if="chartViewModel.selectedProcess.processName != null" class="animate-if">
                                    <div class="panel panel-default">
                                        <div class="panel-heading foldablearrow " data-toggle="collapse" data-parent="#accordion" data-target="#processdetails">
                                            <spring:message code="wfdesigner.page.process_details"/>
                                        </div>
                                        <div id="processdetails" class="panel-collapse collapse in">
                                            <div class="panel-body">
                                                <form role="form" class="form-horizontal">
                                                    <div class="form-group" style="display:none;">
                                                        <label class="control-label col-md-4"><spring:message code="wfdesigner.page.id"/></label>
                                                        <div class="col-md-8">
                                                            <h5><span class="label label-info">{{ chartViewModel.selectedProcess.processId }}</span></h5>
                                                        </div>
                                                    </div>

                                                    <div class="form-group" style="display:none;">
                                                        <label class="control-label col-md-4"><spring:message code="wfdesigner.page.type"/></label>
                                                        <div class="col-md-8">
                                                            <h5><span class="label label-default">{{ chartViewModel.selectedProcess.processTypeId }}</span></h5>
                                                        </div>
                                                    </div>
                                                    <div class="form-group">
                                                        <label class="control-label col-md-4" for="process.name"><spring:message code="wfdesigner.page.name"/></label>
                                                        <div class="col-md-8">
                                                            <input name="processName" id="process.name" class="form-control input-sm" type="text" ng-model="chartViewModel.selectedProcess.processName">
                                                        </div>
                                                    </div>
                                                    <div class="form-group">
                                                        <label class="control-label col-md-4" for="process.description"><spring:message code="wfdesigner.page.description"/></label>
                                                        <div class="col-md-8">
                                                            <input name="description" id="process.description" class="form-control input-sm" type="text" ng-model="chartViewModel.selectedProcess.description">
                                                        </div>
                                                    </div>
                                                    <div class="clearfix"></div>

                                                    <input type="hidden" name="processId" value="{{ chartViewModel.selectedProcess.processId }}">
                                                    <input type="hidden" name="batchPattern" value="{{ chartViewModel.selectedProcess.batchPattern }}">
                                                    <input type="hidden" name="parentProcessId" value="{{ chartViewModel.selectedProcess.parentProcessId }}">
                                                    <input type="hidden" name="canRecover" value="{{ chartViewModel.selectedProcess.canRecover }}">
                                                    <input type="hidden" name="nextProcessIds" value="{{ chartViewModel.selectedProcess.nextProcessIds }}">
                                                    <input type="hidden" name="enqProcessId" value="{{ chartViewModel.selectedProcess.enqProcessId }}">
                                                    <input type="hidden" name="busDomainId" value="{{ chartViewModel.selectedProcess.busDomainId }}">
                                                    <input type="hidden" name="processTypeId" value="{{ chartViewModel.selectedProcess.processTypeId }}">
                                                    <input type="hidden" name="workflowId" value="{{ chartViewModel.selectedProcess.workflowId }}">
                                                    <input type="hidden" name="processTemplateId" value="{{ chartViewModel.selectedProcess.processTemplateId }}">
                                                    <button type="button" class="btn btn-sm btn-primary pull-right" ng-click="updateProcessDetails()"><spring:message code="wfdesigner.page.update_process_details"/></button>
                                                </form>
                                            </div>
                                        </div>
                                    </div>



                                    <div class="panel panel-default" ng-if="chartViewModel.selectedProcess.parentProcessId == null">
                                        <div class="panel-heading foldablearrow " data-toggle="collapse" data-parent="#accordion" data-target="#jarupload" ng-click="getJarList()">
                                            <spring:message code="wfdesigner.page.jar_configuration"/>
                                        </div>
                                        <div id="jarupload" class="panel-collapse collapse">
                                            <div class="panel-body">
                                                <div class="makescrollable">
                                                    <div class="row" ng-repeat="jar in jarList">
                                                        <div class="col-md-9">
                                                            <p class="form-control-static" ng-if="jar.length > 20" id="{{jar}}" for="{{jar}}" title="{{jar}}">{{ jar| limitTo : 20 : 0}}...</p>
                                                            <p class="form-control-static" ng-if="jar.length <= 20" id="{{jar}}" for="{{jar}}">{{ jar}}</p>
                                                        </div>
                                                        <div class="col-md-3">
                                                            <a href="#" class="glyphicon glyphicon-trash" ng-click="deleteJar(chartViewModel.selectedProcess.processId,'lib',jar)"></a>
                                                        </div>
                                                    </div>
                                                </div>
                                                <hr/>
                                                <form role="form" class="form-horizontal">
                                                    <div class="form-group form-group-file">
                                                        <label class="control-label col-sm-3"><spring:message code="wfdesigner.page.select_file"/></label>
                                                        <div class="col-sm-10">
                                                            <input type="file" name="file" class="form-control" id="jar-id" required>
                                                        </div>
                                                    </div>
                                                    <div class="clearfix"></div>
                                                    <button type="button" class="btn btn-sm btn-primary pull-right" ng-click="uploadJar(chartViewModel.selectedProcess.processId,'lib','jar-id')"><spring:message code="wfdesigner.page.upload_jar"/></button>
                                                </form>
                                            </div>
                                        </div>
                                    </div>



                                    <div class="panel panel-default" ng-repeat="genConfig in chartViewModel.selectedProcessGenConfigProp">
                                        <div class="panel-heading foldablearrow" data-toggle="collapse" data-parent="#accordion" data-target="#-{{genConfig.key}}" ng-click="addFilterDataAlreadyPresent(chartViewModel.selectedProcess.processId,genConfig);chartViewModel.getKeyValueFunction(genConfig);">
                                            {{genConfig.value}}
                                        </div>
                                        <div id="-{{genConfig.key}}" class="panel-collapse collapse">

                                            <div id="propertySearchBox">
                                                <input type="text" class="form-control input-sm" placeholder=<spring:message code="wfdesigner.page.search_property_placeholder"/> ng-model="searchText">
                                            </div>

                                            <div class="panel-body">
                                                <div id="makescrollable">
                                                    <div class="row" ng-repeat="gen in chartViewModel.selectedProcessConfigKeyValue | filter:searchText" ng-if="gen.key != 'scriptPath' && gen.key != 'mapper'  && gen.key != 'reducer' &&  !isFileId(gen.key)">
                                                        <div class="col-md-3">
                                                            <label class="control-label" ng-if="gen.key.length > 7" id="{{genConfig.key}}-{{gen.key}}" for="{{genConfig.key}}-{{gen.value}}" title="{{ gen.key }}">{{ gen.key | limitTo : 7 : 0}}...:</label>
                                                            <label class="control-label" ng-if="gen.key.length <= 7" id="{{genConfig.key}}-{{gen.key}}" for="{{genConfig.key}}-{{gen.value}}">{{ gen.key }}:</label>
                                                        </div>
                                                        <div class="col-md-6">
                                                            <input required class="form-control input-sm" id="{{genConfig.key}}-{{gen.value}}" type="text" ng-model="gen.value" />
                                                        </div>
                                                        <div class="col-md-3">
                                                            <a href="#" class="glyphicon glyphicon-ok-circle" ng-click="updateProp(genConfig, gen)"></a>
                                                            <a href="#" class="glyphicon glyphicon-trash" ng-click="deleteProp(genConfig,gen)"></a>
                                                        </div>
                                                    </div>
                                                    <div class="row" ng-repeat="gen in chartViewModel.selectedProcessConfigKeyValue" ng-if="gen.key == 'scriptPath'">
                                                        <div class="col-md-3">
                                                            <label class="control-label" ng-if="gen.key.length > 7" id="{{genConfig.key}}-{{gen.key}}" for="{{genConfig.key}}-{{gen.value}}" title="{{ gen.key }}">{{ gen.key | limitTo : 7 : 0}}...:</label>
                                                            <label class="control-label" ng-if="gen.key.length <= 7" id="{{genConfig.key}}-{{gen.key}}" for="{{genConfig.key}}-{{gen.value}}">{{ gen.key }}:</label>
                                                        </div>
                                                        <div class="col-md-6">
                                                            <p class="form-control-static" ng-if="gen.value.length > 20" id="{{genConfig.key}}-{{gen.value}}" for="{{genConfig.key}}-{{gen.value}}" title="{{ gen.value }}">{{ gen.value | limitTo : 20 : 0}}...</p>
                                                            <p class="form-control-static" ng-if="gen.value.length <= 20" id="{{genConfig.key}}-{{gen.value}}" for="{{genConfig.key}}-{{gen.value}}">{{ gen.value }}</p>
                                                        </div>
                                                        <div class="col-md-3">
                                                            <a href="#" class="glyphicon glyphicon-trash" ng-click="deleteFile(chartViewModel.selectedProcess.parentProcessId,genConfig, gen)"></a>
                                                        </div>
                                                    </div>
                                                     <div class="row" ng-repeat="gen in chartViewModel.selectedProcessConfigKeyValue" ng-if="gen.key == 'mapper' || gen.key == 'reducer'">
                                                            <div class="col-md-3">
                                                                <label class="control-label" ng-if="gen.key.length > 7" id="{{genConfig.key}}-{{gen.key}}" for="{{genConfig.key}}-{{gen.value}}" title="{{ gen.key }}">{{ gen.key | limitTo : 7 : 0}}...:</label>
                                                                <label class="control-label" ng-if="gen.key.length <= 7" id="{{genConfig.key}}-{{gen.key}}" for="{{genConfig.key}}-{{gen.value}}">{{ gen.key }}:</label>
                                                            </div>
                                                            <div class="col-md-6">
                                                                <p class="form-control-static" ng-if="gen.value.length > 20" id="{{genConfig.key}}-{{gen.value}}" for="{{genConfig.key}}-{{gen.value}}" title="{{ gen.value }}">{{ gen.value | limitTo : 20 : 0}}...</p>
                                                                <p class="form-control-static" ng-if="gen.value.length <= 20" id="{{genConfig.key}}-{{gen.value}}" for="{{genConfig.key}}-{{gen.value}}">{{ gen.value }}</p>
                                                            </div>
                                                            <div class="col-md-3">
                                                                <a href="#" class="glyphicon glyphicon-trash" ng-click="deleteFile(chartViewModel.selectedProcess.parentProcessId,genConfig, gen)"></a>
                                                            </div>
                                                        </div>
                                                    <div class="row" ng-repeat="gen in chartViewModel.selectedProcessConfigKeyValue" ng-if="isFileId(gen.key)">
                                                        <div class="col-md-3">
                                                            <label class="control-label" ng-if="gen.key.length > 7" id="{{genConfig.key}}-{{gen.key}}" for="{{genConfig.key}}-{{gen.value}}" title="{{ gen.key }}">{{ gen.key | limitTo : 7 : 0}}...:</label>
                                                            <label class="control-label" ng-if="gen.key.length <= 7" id="{{genConfig.key}}-{{gen.key}}" for="{{genConfig.key}}-{{gen.value}}">{{ gen.key }}:</label>
                                                        </div>
                                                        <div class="col-md-6">
                                                            <p class="form-control-static" ng-if="gen.value.length > 20" id="{{genConfig.key}}-{{gen.value}}" for="{{genConfig.key}}-{{gen.value}}" title="{{ gen.value }}">{{ gen.value | limitTo : 20 : 0}}...</p>
                                                            <p class="form-control-static" ng-if="gen.value.length <= 20" id="{{genConfig.key}}-{{gen.value}}" for="{{genConfig.key}}-{{gen.value}}">{{ gen.value }}</p>
                                                        </div>
                                                        <div class="col-md-3">
                                                            <a href="#" class="glyphicon glyphicon-trash" ng-click="deleteFile(chartViewModel.selectedProcess.parentProcessId,genConfig, gen)"></a>
                                                        </div>
                                                    </div>
                                                </div>
                                                <hr/>
                                                <form class="form-horizontal" role="form" ng-if="genConfig.type != 'hql'  && genConfig.type != 'hadoopstream' && genConfig.type != 'r'  && genConfig.type != 'spark' && genConfig.type != 'pig' && genConfig.type != 'shell' && genConfig.type != 'source' && genConfig.type != 'filter' && genConfig.type != 'sort' && genConfig.type != 'take' && genConfig.type != 'persist' && genConfig.type != 'repartition' && genConfig.type != 'hive' && genConfig.type!='join' && genConfig.type != 'MapToPair' && genConfig.type != 'Map' && genConfig.type != 'FlatMap' && genConfig.type != 'Reduce' && genConfig.type != 'ReduceByKey' && genConfig.type != 'window' && genConfig.type != 'GroupByKey' && genConfig.type != 'emitter' && genConfig.type != 'persistentStore' && genConfig.type != 'addFiles' && genConfig.type != 'aggregation'  && genConfig.type != 'deDuplication' && genConfig.type != 'Custom' && genConfig.type != 'enricher' && genConfig.type != 'linearRegression'">

                                                    <div class="form-group">
                                                        <label class="control-label col-sm-2" for="{{genConfig.key}}-propkey"><spring:message code="wfdesigner.page.propkey_name"/></label>
                                                        <div class="col-sm-10">
                                                            <input type="{{genConfig.key}}" class="form-control" id="{{genConfig.key}}-propkey" placeholder=<spring:message code="wfdesigner.page.propkey_name_placeholder"/> required>
                                                        </div>
                                                    </div>
                                                    <div class="form-group">
                                                        <label class="control-label col-sm-2" for="{{genConfig.key}}-propkey"><spring:message code="wfdesigner.page.propkey_value"/></label>
                                                        <div class="col-sm-10">
                                                            <input type="text" class="form-control" id="{{genConfig.key}}-propval" placeholder=<spring:message code="wfdesigner.page.propval_value_placeholder"/> required>
                                                        </div>
                                                    </div>
                                                    <div class="clearfix"></div>
                                                    <button type="submit" ng-click="insertProp(genConfig)" class="btn btn-primary  pull-right"><spring:message code="wfdesigner.page.button_add"/> {{genConfig.value}}</button>
                                                </form>

                                              <form class="form-horizontal" role="form" ng-if="genConfig.type == 'source'">

                                                       <div class="form-group">
                                                            <label for="messageName">Message Name</label>
                                                            <select class="form-control" id="messageName">
                                                                <option ng-repeat="message in newMessagesList" id="{{$index}}" value="{{ message.Value }}">{{ message.DisplayText }}</option>
                                                            </select>
                                                        </div>

                                              		  <div class="clearfix"></div>
                                              		  <button type="submit" ng-click="insertSourceProp(chartViewModel.selectedProcess.processId)" class="btn btn-primary  pull-right">Save</button>
                                              	  </form>


                                                <form class="form-horizontal" role="form" id="processFieldsForm3" ng-if="genConfig.type == 'filter'">


                                                <h3 style="margin-left: 2%;">Filter Data Details</h3>
                                                        <div>
                                                        <div class="col-md-2">Logical Op.</div>

                                                          <div class="col-md-3">Column </div>
                                                           <div class="col-md-3">Operator</div>
                                                           <div class="col-md-4">Filter Value</div>
                                                         </div>




                                                  <div class="" id="filterFormGroup1" >

                                                  <div class="col-sm-2" >

                                                  </div>


                                                   <div class="col-sm-3">

                                                      <select class="form-control" id="column.1" name="column.1">
                                                          <option ng-repeat="column in chartViewModel.columnList" id="{{$index}}" value="{{ column.Value }}">{{ column.DisplayText }}</option>
                                                      </select>
                                                  </div>

                                                  <div class="col-sm-3">

                                                    <select class="form-control" id="operator.1" name="operator.1" onchange="changeFilter(this);">
                                                        <option ng-repeat="operator in operators"  id="{{$index}}" value="{{ operator }}">{{ operator }}</option>
                                                    </select>
                                                </div>

                                                 <div class="col-sm-3">
                                                       <input type="text" class="form-control" id="filterValue.1" name="filterValue.1" placeholder="Filter value">
                                                    </div>

                                                   <button id="remove2" class="btn btn-danger remove-me-filter"><span class="glyphicon glyphicon-trash"></span></button>

                                                    </div>
                                                   <div class="" id="deletedivFilter">

                                                     <div class="col-md-5">
                                                    <button id="b1" class="btn add-more" onclick="addMoreFilter()">
                                                        <span class="glyphicon glyphicon-plus" style="font-size:large"></span>
                                                    </button>
                                                    </div>
                                                    <div class="col-md-7">
                                                    <button type="submit" style="margin-right: 0px;" ng-click="insertFilterProp(chartViewModel.selectedProcess.processId)" class="btn btn-primary  pull-right">Save</button>
                                                    </div>

                                                    </div>
                                               <div class="clearfix"></div>
                                                </form>

              <script>

                   function changeFilter(item)
                   {
                    var id= $(item).attr("id");
                    var res = id.split(".");
                    var num=res[res.length-1];
                    console.log("id is "+id+"res is "+num);
                    var operatorValue=document.getElementById(id).value;
                    console.log("operatorValue is "+operatorValue);
                    if(operatorValue.includes('null'))
                    {
                    document.getElementById("filtervalue."+num).value="";
                    document.getElementById("filtervalue."+num).disabled = true;
                    }
                    else
                    document.getElementById("filtervalue."+num).disabled = false;
                   }

                   function columnNames()
                   {
                   var connectionListArray=$('[ng-controller="AppCtrl"]').scope().chartViewModel.columnList;
                  console.log(connectionListArray);
                  var opt='';
                  for(var i=0;i<connectionListArray.length;i++){
                  console.log(connectionListArray[i].DisplayText);
                  opt+='<option value="'+connectionListArray[i].Value+'">'+connectionListArray[i].DisplayText+'</option>';
                  }
                  return opt;

                   }

                    function operators()
                      {
                      var connectionListArray=$('[ng-controller="AppCtrl"]').scope().operators;
                     console.log(connectionListArray);
                     var opt='';
                     for(var i=0;i<connectionListArray.length;i++){
                     console.log(connectionListArray[i]);
                     opt+='<option value="'+connectionListArray[i]+'">'+connectionListArray[i]+'</option>';
                     }
                     return opt;

                      }

                          var nextFilter = 1;
                            function addMoreFilter()
                            {
                            console.log("in add more function");
                             var addto = "#deletedivFilter";
                                    var addRemove = "#filterFormGroup" + (nextFilter);
                                    nextFilter = nextFilter + 1;
                                    var removeBtn = '<button id="remove' + (nextFilter) + '" class="btn btn-danger remove-me-filter" ><span class="glyphicon glyphicon-trash" ></span></button></div><div id="field">';
                                    var newIn = '';
                                    newIn = newIn +  '<div class="" id="filterFormGroup' + nextFilter + '">' ;
                                    newIn = newIn +  '<div class="col-md-2">' ;
                                    newIn = newIn +  '<select class="form-control" id="logicalOperator.' + nextFilter + '" name="logicalOperator.' + nextFilter + '">' ;
                                     newIn = newIn +  '<option  value="AND" selected>AND</option>' ;
                                     newIn = newIn +  '<option  value="OR">OR</option>';
                                    newIn = newIn +  '</select>' ;
                                    newIn = newIn +  '</div>' ;
                                    newIn = newIn +  '<div class="col-md-3">' ;
                                    newIn = newIn +  '<select class="form-control" id="column.' + nextFilter + '" name="column.' + nextFilter + '">' ;
                                     newIn = newIn +  columnNames() ;
                                    newIn = newIn +  '</select>' ;
                                    newIn = newIn +  '</div>' ;
                                    newIn = newIn +  '<div class="col-md-3">' ;
                                    newIn = newIn +  '<select class="form-control" id="operator.' + nextFilter + '"onchange="changeFilter(this);"  name="operator.' + nextFilter + '">' ;
                                     newIn = newIn +  operators() ;
                                    newIn = newIn +  '</select>' ;
                                    newIn = newIn +  '</div>' ;

                                    newIn = newIn +  '<div class="col-md-3">' ;
                                    newIn = newIn +  '<input class="form-control" id="filterValue.' + nextFilter + '"placeholder="Filter value" name="filterValue.' + nextFilter + '">' ;
                                    newIn = newIn +  '</input>' ;
                                    newIn = newIn +  '</div>' ;



                                    newIn = newIn + removeBtn;
                                    newIn = newIn +  '</div>' ;

                                    var newInput = $(newIn);
                                    var removeButton = $(removeBtn);
                                    $(addto).before(newInput);

                                    $("#filterFormGroup" + nextFilter).attr('data-source',$(addto).attr('data-source'));
                                    $("#count").val(nextFilter);

                                        $('.remove-me-filter').click(function(e){
                                            e.preventDefault();
                                            var fieldNum = this.id.charAt(this.id.length-1);
                                            var fieldID = "#filterFormGroup" + fieldNum;
                                            console.log($(this));
                                            //$(this).remove();
                                            $(fieldID).remove();
                                        });
                         }





                     </script>






                                         <form class="form-horizontal" role="form" ng-if="genConfig.type == 'sort'">

                                                   <div class="form-group">
                                                      <label class="control-label col-sm-2" for="{{genConfig.key}}-propkey">Column</label>
                                                      <select class="form-control" id="sortcolumn" >
                                                          <option ng-repeat="column in chartViewModel.columnList" id="{{$index}}" value="{{ column.Value }}">{{ column.DisplayText }}</option>
                                                      </select>
                                                  </div>

                                                  <div class="form-group">
                                                    <label class="control-label col-sm-2" for="{{genConfig.key}}-propkey">Order</label>
                                                    <select class="form-control" id="sortorder">
                                                        <option value="ascending">Ascending</option>
                                                        <option value="descending">Descending</option>
                                                     </select>
                                                </div>


                                                    <div class="clearfix"></div>
                                                     <button type="submit" ng-click="insertSortProp(chartViewModel.selectedProcess.processId)" class="btn btn-primary  pull-right">Save</button>
                                                </form>


                                           <form class="form-horizontal" role="form" ng-if="genConfig.type == 'take'">
                                             <div class="form-group">
                                                 <label class="control-label col-sm-2" for="number">Number of Elements</label>
                                                 <input type="text" class="form-control col-sm-10" id="number" required>
                                             </div>

                                              <div class="clearfix"></div>
                                                <button type="submit" ng-click="insertTakeProp(chartViewModel.selectedProcess.processId)" class="btn btn-primary  pull-right">Save</button>
                                           </form>


                                            <form class="form-horizontal" role="form" ng-if="genConfig.type == 'persist'">
                                                <div class="form-group">
                                                    <label class="control-label col-sm-2" for="storageLevel">Storage Level</label>
                                                    <select class="form-control" id="storageLevel">
                                                        <option value="NONE">NONE</option>
                                                        <option value="DISK_ONLY">DISK_ONLY</option>
                                                        <option value="DISK_ONLY_2">DISK_ONLY_2</option>
                                                        <option value="MEMORY_ONLY">MEMORY_ONLY</option>
                                                        <option value="MEMORY_ONLY_2">MEMORY_ONLY_2</option>
                                                        <option value="MEMORY_ONLY_SER">MEMORY_ONLY_SER</option>
                                                        <option value="MEMORY_ONLY_SER_2">MEMORY_ONLY_SER_2</option>
                                                        <option value="MEMORY_AND_DISK">MEMORY_AND_DISK</option>
                                                        <option value="MEMORY_AND_DISK_2">MEMORY_AND_DISK_2</option>
                                                        <option value="MEMORY_AND_DISK_SER">MEMORY_AND_DISK_SER</option>
                                                        <option value="MEMORY_AND_DISK_SER_2">MEMORY_AND_DISK_SER_2</option>
                                                        <option value="OFF_HEAP">OFF_HEAP</option>
                                                     </select>
                                                </div>

                                                 <div class="clearfix"></div>
                                                   <button type="submit" ng-click="insertPersistProp(chartViewModel.selectedProcess.processId)" class="btn btn-primary  pull-right">Save</button>
                                              </form>


                                              <form class="form-horizontal" role="form" ng-if="genConfig.type == 'repartition'">
                                                   <div class="form-group">
                                                       <label class="control-label col-sm-2" for="numPartitions">Number of Partitions</label>
                                                       <input type="text" class="form-control col-sm-10" id="numPartitions" required>
                                                   </div>

                                                   <div class="clearfix"></div>
                                                     <button type="submit" ng-click="insertRepartitionProp(chartViewModel.selectedProcess.processId)" class="btn btn-primary  pull-right">Save</button>
                                                </form>

                                               <form class="form-horizontal" role="form" ng-if="genConfig.type == 'window'">
                                                  <div class="form-group">
                                                      <label class="control-label col-sm-2" for="windowType">Window Type</label>
                                                      <select class="form-control" id="windowType">
                                                          <option value="FixedWindow">Fixed Window</option>
                                                          <option value="SlidingWindow">Sliding Window</option>
                                                       </select>
                                                  </div>

                                                  <div class="form-group">
                                                         <label class="control-label col-sm-2" for="windowDuration">Window Duration</label>
                                                         <input type="text" class="form-control col-sm-10" id="windowDuration">
                                                     </div>
                                                  <div class="form-group">
                                                     <label class="control-label col-sm-2" for="slideDuration">Slide Duration</label>
                                                     <input type="text" class="form-control col-sm-10" id="slideDuration">
                                                 </div>


                                                  <div class="clearfix"></div>
                                                    <button type="submit" ng-click="insertWindowProp(chartViewModel.selectedProcess.processId)" class="btn btn-primary  pull-right">Save</button>
                                               </form>



                                               <form class="form-horizontal" role="form" ng-if="genConfig.type == 'MapToPair'">

                                               <div class="form-group" style="width: 350px ! important;">
                                                   <label class="col-md-4 control-label" for="keyFields">Key Fields</label>
                                                       <select id="keyFields" class="js-example-basic-multiple form-control" multiple="multiple">
                                                           <option ng-repeat="column in chartViewModel.columnList" id="{{$index}}" value="{{ column.Value }}">{{ column.DisplayText }}</option>
                                                       </select>
                                                   </div>


                                               <script type="text/javascript">
                                                       $(".js-example-basic-multiple").select2();
                                                   </script>

                                                  <div class="clearfix"></div>
                                                    <button type="submit" ng-click="insertMapToPairProp(chartViewModel.selectedProcess.processId)" class="btn btn-primary  pull-right">Save</button>
                                               </form>


                                              <form class="form-horizontal" role="form" ng-if="genConfig.type == 'Map'">
                                                 <div class="form-group">
                                                     <label class="control-label col-sm-2" for="mapper">Mapper</label>
                                                     <select class="form-control" id="mapper">
                                                         <option value="Identity Mapper">Identity Mapper</option>
                                                         <option value="Custom">Custom</option>
                                                      </select>
                                                 </div>

                                                 <div class="form-group">
                                                        <label class="control-label col-sm-2" for="executorPlugin">Executor Plugin</label>
                                                        <input type="text" class="form-control col-sm-10" id="executorPlugin">
                                                 </div>


                                                <div class="form-group">
                                                    <label class="control-label col-sm-2" for="uploadJar">Upload Jar</label>
                                                    <input type="file" class="form-control col-sm-10" style="opacity: 100; position: inherit;" id="mapJar"></input>
                                                </div>

                                                 <div class="clearfix"></div>
                                                   <button type="submit" ng-click="insertMapProp(chartViewModel.selectedProcess.parentProcessId,chartViewModel.selectedProcess.processId)" class="btn btn-primary  pull-right">Save</button>
                                              </form>


                                              <form class="form-horizontal" role="form" ng-if="genConfig.type == 'FlatMap'">

                                                    <div class="form-group">
                                                       <label class="control-label col-sm-2" for="operator">Operator</label>
                                                       <select class="form-control" id="operator">
                                                           <option value="FlatMap">FlatMap</option>
                                                           <option value="FlatMapToPair">FlatMapToPair</option>
                                                        </select>
                                                   </div>

                                                 <div class="form-group">
                                                     <label class="control-label col-sm-2" for="mapper">Mapper</label>
                                                     <select class="form-control" id="mapper">
                                                         <option value="Identity Mapper">Identity Mapper</option>
                                                         <option value="Custom">Custom</option>
                                                      </select>
                                                 </div>

                                                 <div class="form-group">
                                                        <label class="control-label col-sm-2" for="executorPlugin">Executor Plugin</label>
                                                        <input type="text" class="form-control col-sm-10" id="executorPlugin">
                                                 </div>

                                                  <div class="form-group">
                                                     <label class="control-label col-sm-2" for="uploadJar">Upload Jar</label>
                                                     <input type="file" class="form-control col-sm-10" style="opacity: 100; position: inherit;" id="flatmapJar"></input>
                                                 </div>


                                                 <div class="clearfix"></div>
                                                   <button type="submit" ng-click="insertFlatMapProp(chartViewModel.selectedProcess.parentProcessId,chartViewModel.selectedProcess.processId)" class="btn btn-primary  pull-right">Save</button>
                                              </form>
                                                <form class="form-horizontal" role="form" ng-if="genConfig.type == 'Custom'">
                                               <div class="form-group">
                                                      <label class="control-label col-sm-2" for="executorPlugin">Executor Plugin</label>
                                                      <input type="text" class="form-control col-sm-10" id="executorPlugin">
                                               </div>
                                                <div class="form-group">
                                                   <label class="control-label col-sm-2" for="uploadJar">Upload Jar</label>
                                                   <input type="file" class="form-control col-sm-10" style="opacity: 100; position: inherit;" id="customJar"></input>
                                               </div>
                                               <div class="clearfix"></div>
                                                 <button type="submit" ng-click="insertCustomProp(chartViewModel.selectedProcess.parentProcessId,chartViewModel.selectedProcess.processId)" class="btn btn-primary  pull-right">Save</button>
                                            </form>
                                             <form class="form-horizontal" role="form" ng-if="genConfig.type == 'CustomFilter'">
                                               <div class="form-group">
                                                      <label class="control-label col-sm-2" for="executorPlugin">Executor Plugin</label>
                                                      <input type="text" class="form-control col-sm-10" id="executorPlugin">
                                               </div>
                                                <div class="form-group">
                                                   <label class="control-label col-sm-2" for="uploadJar">Upload Jar</label>
                                                   <input type="file" class="form-control col-sm-10" style="opacity: 100; position: inherit;" id="customFilterJar"></input>
                                               </div>
                                               <div class="clearfix"></div>
                                                 <button type="submit" ng-click="insertCustomFilterProp(chartViewModel.selectedProcess.parentProcessId,chartViewModel.selectedProcess.processId)" class="btn btn-primary  pull-right">Save</button>
                                            </form>

                                                <form class="form-horizontal" role="form" ng-if="genConfig.type == 'Reduce'">
                                                 <div class="form-group">
                                                     <label class="control-label col-sm-2" for="operator">Operator</label>
                                                     <select class="form-control" id="operator">
                                                         <option value="Reduce">Reduce</option>
                                                         <option value="ReduceByWindow">ReduceByWindow</option>
                                                      </select>
                                                 </div>

                                                 <div class="form-group">
                                                        <label class="control-label col-sm-2" for="executorPlugin">Executor Plugin</label>
                                                        <input type="text" class="form-control col-sm-10" id="executorPlugin">
                                                 </div>

                                                  <div class="form-group">
                                                     <label class="control-label col-sm-2" for="uploadJar">Upload Jar</label>
                                                     <input type="file" class="form-control col-sm-10" style="opacity: 100; position: inherit;" id="reduceJar" ></input>
                                                 </div>


                                                 <div class="form-group">
                                                          <label class="control-label col-sm-2" for="windowDuration">Window Duration</label>
                                                          <input type="text" class="form-control col-sm-10" id="windowDuration">
                                                  </div>

                                                   <div class="form-group">
                                                      <label class="control-label col-sm-2" for="slideDuration">Slide Duration</label>
                                                      <input type="text" class="form-control col-sm-10" id="slideDuration">
                                                  </div>


                                                 <div class="clearfix"></div>
                                                   <button type="submit" ng-click="insertReduceProp(chartViewModel.selectedProcess.parentProcessId,chartViewModel.selectedProcess.processId)" class="btn btn-primary  pull-right">Save</button>


                                              </form>
                                                  <form class="form-horizontal" role="form" ng-if="genConfig.type == 'ReduceByKey'">
                                                   <div class="form-group">
                                                       <label class="control-label col-sm-2" for="operator">Operator</label>
                                                       <select class="form-control" id="operator">
                                                           <option value="ReduceByKey">ReduceByKey</option>
                                                           <option value="ReduceByKeyAndWindow">ReduceByKeyAndWindow</option>
                                                        </select>
                                                   </div>

                                                   <div class="form-group">
                                                          <label class="control-label col-sm-2" for="executorPlugin">Executor Plugin</label>
                                                          <input type="text" class="form-control col-sm-10" id="executorPlugin">
                                                   </div>

                                                    <div class="form-group">
                                                       <label class="control-label col-sm-2" for="uploadJar">Upload Jar</label>
                                                       <input type="file" class="form-control col-sm-10" style="opacity: 100; position: inherit;" id="reducebykeyJar"></input>
                                                   </div>


                                                   <div class="form-group">
                                                            <label class="control-label col-sm-2" for="windowDuration">Window Duration</label>
                                                            <input type="text" class="form-control col-sm-10" id="windowDuration">
                                                    </div>

                                                     <div class="form-group">
                                                        <label class="control-label col-sm-2" for="slideDuration">Slide Duration</label>
                                                        <input type="text" class="form-control col-sm-10" id="slideDuration">
                                                    </div>


                                                   <div class="clearfix"></div>
                                                     <button type="submit" ng-click="insertReduceByKeyProp(chartViewModel.selectedProcess.parentProcessId,chartViewModel.selectedProcess.processId)" class="btn btn-primary  pull-right">Save</button>
                                                </form>


                                <form class="form-horizontal" role="form" id="processFieldsForm1" ng-if="genConfig.type == 'aggregation'">
                                           <h3>Choose column level aggregations</h3>

                                            <div class="" id="formGroup1" >
                                            <div class="col-md-5">
                                              <select class="form-control" id="column" name="column.1">
                                                <option ng-repeat="column in chartViewModel.columnList" id="{{$index}}" value="{{ column.Value }}">{{ column.DisplayText }}</option>
                                            </select>
                                            </div>
                                            <div class="col-md-5">
                                             <select class="form-control" id="aggregation" name="aggregation.1">
                                                <option ng-repeat="aggregation in aggregations" id="{{$index}}" value="{{ aggregation }}">{{ aggregation }}</option>
                                            </select>
                                            </div>

                                            <button id="remove1" class="btn btn-danger remove-me"><span class="glyphicon glyphicon-trash"></span></button>


                                        </div>

                                        <div class="" id="deletediv">

                                                <div class="col-md-5">
                                               <button id="b1" class="btn add-more" onclick="addMore()">
                                                   <span class="glyphicon glyphicon-plus" style="font-size:large"></span>
                                               </button>
                                               </div>
                                               <div class="col-md-6">
                                               <button type="submit" style="margin-right: 0px;" ng-click="insertAggProp(chartViewModel.selectedProcess.processId)" class="btn btn-primary  pull-right">Save</button>
                                               </div>

                           </div>


													</form>


                                <script type="text/javascript">
                                      function columnTypes(){
                                        var columnListArray=angular.element("#aggregation").scope().chartViewModel.columnList;
                                        console.log(columnListArray);
                                        var opt='';
                                        for(var i=0;i<columnListArray.length;i++){
                                        console.log(columnListArray[i].DisplayText);
                                        opt+='<option value="'+columnListArray[i].Value+'">'+columnListArray[i].DisplayText+'</option>';
                                        }
                                          return opt;
                                      }



                                       function aggregationTypes(){
                                         var aggregationListArray=angular.element("#aggregation").scope().aggregations;
                                         console.log(aggregationListArray);
                                         var opt='';
                                         for(var i=0;i<aggregationListArray.length;i++){
                                         console.log(aggregationListArray[i]);
                                         opt+='<option value="'+aggregationListArray[i]+'">'+aggregationListArray[i]+'</option>';
                                         }
                                         return opt;
                                        }



                                      var next = 1;
                                       function addMore()
                                       {
                                       console.log("in add more function");
                                        var addto = "#deletediv";
                                               var addRemove = "#formGroup" + (next);
                                               next = next + 1;
                                               var removeBtn = '<button id="remove' + (next) + '" class="btn btn-danger remove-me" ><span class="glyphicon glyphicon-trash" ></span></button></div><div id="field">';
                                               var newIn = '';
                                               newIn = newIn +  '<div class="" id="formGroup' + next + '">' ;
                                               newIn = newIn +  '<div class="col-md-5">' ;
                                               newIn = newIn +  '<select class="form-control" id="column.' + next + '" name="column.' + next + '">' ;
                                                newIn = newIn +  columnTypes() ;
                                               newIn = newIn +  '</select>' ;
                                               newIn = newIn +  '</div>' ;
                                               newIn = newIn +  '<div class="col-md-5">' ;
                                               newIn = newIn +  '<select class="form-control" id="aggregation.' + next + '" name="aggregation.' + next + '">' ;
                                                newIn = newIn +  aggregationTypes() ;
                                               newIn = newIn +  '</select>' ;
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
                                                       var fieldID = "#formGroup" + fieldNum;
                                                       console.log($(this));
                                                       //$(this).remove();
                                                       $(fieldID).remove();
                                                   });
                                    }





                                </script>



                                                </form>
                                                  <form class="form-horizontal" role="form" ng-if="genConfig.type == 'GroupByKey'">
                                                   <div class="form-group">
                                                       <label class="control-label col-sm-2" for="operator">Operator</label>
                                                       <select class="form-control" id="operator">
                                                           <option value="GroupByKey">GroupByKey</option>
                                                           <option value="GroupByKeyAndWindow">GroupByKeyAndWindow</option>
                                                        </select>
                                                   </div>


                                                   <div class="form-group">
                                                            <label class="control-label col-sm-2" for="windowDuration">Window Duration</label>
                                                            <input type="text" class="form-control col-sm-10" id="windowDuration">
                                                    </div>

                                                     <div class="form-group">
                                                        <label class="control-label col-sm-2" for="slideDuration">Slide Duration</label>
                                                        <input type="text" class="form-control col-sm-10" id="slideDuration">
                                                    </div>


                                                   <div class="clearfix"></div>
                                                     <button type="submit" ng-click="insertGroupByKeyProp(chartViewModel.selectedProcess.processId)" class="btn btn-primary  pull-right">Save</button>
                                                </form>

                              <form class="form-horizontal" id="joinabc" role="form" ng-if="genConfig.type == 'join'" >




                              <h3 style="margin-left: 2%;">Join Data Details</h3>
                              <div>
                              <div class="col-md-3">Join Type</div>
                              <div class="col-md-3">Join Table</div>
                                <div class="col-md-3">Join Column</div>
                                 <div class="col-md-3">Output column</div>
                               </div>








                                            </form>
      <script>

                    function messageColumnNames()
                              {
                              var connectionListArray=$('[ng-controller="AppCtrl"]').scope().messageColumnListChart;
                             console.log(connectionListArray);
                             var opt='';
                             for(var i=0;i<connectionListArray.length;i++){

                             opt+='<option value="'+connectionListArray[i].Value+'">'+connectionListArray[i].DisplayText+'</option>';
                             }
                             return opt;

                              }



                   var nextJoin=0;
                   function addMoreJoin(message)
                            {
                               console.log("in add more function");
                                    nextJoin = nextJoin + 1;
                                    var newIn = '';
                                    newIn = newIn +  '<div class="" id="joinFormGroup' + nextJoin + '">' ;
                                    if(nextJoin==1)
                                    {
                                    newIn = newIn +  '<div class="col-md-3">' ;
                                     newIn = newIn +  '</div>' ;
                                    }
                                    else{

                                     newIn = newIn +  '<div class="col-md-3">' ;
                                     newIn = newIn +  '<select class="form-control" id="join-type.' + nextJoin + '"  name="join-type.' + nextJoin + '">' ;
                                     newIn = newIn +  '<option value="inner">Inner Join</option>';
                                     newIn = newIn +  '<option value="leftouter">Left Outer Join</option>';
                                     newIn = newIn +  '<option value="rightouter">Right Outer Join</option>';
                                     newIn = newIn +  '<option value="fullouter">Full Outer Join</option>';
                                     newIn = newIn +  ' <option value="left">Left Join</option>';
                                     newIn = newIn +  ' <option value="right">Right Join</option>';
                                     newIn = newIn +  ' <option value="outer">Outer Join</option>';
                                     newIn = newIn +  ' <option value="full">Full Join</option>';
                                     newIn = newIn +  ' <option value="leftsemi">Left Semi Join</option>';
                                     newIn = newIn +  '</select>' ;
                                     newIn = newIn +  '</div>' ;
                                    }


                                    newIn = newIn +  '<div class="col-md-3">' ;
                                    newIn = newIn +  '<input  class="form-control" id="joinTable.' + nextJoin + '" value="'+message+'" name="joinTable.' + nextJoin + '">' ;

                                    newIn = newIn +  '</input>' ;
                                    newIn = newIn +  '</div>' ;
                                    newIn = newIn +  '<div class="col-md-3">' ;
                                    newIn = newIn +  '<select class="form-control" id="joinColumn.' + nextJoin + '" name="joinColumn.' + nextJoin + '">' ;
                                     newIn = newIn +  messageColumnNames() ;
                                    newIn = newIn +  '</select>' ;
                                    newIn = newIn +  '</div>' ;

                                    newIn = newIn +  '<div class="col-md-3">' ;
                                    newIn = newIn +  '<select class="form-control" id="joinColumns.' + nextJoin + '" multiple="multiple" name="joinColumns.' + nextJoin + '">' ;
                                     newIn = newIn +  messageColumnNames() ;
                                    newIn = newIn +  '</select>' ;
                                    newIn = newIn +  '</div>' ;
                                    newIn = newIn +  '</div>' ;
                                    var newInput = $(newIn);
									$("#joinabc").append(newInput);
                                    $("#count").val(nextJoin);

                         }


             function addSaveButton()
                   {
                    $("#joinabc").append(' <div class="clearfix"></div><button type="submit" onclick="insertJoinProp()" class="btn btn-primary  pull-right">Save</button>');
                   }





          function insertJoinProp()
          {
          var processId=$('[ng-controller="AppCtrl"]').scope().chartViewModel.selectedProcess.processId;
          console.log("processid is "+processId);
           angular.element(document.getElementById('joinabc')).scope().insertJoinProperties(processId);
          }

</script>



                                            <form class="form-horizontal" role="form" ng-if="genConfig.type == 'emitter'">

                                            <div class="form-group">
                                                    <label for="emitterConnectionName">Connection Name</label>
                                                    <select class="form-control" id="emitterConnectionName">
                                                        <option ng-repeat="connection in emitterConnectionsList" id="{{$index}}" value="{{ connection.Value }}">{{ connection.DisplayText }}</option>
                                                    </select>
                                                     </div>

                                               <div class="clearfix"></div>
                                                    <button type="submit" ng-click="insertEmitterProp(chartViewModel.selectedProcess.processId)" class="btn btn-primary  pull-right">Save</button>
                                                </form>


                                                <form class="form-horizontal" role="form" ng-if="genConfig.type == 'persistentStore'">

                                                    <div class="form-group">
                                                        <label for="persistentStoreConnectionName">Connection Name</label>
                                                        <select class="form-control" id="persistentStoreConnectionName">
                                                            <option ng-repeat="connection in persistentStoreConnectionsList" id="{{$index}}" value="{{ connection.Value }}">{{ connection.DisplayText }}</option>
                                                        </select>
                                                    </div>


                                                        <div class="clearfix"></div>
                                                        <button type="submit" ng-click="insertPersistentStoreProp(chartViewModel.selectedProcess.processId)" class="btn btn-primary  pull-right">Save</button>
                                                    </form>

                      <script>
                      function loadModelProperties(loadMethod) {
                      console.log(loadMethod);
                      var div = document.getElementById('modelRequiredFields');
                                            console.log($('[ng-controller="AppCtrl"]').scope().chartViewModel.columnList);
                                            var columns=$('[ng-controller="AppCtrl"]').scope().chartViewModel.columnList;



                                            if(loadMethod=="serializedModel" || loadMethod=="pmmlFile"){

                                            var formHTML='';

                                            formHTML=formHTML+'<div id="rawTablDetailsDB">';
                                            formHTML=formHTML+'<div class="form-group" style="dispaly:inline-block" >';
                                            formHTML=formHTML+'<label class="control-label col-sm-3" for="regFile">Model File</label>';
                                            //formHTML=formHTML+'<div class="col-sm-10" style="dispaly:inline-block">';
                                            formHTML=formHTML+'<input name = "regFile" id = "regFile" type = "file" class = "form-control" style="opacity: 100; position: inherit;" />';
                                            formHTML=formHTML+'</div>';
                                            formHTML=formHTML+'</div>';
                                            formHTML=formHTML+'<button class = "btn btn-default  btn-success" style="margin-top: 30px;background: lightsteelblue;" type = "button" onClick = "uploadZip(\''+"model"+'\',\''+"regFile"+'\')" href = "#" >Upload File</button >';


                                            formHTML=formHTML+'</div>';

                                            div.innerHTML = formHTML;
                                            }

                                else if(loadMethod=='modelInformation'){
                                    var formHTML='';
                                    var next=1;
                               	    formHTML=formHTML+'<div class="col-md-12" >';
                                  formHTML=formHTML+'<div class="col-md-4">Column </div>';
                                  formHTML=formHTML+'<div class="col-md-4">Coefficient</div>';
                                  formHTML=formHTML+'<div class="col-md-4">Intercept</div>';
                               	formHTML=formHTML+'</div>';
                                 <!--formHTML=formHTML+'<form class="form-horizontal" role="form" id="modelData">';-->

                                  for(var t=0;t<columns.length;t++){
                                  formHTML=formHTML+'<div class="col-md-12" >';
                                  formHTML = formHTML +  '<div class="col-md-4">' ;
                                  formHTML = formHTML +  '<input class="form-control" id="column.' + next + '" value='+ columns[t].Value +' name="column.' + next + '">' ;
                                  formHTML = formHTML +  '</input>' ;
                                  formHTML = formHTML +  '</div>' ;
                                  formHTML = formHTML +  '<div class="col-md-4">' ;
                                  formHTML = formHTML +  '<input class="form-control" id="Coefficient.' + next + '"value='+ 0 +' name="Coefficient.' + next + '">' ;
                                  formHTML = formHTML +  '</input>' ;
                                  formHTML = formHTML +  '</div>' ;

                                  if(t==0){
                                  formHTML = formHTML +  '<div class="col-md-4">' ;
                                  formHTML = formHTML +  '<input class="form-control" id="Intercept.' + next + '"value='+ 0 +' name="Intercept.' + next + '">' ;
                                  formHTML = formHTML +  '</input>' ;
                                  formHTML = formHTML +  '</div>' ;
                                  formHTML=formHTML+'</div>';
                                  }
                                  else
                                  {
                                  formHTML = formHTML +  '<div class="col-md-4">' ;
                                  //formHTML = formHTML +  '<input class="form-control" id="Intercept.' + next + '"value='+ 0 +' name="Intercept.' + next + '">' ;
                                  //formHTML = formHTML +  '</input>' ;
                               	formHTML = formHTML +  '</div>' ;
                                  formHTML=formHTML+'</div>';
                                  }
                                  next++;
                                  }
                                  div.innerHTML = formHTML;

                                            }
                                            else{
                                            var formHTML='';
                                            div.innerHTML = formHTML;
                                            }
                                            }
                      </script>
                       <form class="form-horizontal" role="form" ng-if="genConfig.type == 'linearRegression'">

                            <div class="form-group">
                                                         <label class="control-label col-sm-3" for="modelImportType">Model Import Type</label>
                                                         <select class="form-control" id="modelImportType" onchange="loadModelProperties(this.value);">
                                                         <option value="s">Select the model</option>
                                                             <option value="modelInformation">Model Information</option>
                                                             <option value="pmmlFile">PMML File</option>
                                                             <option value="serializedModel">Serialized Model File</option>
                                                         </select>
                                                     </div>
                                                     <br>
                                                     <br>
                                                     <br>
                                                     <br>
                                                     &nbsp
                                                     &nbsp
                                                     &nbsp
                                                     &nbsp
                                                     &nbsp
                                    <div id="modelRequiredFields"></div>

                          <div class="clearfix"></div>
                    <button type="submit" ng-click="insertRegressionProp(chartViewModel.selectedProcess.processId)" class="btn btn-primary  pull-right">Save</button>
                       </form>


                       <form class="form-horizontal" role="form" ng-if="genConfig.type == 'hive'">

                       <div class="form-group">
                             <label for="persistentStoreConnectionName">Connection Name</label>
                             <select class="form-control" id="hivePersistentStoreConnectionName">
                                 <option ng-repeat="connection in persistentStoreConnectionsList" id="{{$index}}" value="{{ connection.Value }}">{{ connection.DisplayText }}</option>
                             </select>
                         </div>

                          <div class="form-group">
                         <label  for="hiveTableName">Table Name</label>
                         <input class="form-control" id="hiveTableName" >

                         </input>
                         </div>


                   <div class="form-group">
                     <label for="hiveFormat">Format</label>
                     <select class="form-control" id="hiveFormat">
                      <option  value="parquet" selected>parquet</option>
                      <option  value="json">json</option>
                      <option  value="orc">orc</option>
                      <option  value="avro">avro</option>
                     </select>
                 </div>


                               <div class="clearfix"></div>
                                <button type="submit" ng-click="insertHiveProp(chartViewModel.selectedProcess.processId)" class="btn btn-primary  pull-right">Save</button>
                           </form>



                       <form class="form-horizontal" role="form" ng-if="genConfig.type == 'deDuplication'">



                                                       <div class="form-group" >
                                                           <label for="deDuplicationType">Deduplication Type</label>
                                                                <select class="form-control" onchange="changeDuplication()" id="deDuplicationType" name="deDuplicationType">
                                                                 <option  value="WindowDeduplication" selected>WindowDeduplication</option>
                                                                 <option  value="HbaseDeduplication">HbaseDeduplication</option>
                                                                 </select>

                                                       </div>
                                                       <div class="clearfix"></div>


                                                       <div  id="windowDeDuplication">
                                                       <div class="form-group">
                                                         <label class="control-label col-sm-2" for="{{genConfig.key}}-propkey">Column</label>
                                                         <select class="form-control" id="windowDeDuplicationColumn" >
                                                             <option ng-repeat="column in chartViewModel.columnList" id="{{$index}}" value="{{ column.Value }}">{{ column.DisplayText }}</option>
                                                         </select>
                                                     </div>
                                                        <div class="form-group">
                                                        <label class="control-label col-sm-2" for="{{genConfig.key}}-propkey">Timeout Duration(in milliseconds)</label>
                                                        <input class="form-control" id="windowDuration" value="30000">

                                                        </input>
                                                    </div>
                                                      <div class="clearfix"></div>
                                                       </div>


                                                       <div id="hbaseDeDuplication"style="display:none;">
                                                          <div class="form-group">
                                                            <label class="control-label col-sm-2" for="{{genConfig.key}}-propkey">Column</label>
                                                            <select class="form-control" id="hbaseDeDuplicationColumn" >
                                                                <option ng-repeat="column in chartViewModel.columnList" id="{{$index}}" value="{{ column.Value }}">{{ column.DisplayText }}</option>
                                                            </select>
                                                        </div>

                                                        <div class="form-group">
                                                        <label class="control-label col-sm-2" for="{{genConfig.key}}-propkey">Hbase Connection</label>

                                                         <select class="form-control" id="hbaseConnectionName" name="connectionName">
                                                         <option ng-repeat="connection in hbaseConnectionsList" id="{{$index}}" value="{{ connection.Value }}">{{ connection.DisplayText }}</option>
                                                     </select>

                                                    </div>

                                                    <div class="form-group">
                                                    <label class="control-label col-sm-2" for="{{genConfig.key}}-propkey">Table Name</label>
                                                    <input class="form-control" id="hbaseTableName" >

                                                    </input>
                                                </div>

                                                        <div class="clearfix"></div>
                                                        </div>

                                                        <div class="clearfix"></div>

                             <div class="form-group">
                                 <button type="Save" ng-click="saveDuplicationProperties(chartViewModel.selectedProcess.processId)" class="btn btn-primary  pull-right">Save</button>
                             </div>

                                                  </form>

                                                <script>
                                                function changeDuplication()
                                                {
                                                var duplicationType=document.getElementById("deDuplicationType").value;
                                                if(duplicationType=="WindowDeduplication"){
                                                 document.getElementById("windowDeDuplication").style.display='block';
                                                 document.getElementById("hbaseDeDuplication").style.display='none';
                                                }
                                                else
                                                {
                                                document.getElementById("windowDeDuplication").style.display='none';
                                                document.getElementById("hbaseDeDuplication").style.display='block';
                                                }
                                                }
                                                </script>


                             <form class="form-horizontal" role="form" id="processFieldsForm4" ng-if="genConfig.type == 'enricher'">


                            <h3 style="margin-left: 2%;">Enricher Data Details</h3>
                             <div>
                                <div class="col-md-5">Column Name</div>
                                <div class="col-md-6">Broadcast Identifier</div>
                              </div>

                             <div class="" id="enricherFormGroup1" >
                             <div class="col-md-5">
                               <select class="form-control" id="enricherColumn.1" name="enricherColumn.1">
                           <option ng-repeat="column in chartViewModel.columnList" id="{{$index}}" value="{{ column.Value }}">{{ column.DisplayText }}</option>
                             </select>
                             </div>

                             <div class="col-md-6">
                            <select class="form-control" id="enricherBroadcastIdentifier.1" name="enricherBroadcastIdentifier.1">
                              <option ng-repeat="broadcastIdentifier in chartViewModel.broadCastIdentifiers" id="{{$index}}" value="{{ broadcastIdentifier.Value }}">{{ broadcastIdentifier.DisplayText }}</option>
                          </select>
                          </div>

                              <button id="remove4" class="btn btn-danger remove-me-enricher"><span class="glyphicon glyphicon-trash"></span></button>


                             </div>

                             <div class="" id="deletedivEnricher">

                                     <div class="col-md-5">
                                    <button id="b1" class="btn add-more" onclick="addMoreEnricher()">
                                        <span class="glyphicon glyphicon-plus" style="font-size:large"></span>
                                    </button>
                                    </div>
                                    <div class="col-md-7">
                                    <button type="submit" style="margin-right: 0px;" ng-click="insertEnricherProp(chartViewModel.selectedProcess.processId)" class="btn btn-primary  pull-right">Save</button>
                                    </div>

                                    </div>

                             </form>

                               <script>
                         function identifiers()
                        {
                        var connectionListArray=$('[ng-controller="AppCtrl"]').scope().chartViewModel.broadCastIdentifiers;
                       console.log(connectionListArray);
                       var opt='';
                       for(var i=0;i<connectionListArray.length;i++){
                       console.log(connectionListArray[i].DisplayText);
                       opt+='<option value="'+connectionListArray[i].Value+'">'+connectionListArray[i].DisplayText+'</option>';
                       }
                       return opt;

                        }


                                var nextEnricher = 1;
                                  function addMoreEnricher()
                                  {
                                  console.log("in add more function");
                                   var addto = "#deletedivEnricher";
                                          var addRemove = "#enricherFormGroup" + (nextEnricher);
                                          nextEnricher = nextEnricher + 1;
                                          var removeBtn = '<button id="remove' + (nextEnricher) + '" class="btn btn-danger remove-me-enricher" ><span class="glyphicon glyphicon-trash" ></span></button></div><div id="field">';
                                          var newIn = '';
                                          newIn = newIn +  '<div class="" id="enricherFormGroup' + nextEnricher + '">' ;
                                          newIn = newIn +  '<div class="col-md-5">' ;
                                          newIn = newIn +  '<select class="form-control" id="enricherColumn.' + nextEnricher + '" name="enricherColumn.' + nextEnricher + '">' ;
                                           newIn = newIn +  columnNames() ;
                                          newIn = newIn +  '</select>' ;
                                          newIn = newIn +  '</div>' ;

                                        newIn = newIn +  '<div class="col-md-6">' ;
                                        newIn = newIn +  '<select class="form-control" id="enricherBroadcastIdentifier.' + nextEnricher + '" name="enricherBroadcastIdentifier.' + nextEnricher + '">' ;
                                         newIn = newIn +  identifiers() ;
                                        newIn = newIn +  '</select>' ;
                                        newIn = newIn +  '</div>' ;


                                          newIn = newIn + removeBtn;
                                          newIn = newIn +  '</div>' ;

                                          var newInput = $(newIn);
                                          var removeButton = $(removeBtn);
                                          $(addto).before(newInput);

                                          $("#enricherFormGroup" + nextEnricher).attr('data-source',$(addto).attr('data-source'));
                                          $("#count").val(nextEnricher);

                                              $('.remove-me-enricher').click(function(e){
                                                  e.preventDefault();
                                                  var fieldNum = this.id.charAt(this.id.length-1);
                                                  var fieldID = "#enricherFormGroup" + fieldNum;
                                                  console.log($(this));
                                                  //$(this).remove();
                                                  $(fieldID).remove();
                                              });
                               }





                           </script>















                                                <form class="form-horizontal" role="form" ng-if="genConfig.type == 'hql'">

                                                    <div class="form-group form-group-file">
                                                        <label class="control-label col-sm-3" for="{{genConfig.key}}-propkey"><spring:message code="wfdesigner.page.select_hql_file"/></label>
                                                    	<div class="col-sm-10">
                                                            <input type="file" name="file" class="form-control" id="{{genConfig.key}}-propval" required>
                                                        </div>
                                                    </div>
                                                    <div class="form-group">
                                                        <button type="upload" ng-click="uploadFile(chartViewModel.selectedProcess.processId,chartViewModel.selectedProcess.parentProcessId,'hql',genConfig.key)" class="btn btn-primary  pull-right">Upload {{genConfig.key}}</button>
                                                    </div>
                                                    <div class="clearfix"></div>
                                                </form>
                                                 <form class="form-horizontal" role="form" ng-if="genConfig.type == 'r'">
                                                    <div class="form-group form-group-file">
                                                        <label class="control-label col-sm-3" for="{{genConfig.key}}-propkey"><spring:message code="wfdesigner.page.select_r_file"/></label>
                                                    	<div class="col-sm-10">
                                                            <input type="file" name="file" class="form-control" id="{{genConfig.key}}-propval" required>
                                                        </div>
                                                    </div>
                                                    <div class="form-group">
                                                        <button type="upload" ng-click="uploadFile(chartViewModel.selectedProcess.processId,chartViewModel.selectedProcess.parentProcessId,'r',genConfig.key)" class="btn btn-primary  pull-right">Upload {{genConfig.key}}</button>
                                                    </div>
                                                    <div class="clearfix"></div>
                                                </form>
                                                 <form class="form-horizontal" role="form" ng-if="genConfig.type == 'hadoopstream'">
                                                    <div class="form-group form-group-file">
                                                        <label class="control-label col-sm-3" for="{{genConfig.key}}-propkey"><spring:message code="wfdesigner.page.label_select"/>  {{genConfig.key}}:</label>
                                                    	<div class="col-sm-10">
                                                            <input type="file" name="file" class="form-control" id="{{genConfig.key}}-propval" required>
                                                        </div>
                                                    </div>
                                                    <div class="form-group">
                                                        <button type="upload" ng-click="uploadFile(chartViewModel.selectedProcess.processId,chartViewModel.selectedProcess.parentProcessId,'hadoopstream',genConfig.key)" class="btn btn-primary  pull-right">Upload {{genConfig.key}}</button>
                                                    </div>
                                                    <div class="clearfix"></div>
                                                </form>

                                                <form class="form-horizontal" role="form" ng-if="genConfig.type == 'shell'">
                                                    <label class="control-label col-sm-3" for="{{genConfig.key}}-propkey"><spring:message code="wfdesigner.page.select_shell_script"/></label>
                                                    <div class="form-group form-group-file">
                                                        <div class="col-sm-10">
                                                            <input type="file" name="file" class="form-control" id="{{genConfig.key}}-propval" required>
                                                        </div>
                                                    </div>
                                                    <div class="form-group">
                                                        <button type="upload" ng-click="uploadFile(chartViewModel.selectedProcess.processId,chartViewModel.selectedProcess.parentProcessId,'shell',genConfig.key)" class="btn btn-primary  pull-right">Upload {{genConfig.key}}</button>
                                                    </div>
                                                    <div class="clearfix"></div>
                                                </form>
                                                <form class="form-horizontal" role="form" ng-if="genConfig.type == 'addFiles'">
                                                    <div class="form-group form-group-file">
                                                        <label class="control-label col-sm-3" for="{{genConfig.key}}-propkey"><spring:message code="wfdesigner.page.add_files"/></label>
                                                        <div class="col-sm-10">
                                                            <input type="file" name="file" class="form-control" id="{{genConfig.key}}-propval" required>
                                                        </div>
                                                    </div>
                                                    <div class="form-group">
                                                        <button type="upload" ng-click="uploadFile(chartViewModel.selectedProcess.processId,chartViewModel.selectedProcess.parentProcessId,'additional',genConfig.key)" class="btn btn-primary  pull-right">Upload {{genConfig.key}}</button>
                                                    </div>
                                                    <div class="clearfix"></div>
                                                </form>
                                                <form class="form-horizontal" role="form" ng-if="genConfig.type == 'pig'">
                                                    <div class="form-group form-group-file">
                                                        <label class="control-label col-sm-3" for="{{genConfig.key}}-propkey"><spring:message code="wfdesigner.page.select_pig_script"/></label>
                                                    	<div class="col-sm-10">
                                                            <input type="file" name="file" class="form-control" id="{{genConfig.key}}-propval" required>
                                                        </div>
                                                    </div>
                                                    <div class="form-group">
                                                        <button type="upload" ng-click="uploadFile(chartViewModel.selectedProcess.processId,chartViewModel.selectedProcess.parentProcessId,'pig',genConfig.key)" class="btn btn-primary  pull-right">Upload {{genConfig.key}}</button>
                                                    </div>
                                                    <div class="clearfix"></div>
                                                </form>
                                                <form class="form-horizontal" role="form" ng-if="genConfig.type == 'spark'">
                                                    <div class="form-group form-group-file">
                                                        <label class="control-label col-sm-3" for="{{genConfig.key}}-propkey"><spring:message code="wfdesigner.page.select_spark_jar"/></label>
                                                        <div class="col-sm-10">
                                                            <input type="file" name="file" class="form-control" id="{{genConfig.key}}-propval" required>
                                                        </div>
                                                    </div>
                                                    <div class="form-group">
                                                        <button type="upload" ng-click="uploadFile(chartViewModel.selectedProcess.processId,chartViewModel.selectedProcess.parentProcessId,'spark',genConfig.key)" class="btn btn-primary  pull-right">Upload {{genConfig.key}}</button>
                                                    </div>
                                                    <div class="clearfix"></div>
                                                </form>






                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            </div>
                            </div>




                            <div class="col-md-12"">
                                <div class="row">
                                    <div class="panel">
                                        <div class="panel-body">
                                            <div class="col-md-8">
                                                <!-- Split button -->
                                                <div class="btn-group">
                                                    <button type="button" class="btn btn-default">Source</button>
                                                    <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                                        <span class="sr-only"><spring:message code="wfdesigner.page.button_dropdown"/></span>&nbsp;
                                                        <span class="caret"></span>
                                                    </button>
                                                    <ul class="dropdown-menu">
                                                        <li ng-repeat="pType in source_processTypes.Options">
                                                            <a href="#" ng-click="addNewNode(pType.Value, pType.DisplayText)">{{pType.DisplayText}}</a>
                                                        </li>
                                                    </ul>
                                                </div>
                                                <!-- Split button -->
                                                <div class="btn-group">
                                                <button type="button" class="btn btn-default">Transformation</button>
                                                <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                                    <span class="sr-only"><spring:message code="wfdesigner.page.button_dropdown"/></span>&nbsp;
                                                    <span class="caret"></span>
                                                </button>
                                                <ul class="dropdown-menu">
                                                    <li ng-repeat="pType in operator_processTypes.Options">
                                                        <a href="#" ng-click="addNewNode(pType.Value)">{{pType.DisplayText}}</a>
                                                    </li>
                                                </ul>
                                            </div>

                                                <!-- Split button -->
                                                <div class="btn-group">
                                                <button type="button" class="btn btn-default">Analytics</button>
                                                <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                                    <span class="sr-only"><spring:message code="wfdesigner.page.button_dropdown"/></span>&nbsp;
                                                    <span class="caret"></span>
                                                </button>
                                                <ul class="dropdown-menu">
                                                     <li ng-repeat="pType in analytics_processTypes.Options">
                                                         <a href="#" ng-click="addNewNode(pType.Value)">{{pType.DisplayText}}</a>
                                                     </li>
                                                </ul>
                                            </div>


                                            <!-- Split button -->
                                                        <div class="btn-group">
                                                        <button type="button" class="btn btn-default">Emitter</button>
                                                        <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                                            <span class="sr-only"><spring:message code="wfdesigner.page.button_dropdown"/></span>&nbsp;
                                                            <span class="caret"></span>
                                                        </button>
                                                        <ul class="dropdown-menu">
                                                            <li ng-repeat="pType in emitter_processTypes.Options">
                                                                <a href="#" ng-click="addNewNode(pType.Value)">{{pType.DisplayText}}</a>
                                                            </li>
                                                        </ul>
                                                    </div>
                                                <!-- Split button -->

                                                 <!-- Split button -->
                                                    <div class="btn-group">
                                                    <button type="button" class="btn btn-default">Persistent Store</button>
                                                    <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                                        <span class="sr-only"><spring:message code="wfdesigner.page.button_dropdown"/></span>&nbsp;
                                                        <span class="caret"></span>
                                                    </button>
                                                    <ul class="dropdown-menu">
                                                        <li ng-repeat="pType in persistentStore_processTypes.Options">
                                                            <a href="#" ng-click="addNewNode(pType.Value)">{{pType.DisplayText}}</a>
                                                        </li>
                                                    </ul>
                                                </div>


                                            <!-- Split button -->

                                                <div class="btn-group">
                                                    <button type="button" class="btn btn-default"><spring:message code="wfdesigner.page.button_action"/></button>
                                                    <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                                        <span class="sr-only"><spring:message code="wfdesigner.page.button_dropdown"/></span>&nbsp;
                                                        <span class="caret"></span>
                                                    </button>
                                                    <ul class="dropdown-menu">
                                                        <li><a ng-click='confirmDialog(<spring:message code="wfdesigner.page.dropdown_confirm_msg"/>, "deleteSelected")'><spring:message code="wfdesigner.page.dropdown_delete"/></a></li>
                                                        <li><a ng-click="duplicateSelected()"><spring:message code="wfdesigner.page.dropdown_duplicate"/></a></li>
                                                        <li><a onclick="goToPage('process-page2')">Go to Workflow Page</a></li>
                                                        <li><a onclick="goToPage('wfdesigner-page2')"><spring:message code="wfdesigner.page.dropdown_wfdesigner"/></a></li>
                                                    </ul>
                                                </div>

                                            </div>
                                            <div class="col-md-4">

                                                <button type="button" class="close" onclick="$('#alertBox').hide()"><span aria-hidden="true">&times;</span></button>
                                                <div id="alertBox">

                                                </div>

                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="panel panel-default grid fixed-height">
                                        <div class="panel-body">
                                            <div class="clearfix">
                                                <div class="pull-left">
                                                    <button class="btn btn-default btn-lg glyphicon glyphicon-align-justify level2" aria-hidden="true" href="#" ng-click="arrangePositions()"></button>
                                                </div>
                                                <div class="pull-right">
                                                    <button class="btn btn-default" aria-hidden="true" href="#" ng-click="alreadyPresentBroadcastData();">Broadcast</button>
                                                    <button class="btn btn-default btn-lg glyphicon glyphicon-zoom-in level2" aria-hidden="true" href="#" onclick="$('#canvas').css('zoom',$('#canvas').css('zoom')*1.1 )"></button>
                                                    <button class="btn btn-default btn-lg glyphicon glyphicon-zoom-out level2" aria-hidden="true" href="#" onclick="$('#canvas').css('zoom',$('#canvas').css('zoom')/1.1 )"></button>
                                                </div>
                                            </div>
                                            <flow-chart id="canvas" style="margin: 5px; width: 3500px; height: 3500px; position: relative; left: 0px; top: 0px" chart="chartViewModel"></flow-chart>
                                        </div>
                                    </div>
                                </div>

                            </div>

                        </div>

                         <div id="broadcast" class="modelwindow">

                         <!-- Modal content -->
                                 <div class="modal-content">
                                   <span id="closeBroadcast" class="closemodal" onclick="closeBroadcast()">&times;</span>
                           <div id="broadcastDetails">
                           <form class="form-horizontal" role="form" >
                           <div class="form-group" >
                               <label for="BroadcastSource">Broadcast Source</label>
                                    <select class="form-control" id="BroadcastSource" name="BroadcastSource">
                                     <option  value="hbase" selected>HBase</option>
                                    <!-- <option  value="file">File</option> -->
                                     </select>

                           </div>


                            <div class="clearfix"></div>
                            </form>



                    <form class="form-horizontal" role="form" id="processFieldsForm2" style="margin-left: 2%;">
                               <h3 style="margin-left: 2%;">Broadcast Data Details</h3>
                                <div>
                                <div class="col-md-2">Con Name</div>
                                 <div class="col-md-2">Table Name</div>
                                  <div class="col-md-2">Column Family</div>
                                   <div class="col-md-2">Column Name</div>
                                   <div class="col-md-4">Broadcast Identifier</div>
                                 </div>

                                <div class="" id="broadCastFormGroup1" >
                                <div class="col-md-2">
                                  <select class="form-control" id="connectionName.1" name="connectionName.1">
                                    <option ng-repeat="connection in hbaseConnectionsList" id="{{$index}}" value="{{ connection.Value }}">{{ connection.DisplayText }}</option>
                                </select>
                                </div>
                                <div class="col-md-2 ">
                                 <input class="form-control" id="tableName.1" name="tableName.1" placeholder="Table Name">
                                </input>
                                </div>
                                <div class="col-md-2">
                               <input class="form-control" id="columnFamily.1" name="columnFamily.1" placeholder="Column Family">
                                </input>
                                </div>
                              <div class="col-md-2">
                               <input class="form-control" id="columnName.1" name="columnName.1" placeholder="Column Name">
                              </input>
                              </div>
                               <div class="col-md-3">
                                 <input class="form-control" id="broadcastIdentifier.1" name="broadcastIdentifier.1" onclick="changeBroadcast(this);" placeholder="Broadcast Identifier">
                                </input>
                                </div>

                                <button id="remove1" class="btn btn-danger remove-me-broadcast"><span class="glyphicon glyphicon-trash"></span></button>


                            </div>

                            <div class="" id="deletedivBroadcast">

                                    <div class="col-md-5">
                                   <button id="b1" class="btn add-more" onclick="addMoreBroadcast()">
                                       <span class="glyphicon glyphicon-plus" style="font-size:large"></span>
                                   </button>
                                   </div>
                                   <div class="col-md-7">
                                   <button type="submit" style="margin-right: 0px;" ng-click="insertBroadcastProp()" class="btn btn-primary  pull-right">Save</button>
                                   </div>

                                   </div>


                                        </form>

                              <div class="clearfix"></div>

                           <div>
                         <div class="clearfix"></div>
                         </div>
                         </div>
                         <script>

                         function changeBroadcast(item) {
                             var id= $(item).attr("id");
                             var res = id.split(".");
                             var num=res[res.length-1];
                             console.log("id is "+id+"res is "+num);
                             var connectionName=document.getElementById("connectionName."+num).value;
                              var tableName=document.getElementById("tableName."+num).value;
                              var columnFamily=document.getElementById("columnFamily."+num).value;
                              var columnName=document.getElementById("columnName."+num).value;
                              console.log(connectionName+" "+tableName+" "+columnFamily+" "+columnName);
                              document.getElementById(id).value=tableName+"_"+columnFamily+"_"+columnName;
                            }

                      function connectionNames(){

                       var connectionListArray=$('[ng-controller="AppCtrl"]').scope().hbaseConnectionsList;
                       console.log(connectionListArray);
                       var opt='';
                       for(var i=0;i<connectionListArray.length;i++){
                       console.log(connectionListArray[i].DisplayText);
                       opt+='<option value="'+connectionListArray[i].Value+'">'+connectionListArray[i].DisplayText+'</option>';
                       }
                       return opt;
                   }


                          var nextBroadcast = 1;
                            function addMoreBroadcast()
                            {
                            console.log("in add more function");
                             var addto = "#deletedivBroadcast";
                                    var addRemove = "#broadCastFormGroup" + (nextBroadcast);
                                    nextBroadcast = nextBroadcast + 1;
                                    var removeBtn = '<button id="remove' + (nextBroadcast) + '" class="btn btn-danger remove-me-broadcast" ><span class="glyphicon glyphicon-trash" ></span></button></div><div id="field">';
                                    var newIn = '';
                                    newIn = newIn +  '<div class="" id="broadCastFormGroup' + nextBroadcast + '">' ;
                                    newIn = newIn +  '<div class="col-md-2">' ;
                                    newIn = newIn +  '<select class="form-control" id="connectionName.' + nextBroadcast + '" name="connectionName.' + nextBroadcast + '">' ;
                                     newIn = newIn +  connectionNames() ;
                                    newIn = newIn +  '</select>' ;
                                    newIn = newIn +  '</div>' ;
                                    newIn = newIn +  '<div class="col-md-2">' ;
                                    newIn = newIn +  '<input class="form-control" id="tableName.' + nextBroadcast + '"placeholder="Table Name" name="tableName.' + nextBroadcast + '">' ;
                                    newIn = newIn +  '</input>' ;
                                    newIn = newIn +  '</div>' ;
                                    newIn = newIn +  '<div class="col-md-2">' ;
                                    newIn = newIn +  '<input class="form-control" id="columnFamily.' + nextBroadcast + '"placeholder="Column Family" name="columnFamily.' + nextBroadcast + '">' ;
                                    newIn = newIn +  '</input>' ;
                                    newIn = newIn +  '</div>' ;

                                    newIn = newIn +  '<div class="col-md-2">' ;
                                    newIn = newIn +  '<input class="form-control" id="columnName.' + nextBroadcast + '"placeholder="Column Name" name="columnName.' + nextBroadcast + '">' ;
                                    newIn = newIn +  '</input>' ;
                                    newIn = newIn +  '</div>' ;

                                    newIn = newIn +  '<div class="col-md-3">' ;
                                    newIn = newIn +  '<input class="form-control" id="broadcastIdentifier.' + nextBroadcast + '"placeholder="Broadcast Identifier" onclick="changeBroadcast(this);" name="broadcastIdentifier.' + nextBroadcast + '">' ;
                                    newIn = newIn +  '</input>' ;
                                    newIn = newIn +  '</div>' ;

                                    newIn = newIn + removeBtn;
                                    newIn = newIn +  '</div>' ;

                                    var newInput = $(newIn);
                                    var removeButton = $(removeBtn);
                                    $(addto).before(newInput);

                                    $("#broadCastFormGroup" + nextBroadcast).attr('data-source',$(addto).attr('data-source'));
                                    $("#count").val(nextBroadcast);

                                        $('.remove-me-broadcast').click(function(e){
                                            e.preventDefault();
                                            var fieldNum = this.id.charAt(this.id.length-1);
                                            var fieldID = "#broadCastFormGroup" + fieldNum;
                                            console.log($(this));
                                            //$(this).remove();
                                            $(fieldID).remove();
                                        });
                         }





                     </script>




                        <div id="modalCover"></div>
                        <script type="text/javascript" src="../js/fc/wf-utilities.js"></script>
                    </body>
                </c:when>
                <c:otherwise>

                    <body ng-app="app" data-ng-init="intialiseNewProcessPage()" ng-controller="AppCtrl">
                        <div class="row">&nbsp;</div>
                        <div class="row">
                            <div class="col-md-2"> </div>
                            <div class="col-md-8 ">
                                <div class="panel panel-primary">
									<%-- <div class="panel-heading"><spring:message code="wfdesigner.page.create_new_workflow"/></div> --%>
									<div class="panel-body" style="padding: 0;">
                                        <form role="form">
                                            <div class="form-group">
                                                <label for="processName">Workflow Name</label>
                                                <input type="text" class="form-control" id="processname" required>
                                            </div>
                                            <div class="form-group">
                                                <label for="description"><spring:message code="wfdesigner.page.description"/></label>
                                                <input type="text" class="form-control" id="description" required>
                                            </div>
                                            <div class="form-group">
                                            <label for="application"><spring:message code="wfdesigner.page.domain"/></label>
                                            <select class="form-control" id="domain">
                                                <option ng-repeat="busdomain in newPageBusDomain" id="{{$index}}" value="{{ busdomain.Value }}">{{ busdomain.DisplayText }}</option>
                                            </select>
                                        </div>
                                            <div class="actions text-center pull-right">
                                            <button type="submit" class="btn btn-primary" ng-click="createFirstProcess()">Create Workflow</button>
                                            </div>
                                        </form>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-2"> </div>
                    </body>
                </c:otherwise>
            </c:choose>

            </html>