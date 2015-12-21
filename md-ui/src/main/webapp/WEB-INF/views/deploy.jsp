<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!doctype html>
<html lang="en">
    <head>
	<meta charset="utf-8">
	<title>BDRE | Deployment</title>
	<link href="../css/jquery-ui-1.10.3.custom.css" rel="stylesheet">
	<link href="../css/css/bootstrap.min.css" rel="stylesheet" type="text/css" />
	<script src="../js/jquery.min.js"></script>
	<script src="../js/jquery-ui-1.10.3.custom.js"></script>
	<style>
	    .ui-progressbar {
		position: relative;
	    }
	    .ui-widget-header-info {
		color: #00529B !important;
		background-color: #BDE5F8 !important;
	    }
	    .ui-widget-header-failure{
		color: #D8000C !important;
		background-color: #FFBABA !important;
	    }
	    .ui-widget-header-unstable{
		color: #9F6000 !important;
		background-color: #FEEFB3 !important;
	    }
	    .ui-widget-header-stable{
		color: #4F8A10 !important;
		background-color: #DFF2BF !important;
	    }
	    .progress-label {
		background: transparent  !important;
		position: absolute;
		left: 50%;
		top: 4px;
		font-weight: bold;
		text-shadow: 1px 1px 0 #fff;
	    }
	</style>
	<script>


	    $(function () {
		var progressbar = $("#progressbar-2");
		var progressLabel = $("#progressbar-label");
		$("#progressbar-2").progressbar({
		    value: false,
		    max: 100,
		    change: function () {


		    },
		    complete: function () {
			progressLabel.text("100%");
			//$("#parameters").append("<div class='glyphicon glyphicon-triangle-right'>"+"ETA : "+ getDuration(eta) + "</div>");
		    }
		}).children().addClass('ui-widget-header-info');

		// /deploy/{busDomainId  }/{processTypeId}/{pid}
		$.getJSON('/mdrest/jenkins/deploy/${param.busDomainId}/${param.processTypeId}/${param.processId}','', function (data) {
		    setTimeout(progress, 100);
		});

		function progress() {

		    $.getJSON('/mdrest/jenkins/status/${param.processTypeId}','', function (data) {

			$.each(data.actions[0].parameters, function (index, value) {
			    if (value.name == 'processId')
			    {
				$("#parameters").html("<h4 class='glyphicon glyphicon-cloud-upload'>  Currently Deploying Process# : <span class='label label-info'>" + value.value + "</span></h4>");
				$("#parameters").addClass('alert alert-info');
			    }

			});



			if (data.building) {

			    console.log("building");
			    $("#progressbar-2").progressbar().children().removeClass('ui-widget-header-stable');
			    $("#progressbar-2").progressbar().children().removeClass('ui-widget-header-unstable');
			    $("#progressbar-2").progressbar().children().removeClass('ui-widget-header-failure');
			    $("#progressbar-2").progressbar().children().addClass('ui-widget-header-info');
			    progressbar.progressbar("value", eval(data.executor.progress));
			    var eta = data.estimatedDuration - ((new Date()).getTime() - data.timestamp);

			    progressLabel.text(progressbar.progressbar("value") + "%");
			    setTimeout(progress, 1000);
			}
			else {
			    console.log("not building");
			    progressbar.progressbar("value", false);
			    progressLabel.text("Looking for new deploy jobs...");
			    if (data.result == 'STABLE') {
				console.log('STABLE');
				$("#progressbar-2").progressbar().children().addClass('ui-widget-header-stable');
				$('#buildResult').html('<div class="alert alert-success" role="alert">Last deployment was successfully complete.</div>');

			    }
			    else if (data.result == 'UNSTABLE') {
				console.log('UNSTABLE');
				$("#progressbar-2").progressbar().children().addClass('ui-widget-header-unstable');
				$('#buildResult').html('<div class="alert alert-warning" role="alert">Last deployment was completed with warnings. Please check the log for details.</div>');
			    }
			    else if (data.result == 'FAILURE') {
				console.log('FAILURE');
				$("#progressbar-2").progressbar().children().addClass('ui-widget-header-failure');
				$('#buildResult').html('<div class="alert alert-error" role="alert">Last deployment was failed. Please check the log for details.</div>');

			    }
			    setTimeout(progress, 5000);

			}

		    });
		}

	    });
	</script>
	<script>
	    var getDuration = function (millis) {
		if (millis < 0)
		    return 'Almost done';
		var dur = {};
		var units = [
		    {label: "millis", mod: 1000},
		    {label: "seconds", mod: 60},
		    {label: "minutes", mod: 60},
		    {label: "hours", mod: 24},
		    {label: "days", mod: 31}
		];
		// calculate the individual unit values...
		units.forEach(function (u) {
		    millis = (millis - (dur[u.label] = (millis % u.mod))) / u.mod;
		});
		// convert object to a string representation...
		dur.toString = function () {
		    return units.reverse().map(function (u) {
			return dur[u.label] + " " + u.label;
		    }).join(', ');
		};
		return dur;
	    };
	</script>
    </head>
    <body>
	<br/>
	<h3>BDRE Job Deployment</h3>
	<div role="alert" id="parameters"> </div>
	<div id="progressbar-2"><div id="progressbar-label" class="progress-label">Loading...</div></div>
	<br/>
	<div role="alert" id="buildResult"> </div>
    </body>
</html>