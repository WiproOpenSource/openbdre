<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<html id="ng-app">
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

	<meta name="viewport" content="width=device-width, initial-scale=1">
	<link rel="stylesheet" href="../css/css/bootstrap.min.css" />
	<script src="../js/angular.min.js"></script>
	<style>
	    body{
		overflow:scroll;

	    }
	    .desc{
		padding-top: 2px;
	    }
	    .lead{
		margin-bottom: 1px;
	    }
	</style>
    </head>
    <body onload="circle(0)">


	<div class="container-fluid" ng-app="myApp" ng-controller="myCtrl">
	<div class="jumbotron  alert-info">
        <h1><spring:message code="common.page.title_bdre_1"/></h1>
        <p><spring:message code="welcome.page.bdre_description"/></p>
      </div>
	    <div class="row ">
		<span ng-repeat="value in text track by $index" ng-class="{row:($index + 1) % 3 == 0}">
		    <canvas id="myCanvas{{$index}}" class="col-md-2" >
			<spring:message code="welcome.page.canvas_error"/></canvas>
		    <div class="col-md-2 "><p class="lead">{{head[$index]}}</p><p>{{desc[$index]}} <a href="{{taillinks[$index]}}">{{tail[$index]}}</a></p>
		    </div>
		</span>
	    </div>
	</div>
	<script>
	    //**********************************************Just update these arrays with appropriate data*********************
	    var text = [<spring:message code="welcome.page.text_var"/>];
	    var head = [<spring:message code="welcome.page.head_var"/>]
	    var desc = [<spring:message code="welcome.page.desc_var"/>];
	    var tail = [<spring:message code="welcome.page.tail_var"/>];
	    var taillinks = [<spring:message code="welcome.page.taillinks_var"/>];
	    var imgs = [<spring:message code="welcome.page.imgs_var"/>];

	    //*************************************
	    var app = angular.module("myApp", []);
	    app.controller("myCtrl", function ($scope) {
		$scope.text = text;
		$scope.desc = desc;
		$scope.head = head;
		$scope.tail = tail;
		$scope.taillinks = taillinks;
		$scope.imgs = imgs;
	    });

	    function circle(i) {
		if (i == imgs.length)
		    return;
		var c = document.getElementsByTagName("canvas");
		c = document.getElementById("myCanvas" + i);
		var ctx = c.getContext("2d");
		ctx.beginPath();
		var temp = ctx.arc(80, 80, 60, 0, 2 * Math.PI);
		ctx.lineWidth = 4;
		ctx.strokeStyle = '#ffffff';
		ctx.shadowColor = 'gray';    //change color of shadow here
		ctx.shadowBlur = 8;
		ctx.stroke();
		ctx.shadowColor = null;
		ctx.shadowBlur = null;
		ctx.font = "20px Helvetica Neue";		//adjust font-size
		var img = new Image();
		img.setAttribute("src", imgs[i]);
		img.onload = function () {
		    var pat = ctx.createPattern(img, 'no-repeat');
		    ctx.fillStyle = pat;
		    ctx.fill();
		    circle(i + 1);
		};
	    }
	</script>
    </body>
</html>