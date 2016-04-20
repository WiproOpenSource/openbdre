<%@ taglib prefix="security"
	   uri="http://www.springframework.org/security/tags" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
        <%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	 pageEncoding="ISO-8859-1"%>
            <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

            <html>

            <head>
                <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
                <title>Bigdata Ready Enterprise</title>
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

                <link href="../css/css/bootstrap.min.css" rel="stylesheet" type="text/css" />
                <link href="../css/jtables-bdre.css" rel="stylesheet" type="text/css" />
                <link rel="stylesheet" href="../css/data-ingestion.css" />
                <link rel="stylesheet" href="../css/data-ingestion-forms.css" />
                <link href="../css/jquery-ui-1.10.3.custom.css" rel="stylesheet" type="text/css" />

                <!-- Include jTable script file. -->
                <script src="../js/jquery.min.js" type="text/javascript"></script>
                <script src="../js/bootstrap.js" type="text/javascript"></script>
                <script src="../js/jquery-ui-1.10.3.custom.js" type="text/javascript"></script>

                <link rel="stylesheet" type="text/css" href="../js/fc/wf.css">

                <!-- Library code. -->

                <script src="../js/angular.min.js" type="text/javascript"></script>

                <!-- Flowchart code. -->
                <script src="../js/svgutil.js" type="text/javascript"></script>
                <script src="../js/fc/app.js" type="text/javascript"></script>
                <script src="../js/fc/debug.js" type="text/javascript"></script>
                <script src="../js/fc/svg_class.js" type="text/javascript"></script>
                <script src="../js/fc/mouse_capture_service.js" type="text/javascript"></script>
                <script src="../js/fc/dragging_service.js" type="text/javascript"></script>
                <script src="../js/fc/flowchart_viewmodel.js" type="text/javascript"></script>
                <script src="../js/fc/flowchart_directive.js" type="text/javascript"></script>


                <!--Ajax calls Code. -->
                <script type="text/javascript" src="../js/fc/wfd-ac.js"></script>
                <script type="text/javascript" src="../js/fc/cachejs.js"></script>

                <!--Utilities -->
                <script src="../js/nanobar.min.js" type="text/javascript"></script>

                <style>
                    /* make sidebar nav vertical */
                    
                    .sidebar-nav {
                        min-height: 700px;
                    }
                    
                    .alert {
                        height: 34px !important;
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
                </style>

            </head>
            <c:choose>
                <c:when test="${not empty param.processId}">

                    <body ng-app="app" data-ng-init="init(${param.processId})" ng-controller="AppCtrl" mouse-capture ng-keydown="keyDown($event)" ng-keyup="keyUp($event)">
                        <div id="nanobardiv"></div>
                        <div class="row">
                            <div class="col-md-3">&nbsp;</div>
                        </div>
                        <div class="row">
                            <div class="col-md-3 sidebar-nav">
                                <div class="panel panel-default" ng-if="chartViewModel.selectedProcess.processName == null" class="animate-if">
                                    <div class="panel-heading" data-toggle="collapse" data-parent="#accordion">Information</div>
                                    <div class="panel-body">Click on the node to see node properties</div>
                                </div>
                                <div class="panel-group" id="accordion" ng-if="chartViewModel.selectedProcess.processName != null" class="animate-if">
                                    <div class="panel panel-default">
                                        <div class="panel-heading foldablearrow" data-toggle="collapse" data-parent="#accordion" data-target="#processdetails">
                                            Process Details
                                        </div>
                                        <div id="processdetails" class="panel-collapse collapse in">
                                            <div class="panel-body">
                                                <form role="form" class="form-horizontal">
                                                    <div class="form-group">
                                                        <label class="control-label col-md-4">Id: </label>
                                                        <div class="col-md-8">
                                                            <h5><span class="label label-info">{{ chartViewModel.selectedProcess.processId }}</span></h5>
                                                        </div>
                                                    </div>

                                                    <div class="form-group">
                                                        <label class="control-label col-md-4">Type: </label>
                                                        <div class="col-md-8">
                                                            <h5><span class="label label-default">{{ chartViewModel.selectedProcess.processTypeId }}</span></h5>
                                                        </div>
                                                    </div>
                                                    <div class="form-group">
                                                        <label class="control-label col-md-4" for="process.name">Name: </label>
                                                        <div class="col-md-8">
                                                            <input name="processName" id="process.name" class="form-control input-sm" type="text" ng-model="chartViewModel.selectedProcess.processName">
                                                        </div>
                                                    </div>
                                                    <div class="form-group">
                                                        <label class="control-label col-md-4" for="process.description">Description: </label>
                                                        <div class="col-md-8">
                                                            <input name="description" id="process.description" class="form-control input-sm" type="text" ng-model="chartViewModel.selectedProcess.description">
                                                        </div>
                                                    </div>
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
                                                    <button type="button" class="btn btn-sm btn-primary pull-right" ng-click="updateProcessDetails()">Update Process Details</button>
                                                </form>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="panel panel-default" ng-if="chartViewModel.selectedProcess.parentProcessId == null">
                                        <div class="panel-heading foldablearrow" data-toggle="collapse" data-parent="#accordion" data-target="#jarupload" ng-click="getJarList()">
                                            Jar Configuration
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
                                                    <div class="form-group">
                                                        <label class="control-label col-sm-3">Select file :</label>
                                                    </div>
                                                    <div class="form-group">
                                                        <div class="col-sm-10">
                                                            <input type="file" name="file" class="form-control" id="jar-id" required>
                                                        </div>
                                                    </div>
                                                    <button type="button" class="btn btn-sm btn-primary pull-right" ng-click="uploadJar(chartViewModel.selectedProcess.processId,'lib','jar-id')">Upload jar</button>
                                                </form>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="panel panel-default" ng-repeat="genConfig in chartViewModel.selectedProcessGenConfigProp">
                                        <div class="panel-heading foldablearrow" data-toggle="collapse" data-parent="#accordion" data-target="#-{{genConfig.key}}" ng-click="chartViewModel.getKeyValueFunction(genConfig)">
                                            {{genConfig.value}}
                                        </div>
                                        <div id="-{{genConfig.key}}" class="panel-collapse collapse">

                                            <div id="propertySearchBox">
                                                <input type="text" class="form-control input-sm" placeholder="Search Property" ng-model="searchText">
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
                                                <form class="form-horizontal" role="form" ng-if="genConfig.type != 'hql'  && genConfig.type != 'hadoopstream' && genConfig.type != 'r'  && genConfig.type != 'spark' && genConfig.type != 'pig' && genConfig.type != 'shell' && genConfig.type != 'addFiles'">
                                                    <div class="form-group">
                                                        <label class="control-label col-sm-2" for="{{genConfig.key}}-propkey">Name:</label>
                                                        <div class="col-sm-10">
                                                            <input type="{{genConfig.key}}" class="form-control" id="{{genConfig.key}}-propkey" placeholder="Enter name" required>
                                                        </div>
                                                    </div>
                                                    <div class="form-group">
                                                        <label class="control-label col-sm-2" for="{{genConfig.key}}-propkey">Value:</label>
                                                        <div class="col-sm-10">
                                                            <input type="text" class="form-control" id="{{genConfig.key}}-propval" placeholder="Enter value" required>
                                                        </div>
                                                    </div>
                                                    <button type="submit" ng-click="insertProp(genConfig)" class="btn btn-primary  pull-right">Add {{genConfig.value}}</button>
                                                </form>
                                                <form class="form-horizontal" role="form" ng-if="genConfig.type == 'hql'">
                                                    <div class="form-group">
                                                        <label class="control-label col-sm-3" for="{{genConfig.key}}-propkey">Select HQL file :</label>
                                                    </div>
                                                    <div class="form-group">
                                                        <div class="col-sm-10">
                                                            <input type="file" name="file" class="form-control" id="{{genConfig.key}}-propval" required>
                                                        </div>
                                                    </div>
                                                    <div class="form-group">
                                                        <button type="upload" ng-click="uploadFile(chartViewModel.selectedProcess.processId,chartViewModel.selectedProcess.parentProcessId,'hql',genConfig.key)" class="btn btn-primary  pull-right">Upload {{genConfig.key}}</button>
                                                    </div>
                                                </form>
                                                 <form class="form-horizontal" role="form" ng-if="genConfig.type == 'r'">
                                                    <div class="form-group">
                                                        <label class="control-label col-sm-3" for="{{genConfig.key}}-propkey">Select R file :</label>
                                                    </div>
                                                    <div class="form-group">
                                                        <div class="col-sm-10">
                                                            <input type="file" name="file" class="form-control" id="{{genConfig.key}}-propval" required>
                                                        </div>
                                                    </div>
                                                    <div class="form-group">
                                                        <button type="upload" ng-click="uploadFile(chartViewModel.selectedProcess.processId,chartViewModel.selectedProcess.parentProcessId,'r',genConfig.key)" class="btn btn-primary  pull-right">Upload {{genConfig.key}}</button>
                                                    </div>
                                                </form>
                                                 <form class="form-horizontal" role="form" ng-if="genConfig.type == 'hadoopstream'">
                                                    <div class="form-group">
                                                        <label class="control-label col-sm-3" for="{{genConfig.key}}-propkey">Select  {{genConfig.key}}:</label>
                                                    </div>
                                                    <div class="form-group">
                                                        <div class="col-sm-10">
                                                            <input type="file" name="file" class="form-control" id="{{genConfig.key}}-propval" required>
                                                        </div>
                                                    </div>
                                                    <div class="form-group">
                                                        <button type="upload" ng-click="uploadFile(chartViewModel.selectedProcess.processId,chartViewModel.selectedProcess.parentProcessId,'hadoopstream',genConfig.key)" class="btn btn-primary  pull-right">Upload {{genConfig.key}}</button>
                                                    </div>
                                                </form>

                                                <form class="form-horizontal" role="form" ng-if="genConfig.type == 'shell'">
                                                    <div class="form-group">
                                                        <label class="control-label col-sm-3" for="{{genConfig.key}}-propkey">Select shell script :</label>
                                                    </div>
                                                    <div class="form-group">
                                                        <div class="col-sm-10">
                                                            <input type="file" name="file" class="form-control" id="{{genConfig.key}}-propval" required>
                                                        </div>
                                                    </div>
                                                    <div class="form-group">
                                                        <button type="upload" ng-click="uploadFile(chartViewModel.selectedProcess.processId,chartViewModel.selectedProcess.parentProcessId,'shell',genConfig.key)" class="btn btn-primary  pull-right">Upload {{genConfig.key}}</button>
                                                    </div>
                                                </form>
                                                <form class="form-horizontal" role="form" ng-if="genConfig.type == 'addFiles'">
                                                    <div class="form-group">
                                                        <label class="control-label col-sm-3" for="{{genConfig.key}}-propkey">Add files:</label>
                                                    </div>
                                                    <div class="form-group">
                                                        <div class="col-sm-10">
                                                            <input type="file" name="file" class="form-control" id="{{genConfig.key}}-propval" required>
                                                        </div>
                                                    </div>
                                                    <div class="form-group">
                                                        <button type="upload" ng-click="uploadFile(chartViewModel.selectedProcess.processId,chartViewModel.selectedProcess.parentProcessId,'additional',genConfig.key)" class="btn btn-primary  pull-right">Upload {{genConfig.key}}</button>
                                                    </div>
                                                </form>
                                                <form class="form-horizontal" role="form" ng-if="genConfig.type == 'pig'">
                                                    <div class="form-group">
                                                        <label class="control-label col-sm-3" for="{{genConfig.key}}-propkey">Select pig script :</label>
                                                    </div>
                                                    <div class="form-group">
                                                        <div class="col-sm-10">
                                                            <input type="file" name="file" class="form-control" id="{{genConfig.key}}-propval" required>
                                                        </div>
                                                    </div>
                                                    <div class="form-group">
                                                        <button type="upload" ng-click="uploadFile(chartViewModel.selectedProcess.processId,chartViewModel.selectedProcess.parentProcessId,'pig',genConfig.key)" class="btn btn-primary  pull-right">Upload {{genConfig.key}}</button>
                                                    </div>
                                                </form>
                                                <form class="form-horizontal" role="form" ng-if="genConfig.type == 'spark'">
                                                    <div class="form-group">
                                                        <label class="control-label col-sm-3" for="{{genConfig.key}}-propkey">Select spark jar:</label>
                                                    </div>
                                                    <div class="form-group">
                                                        <div class="col-sm-10">
                                                            <input type="file" name="file" class="form-control" id="{{genConfig.key}}-propval" required>
                                                        </div>
                                                    </div>
                                                    <div class="form-group">
                                                        <button type="upload" ng-click="uploadFile(chartViewModel.selectedProcess.processId,chartViewModel.selectedProcess.parentProcessId,'spark',genConfig.key)" class="btn btn-primary  pull-right">Upload {{genConfig.key}}</button>
                                                    </div>
                                                </form>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-9">
                                <div class="row">
                                    <div class="panel panel-default">
                                        <div class="panel-body">
                                            <div class="col-md-4">
                                                <!-- Split button -->
                                                <div class="btn-group">
                                                    <button type="button" class="btn btn-default">Add Node</button>
                                                    <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                                        <span class="sr-only">Toggle Dropdown</span>&nbsp;
                                                        <span class="caret"></span>
                                                    </button>
                                                    <ul class="dropdown-menu">
                                                        <li ng-repeat="pType in processTypes.Options">
                                                            <a href="#" ng-click="addNewNode(pType.Value)">{{pType.DisplayText}}</a>
                                                        </li>
                                                    </ul>
                                                </div>
                                                <!-- Split button -->
                                                <div class="btn-group">
                                                    <button type="button" class="btn btn-default">Actions</button>
                                                    <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                                        <span class="sr-only">Toggle Dropdown</span>&nbsp;
                                                        <span class="caret"></span>
                                                    </button>
                                                    <ul class="dropdown-menu">
                                                        <li><a ng-click="confirmDialog('Do you really want to delete the selected element?', 'deleteSelected')">Delete Selected</a></li>
                                                        <li><a ng-click="duplicateSelected()">Duplicate Selected</a></li>
                                                        <li><a onclick="goToPage('process-page')">Go To Process Page</a></li>
                                                        <li><a onclick="goToPage('wfdesigner-page')">Create New Workflow</a></li>
                                                    </ul>
                                                </div>

                                            </div>
                                            <div class="col-md-8">

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
                                    <div class="panel-body">
                                        <form role="form">
                                            <div class="form-group">
                                                <label for="processName">Process Name</label>
                                                <input type="text" class="form-control" id="processname" required>
                                            </div>
                                            <div class="form-group">
                                                <label for="description">Description</label>
                                                <input type="text" class="form-control" id="description" required>
                                            </div>
                                            <div class="form-group">
                                                <label for="application">Domain</label>
                                                <select class="form-control" id="domain">
                                                    <option ng-repeat="busdomain in newPageBusDomain" id="{{$index}}" value="{{ busdomain.Value }}">{{ busdomain.DisplayText }}</option>
                                                </select>
                                            </div>
                                            <div class="form-group">
                                                <label for="type">Type</label>
                                                <select class="form-control" id="type">
                                                    <option ng-repeat="type in newPageProcessType" id="{{$index}}" value="{{ type.Value }}">{{ type.DisplayText }}</option>
                                                </select>
                                            </div>
                                            <div class="form-group">
                                                <label for="workflowtype">Workflow Type</label>
                                                <select class="form-control" id="workflowtype">
                                                    <option ng-repeat="workflowtype in newPageWorkflowType" id="{{$index}}" value="{{ workflowtype.Value }}">{{ workflowtype.DisplayText }}</option>
                                                </select>
                                            </div>
                                            <div class="actions text-center pull-right">
                                            <button type="submit" class="btn btn-primary" ng-click="createFirstProcess()">Create Process</button>
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