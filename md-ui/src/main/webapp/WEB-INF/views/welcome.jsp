<html id="ng-app">
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
        <h1>Bigdata Ready Enterprise</h1>
        <p>Our product attempts to make big data technology simpler by optimizing and integrating various big data solutions and providing them under one integrated package, Big Data Ready Enterprises, or BDRE. BDRE is a Bigdata/Hadoop unified framework developed with the goal of drastically minimizing development time.</p>
      </div>

	    <div class="row ">
		<span ng-repeat="value in text track by $index" ng-class="{row:($index + 1) % 3 == 0}">
		    <canvas id="myCanvas{{$index}}" class="col-md-2" >
			Your browser does not support the HTML5 canvas tag.</canvas>
		    <div class="col-md-2 "><p class="lead">{{head[$index]}}</p><p>{{desc[$index]}} <a href="{{taillinks[$index]}}">{{tail[$index]}}</a></p>
		    </div>
		</span>
	    </div>
	</div>
	<script>
	    //**********************************************Just update these arrays with appropriate data*********************
	    var text = ["Automation1", "Automation2", "Automation3", "Automation4", "Automation1", "", "", "", "", "", "", ""];
	    var head = ["Metadata Management", "Data Integrity", "Batch Lineage", "Data Quality", "Automation", "Data Extraction", "Run Control", "Test Data Generation", "Dependency Management", "Data Loading", "Visualization", "Analytics"];
	    var desc = ["End-to-end process and governance framework.",
		"End-to-end data integrity protection by error checking and validation at necessary steps.",
		"Detailed end-to-end batch lineage information.",
		"Makes data reliable for making business decisions.",
		"Enables business streamlining.Drastically reduces errors and prevents jobs from falling through the cracks.",
		"Extraction of data to retrieve relevant information from data sources.",
		"Monitoring and controlling the process execution.",
		"Automated bulk test data generation.",
		"Process and workflow dependencies for auditing.",
		"Fast dataset loading.",
		"Graphical representaion of workflows and dependencies.",
		"Analysis of process run execution time."];
	    var tail = ["", "", "", "", ""];
	    var taillinks = ["url1", "url2", "url3", "url4", "url1", "url2", "url3", "url4"]
	    var imgs = ["../css/images/metadata.jpg", "../css/images/dataintegrity.jpg", "../css/images/datalineage.jpg", "../css/images/dataquality.jpg", "../css/images/automation.jpg", "../css/images/extraction.jpg", "../css/images/runcontrol.jpg", "../css/images/datagen.jpg", "../css/images/dependancy.jpg", "../css/images/loading.jpg", "../css/images/visualization.jpg", "../css/images/analytic.jpg"];

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