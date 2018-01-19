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
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
     pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
    <head>


    		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <script src="../js/jquery.min.js"></script>
            <link href="../css/jquery-ui-1.10.3.custom.css" rel="stylesheet">
            <link href="../css/css/bootstrap.min.css" rel="stylesheet" />
            <script src="../js/jquery-ui-1.10.3.custom.js"></script>
            <script src="../js/jquery.steps.min.js"></script>
            <link rel="stylesheet" href="../css/jquery.steps.css" />
            <link rel="stylesheet" href="../css/jquery.steps.custom.css" />
            <link href="../css/bootstrap.custom.css" rel="stylesheet" type="text/css" />
            <script src="../js/bootstrap.js" type="text/javascript"></script>
            <script src = "../js/jquery.fancytree.js" ></script >
            <link rel = "stylesheet" href = "../css/ui.fancytree.css" />
            <script src = "../js/jquery.fancytree.gridnav.js" type = "text/javascript" ></script >
            <script src = "../js/jquery.fancytree.table.js" type = "text/javascript" ></script >
            <script src="../js/jquery.jtable.js" type="text/javascript"></script>
            <link href="../css/jtables-bdre.css" rel="stylesheet" type="text/css" />


	<script>
    	  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
    	  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
    	  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
    	  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');
    	  //Please replace with your own analytics id
    	  ga('create', 'UA-72345517-1', 'auto');
    	  ga('send', 'pageview');
    	</script>
    <style>
    html, body, .container-table {
        height: 100%;
    }
    .container-table {
        display: table;
    }
    .vertical-center-row {
        display: table-cell;
        horizontal-align: middle;
        padding-top: 2cm;
    }

    </style>
  </head>

   <body >
     <%
       String processId=request.getParameter("pid");
      %>


        <script>

                 var map = new Object();
                  var createJobResult;
                     var requiredProperties;
                     var sourceFlag;
                     var created = 0;
                function formIntoMap(typeProp, typeOf) {
                 var x = '';
                 x = document.getElementById(typeOf);
                 console.log(x);
                 var text = "";
                 var i;
                 for(i = 0; i < x.length; i++) {
                             map[typeProp + x.elements[i].name] = x.elements[i].value;
                 }
                }
         </script>

         		<div class="page-header">Scheduling</div>
                                 <div class="row">&nbsp;</div>
                                 <div class="row">
                                     <div class="col-md-2"></div>
                                     <div class="col-md-8" id="divEncloseHeading" >
                                         <div class="panel panel-primary">

                                             <div class="panel-body">
                                                 <form role="form" id="propertiesFieldsForm">
                                                     <div class="form-group">
                                                         <label >Frequency (in cron syntax)</label>
                                                         <input type="text" class="form-control" id="frequency" name="frequency" required>
                                                     </div>

                                                     <div class="form-group">
                                                         <label >Start Time</label>
                                                         <input type="text" class="form-control" id="startTime" name="startTime" required>
                                                     </div>

                                                     <div class="form-group">
                                                             <label >End Time</label>
                                                             <input type="text" class="form-control" id="endTime" name="endTime" required>
                                                         </div>

                                                      <div class="form-group">
                                                          <label >Time Zone</label>
                                                          <input type="text" class="form-control" id="timeZone" name="timeZone" required>
                                                      </div>

                                                      <div class="actions text-center pull-right" >
                                                         <button type="button" id="schedulejobs" class="btn btn-primary btn-lg">Schedule Jobs</button>
                                                      </div>
                                                 </form>
         									</div>
                                         </div>
                                     </div>
                                     <div class="col-md-2"> </div>
                         <div class="row">&nbsp;</div>
                             <div class="row">
                                 <div class="col-md-3"> </div>
                                 <div class="col-md-6 ">
                                 <div class="panel panel-success">
                                     <div class="panel-heading" name="successHeader" id="successHeader">scheduling started</div>
                                     <div id="Process"></div>
                                 </div>
                                 </div>
                             </div>



          <script>
          $("#successHeader").hide();

          var property;
          var frequency = getPropValue("schedule-frequency");
          var startTime = getPropValue("schedule-start-time");
          var endTime = getPropValue("schedule-end-time");
          var timeZone = getPropValue("schedule-time-zone");

          document.getElementById("frequency").defaultValue = frequency["Record"];
          document.getElementById("startTime").defaultValue = startTime["Record"];
          document.getElementById("endTime").defaultValue = endTime["Record"];
          document.getElementById("timeZone").defaultValue = timeZone["Record"];


          function getPropValue(key){
                      $.ajax({
                      url: '/mdrest/properties/' + <%=processId %> + '/schedule/'+key ,
                          type: 'GET',
                          dataType: 'json',
                          async: false,
                          success: function (data) {
                              property = data;
                              console.log("properties from ajax"+ property);
                          },
                          error: function () {
                              alert('danger'+key);
                          }
                      });
                      return property
             }
             </script>


            <script>
            var processId = <%=processId %>
             $('#schedulejobs').on('click', function(e) {
                  formIntoMap('scheduleProperties_','propertiesFieldsForm');

                     $.ajax({
                          url: "/mdrest/scheduler/schedulejob/"+processId,
                          type: 'POST',
                          data: jQuery.param(map),

                             success: function(data) {
                                if(data.Result == "OK") {
                                    console.log("OK");
                                    location.href = '<c:url value="/pages/process.page?pid="/>' + processId;
                                }
                                else
                                {
                                 alert('danger');
                                }
                            },
                            error: function() {
                                 alert('danger');
                            }
                        });

              });
            </script>





   </body>
</html>