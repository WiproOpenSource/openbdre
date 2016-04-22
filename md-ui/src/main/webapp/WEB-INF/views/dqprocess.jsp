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

                <link href="../css/css/bootstrap.min.css" rel="stylesheet" type="text/css" />
                <link href="../css/jquery-ui-1.10.3.custom.css" rel="stylesheet" type="text/css" />
                <link rel="stylesheet" href="../css/jquery.steps.custom.css" />
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
            <div class="page-heading"><spring:message code="dqprocess.page.panel_heading"/></div>
                <div class="row">&nbsp;</div>
                <div class="row basic-forms">
                    <div class="col-md-2"> </div>
                    <div class="col-md-8">
                        <div class="panel panel-primary">
							<div class="panel-body">
                                <form role="form">
                                    <div class="form-group">
                                        <label for="rulesUserNameValue"><spring:message code="dqprocess.page.form_rules_username"/></label>
                                        <input type="text" class="form-control" id="rulesUserNameValue" required>
                                    </div>
                                    <div class="form-group">
                                        <label for="rulesPasswordValue"><spring:message code="dqprocess.page.form_rules_psswd"/></label>
                                        <input type="password" class="form-control" id="rulesPasswordValue" required>
                                    </div>
                                    <div class="form-group">
                                        <label for="rulesPackageValue"><spring:message code="dqprocess.page.form_rules_pckgs"/></label>
                                        <input type="text" class="form-control" id="rulesPackageValue">
                                    </div>
                                    <div class="form-group">
                                        <label for="fileDelimiterRegexValue"><spring:message code="dqprocess.page.form_file_delimiter"/></label>
                                        <input type="text" class="form-control" id="fileDelimiterRegexValue">
                                    </div>
                                    <div class="form-group">
                                        <label for="minPassThresholdPercentValue"><spring:message code="dqprocess.page.form_threshold_min_val"/></label>
                                        <input type="number" class="form-control" id="minPassThresholdPercentValue">
                                    </div>
                                    <div class="form-group">
                                        <label for="busDomainId"><spring:message code="dqprocess.page.form_bus_domainID"/></label>
                                        <select class="form-control" id="busDomainId">
                                            <option ng-repeat="busdomainId in busDomainIds" id="{{$index}}" value="{{ busdomainId.Value }}">{{ busdomainId.DisplayText }}</option>
                                        </select>
                                     </div>
                                     <div class="form-group">
                                            <label for="canRecover"><spring:message code="dqprocess.page.form_recoverability"/></label>
                                            <input type="text" class="form-control" id="canRecover">
                                        </div>
                                        <div class="form-group">
                                            <label for="enqId"><spring:message code="dqprocess.page.form_enq_id"/></label>
                                            <input type="text" class="form-control" id="enqId">
                                        </div>
                                        <div class="form-group">
                                            <label for="processName"><spring:message code="dqprocess.page.form_process_name"/></label>
                                            <input type="text" class="form-control" id="processName">
                                        </div>
                                        <div class="form-group">
                                            <label for="description"><spring:message code="dqprocess.page.form_desc"/></label>
                                            <input type="text" class="form-control" id="description">
                                        </div>
                                     <div class="actions text-center pull-right">
                                     <button type="submit" class="btn btn-primary" onclick="addRecord()">Add Record</button>
                                     </div>
                                </form>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-2"> </div>
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