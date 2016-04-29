<!--
  ~ Copyright (c) 2014 Wipro Limited
  ~ All Rights Reserved
  ~
  ~ This code is protected by copyright and distributed under
  ~ licenses restricting copying, distribution and decompilation.
  -->
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="security"
	   uri="http://www.springframework.org/security/tags" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>

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
       		<script src="../../mdui/js/jquery.min.js"></script>
       		<link href="../../mdui/css/jquery-ui-1.10.3.custom.css" rel="stylesheet">
       		<link href="../../mdui/css/css/bootstrap.min.css" rel="stylesheet" />
       		<script src="../../mdui/js/jquery-ui-1.10.3.custom.js"></script>
       		<script src="../../mdui/js/jquery.steps.min.js"></script>
       		<link rel="stylesheet" href="../../mdui/css/jquery.steps.css" />
       		<link rel="stylesheet" href="../../mdui/css/jquery.steps.custom.css" />
       		<link href="../../mdui/css/bootstrap.custom.css" rel="stylesheet" />
       		<script src="../../mdui/js/bootstrap.js" type="text/javascript"></script>
       		<script src="../../mdui/js/jquery.jtable.js" type="text/javascript"></script>
       		<script src="../../mdui/js/angular.min.js" type="text/javascript"></script>
       		<link href="../../mdui/css/jtables-bdre.css" rel="stylesheet" type="text/css" />

          <script>

          var industry;
          var category;
          var app;
          var durl;
          var json;

            var wizard = null;
            wizard = $(document).ready(function() {

            	$("#analytics-app").steps({
            		headerTag: "h3",
            		bodyTag: "section",
            		transitionEffect: "slideLeft",
            		stepsOrientation: "vertical",
            		enableCancelButton: true,

            		onStepChanging: function(event, currentIndex, newIndex) {

            		    return true;
                    },

                    onStepChanged: function(event, currentIndex, priorIndex) {
                      console.log(currentIndex + " " + priorIndex);
                    if(currentIndex == 1 && priorIndex == 0) {
                     $("#category").empty();
                       industry = $('#industry').val();
                       console.log("industry is "+industry);

                                  var categories = [];
                                   $.ajax({
                                        url: '/mdrest/analyticsapp/category/'+industry,
                                            type: 'GET',
                                            dataType: 'json',
                                            async: false,
                                            success: function (data) {
                                            categories=data;
                                            },
                                            error: function () {
                                                alert(' danger');
                                            }

                                        });

                                        var str;
                                            var x = document.getElementById("category");
                                        for( str in categories["Records"]){
                                            var option = document.createElement("option");
                                            option.text = categories["Records"][str].categoryName;
                                            option.value = categories["Records"][str].categoryName;
                                            x.appendChild(option);
                                        }

                         }
                         if(currentIndex == 2 && priorIndex == 1) {
                                    category = $('#category').val();
                                    console.log("category is "+category);
                                     $("#app").empty();
                                   var apps = [];
                                         $.ajax({
                                              url: '/mdrest/analyticsapp/apps/'+industry+'/'+category,
                                                  type: 'GET',
                                                  dataType: 'json',
                                                  async: false,
                                                  success: function (data) {
                                                  apps=data;
                                                  },
                                                  error: function () {
                                                      alert('danger');
                                                  }

                                              });

                                              var str;
                                                  var x = document.getElementById("app");
                                              for( str in apps["Records"]){
                                                  var option = document.createElement("option");
                                                  option.text = apps["Records"][str].appName;
                                                  option.value = apps["Records"][str].appName;
                                                  x.appendChild(option);
                                              }
                          }
                           if(currentIndex == 3 && priorIndex == 2) {
                                      app = $('#app').val();
                                      console.log("app is "+app);


                          }
                      }
          	});
            });

        </script>



          <script>

                         var app = angular.module('myApp', []);
                          app.controller('myCtrl', function($scope) {

                              $scope.industries = {};
                              $.ajax({
                              url: '/mdrest/analyticsapp/industries/',
                                  type: 'GET',
                                  dataType: 'json',
                                  async: false,
                                  success: function (data) {
                                      $scope.industries = data;
                                  },
                                  error: function () {
                                      alert('danger');
                                  }
                              });
                          });
                  </script>

                  <script>
                    function gotoDashboardurl(){
                     durl = [];
                           $.ajax({
                                url: '/mdrest/analyticsapp/dashboardurl/'+industry+'/'+category+'/'+app,
                                    type: 'GET',
                                    dataType: 'json',
                                    async: false,
                                    success: function (data) {
                                    durl=data;
                                    },
                                    error: function () {
                                        alert('danger');
                                    }

                                });

                        window.open(durl["Records"][0].dashboardUrl);
                    }

                    function gotoDdpurl(){
                        json = [];
                           $.ajax({
                                url: '/mdrest/analyticsapp/json/'+industry+'/'+category+'/'+app,
                                    type: 'GET',
                                    dataType: 'json',
                                    async: false,
                                    success: function (data) {
                                    json=data;
                                    },
                                    error: function () {
                                        alert('danger');
                                    }

                                });
                          console.log(json["Records"][0].questionsJson);

                      }

                  </script>



    </head>


    <body ng-app="myApp">
         <div class="page-header">Analytics App</div>

         <div id="analytics-app" class="wizard-vertical"  >
                 <h3><div class="number-circular">1</div>Industry</h3>

                  <section>
                   <form class="form-horizontal" role="form" id="industryForm">
                       <div id="industryDetails" ng-controller="myCtrl">

                                         <div class="form-group">
                                                 <label class="control-label col-sm-2" for="industry">Select Industry:</label>
                                                 <div class="col-sm-10">
                                                     <select class="form-control" id="industry" name="industry">
                                                         <option ng-repeat="industry in industries.Records" value="{{industry.industryName}}" name="industry">{{industry.industryName}}</option>

                                                     </select>
                                                 </div>
                                         </div>

                       </div>

                   </form>
                 </section>

                 <h3><div class="number-circular">2</div>Category</h3>

                        <section>

                            <form class="form-horizontal" role="form" id="categoryForm">
                                <div id="categoryDiv">
                                   <div class="form-group">
                                      <label class="control-label col-sm-2" for="category">Select Category:</label>
                                        <div class="col-sm-10">
                                          <select class="form-control" id="category" name="category" >
                                          </select>
                                        </div>
                                    </div>

                            </form>
                        </section>

                  <h3><div class="number-circular">3</div>App</h3>

                         <section>

                             <form class="form-horizontal" role="form" id="appForm">
                                 <div id="appDiv">
                                    <div class="form-group">
                                       <label class="control-label col-sm-2" for="app">Select App:</label>
                                         <div class="col-sm-10">
                                           <select class="form-control" id="app" name="app" >
                                           </select>
                                         </div>
                                     </div>

                             </form>
                         </section>

                   <h3><div class="number-circular">4</div>Dashboard-ddp</h3>
                        <section>
                         <div id=dashboard>
                             <button class="control-label col-sm-2" onclick="gotoDashboardurl()">Dashboard</button>

                         </div>

                          <div id=dashboard>
                                  <button class="control-label col-sm-2" onclick="gotoDdpurl()">DDP URL</button>

                              </div>
                        </section>


    </body>

</html>

