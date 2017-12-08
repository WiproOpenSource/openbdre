<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="security"
	   uri="http://www.springframework.org/security/tags" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link rel="stylesheet" href="../css/jquery.steps.css" />
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
        <link rel="stylesheet" href="http://ui-grid.info/release/ui-grid.css" type="text/css">

         <link href="../css/select2.min.css" rel="stylesheet" />
                        <script src="../js/select2.min.js"></script>

        <style type="text/css">
            .myGrid {
            width: 1350px;
            height: 500px;
            }
    </style>

    </head>
    <script>
    function goToViewModelPage(){
    location.href = '<c:url value="/pages/ViewModel.page"/>';
    }
    </script>

    <script type="text/javascript">
    var app = angular.module("uigridApp", ["ui.grid"]);
    app.controller("uigridCtrl", function ($scope) {
    console.log(window.location.href);
    $scope.str = window.location.href.split("=")[1];
    $scope.users={};
    $scope.srenv="localhost:10000";
    $.ajax({
      url: "/mdrest/ml/data/" + $scope.srenv + '/' + "default" + '/' + $scope.str,
          type: 'GET',
          dataType: 'json',
          async: false,
          success: function (data) {
              console.log(data);
             $scope.users= data.Records;

          },
          error: function () {
              alert('danger');
          }
      });
    $scope.gridOptions = {
    enableFiltering: true,
    onRegisterApi: function (gridApi) {
    $scope.grid1Api = gridApi;
    }
    };
    $scope.gridOptions.data = $scope.users;
    });
    </script>

    <body ng-app="uigridApp">
    <button class = "btn btn-default  btn-success" style="margin-top: 30px;background: lightsteelblue;" type = "button" onClick = goToViewModelPage("ModelInformation")  >Back</button >
    <div ng-controller="uigridCtrl">
    <div ui-grid="gridOptions" class="myGrid"></div>
    </div>
    </body>