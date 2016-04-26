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
	    padding-bottom: 8% !important;
    	padding-top: 7% !important;
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
        <h1><spring:message code="common.page.title_bdre_1"/></h1>
        <p class="col-md-6 col-xs-6 col-sm-6 col-lg-6 nopadding"><spring:message code="welcome.page.bdre_description"/></p>
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
		var text = [<spring:message code="welcome.page.text_var"/>];
	    var head = [<spring:message code="welcome.page.head_var"/>]
	    var desc = [<spring:message code="welcome.page.desc_var"/>];
	    var tail = [<spring:message code="welcome.page.tail_var"/>];
	    var taillinks = [<spring:message code="welcome.page.taillinks_var"/>];
	    var imgs = [<spring:message code="welcome.page.imgs_var"/>];
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