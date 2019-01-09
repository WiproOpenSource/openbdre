<%@ taglib prefix="security"
	   uri="http://www.springframework.org/security/tags" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	 pageEncoding="ISO-8859-1"%>
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
	    title: '<spring:message code="adq.page.title_adq_list"/>',
		    paging: true,
		    edit: false,
		    create: false,
		    pageSize: 10,
		    sorting: true,
		    actions: {
		    listAction: function (postData, jtParams) {
	         	    console.log(postData);
     			    return $.Deferred(function ($dfd) {
			    $.ajax({
			    url: '/mdrest/adq?page=' + jtParams.jtStartIndex + '&size='+jtParams.jtPageSize,
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
		    },
	    <security:authorize access="hasAnyRole('ROLE_ADMIN','ROLE_USER')">
	   			 updateAction: function(item) {
        			    console.log(item);
        				    return $.Deferred(function($dfd) {
        				    $.ajax({
        				    url: '/mdrest/adq',
        					    type: 'POST',
        					    data: postData,
        					    dataType: 'json',
        					    success: function(data) {
        					    $dfd.resolve(data);
        					    },
        					    error: function() {
        					    $dfd.reject();
        					    }
        				    });
        				    });
        			    },
			    deleteAction: function (item) {
			    console.log(item);
				    return $.Deferred(function ($dfd) {
				    $.ajax({
				    url: '/mdrest/adq/' + item.deploymentId,
					    type: 'DELETE',
					    data: item,
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
	    </security:authorize>
		    },
		    fields:
	    {
            appDeploymentQueueId: {
            title: '<spring:message code="adq.page.title_deploy_id"/>',
            key: true,
                list: true,
                edit:false
            },
            processId: {
			title: '<spring:message code="adq.page.title_p_id"/>',
			edit:false,

			},
		    appDeploymentStatusId: {
		    title: '<spring:message code="adq.page.title_aps_id"/>',
		    edit:false,


		    },
            username: {
           title: '<spring:message code="adq.page.title_usernasme"/>',
           edit:false
           },
		   appDomain: {
		   title: '<spring:message code="adq.page.title_app_domain"/>',
		   edit:false
			 },
             appName: {
             title: '<spring:message code="adq.page.title_app_name"/>',
             edit:false
             },
		   mergeButton: {

			 sorting: false,
			 width: '2%',
			 listClass: 'bdre-jtable-button',
			 create: false,
			 edit: false,
			 display: function(data) {

				 return '<span class="label label-primary" onclick="mergeApp(' + data.record.appDeploymentQueueId + ')"><spring:message code="adq.page.merge"/></span> ';
			 },
		 },
		 rejectButton: {

         			 sorting: false,
         			 width: '2%',
         			 listClass: 'bdre-jtable-button',
         			 create: false,
         			 edit: false,
         			 display: function(data) {
         				 return '<span class="label label-primary" onclick="rejectApp(' + data.record.appDeploymentQueueId + ')"><spring:message code="adq.page.reject"/></span> ';
         			 },
         		 }

	    }
	    });
		    $('#Container').jtable('load');
	    });

	    mergeApp =function (appDeploymentQueueId){
                                                console.log(appDeploymentQueueId);
                                        $("#dialog-confirm").dialog({
                                    resizable: false,
                                    height: 'auto',
                                    modal: true,
                                    buttons: {
                                        '<spring:message code="adq.page.yes_merge"/>': function() {
                                            $(this).dialog('<spring:message code="adq.page.close"/>');



                                            $("#gitUrl").dialog({
                                                resizable: false,
                                                height: 'auto',
                                                modal: true,
                                                buttons: {
                                                     submit :function() {
                                                     console.log("github url is "+document.getElementById('githubUrl').value);
                                                     $(this).dialog('<spring:message code="adq.page.close"/>');


                                           $("#during-merge").dialog({
                                                         resizable: false,
                                                         height: 'auto',
                                                         modal: true
                                                         }).html('<h3><center><spring:message code="adq.page.merging"/></center></h3>');
                                                  console.log(appDeploymentQueueId);
                                                 return $.Deferred(function($dfd) {
                                                     $.ajax({
                                                      url: '/mdrest/adq/merge/'+appDeploymentQueueId,
                                                       type: 'POST',
                                                       dataType: 'json',
                                                       data :{gitUrl :document.getElementById('githubUrl').value},
                                                        success: function(data) {
                                                        if (data.Result == "OK") {
                                                        $("#during-merge").dialog('<spring:message code="adq.page.close"/>');
                                                        console.log(data);
                                                       $("#execute-result").dialog({
                                                                         resizable: false,
                                                                         height: 'auto',
                                                                         modal: true,
                                                                         buttons: {
                                                                             '<spring:message code="adq.page.ok"/>': function() {
                                                                                 $(this).dialog('<spring:message code="adq.page.close"/>');

                                                                             }
                                                                         }
                                                                     }).html('<spring:message code="adq.page.app_having_adqid"/> <b> ' +appDeploymentQueueId +' </b> <spring:message code="adq.page.succes_merge"/>');


                                                     }
                                                          if (data.Result == "ERROR"){
                                                          $("#during-merge").dialog('<spring:message code="adq.page.close"/>');
                                                          $("#execute-result").dialog({
                                                                      resizable: false,
                                                                      height: 'auto',
                                                                      modal: true,
                                                                      buttons: {
                                                                         '<spring:message code="adq.page.ok"/>': function() {
                                                                              $(this).dialog('<spring:message code="adq.page.close"/>');

                                                                          }
                                                                      }
                                                                  }).html('<spring:message code="adq.page.error_app_merge"/>');
                                                            }
                                                       },
                                                        error: function() {
                                                        alert('<spring:message code="adq.page.error_app_merge"/>');
                                                    }
                                                });
                                             });
                                             }

                                                }
                                                         });


                                        },
                                        Cancel: function() {
                                            $(this).dialog('<spring:message code="adq.page.close"/>');
                                        }
                                    }
                                });


                                        }


         rejectApp =function (appDeploymentQueueId){
                                                         console.log(appDeploymentQueueId);
                                                 $("#dialog-reject").dialog({
                                                     resizable: false,
                                                     height: 'auto',
                                                     modal: true,
                                                     buttons: {
                                                    	 '<spring:message code="adq.page.yes_reject"/>': function() {
                                                             $(this).dialog('<spring:message code="adq.page.close"/>');
                                                             $("#during-merge").dialog({
                                                                     resizable: false,
                                                                     height: 'auto',
                                                                     modal: true
                                                                     }).html('<spring:message code="adq.page.rejecting"/>');
                                                              console.log(appDeploymentQueueId);
                                                             return $.Deferred(function($dfd) {
                                                                 $.ajax({
                                                                  url: '/mdrest/adq/reject/'+appDeploymentQueueId,
                                                                   type: 'POST',
                                                                   dataType: 'json',
                                                                    success: function(data) {
                                                                    if (data.Result == "OK") {
                                                                    $("#during-merge").dialog('<spring:message code="adq.page.close"/>');
                                                                    console.log(data);
                                                                   $("#execute-result").dialog({
                                                                                     resizable: false,
                                                                                     height: 'auto',
                                                                                     modal: true,
                                                                                     buttons: {
                                                                                    	 '<spring:message code="adq.page.ok"/>': function() {
                                                                                             $(this).dialog('<spring:message code="adq.page.close"/>');

                                                                                         }
                                                                                     }
                                                                                 }).html('<spring:message code="adq.page.app_having_adqid"/> <b>' +appDeploymentQueueId +'</b> <spring:message code="adq.page.succes_reject"/>');


                                                                 }
                                                                      if (data.Result == "ERROR")
                                                                        alert(data.Message);
                                                                   },
                                                                    error: function() {
                                                                    alert('<spring:message code="adq.page.error_app_merge"/>');
                                                                }
                                                            });
                                                         });

                                                         },
                                                         Cancel: function() {
                                                             $(this).dialog('<spring:message code="adq.page.close"/>');
                                                         }
                                                     }
                                                 });


                                                 }


	</script>
    </head>
    <body>

    <section style="width:100%;text-align:center;">
	<div id="Container"></div>
    </section>
<div id="dialog-confirm" style="display: none;">
					<p>
						<span class="ui-icon-alert"></span>
						<span class="dialog-title-custom"><spring:message code="process.page.span_sure"/></span>
						<span class="jtable-confirm-message"><spring:message code="adq.page.export_to_app_store"/>
						</span>
					</p>
				</div>


				<div id="gitUrl" style="display: none;">
                					<p>
                						<span class="dialog-title-custom">Please give github url </span>
                						<span class="jtable-confirm-message"><spring:message code="adq.page.export_to_app_store"/>
                						</span>
                						<span class="jtable-confirm-message"> Git URL : <input type="text" name="githubUrl" id="githubUrl"></input></span>
                					</p>
                				</div>


          <div id="dialog-reject" style="display: none;">
                    <p>
                        <span class="ui-icon-alert"></span>
                        <span class="dialog-title-custom"><spring:message code="process.page.span_sure"/></span>
                        <span class="jtable-confirm-message"><spring:message code="adq.page.export_reject"/>
                        </span>
                    </p>
                </div>
               <div id="execute-result" style="display: none;">
                    <p>
                        <span class="dialog-title-custom"><spring:message code="adq.page.process_started"/></span>
                        <span class="jtable-confirm-message"><spring:message code="adq.page.process_initiation_status"/>
                        </span>
                    </p>
                </div>
                <div id="during-merge" style="display: none;">
                    <p>
                        <span class="dialog-title-custom"><spring:message code="adq.page.process_started"/></span>
                        <span class="jtable-confirm-message"><spring:message code="adq.page.process_initiation_status"/>
                        </span>
                    </p>
                </div>
</body>
</html>