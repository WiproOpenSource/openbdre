<%@ taglib prefix="security"
	   uri="http://www.springframework.org/security/tags" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
    <%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
        <%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	 pageEncoding="ISO-8859-1"%>
            <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">



<html id="ng-app">
    <head>
	<title><spring:message code="common.page.title_bdre_2"/></title>
	<link href="../css/css/bootstrap.min.css" rel="stylesheet" type="text/css" />
	<link href="../css/bootstrap.custom.css" rel="stylesheet" type="text/css" />
    <script src="../js/angular.min.js" type="text/javascript"></script>
    <script src="../js/jquery.min.js" type="text/javascript"></script>
    <script src="../js/bootstrap.js" type="text/javascript"></script>
  	<script>

$(document).ready(function(){
	$( "a[class^='bdretab']" ).click(function() {
    this.href="#"+this.className.slice(7);
    var link="#"+this.className.slice(7);
    $('.nav-tabs a[href=link]').tab('show')
});
});

</script>
	<style>
body {
	padding: 10px;
	margin-left: 5%;
	margin-right:5%;
	overflow: scroll;
}
.alert-info {
	color: #31708f;
	background-color: #fff;
	border: 1px solid #B0B0B0 !important;
}

.appimage {
	padding-top: 2%;
	padding-bottom: 2%;
}


</style>
    </head>
<body>

<script>
	angular
			.module('myApp', [])
			.controller(
					'myCtrl',
					function($scope, $window, $http) {
						$.ajax({
							url : "../../store/store.json?rand="
									+ Math.random(),
							dataType : "json",
							async : false,
							success : function(response) {
								console.log(response);
								$scope.rows = response.applicationList;
							}
						});

						$scope.addClass = function(check, className) {
							var cssClass = check ? className : null;
							return cssClass;
						};
						$scope.createApp = function(location) {

							$('#confirm')
									.modal({
										backdrop : 'static',
										keyboard : false
									})
									.one(
											'click',
											'#yes',
											function(e) {
												$('#installing').modal({
													backdrop : 'static',
													keyboard : false
												})

												$
														.ajax({
															url : "/mdrest/process/import",
															type : "POST",
															data : {
																'fileString' : location
															},
															success : function(
																	getData) {
																$('#installing')
																		.modal(
																				'hide');
																if (getData.Result == "OK") {
																	$(
																			'#div-dialog-warning')
																			.modal(
																					{
																						backdrop : 'static',
																						keyboard : false
																					})
																			.one(
																					'click',
																					'#ok',
																					function(
																							e) {
																						$window.location.href = '<c:url value="/pages/process.page?pid="/>'
																								+ getData.Records.processList[0].processId;
																						return false;
																					});
																}
																if (getData.Result == "ERROR") {
																	$(
																			'#div-dialog-error')
																			.modal(
																					{
																						backdrop : 'static',
																						keyboard : false
																					})
																			.one(
																					'click',
																					'#ok',
																					function(
																							e) {
																						return false;
																					});
																}
															}
														});
											});
						};

					});
</script>
<div ng-app="myApp" ng-controller="myCtrl">
<div class="page-header"><spring:message code="appstore.page.app_store"/></div>
<ul class="nav nav-tabs" ng-if="rows">
  <li ng-repeat="row in rows" ng-class="addClass($first,'active')"><a data-toggle="tab" class='bdretab{{ row.id }}'>{{ row.name }}</a></li>
</ul>

<div class="tab-content" ng-if="rows">
  <div id="{{ row.id }}" ng-repeat="row in rows" class="tab-pane fade" ng-class="addClass($first,'active in')">
    <div class="row" >
    	<div class="col-md-2 appimage" ng-repeat="column in row.columns">
		<div class="alert alert-info thumbnail">
			<div class="text-center"><strong>{{column.name}}</strong></div>
			<img src="../../store/{{ column.icon }}"   ng-click="createApp(column.location)" alt="App image" width="150" height="118">
			<button class="btn btn-info ng-binding center-block" ng-click="createApp(column.location)"><spring:message code="appstore.page.install"/></button>
		</div>
	</div>
  </div>
</div>

<!-- Modal -->
  <div class="modal fade" id="confirm" role="dialog">
    <div class="modal-dialog modal-sm">
      <div class="modal-content">
        <div class="modal-header">
          <h4 class="modal-title"><spring:message code="appstore.page.install"/></h4>
        </div>
        <div class="modal-body">
          <p><spring:message code="appstore.page.p_confirmation"/></p>
        </div>
        <div class="modal-footer">
			<button type="button" data-dismiss="modal" class="btn btn-primary" id="yes"><spring:message code="appstore.page.yes"/></button>
    		<button type="button" data-dismiss="modal" class="btn"><spring:message code="appstore.page.no"/></button>
        </div>
      </div>
    </div>
  </div>
    <div class="modal fade" id="installing" role="dialog">
      <div class="modal-dialog modal-sm">
        <div class="modal-content">
          <div class="modal-header">
            <h4 class="modal-title"><spring:message code="appstore.page.install"/></h4>
          </div>
          <div class="modal-body">
            <p><spring:message code="appstore.page.install_progress"/></p>
          </div>
        </div>
      </div>
    </div>
  <div class="modal fade" id="div-dialog-warning" role="dialog">
      <div class="modal-dialog modal-sm">
        <div class="modal-content">
          <div class="modal-header">
            <h4 class="modal-title"><spring:message code="appstore.page.install"/></h4>
          </div>
          <div class="modal-body">
            <p><spring:message code="appstore.page.install_complete"/></p>
          </div>
          <div class="modal-footer">
  			<button type="button" data-dismiss="modal" class="btn btn-primary" id="ok"><spring:message code="appstore.page.ok"/></button>
          </div>
        </div>
      </div>
    </div>
       <div class="modal fade" id="div-dialog-error" role="dialog">
          <div class="modal-dialog modal-sm">
            <div class="modal-content">
              <div class="modal-header">
                <h4 class="modal-title"><spring:message code="appstore.page.install"/></h4>
              </div>
              <div class="modal-body">
                <p><spring:message code="appstore.page.installtall_error"/></p>
              </div>
              <div class="modal-footer">
      			<button type="button" data-dismiss="modal" class="btn btn-primary" id="ok"><spring:message code="appstore.page.ok"/></button>
              </div>
            </div>
          </div>
       </div>
</div>
</body>
</html>
