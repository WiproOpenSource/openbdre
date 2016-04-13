<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	 pageEncoding="ISO-8859-1"%>
	 <%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
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

	<link href="../css/metro/brown/jtable.css" rel="stylesheet" type="text/css" />
	<link href="../css/jquery-ui-1.10.3.custom.css" rel="stylesheet" type="text/css" />

	<!-- Include jTable script file. -->
	<script src="../js/jquery.min.js" type="text/javascript"></script>
	<script src="../js/Chart.js" type="text/javascript"></script>
	<script src="../js/jquery-ui-1.10.3.custom.js" type="text/javascript"></script>
	<script src="../js/jquery.jtable.js" type="text/javascript"></script>
	<script type="text/javascript">



	    function GenerateChart(pid, time)
	    {

		$.ajax({
		    url: '/mdrest/processperformance/' + pid + '/' + time,
		    type: 'GET',
		    dataType: 'json',
		    success: function (data) {

			var chartData = data;
			var values = [];
			var xlabel = [];
			for (var ln = 0; ln < chartData.Records.length; ln++) {
			    values[ln] = chartData.Records[ln].durationInSec;
			    xlabel[ln] = Math.floor(ln * (time / chartData.Records.length / 60));
			}
			var lineChartData = {
			    labels: xlabel,
			    datasets: [
				{
				    label: 'Performance graph for ' + pid,
				    fillColor: "rgba(31, 160, 228,0.3)",
				    strokeColor: "rgba(31, 160, 228,1)",
				    pointColor: "rgba(31, 160, 228,1)",
				    pointStrokeColor: "#fff",
				    pointHighlightFill: "#fff",
				    pointHighlightStroke: "rgba(31, 160, 228,1)",
				    data: values,
				    scaleLabel: 'Process#' + pid

				}

			    ]

			}

			var ctx = document.getElementById("canvas").getContext("2d");
			window.myLine = new Chart(ctx).Line(lineChartData, {
			    responsive: true
			});

		    },
		    error: function () {
			$dfd.reject();

		    }
		});
	    }

	</script>
    </head>

    <body>

    <section style="width:100%;text-align:center;">
	<div id="Container"></div>
    </section>
    <section>
	Process#: <input type="number" name="pid" id="pid" value =""/>
	<select name="time" id="time" onChange="GenerateChart(jQuery('#pid').val(), jQuery('#time').val());">
	    <option value="1440">Last 24 hours</option>
	    <option value="10080">Last 1 week</option>
	    <option value="43200">Last 1 month</option>
	    <option value="518400">Last 12 months</option>
	</select>
	<button onClick="GenerateChart(jQuery('#pid').val(), jQuery('#time').val());" href="#">Show Performance</button>
    </section>
    <div style="width:100%;">
	<div>
	    <canvas id="canvas"></canvas>
	    <p>X axis : Sample time(from current) in hours</p>
	    <p>Y axis : Duration of the execution in section</p>
	</div>
    </div>
</body>
</html>