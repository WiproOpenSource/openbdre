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
                div.jtable-main-container > table.jtable > tbody > tr.jtable-data-row > td:nth-child(2){color: #F75C17;font-size: 24px;font-weight: 500;}
                div.jtable-main-container > table.jtable > thead th:nth-child(2){width: 3% !important;}
				div.jtable-main-container > table.jtable > thead th:nth-child(2),div.jtable-main-container > table.jtable > thead th:nth-child(12),div.jtable-main-container > table.jtable > thead th:nth-child(15),div.jtable-main-container > table.jtable > thead th:nth-child(17),div.jtable-main-container > table.jtable > thead th:nth-child(18){padding-top: 0px !important;padding-bottom: 20px !important;}
				div.jtable-main-container > table.jtable > tbody > tr.jtable-data-row > td img{width: 15px;height: 15px;	}
				.form-control-process{background-color: #e4e5e6 !important;height: 36px !important;border-radius: 1px !important;}
				.glyphicon-arrow-right{color: #606161 !important;}
				.btn-primary-process{background-color: #ADAFAF !important;border: 1px solid #828283 !important;padding-top:7.5px !important;padding-bottom: 7.5px !important;border-radius: 1px !important;}
                .input-box-button-filter{background: #4A4B4B;background: -webkit-linear-gradient(#4A4B4B 50%, #3A3B3B 50%);background: -o-linear-gradient(#4A4B4B 50%, #3A3B3B 50%);background: -moz-linear-gradient(#4A4B4B 50%, #3A3B3B 50%);background: -ms-linear-gradient(#4A4B4B 50%, #3A3B3B 50%);background: linear-gradient(#4A4B4B 50%, #3A3B3B 50%);position: absolute;top: 0;right: 134px;color:white;padding:5px;cursor:pointer}
				.filter-icon{background-image: url('../css/images/filter_icon.png');background-size: 100%;background-repeat: no-repeat;  display: inline-block;margin: 2px;vertical-align: middle;width: 16px;height: 16px;}
				.filter-text{display: inline-block;margin: 2px;vertical-align: middle;font-size: 0.9em;font-family: 'Segoe UI Semilight', 'Open Sans', Verdana, Arial, Helvetica, sans-serif;font-weight: 300;}
                .input-box-button{display:none;position: absolute;top: 34px;right: 133px; width: 129px;}
                .subprocess-arrow-down{
                    -ms-transform: rotate(90deg); /* IE 9 */
    				-webkit-transform: rotate(90deg); /* Chrome, Safari, Opera */
    				transform: rotate(90deg);
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
                            title: 'Process List',
                            paging: true,
                            pageSize: 10,
                            sorting: false,
                            openChildAsAccordion: true,
                            actions: {
                                listAction: function(postData, jtParams) {
                                    return $.Deferred(function($dfd) {
                                        $.ajax({
                                        <c:if test = "${param.pid==null}">
                                                url: '/mdrest/process?page=' + jtParams.jtStartIndex + '&size='+jtParams.jtPageSize,
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
                         $jqueryObj.html('<span title="Process is not deployed." class="label label-danger" onclick=fetchDeployPage(' + params + ')  >Deploy</span>');
                     } else if (deploy === 1) {
                         $jqueryObj.html('<span title="Process is updated.Please redeploy." class="label label-warning" onclick=fetchDeployPage(' + params + ')  >Redeploy</span>');
                     }else if (deploy === 3) {
                         $jqueryObj.html('<span title="Process is in deployment queue." class="label label-warning" onclick=fetchDeployPage(' + params + ')  >Deploy </span>');
                     }
                     else if (deploy === 4) {
                         $jqueryObj.html('<span title="Process is failed.Please redeploy." class="label label-danger" onclick=fetchDeployPage(' + params + ')  >Redeploy </span>');
                     }
                     else {
                         $jqueryObj.html('<span title="No changes in process after last deployment." class="label label-success"  onclick=fetchDeployPage(' + params + ') >Deployed</span>');
                     }
                 });

						  }

					   });
			}else {
			    $dfd.reject();
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
                }).html("No Process exist for mentioned ID.");;

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
                                                                
                                        var $img = $('<img class="subprocess-arrow" src="../css/images/subprocess-rarrow.png" title="Sub processes info" />');                         //Open child table when user clicks the image
                                                                
                                        $img.click(function() {
                                        	$('.subprocess-arrow').removeClass('subprocess-arrow-down');
                                        	$(this).addClass('subprocess-arrow-down');
                                        	$('#Container').jtable('openChildTable',                                     
                                                $img.closest('tr'),                                      {                                        
                                                    title: ' Sub processes of ' + item.record.processId,
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

                                                                var $img = $('<span class="label label-primary">Show<span class="glyphicon glyphicon-chevron-right "></span></span>'); //Open child table when user clicks the image

                                                                $img.click(function() {
                                                                    $('#Container').jtable('openChildTable',
                                                                        $img.closest('tr'), {
                                                                            title: ' Properties of ' + item.record.processId,
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
                                                                                    title: 'Process',
                                                                                    defaultValue: item.record.processId,
                                                                                },
                                                                                configGroup: {
                                                                                    title: 'Config Group',
                                                                                    defaultValue: item.record.configGroup,
                                                                                },
                                                                                key: {
                                                                                    title: 'Key',
                                                                                    key: true,
                                                                                    list: true,
                                                                                    create: true,
                                                                                    edit: false,
                                                                                    defaultValue: item.record.key,
                                                                                },
                                                                                value: {
                                                                                    title: 'Value',
                                                                                    defaultValue: item.record.value,
                                                                                },
                                                                                description: {
                                                                                    title: 'Description',
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
                                                            title: 'Name',
                                                            defaultValue: 'Child of ' + item.record.processId,
                                                        },
                                                        description: {
                                                            title: 'Description',
                                                            defaultValue: 'A Child of ' + item.record.processId

                                                        },
                                                        tableAddTS: {
                                                            title: 'Add TS',
                                                            list: true,
                                                            create: false,
                                                            edit: true
                                                        },
                                                        tableEditTS: {
                                                            title: 'Edit TS',
                                                            list: false,
                                                            create: false,
                                                            edit: false
                                                        },
                                                        batchPattern: {
                                                            title: 'Batch Mark'

                                                        },
                                                        parentProcessId: {
                                                            type: 'hidden',
                                                            defaultValue: item.record.processId,
                                                        },
                                                        canRecover: {
                                                            title: 'Restorability',
                                                            edit: true,
                                                            type: 'combobox',
							                                options: { '1': 'Restorable', '0': 'Non-Restorable'},
                                                            defaultValue: "1"
                                                        },
                                                        nextProcessIds: {
                                                            title: 'Next'

                                                        },
                                                        enqProcessId: {
                                                            title: 'Enqueued by',
                                                            defaultValue: '0',
                                                            edit: true
                                                        },
                                                        busDomainId: {
                                                            type: 'hidden',
                                                            defaultValue: item.record.busDomainId,
                                                        },
                                                        processTypeId: {
                                                            title: 'Type',
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
                                    title: 'Job Id'
                                },
                                Properties: {
                                    title: 'Properties',
                                    width: '5%',
                                    sorting: false,
                                    edit: false,
                                    create: false,
                                    listClass: 'bdre-jtable-button',
                                    display: function(item) { //Create an image that will be used to open child table

                                        var $img = $('<span class="label label-primary">Show<span class="glyphicon glyphicon-chevron-right"></span>'); //Open child table when user clicks the image

                                        $img.click(function() {
                                            $('#Container').jtable('openChildTable',
                                                $img.closest('tr'), {
                                                    title: ' Properties of ' + item.record.processId,
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
                                                            title: 'Process',
                                                            defaultValue: item.record.processId,
                                                        },
                                                        configGroup: {
                                                            title: 'Config Group',
                                                            defaultValue: item.record.configGroup,
                                                        },
                                                        key: {
                                                            title: 'Key',
                                                            key: true,
                                                            list: true,
                                                            create: true,
                                                            edit: false,
                                                            defaultValue: item.record.key,
                                                        },
                                                        value: {
                                                            title: 'Value',
                                                            defaultValue: item.record.value,
                                                        },
                                                        description: {
                                                            title: 'Description',
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
                                    title: 'Name'
                                },
                                tableAddTS: {
                                    title: 'Add TS',
                                    create: false,
                                    edit: true,
                                    list: true
                                },
                                tableEditTS: {
                                    title: 'Edit TS',
                                    list: false,
                                    create: false,
                                    edit: false
                                },
                                description: {
                                    title: 'Description',
                                },
                                batchPattern: {
                                    title: 'Batch Mark',
                                    list: false,
                                    create: false,
                                    edit: false

                                },
                                parentProcessId: {
                                    title: 'Parent',
                                    edit: false,
                                    create: false,
                                    list: false
                                },
                                canRecover: {
                                    title: 'Restorable',
                                    type: 'hidden',
                                    list: false,
                                    defaultValue: "0"
                                },
                                nextProcessIds: {
                                    title: 'Next'

                                },
                                enqProcessId: {
                                    title: 'Enqueuer',
                                    list: false,
                                    type: 'hidden',
                                    defaultValue: "0"

                                },
                                busDomainId: {
                                    title: 'Application',
                                    type: 'combobox',
                                    options: '/mdrest/busdomain/options/',
                                    defaultValue: "1"
                                },
                                permissionTypeByUserAccessId: {
                                    title: 'User Access',
                                    type: 'combobox',
                                    list: false,
                                    options: '/mdrest/process/options/',
                                    defaultValue: "7"
                                },
                                permissionTypeByGroupAccessId: {
                                  title: 'Group Access',
                                  type: 'combobox',
                                  list: true,
                                  options: '/mdrest/process/options/',
                                  defaultValue: "6"
                               },
                               permissionTypeByOthersAccessId: {
                                  title: 'Other Access',
                                  type: 'combobox',
                                  list: false,
                                  options: '/mdrest/process/options/',
                                  defaultValue: "0"
                               },
                               ownerRoleId: {
                                 title: 'Owner Group',
                                 type: 'combobox',
                                 list:true,
                                 options: '/mdrest/userroles/options/',
                              },
                              userName: {
                                       title: 'Username',

                                    },
                                processTypeId: {
                                    title: 'Type',
                                    type: 'combobox',
                                    options: '/mdrest/processtype/optionslist',
                                    defaultValue: "1",
                                },
                                processTemplateId: {
                                    type: 'hidden',
                                    defaultValue: null,
                                },
                                ProcessPipelineButton: {
                                    title: 'Pipeline',
                                    sorting: false,
                                    width: '2%',
                                    listClass: 'bdre-jtable-button',
                                    create: false,
                                    edit: false,
                                    display: function(data) {
                                        return '<span class="label label-primary" onclick="fetchPipelineInfo(' + data.record.processId + ')">Display</span> ';
                                    },
                                },
                                DataLineageButton: {
                                    title: 'Data Lineage',
                                    sorting: false,
                                    width: '2%',
                                    listClass: 'bdre-jtable-button',
                                    create: false,
                                    edit: false,
                                    display: function(data) {
                                        return '<span class="label label-primary" onclick="fetchBatchLineageInfo(' + data.record.processId + ')">Column Lineage</span> ';
                                    },
                                },
                                DeployProcess: {                    
                                    width: '5%',
                                    sorting: false,
                                    edit: false,
                                    create: false,
                                    title: "Deploy Job",

                                },
                                  
                                RunProcess: {                    
                                	width: '5%',
                                	sorting: false,
                                	edit: false,
                                	create: false,
                                	title: "Run Job",
                                	display: function(data) {
                                		var $img2 = $('<span title="Execute the process." class="label label-danger" >Execute</span>');
                                		$img2.click(function() {
                                			console.log(data);
                                			$("#execute-dialog-confirm").dialog({
                                				resizable: false,
                                				height: 'auto',
                                				modal: true,
                                				buttons: {
                                					"Yes Execute": function() {
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
                                										}).html("Process <b>" +data.Record.processId +"</b> successfully launched from Edge node with OS process id: <b>" + data.Record.osprocessId + "</b>");
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
                                										}).html("Process failed to launch.");;
                                									}}
                                								},
                                								error: function() {
                                									$dfd.reject();
                                								}
                                
                                							});
                                						});
                                					},
                                					Cancel: function() {
                                						$(this).dialog("close");
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
                                                                
                                        var $img = $('<span class="label label-primary">Show</span>');                      //Open child table when user clicks the image
                                                                
                                        $img.click(function() {                            
                                            $('#Container').jtable('openChildTable',                                     
                                                $img.closest('tr'),                                      {                                        
                                                    title: ' Executions of ' + item.record.processName,
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
                                                            title: 'Id'
                                                        },

                                                        processId: {
                                                            title: 'Process ID'
                                                        },
                                                        LineageButton: {
                                                            sorting: false,
                                                            width: '5%',
                                                            title: 'Batch Lineage',
                                                            create: false,
                                                            edit: false,
                                                            display: function(data) {
                                                                if (data.record.execState === 2) {
                                                                    return '<span class="label label-warning" onclick="fetchLineageInfo(' + data.record.instanceExecId + ')">Display</span> ';
                                                                } else if (data.record.execState === 3) {
                                                                    return '<span class="label label-success" onclick="fetchLineageInfo(' + data.record.instanceExecId + ')">Display</span> ';;
                                                                } else if (data.record.execState === 6) {
                                                                    return '<span class="label label-danger" onclick="fetchLineageInfo(' + data.record.instanceExecId + ')">Display</span> ';;
                                                                } else {
                                                                    return '<span class="label label-info">Failed</span> ';
                                                                }
                                                            },
                                                        },
                                                        tableStartTs: {
                                                            title: 'Start Time',
                                                        },
                                                        tableEndTs: {
                                                            title: 'End Time',
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
                                workflowId: {
                                    title: 'Workflow Type',
                                    type: 'combobox',
                                    options: '/mdrest/workflowtype/optionslist',
                                    defaultValue: "1"
                                },
								Export: {
                                    title: 'Export',
                                    width: '10%',
                                    sorting: false,
                                    create: false,
                                    edit: false,
                                    display: function(data) {

                                     return '<span class="label label-primary" onclick="goToExportPage(' + data.record.processId + ')">Export</span> ';
                                     },

                                },


                                SLAMonitoring: {
                                    title: 'SLA Monitoring',
                                    width: '10%',
                                    sorting: false,
                                    create: false,
                                    edit: false,
                                    display: function(data) {

                                     return '<span class="label label-primary" onclick="goToSLAMonitoringPage(' + data.record.processId + ')">SLA Monitoring</span> ';
                                     },

                                },
                                EditGraphically: {
                                    title: 'Edit Graphically',
                                    sorting: false,
                                    width: '2%',
                                    listClass: 'bdre-jtable-button',
                                    create: false,
                                    edit: false,
                                    display: function(data) {
                                        return '<span class="label label-primary" onclick="goToEditGraphically(' + data.record.processId + ')">Edit Graphically</span> ';
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
                                "Yes Deploy": function() {
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

                                },
                                Cancel: function() {
                                    $(this).dialog("close");
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
                                  location.href = '<c:url value="/pages/lineage.page?pid="/>' + pid;
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

                    function fetchBatchLineageInfo(pid) {
                                      $.ajax({
                                                url: '/mdrest/process/permission/'+pid,
                                                type: 'PUT',
                                                dataType: 'json',
                                                 success: function(data) {
                                                    if(data.Result == "OK") {
                                                    location.href = '<c:url value="/pages/columnlineage.page?pid="/>' + pid;
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

                     function goToEditGraphically(pid) {
                                      $.ajax({
                                               url: '/mdrest/process/permission/'+pid,
                                               type: 'PUT',
                                               dataType: 'json',
                                                success: function(data) {
                                                   if(data.Result == "OK") {
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
                	<span class="filter-icon"></span><span class="filter-text">Filter By Process</span>
                </div>
                <div id="input-box-button" class="input-box-button">
                    <form onsubmit="showProcessPage(jQuery('#pid').val()); return false;">
                        <div class="input-group">
                            <input class="form-control form-control-process" type="number" name="pid" id="pid" value="" placeholder=<spring:message code="process.page.pid_placeholder"/> />
                            <!-- <button  class="btn btn-default btn-lg btn-primary"><span id="sizing-addon2"><span class="glyphicon glyphicon-arrow-right" aria-hidden="true"></span> Show Lineage </button> -->
                            <span class="input-group-btn">
		    <button class="btn btn-default  btn-primary-process" type="submit" onClick="showProcessPage(jQuery('#pid').val())"><span id="sizing-addon2"><span class="glyphicon glyphicon-arrow-right" aria-hidden="true"></span>&nbsp;</button>
                            </span>
                        </div>
                    </form>
                </div>
				<div id="dialog-confirm" style="display:none;">
                    <span class="ui-icon-alert-custom"></span><div class="dialog-title-custom">Are you sure?</div><p>This will build the workflow for this process and deploy necessary codes in cluster. Existing workflow may be replaced.</p>
                </div>
                <div id="execute-dialog-confirm" title="Are you sure?" style="display:none;">
                    <span class="ui-icon-alert-custom"></span><div class="dialog-title-custom">Are you sure?</div><p>This will start the execution of process in cluster.</p>
                </div>
                <div id="execute-result" title="Process Started" style="display:none;">
                    <p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>Process Started Successfully</p>
                </div>
                <div id="execute-fail" title="Process Failed" style="display:none;">
                    <p><span class="ui-icon ui-icon-warning" style="float:left; margin:0 7px 20px 0;"></span>Process Initiation Failed</p>
                </div>
                <div id="process-not-found" title="Process Not Found" style="display:none;">
                    <p><span class="ui-icon ui-icon-warning" style="float:left; margin:0 7px 20px 0;"></span>Process Not Found</p>
                </div>
                <div id="dialog-form" title="Are you sure?" style="display:none;">
                    <p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>This will export process and related properties.</p>
                </div>
			</body>

            </html>
