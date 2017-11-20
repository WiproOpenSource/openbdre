<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
    <%@ taglib prefix="security"
	   uri="http://www.springframework.org/security/tags" %>
        <%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
        <%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
            <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
            <html>

            <head>
                <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
                <title><spring:message code="common.page.title_bdre_1"/></title>
                <style>
					div.jtable-main-container>table.jtable>tbody>tr.jtable-data-row>td:nth-child(2){
						color: #F75C17;
						font-size: 24px;
						font-weight: 500;
					}
					div.jtable-main-container>table.jtable>tbody>tr.jtable-data-row>td img {
						width: 15px;
						height: 15px;
					}
					.form-control {
						background-color: #e4e5e6 !important;
						height: 36px !important;
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
						right: 134px;
						color: white;
						padding: 5px;
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
						right: 133px;
						width: 129px;
					}

					.subprocess-arrow-down {
						-ms-transform: rotate(90deg); /* IE 9 */
						-webkit-transform: rotate(90deg); /* Chrome, Safari, Opera */
						transform: rotate(90deg);
					}

					.label-icons {
						margin: 0 auto;
						width: 45px;
						height: 45px;
						background-size: 100% !important;
						display: block;
						background-repeat: no-repeat !important;
						background-position: center !important;
					}
					.label-properties {
						background: url('../css/images/properties.png') no-repeat center;
					}

					.label-pipeline {
						background: url('../css/images/pipeline.png');
					}

					.label-execution {
						background: url('../css/images/execution.png');
					}

					.label-editgraphically {
						background: url('../css/images/editgraphically.png');
					}

					.label-export {
						background: url('../css/images/export.png');
					}

					.label-execute {
						background: url('../css/images/execute.png');
					}

					.slamonitor {
						background: url('../css/images/slamonitor.png');
					}

					.label-initial {
						background: url('../css/images/label-initial.png');
					}

					.label-icons.label-warning {
						background: url('../css/images/label-warning.png');
					}

					.label-icons.label-success {
						background: url('../css/images/label-success.png');
					}

					.label-icons.label-danger {
						background: url('../css/images/label-danger.png');
					}
					</style>

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
                <link href="../css/bootstrap.custom.css" rel="stylesheet" />


                <!-- Include jTable script file. -->
                <script src="../js/jquery.min.js" type="text/javascript"></script>
                <script src="../js/jquery-ui-1.10.3.custom.js" type="text/javascript"></script>
                <script src="../js/jquery.jtable.js" type="text/javascript"></script>

                <script type="text/javascript">
                    $(document).ready(function() {
                    	$('#Container').jtable({
                            title: '<spring:message code="process.page.title_list"/>',
                            paging: true,
                            pageSize: 10,
                            sorting: false,
                            openChildAsAccordion: true,
                            actions: {
                                listAction: function(postData, jtParams) {
                                    return $.Deferred(function($dfd) {
                                        $.ajax({
                                        <c:if test = "${param.pid==null}">
                                               url: '/mdrest/process?page=' + jtParams.jtStartIndex + '&size='+jtParams.jtPageSize+ '&pTypeId='+86,
                                        </c:if>
                                        <c:if test = "${param.pid!=null}">
                                            url: '/mdrest/process?pid=${param.pid}&page=' + jtParams.jtStartIndex + '&size='+jtParams.jtPageSize,
                                        </c:if>
                                            type: 'GET',
                                                data: postData,
                                                dataType: 'json',
                                                success: function(data) {
                                                if (data.Result == "OK"){
                                                    $dfd.resolve(data);


				var processIdIndex;
				var deployStatusIndex;
				var totalColumnCount;

			   totalColumnCount=($("div#Container").find("table > thead >tr >th").length);
			   $.each(	$("div#Container").find("table > thead >tr >th"),function( index, value ) {
			   if(value.innerText.indexOf("Job Id") > -1) { processIdIndex=index;  }
			   if(value.innerText.indexOf("Deploy Job")>-1) { deployStatusIndex=index;   }
				 });
				console.log("totalColumnCount = "+totalColumnCount);
				console.log("processIdIndex = "+processIdIndex);
				console.log("deployStatusIndex = "+deployStatusIndex);
		        var pid;

		$.each(	$("div#Container").find("table > tbody > tr > td"),function( index, value ) {
		var deploy=0;
		if((index-processIdIndex)%totalColumnCount==0) pid= value.innerText;
		if((index-deployStatusIndex)%totalColumnCount==0){
			 var $jqueryObj=$(this);
              $.ajax({
				  url: '/mdrest/ancestors/' + pid,
				  dataType: 'json',
			  }).done(function(obj) {
                     if (obj.Record.deployInsertTs) {
                         if(obj.Record.deploySuccessTs || obj.Record.deployFailTs ){
                              if (obj.Record.editTs > obj.Record.deploySuccessTs && obj.Record.deploySuccessTs > obj.Record.deployFailTs) {
                                  deploy = 1;
                              }
                              else if(obj.Record.deploySuccessTs < obj.Record.deployFailTs){
                                  deploy = 4;
                              }
                         } else {
                                  deploy = 3;
                         }

                     } else {
                         deploy = 2;
                     }
                     params = obj.Record.processId;
                     console.log("params = "+params+" deploy = "+deploy);
                     if (deploy === 2) {
                         $jqueryObj.html('<span title=<spring:message code="process.page.process_not_deployed_msg"/> class="label-icons label-initial" onclick=fetchDeployPage(' + params + ')  ></span>');
                     } else if (deploy === 1) {
                         $jqueryObj.html('<span title=<spring:message code="process.page.process_redeploy_msg"/> class="label label-warning" onclick=fetchDeployPage(' + params + ')  >Redeploy</span>');
                     }else if (deploy === 3) {
                         $jqueryObj.html('<span title=<spring:message code="process.page.process_queue_msg"/> class="label-icons label-warning" onclick=fetchDeployPage(' + params + ')  ></span>');
                     }
                     else if (deploy === 4) {
                         $jqueryObj.html('<span title=<spring:message code="process.page.process_failed_redeploy_msg"/> class="label-icons label-danger" onclick=fetchDeployPage(' + params + ')  ></span>');
                     }
                     else {
                         $jqueryObj.html('<span title=<spring:message code="process.page.process_no_change_msg"/> class="label-icons label-success"  onclick=fetchDeployPage(' + params + ') ></span>');
                     }
                 });

						  }

					   });
			}else {
			    if(data.Message == "ACCESS DENIED")
                             {
                             alert(data.Message);
                              location.href = location.href = '<c:url value="/pages/process.page"/>';
                             }
                             else{
                			    $("#process-not-found").dialog({
                                    resizable: false,
                                    height: 'auto',
                                    modal: true,
                                    buttons: {
                                        "OK": function() {
                                            $(this).dialog("close");
                                             location.href = location.href = '<c:url value="/pages/process.page"/>';
                                        }
                                    }
                                }).html('<p><span class="jtable-confirm-message"><spring:message code="process.page.title_no_process_exist_msg"/></span></p>');
                				}

			}

                                                },
                                                error: function() {
                                                    console.log("error occured");
                                                    $dfd.reject();
                                                }
                                        });
                                    });
                                },
                                <security:authorize access = "hasAnyRole('ROLE_ADMIN','ROLE_USER')" >
                                    createAction: function(postData) {
                                        console.log(postData);
                                        return $.Deferred(function($dfd) {
                                            $.ajax({
                                                url: '/mdrest/process',
                                                type: 'PUT',
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
                                updateAction: function(postData) {
                                    console.log(postData);
                                    return $.Deferred(function($dfd) {
                                        $.ajax({
                                            url: '/mdrest/process',
                                            type: 'POST',
                                            data: postData,
                                            dataType: 'json',
                                            success: function(data) {
                                                if(data.Result == "OK") {

                                                    $dfd.resolve(data);
                                                   $('#Container').jtable('load');

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
                                },
                                deleteAction: function(item) {
                                    console.log(item);
                                    return $.Deferred(function($dfd) {
                                        $.ajax({
                                            url: '/mdrest/process/' + item.processId,
                                            type: 'DELETE',
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
                                </security:authorize>

                            },
                            fields: {


                                SubProcesses: {                    
                                    width: '0%',
                                    sorting: false,
                                    edit: false,
                                    create: false,
                                    listClass: 'bdre-jtable-button',
                                        display: function(item) {                         //Create an image that will be used to open child table
                                                                
                                        var $img = $('<img class="subprocess-arrow" src="../css/images/subprocess-rarrow.png" title=<spring:message code="process.page.img_sub_process_info"/> />');                         //Open child table when user clicks the image
                                                                
                                        $img.click(function() {
                                        	$('.subprocess-arrow').removeClass('subprocess-arrow-down');
                                        	$(this).addClass('subprocess-arrow-down');
                                        	$('#Container').jtable('openChildTable',                                     
                                                $img.closest('tr'),                                      {                                        
                                                    title: ' <spring:message code="process.page.title_sub_process_of"/>'+' ' + item.record.processId,
                                                        actions: {                                        
                                                        listAction: function(postData) {
                                                            return $.Deferred(function($dfd) {
                                                                console.log(item);
                                                                $.ajax({
                                                                    url: '/mdrest/subprocess/' + item.record.processId,
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

                                                                         alert(data.Message);
                                                                         data.Result="OK";
                                                                         $dfd.resolve(data);
                                                                         }
                                                                         else
                                                                         $dfd.resolve(data);
                                                                        }
                                                                    },
                                                                    error: function() {
                                                                        $dfd.reject();
                                                                    }
						                                       }); ;
                                                            });
                                                        },
                                                        deleteAction: function(postData) {
                                                            console.log(postData.processId);
                                                            return $.Deferred(function($dfd) {
                                                                $.ajax({
                                                                    url: '/mdrest/subprocess/' + postData.processId,
                                                                    type: 'DELETE',
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
                                                        },
                                                        updateAction: function(postData) {
                                                            console.log(postData);
                                                            return $.Deferred(function($dfd) {
                                                                $.ajax({
                                                                    url: '/mdrest/subprocess',
                                                                    type: 'POST',
                                                                    data: postData,
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
                                                        },
                                                        createAction: function(postData) {
                                                            console.log(postData);
                                                            return $.Deferred(function($dfd) {
                                                                $.ajax({
                                                                    url: '/mdrest/subprocess',
                                                                    type: 'PUT',
                                                                    data: postData,
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

                                                        processId: {
                                                            key: true,
                                                            width: '5%',
                                                            list: true,
                                                            create: false,
                                                            edit: false,
                                                            title: 'Id'
                                                        },
                                                        Properties: {
                                                            title: 'Properties',
                                                            width: '5%',
                                                            sorting: false,
                                                            edit: false,
                                                            create: false,
                                                            listClass: 'bdre-jtable-button',
                                                            display: function(item) { //Create an image that will be used to open child table

                                                                var $img = $('<span class="label-icons label-properties"></span>'); //Open child table when user clicks the image

                                                                $img.click(function() {
                                                                    $('#Container').jtable('openChildTable',
                                                                        $img.closest('tr'), {
                                                                            title: ' <spring:message code="process.page.title_properties_of"/>'+' ' + item.record.processId,
                                                                            paging: true,
                                                                            pageSize: 10,
                                                                            actions: {
                                                                                listAction: function(postData,jtParams) {
                                                                                    return $.Deferred(function($dfd) {
                                                                                        console.log(item);
                                                                                        $.ajax({
                                                                                            url: '/mdrest/properties/' + item.record.processId+'?page=' + jtParams.jtStartIndex + '&size='+jtParams.jtPageSize,
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
                                                                                                  alert(data.Message);
                                                                                                  data.Result="OK";
                                                                                                  $dfd.resolve(data);

                                                                                                }
                                                                                                else
                                                                                                $dfd.resolve(data);
                                                                                               }
                                                                                           },
                                                                                            error: function() {
                                                                                                $dfd.reject();
                                                                                            }
                                                                                        }); ;
                                                                                    });
                                                                                },
                                                                                deleteAction: function(postData) {
                                                                                    console.log(postData.processId);
                                                                                    return $.Deferred(function($dfd) {
                                                                                        $.ajax({
                                                                                            url: '/mdrest/properties/' + item.record.processId + '/' + postData.key + '/',
                                                                                            type: 'DELETE',
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
                                                                                },
                                                                                updateAction: function(postData) {
                                                                                    console.log(postData);
                                                                                    return $.Deferred(function($dfd) {
                                                                                        $.ajax({
                                                                                            url: '/mdrest/properties',
                                                                                            type: 'POST',
                                                                                            data: postData + '&processId=' + item.record.processId,
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
                                                                                },
                                                                                createAction: function(postData) {
                                                                                    console.log(postData);
                                                                                    return $.Deferred(function($dfd) {
                                                                                        $.ajax({
                                                                                            url: '/mdrest/properties',
                                                                                            type: 'PUT',
                                                                                            data: postData + '&processId=' + item.record.processId,
                                                                                            dataType: 'json',
                                                                                             success: function(data) {
                                                                                                if(data.Result == "OK") {

                                                                                                    $dfd.resolve(data);

                                                                                                }
                                                                                                else
                                                                                                {
                                                                                                 if(data.Message == "ACCESS DENIED")
                                                                                                 {
                                                                                                  alert(data.Message);
                                                                                                 data.Result="OK";
                                                                                                 $dfd.resolve(data);


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

                                                                                processId: {
                                                                                    key: true,
                                                                                    list: false,
                                                                                    create: false,
                                                                                    edit: true,
                                                                                    title: '<spring:message code="process.page.title_process"/>',
                                                                                    defaultValue: item.record.processId,
                                                                                },
                                                                                configGroup: {
                                                                                    title: '<spring:message code="process.page.title_cg"/>',
                                                                                    defaultValue: item.record.configGroup,
                                                                                },
                                                                                key: {
                                                                                    title: '<spring:message code="process.page.title_key"/>',
                                                                                    key: true,
                                                                                    list: true,
                                                                                    create: true,
                                                                                    edit: false,
                                                                                    defaultValue: item.record.key,
                                                                                },
                                                                                value: {
                                                                                    title: '<spring:message code="process.page.title_value"/>',
                                                                                    defaultValue: item.record.value,
                                                                                },
                                                                                description: {
                                                                                    title: '<spring:message code="process.page.title_desc"/>',
                                                                                    defaultValue: item.record.description,
                                                                                },
                                                                            }
                                                                        },
                                                                        function(data) { //opened handler

                                                                            data.childTable.jtable('load');
                                                                        });
                                                                }); //Return image to show on the person row

                                                                return $img;
                                                            }
                                                        },
                                                        processName: {
                                                            title: '<spring:message code="process.page.title_name"/>',
                                                            defaultValue: 'Child of ' + item.record.processId,
                                                        },
                                                        description: {
                                                            title: '<spring:message code="process.page.title_desc"/>',
                                                            defaultValue: 'A Child of ' + item.record.processId

                                                        },
                                                        tableAddTS: {
                                                            title: '<spring:message code="process.page.title_add_ts"/>',
                                                            list: true,
                                                            create: false,
                                                            edit: true
                                                        },
                                                        tableEditTS: {
                                                            title: '<spring:message code="process.page.title_edit_ts"/>',
                                                            list: false,
                                                            create: false,
                                                            edit: false
                                                        },
                                                        batchPattern: {
                                                            title: '<spring:message code="process.page.title_batch_mark"/>'

                                                        },
                                                        parentProcessId: {
                                                            type: 'hidden',
                                                            defaultValue: item.record.processId,
                                                        },
                                                        canRecover: {
                                                            title: '<spring:message code="process.page.title_restorability"/>',
                                                            edit: true,
                                                            type: 'combobox',
							                                options: { '1': 'Restorable', '0': 'Non-Restorable'},
                                                            defaultValue: "1"
                                                        },
                                                        nextProcessIds: {
                                                            title: '<spring:message code="process.page.title_next"/>'

                                                        },
                                                        enqProcessId: {
                                                            title: '<spring:message code="process.page.title_enqueued_by"/>',
                                                            defaultValue: '0',
                                                            edit: true
                                                        },
                                                        busDomainId: {
                                                            type: 'hidden',
                                                            defaultValue: item.record.busDomainId,
                                                        },
                                                        processTypeId: {
                                                            title: '<spring:message code="process.page.title_type"/>',
                                                            type: 'combobox',
                                                            options: '/mdrest/processtype/options/' + item.record.processTypeId,
                                                        },
                                                        workflowId: {
                                                            type: 'hidden',
                                                            defaultValue: '0'
                                                        },
                                                        processTemplateId: {
                                                            type: 'hidden',
                                                            defaultValue: null,
                                                        },
                                                    }
                                                },
                                                function(data) { //opened handler
                                                                                            
                                                    data.childTable.jtable('load');                                    
                                                });                        

                                        });                         //Return image to show on the person row
                                                                
                                        return $img;                    
                                    }                
                                },
                                processId: {
                                    key: true,
                                    list: true,
                                    create: false,
                                    edit: false,
                                    title: '<spring:message code="process.page.title_job_id"/>'
                                },

                                processName: {
                                    title: '<spring:message code="process.page.title_name"/>'
                                },
                                tableAddTS: {
                                    title: '<spring:message code="process.page.title_add_ts"/>',
                                    create: false,
                                    edit: true,
                                    list: true
                                },
                                tableEditTS: {
                                    title: '<spring:message code="process.page.title_edit_ts"/>',
                                    list: false,
                                    create: false,
                                    edit: false
                                },
                                description: {
                                    title: '<spring:message code="process.page.title_desc"/>',
                                },
                                batchPattern: {
                                    title: '<spring:message code="process.page.title_batch_mark"/>',
                                    list: false,
                                    create: false,
                                    edit: false

                                },
                                parentProcessId: {
                                    title: '<spring:message code="process.page.title_parent"/>',
                                    edit: false,
                                    create: false,
                                    list: false
                                },
                                canRecover: {
                                    title: '<spring:message code="process.page.title_restorable"/>',
                                    type: 'hidden',
                                    list: false,
                                    defaultValue: "0"
                                },
                                nextProcessIds: {
                                    title: '<spring:message code="process.page.title_next"/>'

                                },
                                enqProcessId: {
                                    title: '<spring:message code="process.page.title_enqueu"/>',
                                    list: false,
                                    type: 'hidden',
                                    defaultValue: "0"

                                },
                                busDomainId: {
                                    title: '<spring:message code="process.page.title_app"/>',
                                    type: 'combobox',
                                    options: '/mdrest/busdomain/options/',
                                    defaultValue: "1"
                                },
                                permissionTypeByUserAccessId: {
                                    title: '<spring:message code="process.page.title_user_access"/>',
                                    type: 'combobox',
                                    list: false,
                                    options: '/mdrest/process/options/',
                                    defaultValue: "7"
                                },
                                permissionTypeByGroupAccessId: {
                                  title: '<spring:message code="process.page.title_group_access"/>',
                                  type: 'combobox',
                                  list: true,
                                  options: '/mdrest/process/options/',
                                  defaultValue: "6"
                               },
                               permissionTypeByOthersAccessId: {
                                  title: '<spring:message code="process.page.title_other_Access"/>',
                                  type: 'combobox',
                                  list: false,
                                  options: '/mdrest/process/options/',
                                  defaultValue: "0"
                               },
                               ownerRoleId: {
                                 title: '<spring:message code="process.page.title_owner_group"/>',
                                 type: 'combobox',
                                 list:true,
                                 options: '/mdrest/userroles/options/',
                              },
                              userName: {
                                       title: '<spring:message code="process.page.title_username"/>',
                                        create:false,
                                        edit:false
                                    },
                                processTypeId: {
                                    title: '<spring:message code="process.page.title_type"/>',
                                    type: 'combobox',
                                    options: '/mdrest/processtype/optionslist',
                                    defaultValue: "1",
                                },
                                processTemplateId: {
                                    type: 'hidden',
                                    defaultValue: null,
                                },
                                workflowId: {
                                    title: '<spring:message code="process.page.title_wf_type"/>',
                                    type: 'combobox',
                                    options: '/mdrest/workflowtype/optionslist',
                                    defaultValue: "1"
                                },
                                Properties: {
                                    title: '<spring:message code="process.page.title_properties"/>',
                                    width: '5%',
                                    sorting: false,
                                    edit: false,
                                    create: false,
                                    listClass: 'bdre-jtable-button',
                                    display: function(item) { //Create an image that will be used to open child table

                                        var $img = $('<span class="label-icons label-properties"></span>'); //Open child table when user clicks the image

                                        $img.click(function() {
                                            $('#Container').jtable('openChildTable',
                                                $img.closest('tr'), {
                                                    title: '<spring:message code="process.page.title_properties_of"/>'+' ' + item.record.processId,
                                                    paging: true,
                                                    pageSize: 10,
                                                    actions: {
                                                        listAction: function(postData) {
                                                            return $.Deferred(function($dfd) {
                                                                console.log(item);
                                                                $.ajax({
                                                                    url: '/mdrest/properties/' + item.record.processId,
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
                                                                      alert(data.Message);
                                                                      data.Result="OK";
                                                                      $dfd.resolve(data);

                                                                     }
                                                                     else
                                                                     $dfd.resolve(data);
                                                                    }
                                                                },
                                                                    error: function() {
                                                                        $dfd.reject();
                                                                    }
                                                                }); ;
                                                            });
                                                        },
                                                        deleteAction: function(postData) {
                                                            console.log(postData.processId);
                                                            return $.Deferred(function($dfd) {
                                                                $.ajax({
                                                                    url: '/mdrest/properties/' + item.record.processId + '/' + postData.key +'/',
                                                                    type: 'DELETE',
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
                                                        },
                                                        updateAction: function(postData) {
                                                            console.log(postData);
                                                            return $.Deferred(function($dfd) {
                                                                $.ajax({
                                                                    url: '/mdrest/properties',
                                                                    type: 'POST',
                                                                    data: postData + '&processId=' + item.record.processId,
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
                                                        },
                                                        createAction: function(postData) {
                                                            console.log(postData);
                                                            return $.Deferred(function($dfd) {
                                                                $.ajax({
                                                                    url: '/mdrest/properties',
                                                                    type: 'PUT',
                                                                    data: postData + '&processId=' + item.record.processId,
                                                                    dataType: 'json',
                                                                     success: function(data) {
                                                                        if(data.Result == "OK") {

                                                                            $dfd.resolve(data);

                                                                        }
                                                                        else
                                                                        {
                                                                         if(data.Message == "ACCESS DENIED")
                                                                         {
                                                                         alert(data.Message);
                                                                         data.Result="OK";
                                                                         $dfd.resolve(data);


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

                                                        processId: {
                                                            key: true,
                                                            list: false,
                                                            create: false,
                                                            edit: true,
                                                            title: '<spring:message code="process.page.title_process"/>',
                                                            defaultValue: item.record.processId,
                                                        },
                                                        configGroup: {
                                                            title: '<spring:message code="process.page.title_cg"/>',
                                                            defaultValue: item.record.configGroup,
                                                        },
                                                        key: {
                                                            title: '<spring:message code="process.page.title_key"/>',
                                                            key: true,
                                                            list: true,
                                                            create: true,
                                                            edit: false,
                                                            defaultValue: item.record.key,
                                                        },
                                                        value: {
                                                            title: '<spring:message code="process.page.title_value"/>',
                                                            defaultValue: item.record.value,
                                                        },
                                                        description: {
                                                            title: '<spring:message code="process.page.title_desc"/>',
                                                            defaultValue: item.record.description,
                                                        },
                                                    }
                                                },
                                                function(data) { //opened handler

                                                    data.childTable.jtable('load');
                                                });
                                        }); //Return image to show on the person row

                                        return $img;
                                    }
                                },
                                ProcessPipelineButton: {
                                    title: '<spring:message code="process.page.title_pipeline"/>',
                                    sorting: false,
                                    width: '2%',
                                    listClass: 'bdre-jtable-button',
                                    create: false,
                                    edit: false,
                                    display: function(data) {
                                        return '<span class="label-icons label-pipeline" onclick="fetchPipelineInfo(' + data.record.processId + ')"></span> ';
                                    },
                                },
                                DeployProcess: {                    
                                    width: '5%',
                                    sorting: false,
                                    edit: false,
                                    create: false,
                                    title: '<spring:message code="process.page.title_deploy_job"/>',

                                },
                                  
                                RunProcess: {                    
                                	width: '5%',
                                	sorting: false,
                                	edit: false,
                                	create: false,
                                	title: '<spring:message code="process.page.title_run_job"/>',
                                	display: function(data) {
                                		var $img2 = $('<span title=<spring:message code="process.page.img_execute_process"/> class="label-icons label-execute" ></span>');
                                		$img2.click(function() {
                                			console.log(data);
                                			$("#execute-dialog-confirm").dialog({
                                				resizable: false,
                                				height: 'auto',
                                				modal: true,
                                				buttons: {
                                					Cancel: function() {
                                						$(this).dialog("close");
                                					},
                                					'<spring:message code="process.page.fun_execute"/>': function() {
                                						$(this).dialog("close");
                                						return $.Deferred(function($dfd) {
                                							var processData = jQuery.param(data.record);
                                							console.log(processData);
                                							$.ajax({
                                								url: '/mdrest/process/execute/',
                                								type: 'POST',
                                								data: processData,
                                								dataType: 'json',
                                								success: function(data) {
                                									if(data.Result == "OK") {
                                										console.log(data);
                                										$("#execute-result").dialog({
                                											resizable: false,
                                											height: 'auto',
                                											modal: true,
                                											buttons: {
                                												"OK": function() {
                                													$(this).dialog("close");
                                												}
                                											}
                                										}).html('<p><span class="jtable-confirm-message"><spring:message code="process.page.title_process"/>'+' <b>' +data.Record.processId +'</b> '+'<spring:message code="process.page.success_msg"/>'+' <b>' + data.Record.osprocessId + '</b></span></p>');
                                									} else {
                                									       if(data.Message == "ACCESS DENIED")
                                									        {alert(data.Message);}
                                									         else{
                                										console.log(data);
                                										$("#execute-fail").dialog({
                                											resizable: false,
                                											height: 'auto',
                                											modal: true,
                                											buttons: {
                                												"OK": function() {
                                													$(this).dialog("close");
                                												}
                                											}
                                										}).html('<p><span class="jtable-confirm-message"><spring:message code="process.page.process_failed"/></span></p>');
                                									}}
                                								},
                                								error: function() {
                                									$dfd.reject();
                                								}

                                							});
                                						});
                                					}

                                				}
                                			});
                                		});
                                		return $img2;
                                	}
                                },


                                InstanceExecs: {                    
                                    width: '10%',
                                    sorting: false,
                                    title: 'Executions',
                                    edit: false,
                                    create: false,
                                    listClass: 'bdre-jtable-button',
                                        display: function(item) {                         //Create an image that will be used to open child table
                                                                
                                        var $img = $('<span class="label-icons label-execution"></span>');                      //Open child table when user clicks the image
                                                                
                                        $img.click(function() {                            
                                            $('#Container').jtable('openChildTable',                                     
                                                $img.closest('tr'),                                      {                                        
                                                    title: '<spring:message code="process.page.title_execution_of"/>'+' '+ item.record.processName,
                                                       actions: {
                                                        listAction: function(postData, jtParams) {
                                                            console.log(postData);
                                                            return $.Deferred(function($dfd) {
                                                                $.ajax({
                                                                    url: '/mdrest/instanceexec?pid=' + item.record.processId,
                                                                    type: 'GET',
                                                                    data: postData,
                                                                    dataType: 'json',
                                                                     success: function(data) {
                                                                                    if(data.Result == "OK") {
                                                                                        $dfd.resolve(data);
                                                                                    }
                                                                                    else
                                                                                    {
                                                                                     if(data.Message == "ACCESS DENIED")
                                                                                     {
                                                                                     alert(data.Message);
                                                                                     data.Result="OK";
                                                                                     $dfd.resolve(data);
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
                                                        instanceExecId: {
                                                            key: true,
                                                            list: true,
                                                            create: false,
                                                            edit: false,
                                                            title: '<spring:message code="process.page.title_id"/>'
                                                        },

                                                        processId: {
                                                            title: '<spring:message code="process.page.title_pid"/>'
                                                        },
                                                        LineageButton: {
                                                            sorting: false,
                                                            width: '5%',
                                                            title: '<spring:message code="process.page.title_batch_lineage"/>',
                                                            create: false,
                                                            edit: false,
                                                            display: function(data) {
                                                                if (data.record.execState === 2) {
                                                                    return '<span class="label label-warning" onclick="fetchLineageInfo(' + data.record.instanceExecId + ')"><spring:message code="process.page.title_display"/></span> ';
                                                                } else if (data.record.execState === 3) {
                                                                    return '<span class="label label-success" onclick="fetchLineageInfo(' + data.record.instanceExecId + ')"><spring:message code="process.page.title_display"/></span> ';;
                                                                } else if (data.record.execState === 6) {
                                                                    return '<span class="label label-danger" onclick="fetchLineageInfo(' + data.record.instanceExecId + ')"><spring:message code="process.page.title_display"/></span> ';;
                                                                } else {
                                                                    return '<span class="label label-info"><spring:message code="process.page.title_failed"/></span> ';
                                                                }
                                                            },
                                                        },
                                                        tableStartTs: {
                                                            title: '<spring:message code="process.page.title_start_time"/>',
                                                        },
                                                        tableEndTs: {
                                                            title: '<spring:message code="process.page.title_end_time"/>',
                                                        },
                                                    }
                                                },
                                                function(data) { //opened handler
                                                                                            
                                                    data.childTable.jtable('load');                                    
                                                });                        
                                        });                         //Return image to show on the person row
                                                                
                                        return $img;                    
                                    }                
                                },
                                Export: {
                                    title: '<spring:message code="process.page.title_export"/>',
                                    width: '10%',
                                    sorting: false,
                                    create: false,
                                    edit: false,
                                    display: function(data) {

                                     return '<span class="label-icons label-export" onclick="goToExportPage(' + data.record.processId + ')"></span> ';
                                     },

                                },


                                SLAMonitoring: {
                                    title: '<spring:message code="process.page.title_sla_monitoring"/>',
                                    width: '10%',
                                    sorting: false,
                                    create: false,
                                    edit: false,
                                    display: function(data) {

                                     return '<span class="label-icons slamonitor" onclick="goToSLAMonitoringPage(' + data.record.processId + ')"></span> ';
                                     },

                                },
                                EditGraphically: {
                                    title: '<spring:message code="process.page.title_edit_graphically"/>',
                                    sorting: false,
                                    width: '2%',
                                    listClass: 'bdre-jtable-button',
                                    create: false,
                                    edit: false,
                                    display: function(data) {
                                        return '<span class="label-icons label-editgraphically" onclick="goToEditGraphically(' + data.record.processId+','+data.record.processTypeId+')"></span> ';
                                    },
                                },
                            },
                            selectionChanged: function() {
                                //Get all selected rows
                                var $selectedRows = $('#Container').jtable('selectedRows');
                                //    $selectedRows.each(function() {
                                //        var record = $(this).data('record');
                                //        alert('Process id: ' + record.processId);
                                //    });
                            }
                        });
                        $('#Container').jtable('load');
                        $('#input-box-button-filter').click(function () {
                        	$('#input-box-button').toggle();
						});
                    });

                </script>
                <script>
                    function fetchLineageInfo(ied) {
                        location.href = '<c:url value="/pages/batchlineagebyinstanceexec.page?ied="/>' + ied;
                    }

                </script>


                <script>
                    function fetchDeployPage(processId) {

                        $("#dialog-confirm").dialog({
                            resizable: false,
                            height: 'auto',
                            modal: true,
                            buttons: {
                            	Cancel: function() {
                                    $(this).dialog("close");
                                },
                                '<spring:message code="process.page.fun_deploy"/>': function() {
                                    $(this).dialog("close");
                                    console.log(processId);
                                    return $.Deferred(function($dfd) {
                                        $.ajax({
                                            url: '/mdrest/pdq/' + processId,
                                            type: 'PUT',
                                            data: '&processId=' + processId,
                                            dataType: 'json',
                                            success: function(data) {
                                            if(data.Result == "OK")
                                                {
                                                 $dfd.resolve(data);
                                                 $('div#Container').jtable('load');
                                                 }
                                                 else
                                                 {
                                                 if(data.Message == "ACCESS DENIED")
                                                 {
                                                 data.Result == "OK";
                                                 $dfd.resolve(data);
                                                 $('div#Container').jtable('load');
                                                 alert(data.Message);
                                                 }
                                                 else
                                                 {
                                                 $dfd.resolve(data);
                                                 $('div#Container').jtable('load');
                                                 }
                                                 }
                                            },

                                            error: function() {
                                                $dfd.reject();
                                            }
                                        });
                                    });

                                }

                            }
                        });
                    }

                </script>

                <script>
                    function fetchPipelineInfo(pid) {
                    $.ajax({
                            url: '/mdrest/process/permission/'+pid,
                            type: 'PUT',
                            dataType: 'json',
                             success: function(data) {
                                if(data.Result == "OK") {
                                console.log(data.Record.workflowId);
                                if(data.Record.workflowId == 2){
                                  $("#alert-dialog").dialog({
                                            resizable: false,
                                            height: 'auto',
                                            modal: true,
                                            buttons: {
                                                OK: function() {
                                                    $(this).dialog("close");
                                                }
                                             }
                                           });
                                           }
                                           else
                                           {
                                  location.href = '<c:url value="/pages/lineage.page?pid="/>' + pid;
                                }
                                }
                                else
                                {
                                 alert(data.Message);
                                }
                            },
                            error: function() {
                                $dfd.reject();
                            }
                        });
                    }
					function goToEditGraphically(pid,pTypeId) {
                                      $.ajax({
                                               url: '/mdrest/process/permission/'+pid,
                                               type: 'PUT',
                                               dataType: 'json',
                                                success: function(data) {
                                                   if(data.Result == "OK") {
                                                   if(pTypeId==41)

                                                location.href = '<c:url value="/pages/wfdesigner2.page?processId="/>' + pid;
                                                else
                                                location.href = '<c:url value="/pages/wfdesigner.page?processId="/>' + pid;
                                                   }
                                                   else
                                                   {
                                                    alert(data.Message);
                                                   }
                                               },
                                               error: function() {
                                                   $dfd.reject();
                                               }
                                           });
                    }

                     function goToExportPage(pid)
                           {
                               $.ajax({
                                            url: '/mdrest/process/permission/'+pid,
                                            type: 'PUT',
                                            dataType: 'json',
                                             success: function(data) {
                                                if(data.Result == "OK") {
                                               location.href = '<c:url value="/pages/appexport.page?processId="/>' + pid;
                                                }
                                                else
                                                {
                                                 alert(data.Message);
                                                }
                                            },
                                            error: function() {
                                                $dfd.reject();
                                            }
                                        });
                           }

                     function goToSLAMonitoringPage(pid)
                                          {
                                           $.ajax({
                                                 url: '/mdrest/process/permission/'+pid,
                                                 type: 'PUT',
                                                 dataType: 'json',
                                                  success: function(data) {
                                                     if(data.Result == "OK") {
                                                        location.href = '<c:url value="/pages/sla.page?processId="/>' + pid;                                                     }
                                                     else
                                                     {
                                                      alert(data.Message);
                                                     }
                                                 },
                                                 error: function() {
                                                     $dfd.reject();
                                                 }
                                             });
                                         }
                </script>
                <%--  --%>
                    <script>
                        function showProcessPage(pid) {
                            console.log('entered function');
                            console.log(${param.pid == null});
                            location.href = '<c:url value="/pages/process.page?pid="/>' + pid;
                        }

                    </script>
                    <script type="text/javascript">
                         var auto = setInterval(    function ()
                         {
                               $('div#Container').jtable('load');
                         }, 60000);
                    </script>

            </head>

            <body>

                <section style="width:100%;text-align:center;">
                    <div id="Container"></div>
                </section>
                <div id="input-box-button-filter" class="input-box-button-filter">
                	<span class="filter-icon"></span><span class="filter-text"><spring:message code="process.page.span_filter"/></span>
                </div>
                <div id="input-box-button" class="input-box-button">
                    <form onsubmit="showProcessPage(jQuery('#pid').val()); return false;">
                        <div class="input-group">
                            <input class="form-control" type="number" name="pid" id="pid" value="" placeholder=<spring:message code="process.page.pid_placeholder"/> />
                            <!-- <button  class="btn btn-default btn-lg btn-primary"><span id="sizing-addon2"><span class="glyphicon glyphicon-arrow-right" aria-hidden="true"></span> Show Lineage </button> -->
                            <span class="input-group-btn">
		    <button class="btn btn-default" type="submit" onClick="showProcessPage(jQuery('#pid').val())"><span id="sizing-addon2"><span class="glyphicon glyphicon-arrow-right" aria-hidden="true"></span>&nbsp;</button>
                            </span>
                        </div>
                    </form>
                </div>
				<div id="dialog-confirm" style="display: none;">
					<p>
						<span class="ui-icon-alert"></span>
						<span class="dialog-title-custom"><spring:message code="process.page.span_sure"/></span>
						<span class="jtable-confirm-message"><spring:message code="process.page.span_build_workflow_confirm_msg"/>
						</span>
					</p>
				</div>
				<div id="execute-dialog-confirm" style="display: none;">
					<p>
						<span class="ui-icon-alert"></span>
						<span class="dialog-title-custom"><spring:message code="process.page.span_sure"/></span>
						<span class="jtable-confirm-message"><spring:message code="process.page.span_start_execution_confirm_msg"/>
						</span>
					</p>
				</div>
				<div id="alert-dialog" style="display: none;">
                					<p>
                						<span class="ui-icon-alert"></span>
                						<span class="dialog-title-custom"><spring:message code="process.page.span_alert"/></span>
                						<span class="jtable-confirm-message"><spring:message code="process.page.span_start_execution_alert_msg"/>
                						</span>
                					</p>
                				</div>
				<div id="dialog-form" style="display: none;">
					<p>
						<span class="ui-icon-alert"></span>
						<span class="dialog-title-custom"><spring:message code="process.page.span_sure"/></span>
						<span class="jtable-confirm-message"><spring:message code="process.page.span_export_execution_confirm_msg"/>
						</span>
					</p>
				</div>
				<div id="execute-result" style="display: none;">
					<p>
						<span class="ui-icon ui-icon-alert"></span>
						<span class="jtable-confirm-message"><spring:message code="process.page.span_process_start"/></span>
					</p>
				</div>
				<div id="execute-fail" style="display: none;">
					<p>
						<span class="ui-icon ui-icon-alert"></span>
						<span class="jtable-confirm-message"><spring:message code="process.page.span_process_init_failed"/></span>
					</p>
				</div>
				<div id="process-not-found" style="display: none;">
					<p>
						<span class="ui-icon ui-icon-alert"></span>
						<span class="jtable-confirm-message"><spring:message code="process.page.span_process_not_found"/></span>
					</p>
				</div>
			</body>

            </html>
