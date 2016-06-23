<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
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
	<style>
	.form-control {
						background-color: #e4e5e6 !important;
						height: 35px !important;
						border-radius: 1px !important;
					}
					
					.glyphicon-arrow-right {
						color: #606161 !important;
					}
					.btn-primary {
						background-color: #ADAFAF !important;
						border: 1px solid #828283 !important;
						padding-top: 7.5px !important;
						padding-bottom: 7.5px !important;
						border-radius: 1px !important;
					}
					
					.input-box-button-filter {
						background: #4A4B4B;
						background: -webkit-linear-gradient(#4A4B4B 50%, #3A3B3B 50%);
						background: -o-linear-gradient(#4A4B4B 50%, #3A3B3B 50%);
						background: -moz-linear-gradient(#4A4B4B 50%, #3A3B3B 50%);
						background: -ms-linear-gradient(#4A4B4B 50%, #3A3B3B 50%);
						background: linear-gradient(#4A4B4B 50%, #3A3B3B 50%);
						position: absolute;
						top: 0;
						right: 0;
						color: white;
						padding: 5px 10px;
						cursor: pointer
					}
					
					.filter-icon {
						background-image: url('../css/images/filter_icon.png');
						background-size: 100%;
						background-repeat: no-repeat;
						display: inline-block;
						margin: 2px;
						vertical-align: middle;
						width: 16px;
						height: 16px;
					}
					
					.filter-text {
						display: inline-block;
						margin: 2px;
						vertical-align: middle;
						font-size: 0.9em;
						font-family: 'Segoe UI Semilight', 'Open Sans', Verdana, Arial,
							Helvetica, sans-serif;
						font-weight: 300;
					}
					
					.input-box-button {
						display: none;
						position: absolute;
						top: 34px;
						right: 0;
						width: 129px;
					}
	</style>

	<!-- Include one of jTable styles. -->

	<link href="../css/css/bootstrap.min.css" rel="stylesheet" type="text/css" />
	<link href="../css/jtables-bdre.css" rel="stylesheet" type="text/css" />
	<link href="../css/jquery-ui-1.10.3.custom.css" rel="stylesheet" type="text/css" />

	<!-- Include jTable script file. -->
	<script src="../js/jquery.min.js" type="text/javascript"></script>
	<script src="../js/jquery-ui-1.10.3.custom.js" type="text/javascript"></script>
	<script src="../js/jquery.jtable.js" type="text/javascript"></script>

	<script type="text/javascript">
		    $(document).ready(function () {
	    $('#Container').jtable({
	    title: '<spring:message code="processlog.page.title_list"/>',
		    paging: true,
		    pageSize: 10,
		    sorting: true,
		    actions: {
		    listAction: function (postData, jtParams) {
		    console.log(postData);
			    return $.Deferred(function ($dfd) {
			    $.ajax({
	    <c:if test="${param.pid==null}">
			    url: '/mdrest/processlog?page=' + jtParams.jtStartIndex + '&size='+jtParams.jtPageSize,
	    </c:if>
	    <c:if test="${param.pid!=null}">
			    url: '/mdrest/process?pid=${param.pid}&page=' + jtParams.jtStartIndex + '&size='+jtParams.jtPageSize,
	    </c:if>
			    type: 'GET',
				    data: postData,
				    dataType: 'json',
				    success: function (data) {
				    $dfd.resolve(data);
				    },
				    error: function () {
				    $dfd.reject();
				    }
			    });
			    });
		    }
		    },
		    fields: {

		    SubProcess: {
		    title: '<spring:message code="processlog.page.title_click_to_list"/>',
			    width: '5%',
			    sorting: false,
			    edit: false,
			    create: false,
			    listClass: 'bdre-jtable-button',
			    display: function(item) {                         //Create an image that will be used to open child table

			    var $img = $('<span class="label label-primary"><spring:message code="processlog.page.img_subprocess"/></span>'); //Open child table when user clicks the image

				    $img.click(function() {
				    $('#Container').jtable('openChildTable',
					    $img.closest('tr'), {
				    title: '<spring:message code="processlog.page.title_subprocess_desc"/>'+' ' + item.record.parentProcessId + ' having logs ',
					    paging: true,
					    pageSize: 10,
					    actions: {
					    listAction: function(postData) {
					    return $.Deferred(function($dfd) {
					    console.log(item);
						    console.log (item.record.parentProcessId);
						    console.log (item.record.processId);
						    $.ajax({
						    url: '/mdrest/processlog/?pid=' + item.record.parentProcessId,
							    type: 'GET',
							    data: item,
							    dataType: 'json',
							    success: function(data) {
                                        if(data.Result == "OK") {

                                            $dfd.resolve(data);

                                        }
                                        else
                                        {
                                         if(data.Message == "ACCESS DENIED")
                                         {
                                         data.Result="OK";
                                         $dfd.resolve(data);
                                         alert(data.Message);
                                         $('#Container').jtable('load');
                                         }
                                         else
                                         $dfd.resolve(data);
                                        }
                                    },
							    error: function() {
							    $dfd.reject();
							    }
						    });
					    });
					    }
					    },
					    fields: {
					    Log: {
					    title: '<spring:message code="processlog.page.title_click_to_log"/>',
						    width: '5%',
						    sorting: false,
						    edit: false,
						    create: false,
						    listClass: 'bdre-jtable-button',
						    display: function(item) {                         //Create an image that will be used to open child table

						    var $img = $('<span class="label label-primary"><spring:message code="processlog.page.img_log"/></span>'); //Open child table when user clicks the image

							    $img.click(function() {
							    $('#Container').jtable('openChildTable',
								    $img.closest('tr'), {
							    title: ' <spring:message code="processlog.page.title_log_pid"/>'+' ' + item.record.processId,
								    paging: true,
								    pageSize: 10,
								    actions: {
								    listAction: function(postData) {
								    return $.Deferred(function($dfd) {
								    console.log(item);
									    $.ajax({
									    url: '/mdrest/processlog/' + item.record.processId,
										    type: 'GET',
										    data: item,
										    dataType: 'json',
										    success: function(data) {
										    $dfd.resolve(data);
										    },
										    error: function() {
										    $dfd.reject();
										    }
									    });
								    });
								    }
								    },
								    fields :{
								    processId: {
								    key : true,
									    list: false,
									    create:false,
									    edit: true,
									    title: '<spring:message code="processlog.page.title_pid"/>',
									    defaultValue: item.record.processId,
								    },
									    tableAddTs: {
									    title: '<spring:message code="processlog.page.title_add_ts"/>'
									    },
									    logCategory: {
									    title: '<spring:message code="processlog.page.title_log_cat"/>'

									    },
									    message: {
									    title: '<spring:message code="processlog.page.title_msg"/>'

									    },
									    messageId: {
									    title: '<spring:message code="processlog.page.msg_id"/>',
									    },
									    instanceRef: {

									    title: '<spring:message code="processlog.page.title_instance_ref"/>',
										    create:true,
										    edit :true
									    }

								    }

							    },
								    function(data) { //opened handler

								    data.childTable.jtable('load');
								    });
							    }); //Return image to show on the person row

							    return $img;
						    }
					    },
						    processId: {
						    key : true,
							    list: true,
							    create:true,
							    edit: false,
							    title: '<spring:message code="processlog.page.title_process"/>'

						    }
					    }
				    },
					    function(data) { //opened handler

					    data.childTable.jtable('load');
					    });
				    }); //Return image to show on the person row

				    return $img;
			    }

		    },
			    parentProcessId: {
			    key : true,
				    list: true,
				    create:true,
				    edit: false,
				    title: '<spring:message code="processlog.page.title_process"/>'

			    },
			    processId: {
			    key : true,
				    list: false,
				    create:true,
				    edit: false,
				    title: '<spring:message code="processlog.page.title_process"/>'

			    }
		    }
	    });
		    $('#Container').jtable('load');
		    $('#input-box-button-filter').click(function () {
            	$('#input-box-button').toggle();
			});
	    });</script>
	<script>

		    function showProcessPage(pid){
		    console.log('entered function');
			    console.log(${param.pid == null });
			    location.href = '<c:url value="/pages/processlog.page?pid="/>' + pid;
		    }

	</script>
    </head>
    <body>
    <section style="width:100%;text-align:center;">
	<div id="Container"></div>
    </section>
     <div id="input-box-button-filter" class="input-box-button-filter">
       	<span class="filter-icon"></span><span class="filter-text"><spring:message code="process.page.span_filter"/></span>
       </div>
    <div id="input-box-button" class="input-box-button" >
	<form onsubmit="showProcessPage(jQuery('#pid').val()); return false;">
	    <div class="input-group">
		<input class="form-control" type="number" name="pid" id="pid" value ="" placeholder=<spring:message code="processlog.page.filter_by_processid_placeholder"/>/>
		<!-- <button  class="btn btn-default btn-lg btn-primary"><span id="sizing-addon2"><span class="glyphicon glyphicon-arrow-right" aria-hidden="true"></span> Show Lineage </button> -->
		<span class="input-group-btn">
		    <button class="btn btn-default" type="submit" onClick="showProcessPage(jQuery('#pid').val())"><span id="sizing-addon2"><span class="glyphicon glyphicon-arrow-right" aria-hidden="true"></span>&nbsp;</button>
		</span>
	    </div>
	</form>
    </div>

</body>
</html>​
