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
	<link rel="stylesheet" type="text/css" href="//cdn.jsdelivr.net/jquery.slick/1.5.9/slick.css"/>
	<link rel="stylesheet" type="text/css" href="../css/slick-theme.css"/>
	<script type="text/javascript" src="../js/jquery.min.js"></script>
	<script src="../js/angular.min.js"></script>
	<script type="text/javascript" src="../js/slick.js"></script>
				
	<style>
	    body{
		overflow:scroll;
		}
	  	.lead{
		margin-bottom: 1px;
	    }
	    .jumbotron{
	    background-image: url("../css/images/bdre_hp.jpg");
	    width: 100%;background-size: cover;overflow: hidden;
	    color: #ecebea;
	    border-radius: 0 !important;
	    margin-bottom: 0 !important;
	    padding-bottom: 110px !important;
	    }
	    .jumbotron p{
	     font-size: 18px;
	    }
	    .nopadding{
	     padding: 0 !important;
   		 margin: 0 !important;
	    }
	    .slick-slide{
	    height: 235px !important;
	    }
	    .slick-dots button{
	    display: none;
	    }
	    
	    .carousel-container{
	    	margin: 0 auto;
	    	float: none;
	    	padding: 0 !important;
	    	position: relative;
    		bottom: 45px;
	    }
	    .carousel-items{
	    background-color: #F15248;
	    text-align:center;
	    border-radius: 6px;
	    /* border: 2px solid black !important; */
	    margin-left: 10px !important;
	    }
	   	.carousel-icons{
	    margin:28px auto 14px;
	    }
	    .lead,.desc{color: #fff;}
	    .desc{
	    letter-spacing: 1px;
	    width:90%;
	    margin:0 auto;
	    padding-top: 2px;
	    }
	    .slick-next{
	    background: url("../css/images/carousel_right.png") no-repeat !important;
	    width: 60px;
	    height: 60px;
	    text-indent: -999px;
	    position: absolute;
	    z-index: 99;
	    right: -85px !important;
	    background-size: 35% !important;
		}
	    .slick-prev{
	    background: url("../css/images/carousel_left.png") no-repeat !important;
	    width: 60px;
	    height: 60px;
	    text-indent: -999px;
	    position: absolute;
	    z-index: 99;
	    left: -35px !important;
	    background-size: 35% !important;
		}
		.slick-next:before{
		display: none;
		}
		.slick-dots li.slick-active button::before{
			color: red;
		}
		.slick-dots li button::before{
			content: '\26AB' !important;
			font-size: 18px;
		}
		.slick-dots li button:hover:before, .slick-dots li button:focus:before{
			content: '\26AB' !important;
		    font-size: 18px;
		}
		.row-container{
			background-color: #fdf6ed;
		}
	</style>
	<script>
	$(document).ready(function(){
		$('.carousel-container').slick({
			  dots: true,
			  infinite: false,
			  slidesToShow: 3,
			  slidesToScroll: 3
			});
	});
	</script>
    </head>
    <body>


	<div class="container-fluid nopadding" ng-app="myApp" ng-controller="myCtrl">
	<div class="jumbotron">
        <h1>Bigdata Ready Enterprise</h1>
        <p class="col-md-6 col-xs-6 col-sm-6 col-lg-6 nopadding">Our product attempts to make big data technology simpler by optimizing and integrating various big data solutions and providing them under one integrated package, Big Data Ready Enterprises, or BDRE. BDRE is a Bigdata/Hadoop unified framework developed with the goal of drastically minimizing development time.</p>
      </div>

	    <div class="row-container">
			<div class="carousel-container text-center col-xs-9 col-md-9 col-lg-9 col-sm-9">
				<div ng-repeat="value in text track by $index" ng-class="{row:($index + 1) % 3 == 0}" class="carousel-items">
				    <img width="88" src="{{imgs[$index]}}" id="myCanvas{{$index}}" class="carousel-icons">
				    <div>
				    	<p class="lead">{{head[$index]}}</p><p class="desc">{{desc[$index]}} <a href="{{taillinks[$index]}}">{{tail[$index]}}</a></p>
				    </div>
				</div>
		    </div>
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
	    var imgs = ["../css/images/metadata.png", "../css/images/dataintegrity.png", "../css/images/datalineage.png", "../css/images/dataquality.png", "../css/images/automation.png", "../css/images/extraction.png", "../css/images/runcontrol.png", "../css/images/datagen.png", "../css/images/dependancy.png", "../css/images/loading.png", "../css/images/visualization.png", "../css/images/analytic.png"];

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
	</script>
    </body>
</html>