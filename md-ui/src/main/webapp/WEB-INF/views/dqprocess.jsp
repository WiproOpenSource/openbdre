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
                <!-- Include one of jTable styles. -->

                <link href="../css/css/bootstrap.min.css" rel="stylesheet" type="text/css" />
                <link href="../css/jquery-ui-1.10.3.custom.css" rel="stylesheet" type="text/css" />

                <!-- Include jTable script file. -->
                <script src="../js/jquery.min.js" type="text/javascript"></script>
                <script src="../js/jquery-ui-1.10.3.custom.js" type="text/javascript"></script>
                <script src="../js/bootstrap.js" type="text/javascript"></script>

                <script src="../js/angular.min.js" type="text/javascript"></script>

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
                </style>

            </head>

            <body ng-app="myApp" ng-controller="myCtrlr" ng-init="init()">
                <div class="row">&nbsp;</div>
                <div class="row">
                    <div class="col-md-3"> </div>
                    <div class="col-md-6 ">
                        <div class="panel panel-primary">
                            <div class="panel-heading">Setup DQ Job</div>
                            <div class="panel-body">
                                <form role="form">
                                    <div class="form-group">
                                        <label for="rulesUserNameValue">Rules Username</label>
                                        <input type="text" class="form-control" id="rulesUserNameValue" required>
                                    </div>
                                    <div class="form-group">
                                        <label for="rulesPasswordValue">Rules Password</label>
                                        <input type="text" class="form-control" id="rulesPasswordValue" required>
                                    </div>
                                    <div class="form-group">
                                        <label for="rulesPackageValue">Rules Packages</label>
                                        <input type="text" class="form-control" id="rulesPackageValue">
                                    </div>
                                    <div class="form-group">
                                        <label for="fileDelimiterRegexValue">File Delimiter</label>
                                        <input type="text" class="form-control" id="fileDelimiterRegexValue">
                                    </div>
                                    <div class="form-group">
                                        <label for="minPassThresholdPercentValue">Min pass threshold %</label>
                                        <input type="number" class="form-control" id="minPassThresholdPercentValue">
                                    </div>
                                    <div class="form-group">
                                        <label for="busDomainId">Application</label>
                                        <select class="form-control" id="busDomainId">
                                            <option ng-repeat="busdomainId in busDomainIds" id="{{$index}}" value="{{ busdomainId.Value }}">{{ busdomainId.DisplayText }}</option>
                                        </select>
                                        <div class="form-group">
                                            <label for="canRecover">Can Recover</label>
                                            <input type="text" class="form-control" id="canRecover">
                                        </div>
                                        <div class="form-group">
                                            <label for="enqId">Enq Id</label>
                                            <input type="text" class="form-control" id="enqId">
                                        </div>
                                        <div class="form-group">
                                            <label for="processName">Process Name</label>
                                            <input type="text" class="form-control" id="processName">
                                        </div>
                                        <div class="form-group">
                                            <label for="description">Description</label>
                                            <input type="text" class="form-control" id="description">
                                        </div>
                                        <button type="submit" class="btn btn-primary" onclick="addRecord()">Add Record</button>
                                </form>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-3"> </div>
                        <script type="text/javascript">
                            var myApp = angular.module('myApp', []);
                            myApp.controller('myCtrlr', function ($scope) {
                                $scope.busDomainIds = {};
                                $scope.init = function () {
                                    $.ajax({
                                        url: '/mdrest/busdomain/options/',
                                        type: 'POST',
                                        dataType: 'json',
                                        async: false,
                                        success: function (data) {
                                            if (data.Result == "OK") {
                                                $scope.busDomainIds = data.Options;
                                            } else {
                                                console.log("Result was not OK while retrieving busDomainIds")
                                            }
                                        },
                                        error: function () {
                                            console.log("Error in retrieving busDomainIds");
                                        }
                                    });
                                }
                            });
                            confirmDialog = function (message) {
                                $('<div></div>').appendTo('body')
                                    .attr('title', 'Message')
                                    .html('<div><h6>' + message + '</h6></div>')
                                    .dialog({
                                        buttons: {
                                            Ok: function () {
                                                window.location.replace('/mdui/pages/process.page');
                                                $(this).dialog('close');
                                            }
                                        },
                                        close: function () {
                                            window.location.replace('/mdui/pages/process.page');
                                            $(this).dialog('close');
                                        }
                                    });
                            }
                            var addRecord = function () {
                                var postData = $.param({
                                    rulesUserNameValue: $("#rulesUserNameValue").val(),
                                    rulesPasswordValue: $("#rulesPasswordValue").val(),
                                    rulesPackageValue: $("#rulesPackageValue").val(),
                                    fileDelimiterRegexValue: $("#fileDelimiterRegexValue").val(),
                                    minPassThresholdPercentValue: $("#minPassThresholdPercentValue").val(),
                                    busDomainId: $("#busDomainId").val(),
                                    canRecover: $("#canRecover").val(),
                                    enqId: $("#enqId").val(),
                                    processName: $("#processName").val(),
                                    description: $("#description").val()
                                });
                                console.log(postData);
                                $.ajax({
                                    url: '/mdrest/dqsetup/',
                                    type: 'PUT',
                                    data: postData,
                                    dataType: 'json',
                                    success: function (data) {
                                        if (data.Result == 'OK') {
                                            confirmDialog('New DQ Job Added');
                                        }
                                    },
                                    error: function () {
                                        console.log("Error occured in adding the record");
                                    }
                                })
                            }
                        </script>
            </body>

            </html>