<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="security"
	   uri="http://www.springframework.org/security/tags" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<!--<link rel="stylesheet" href="../css/jquery.steps.css" />
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
        <script src="../js/jquery.min.js" type="text/javascript"></script>
        <script src="../js/jquery-ui-1.10.3.custom.js" type="text/javascript"></script>
        <script src="../js/jquery.steps.min.js" type="text/javascript"></script>
        <script src="../js/jquery.jtable.js" type="text/javascript"></script>
        <script src="../js/bootstrap.js" type="text/javascript"></script>
        <script src="../js/angular.min.js" type="text/javascript"></script>
        <script src="http://ui-grid.info/release/ui-grid.js"></script>
        <link rel="stylesheet" href="https://ui-grid.info/release/ui-grid.css" type="text/css">

         <link href="../css/select2.min.css" rel="stylesheet" />
                        <script src="../js/select2.min.js"></script>-->

        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap.min.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.12.4/css/bootstrap-select.min.css">
        <link rel="stylesheet" href="https://ui-grid.info/release/ui-grid.css" type="text/css">

        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/js/bootstrap.min.js"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.12.4/js/bootstrap-select.min.js"></script>
       <!--  <script src="../js/angular.min.js" type="text/javascript"></script> -->
        <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.6.10/angular.min.js"></script>
        <script src="https://ui-grid.info/release/ui-grid.js"></script>
        <script src="https://cdn.plot.ly/plotly-1.31.2.min.js"></script>

        <script src="../js/mlresults/app.js" type="text/javascript"></script>


        <style type="text/css">
            .myGrid {
            width: 1000px;
            height: 300px;
            }

            .form-control{
                width: 85%
            }

        </style>

        <script type="text/javascript">
            function goToViewModelPage(){
                location.href = '<c:url value="/pages/ViewModel.page"/>';
            }
        </script>


    </head>

    <body ng-app="uigridApp" ng-controller="uigridCtrl">
    <div class="container">
        <button class = "btn btn-default  btn-success" style="margin-top: 30px;background: lightsteelblue;" type = "button" onClick = goToViewModelPage("ModelInformation")  >Back</button >
        <br/><br/>
        <div>
            <div ui-grid="gridOptions" ui-grid-pagination ui-grid-exporter class="myGrid"></div>
        </div>
        <br/><br/>

        <div class="row"><div class="col-sm-12 col-md-12"></div></div>
        <div class="row">
            <div class="col-sm-3 col-md-3">
                <span>X Axis</span>
                <select id="xAxis" class="selectpicker">

                </select>
            </div>
            <div class="col-sm-3 col-md-3">
                <span>Y Axis</span>
                <select id="yAxis" class="selectpicker"></select>
            </div>
            <div class="col-sm-3 col-md-3">
                <span>Graph Type</span>
                <select id="plotType" class="selectpicker">
                    <option>Scatter</option>

                </select>

            </div>
            <div class="col-sm-3 col-md-3">
                <span>&nbsp;</span>
                <button type="button" class="btn btn-primary btn-md" ng-click="plotGraph()">Plot Graph</button>
            </div>
        </div>
        <br/>
        <div class="row" ng-show="mapLogisticPredictionFlag" ng-cloak>

            <div class="col-sm-3 col-md-3" ng-repeat="classifier in classificationElements">
                <span>{{classifier}}</span>
                <input type="text" class="form-control" ng-model="predictionName[$index]"/>

                </select>
            </div>
        </div>
        <div id="modelGraph" style="width:700px;height:250px;"></div>
    </div>


    </body>
