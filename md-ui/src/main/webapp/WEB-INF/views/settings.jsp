<%@ taglib prefix="security"
	   uri="http://www.springframework.org/security/tags" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
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

	<link href="../css/css/bootstrap.min.css" rel="stylesheet" type="text/css" />
	<link href="../css/jtables-bdre.css" rel="stylesheet" type="text/css" />
	<link href="../css/jquery-ui-1.10.3.custom.css" rel="stylesheet" type="text/css" />
	<link rel="stylesheet" href="../css/jquery.steps.css" />
	<link rel="stylesheet" href="../css/jquery.steps.custom.css" />
	<!-- Include jTable script file. -->
	<script src="../js/jquery.min.js" type="text/javascript"></script>
    <script src="../js/bootstrap.js" type="text/javascript"></script>
	<script src="../js/jquery-ui-1.10.3.custom.js" type="text/javascript"></script>
	<script src="../js/jquery.jtable.js" type="text/javascript"></script>
    <script src="../js/jquery.steps.min.js"></script>
    <script src="../js/angular.min.js" type="text/javascript"></script>

	<script type="text/javascript">

		var configGroupVal = '';
            var updateSettings = function (){

						$.ajax({
	        			                    type: "POST",
                                            url: "/mdrest/genconfig/admin",
                                            data: $('#SettingsForm').serialize()+"&configGroup="+configGroupVal,
                                            success: function(data) {
                                                if(data.Result == "OK") {
                                                    created = 1;
                                                    $("#div-dialog-warning").dialog({
                                                        title: "Success",
                                                        resizable: false,
                                                        height: 'auto',
                                                        modal: true,
                                                        buttons: {
                                                            "Ok": function() {
                                                                $(this).dialog("close");
                                                            }
                                                        }
                                                    }).text("Saved successfully.");

                                                }
                                                else{
                                                    $("#div-dialog-warning").dialog({
                                                        title: "Error",
                                                        resizable: false,
                                                        height: 'auto',
                                                        modal: true,
                                                        buttons: {
                                                            "Ok": function() {
                                                                $(this).dialog("close");
                                                            }
                                                        }
                                                    }).html(data.Message);
                                                }

                                            }
                    		});
                	}


	$(document).ready(function () {
    $('#configDropdown').change(function() {
		console.log($(this).val());
		var config=$(this).val();
		configGroupVal = config;
		buildFormDisplay(config,'Settings');
	});
    });

function buildFormDisplay(configGroup, typeDiv) {
	console.log('inside the function');
	$.ajax({
		type: "GET",
		url: "/mdrest/genconfig/" + configGroup + "/?required=1",
		dataType: 'json',
		success: function(data) {
			var root = 'Records';
			var div = document.getElementById(typeDiv);
			var formHTML = '';
			formHTML = formHTML + '<form role="form" id = "' + typeDiv + 'Form" >';
			console.log(data[root]);
			$.each(data[root], function(i, v) {
				formHTML = formHTML + '<div class="form-group" > <label for="' + v.key + '">' + v.key + '</label>';
				formHTML = formHTML + '<span class="" title="' + v.description + '"></span>';
				formHTML = formHTML + '<input name="'+v.key+'" value="' + v.defaultVal + '" class="form-control" id="' + v.key + '"></div>';
			});

			    formHTML = formHTML + '<div class="clearfix"></div><div id="editSettings" class="actions text-center pull-right"><button onclick="updateSettings()" id="editMdSetting" type="button" class="btn btn-primary">save</button></div>';
			formHTML = formHTML + '</form>';
			div.innerHTML = formHTML;
			console.log(div);
		}
	});
	return true;
}
	</script>

	</head>
    <body ng-controller="myCtrl">
    		<div class="page-heading"><spring:message code="settings.page.panel_heading"/></div>
    		<section>
    			<div class="alert-info-outer">
	    			<div class="alert alert-info" role="alert">
	                     <spring:message code="settings.page.configuration_alert"/>
	                </div>
                </div>
                <div id="config">
    				<div id="configDiv">
					<form id="configForm" >
					<div>Select Configuration</div>
   					<select id="configDropdown" class="btn btn-default dropdown-toggle configDropdown" data-toggle="dropdown" aria-haspopup="false" aria-expanded="true">
   						<option value="" disabled selected>Select your option</option>
   						<option value="mdconfig">mdconfig</option>
   						<option value="imconfig">imconfig</option>
   						<option value="scripts_config">scriptsconfig</option>
					</select>
					</form>
					</div>
    			</div>
				<div id="Settings" class="steps-vertical"></div>
				
    </section>
    <div id="div-dialog-warning"/>
</body>
</html>