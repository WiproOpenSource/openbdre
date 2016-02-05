<%@ taglib prefix="security"
       uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
     pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>

<head>
    <title>BDRE | Bigdata Ready Enterprise</title>

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

    <script src="../js/angular.min.js" type="text/javascript"></script>
    <script type="text/javascript">

    var map = new Object();
    var createJobResult;
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
<script >
        function fetchPipelineInfo(pid){
			location.href = '<c:url value="/pages/lineage.page?pid="/>' + pid;
        }
</script >
</head>
<body ng-app="myApp" ng-controller="myCtrl">



    <div id="bdre-crawler" >
      





            <h3>Crawler Details</h3>
            <section>
            <form class="form-horizontal" role="form" id="processFieldsForm2">
                <div id="crawlerDetails">
                    <div class="alert alert-info" role="alert">
                        Application requires crawling details to be entered
                    </div>
                    <!-- btn-group -->
                    <div id="crawlerFields">
                        <div class="form-group">
                            <label class="control-label col-sm-3" for="url">Urls to crawl:</label>
                            <div class="col-sm-9">
                                <input type="text" class="form-control" name="url" placeholder="Enter Urls to crawl (comma seperated)" value="{{ crawlerMap['url'].defaultVal }}">
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label col-sm-3" for="urlsToSearch">Regex Pattern to search:</label>
                            <div class="col-sm-9">
                                <input type="text" class="form-control" name="urlsToSearch" placeholder="Enter Regex Pattern to search" value="{{ crawlerMap['urlsToSearch'].defaultVal }}">
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label col-sm-3" for="urlsNotToSearch">Regex Pattern not to search:</label>
                            <div class="col-sm-9">
                                <input type="text" class="form-control" name="urlsNotToSearch" placeholder="Enter Regex Pattern not to search" value="{{ crawlerMap['urlsNotToSearch'].defaultVal }}">
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label col-sm-3" for="politenessDelay">Politeness Delay:</label>
                            <div class="col-sm-9">
                                <input type="number" class="form-control" id="politenessDelay" value="{{ crawlerMap['politenessDelay'].defaultVal }}" name="politenessDelay" placeholder="Enter Politeness Delay" >
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label col-sm-3" for="maxDepthOfCrawling">Max depth of crawling:</label>
                            <div class="col-sm-9">
                                <input type="number" class="form-control" id="maxDepthOfCrawling" placeholder="Enter max depth of crawling" value="{{ crawlerMap['maxDepthOfCrawling'].defaultVal }}" name="maxDepthOfCrawling">
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label col-sm-3" for="maxPagesToFetch">Max pages to fetch:</label>
                            <div class="col-sm-9">
                                <input type="number" class="form-control" id="maxPagesToFetch" placeholder="Enter max pages to fetch" value="{{ crawlerMap['maxPagesToFetch'].defaultVal }}" name="maxPagesToFetch">
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label col-sm-3" for="includeBinaryContent">Include binary content:</label>
                            <div class="col-sm-9">
                                <input type="text" class="form-control" id="includeBinaryContent" name="includeBinaryContent" value="{{crawlerMap['includeBinaryContentInCrawling'].defaultVal}}" >
                            </div>
                        </div>
                       
                        <div class="form-group">
                            <label class="control-label col-sm-3" for="resumableCrawling">Resumable crawling:</label>
                            <div class="col-sm-9">
                                <input type="text" class="form-control" id="resumableCrawling" name="resumableCrawling" placeholder="Crawling should be resumable?" value="{{ crawlerMap['resumableCrawling'].defaultVal }}">
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label col-sm-3" for="userAgentString">User agent string:</label>
                            <div class="col-sm-9">
                                <input type="text" class="form-control" name="userAgentString" placeholder="Enter user agent string" value="{{ crawlerMap['userAgentString'].defaultVal }}">
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label col-sm-3" for="noOfMappers">Number of mappers:</label>
                            <div class="col-sm-9">
                                <input type="number" class="form-control" name="numMappers" placeholder="Enter number of mappers" value="{{ crawlerMap['numberOfMappers'].defaultVal }}">
                            </div>
                        </div>

                    </div>
                    <!-- /btn-group -->
                </div>
                </form>
                </section>
            




            <h3>Proxy Details</h3>
            <section>
            <form class="form-horizontal" role="form" id="processFieldsForm3">
                <div id="proxyDetails">
                    <div class="alert alert-info" role="alert">
                        Application requires proxy details (if you are using proxy to access internet)
                    </div>
                    <!-- btn-group -->
                    <div id="proxyFields">
                        <div class="form-group">
                            <label class="control-label col-sm-2" for="proxyPort">Proxy-port:</label>
                            <div class="col-sm-10">
                                <input type="number" class="form-control" name="proxyPort" placeholder="Enter proxy-port">
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label col-sm-2" for="proxyHost">Proxy-host:</label>
                            <div class="col-sm-10">
                                <input type="text" class="form-control" name="proxyHost" placeholder="Enter proxy-host e.g.:proxy.domain.com">
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label col-sm-2" for="proxyUsername">Proxy-username:</label>
                            <div class="col-sm-10">
                                <input type="text" class="form-control" name="proxyUsername" placeholder="Enter proxy-username">
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label col-sm-2" for="proxyPassword">Proxy-password:</label>
                            <div class="col-sm-10">
                                <input type="password" class="form-control" name="proxyPassword" placeholder="Enter proxy-password">
                            </div>
                        </div>
                    </div>
                    <!-- /btn-group -->
                </div>
                </form>
                </section>
                <h3>Process Details</h3>
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
                                        <input type="text" class="form-control"  id="processName" name="processName" placeholder="Enter Process Name" required>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label class="control-label col-sm-2" for="processDescription">Process Description:</label>
                                    <div class="col-sm-10">
                                        <input type="text" class="form-control" id="processDescription" name="processDescription" placeholder="Enter Process Description" required>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label class="control-label col-sm-2" for="outputPath">HDFS Output Path:</label>
                                    <div class="col-sm-10">
                                        <input type="text" class="form-control" id="outputPath" name="outputPath" placeholder="Enter the absolute Output Path" required>
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
                <h3>Confirm</h3>
                <section>
                <div id="createProcess">
                    <button ng-click="createJob()" id="createjobs" type="button" class="btn btn-primary">Create Crawler</button>
                </div>
                <div id="Process"></div>
                </section>
                
    </div>

                     
    <script>
        var app = angular.module('myApp', []);
        app.controller('myCtrl', function($scope) {
            $scope.crawlerMap = getGenConfigMap('crawler');
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

            $scope.createJob =function (){
                
                $.ajax({
                                        
                                        type: "POST",
                                        url: "/mdrest/crawler",
                                        data: $('#processFieldsForm1,#processFieldsForm2,#processFieldsForm3').serialize(),
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
                                                }).text("Jobs successfully created.");
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
                                                }).html(data.Message);
                                            }
                                            console.log(createJobResult);
                                        }

                });
            }
        });
    </script>
    <script type="text/javascript">
    var wizard = null;
    
        $("#bdre-crawler").steps({
            headerTag: "h3",
            bodyTag: "section",
            transitionEffect: "slideLeft",
            stepsOrientation: "vertical",
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
            				}).text("Please Enter Process Name and Description");
            				return false;
            			}
            			return true;
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
            location.href = '<c:url value="/pages/crawler.page"/>';
            }

        });
    
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
<div id="div-dialog-warning"/>
</body>

</html>

