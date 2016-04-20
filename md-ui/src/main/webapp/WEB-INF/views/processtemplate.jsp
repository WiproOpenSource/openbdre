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
		    $(document).ready(function() {
	    $('#Container').jtable({
	    title: 'Process Template List',
		    paging: true,
		    pageSize: 10,
		    sorting: false,
		    openChildAsAccordion: true,
		    actions: {
		    listAction: function(postData, jtParams) {
		    console.log(postData);
			    return $.Deferred(function($dfd) {
			    $.ajax({
	    <c:if test="${param.pid==null}">
			    url: '/mdrest/processtemplate?page=' + jtParams.jtStartIndex + '&size='+jtParams.jtPageSize,
	    </c:if>
	    <c:if test="${param.pid!=null}">
			    url: '/mdrest/processtemplate?pid=${param.pid}&page=' + jtParams.jtStartIndex + '&size='+jtParams.jtPageSize,
	    </c:if>
			    type: 'GET',
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
	    <security:authorize access="hasAnyRole('ROLE_ADMIN','ROLE_USER')">
		    createAction: function(postData) {
		    console.log(postData);
			    return $.Deferred(function($dfd) {
			    $.ajax({
			    url: '/mdrest/processtemplate',
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
				    url: '/mdrest/processtemplate',
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
			    deleteAction: function(item) {
			    console.log(item);
				    return $.Deferred(function($dfd) {
				    $.ajax({
				    url: '/mdrest/processtemplate/' + item.processTemplateId,
					    type: 'DELETE',
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
	    </security:authorize>

		    },
		    fields: {
SubProcesses: {                    
		    width: '5%',
			    sorting: false,
			    edit: false,
			    create: false,
			    listClass: 'bdre-jtable-button',
			        display: function(item) {                         //Create an image that will be used to open child table
                                                        
			    var $img = $('<img src="../css/images/three-bar.png" title="Sub processes info" />');                         //Open child table when user clicks the image
                                                        
				    $img.click(function() {                            
				    $('#Container').jtable('openChildTable',                                     
					    $img.closest('tr'),                                      {                                        
				    title: ' Sub process Templates of ' + item.record.processTemplateId,
					        actions: {                                        
					    listAction: function(postData) {
					    return $.Deferred(function($dfd) {
					    console.log(item);
						    $.ajax({
						    url: '/mdrest/subprocesstemplate/' + item.record.processTemplateId,
							    type: 'GET',
							    data: item,
							    dataType: 'json',
							    success: function(data) {
							    $dfd.resolve(data);
							    },
							    error: function() {
							    $dfd.reject();
							    }
						    }); ;
					    });
					    },
						    deleteAction: function(postData) {
						    console.log(postData.processTemplateId);
							    return $.Deferred(function($dfd) {
							    $.ajax({
							    url: '/mdrest/subprocesstemplate/' + postData.processTemplateId,
								    type: 'DELETE',
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
						    },
						    updateAction: function(postData) {
						    console.log(postData);
							    return $.Deferred(function($dfd) {
							    $.ajax({
							    url: '/mdrest/subprocesstemplate',
								    type: 'POST',
								    data: postData,
								    dataType: 'json',
								    success: function(data) {
								    console.log(data);
									    $dfd.resolve(data);
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
							    url: '/mdrest/subprocesstemplate',
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
						    }
					    },
					    fields: {

					    processTemplateId: {
					    key: true,
						    width: '5%',
						    list: true,
						    create: false,
						    edit: false,
						    title: 'Id'
					    },
						    processName: {
						    title: 'Name',
							    defaultValue: 'Child of ' + item.record.processTemplateId,
						    },
						    description: {
						    title: 'Description',
							    defaultValue: 'A Child of ' + item.record.processTemplateId

						    },
						    tableAddTS:  {
						    title: 'Add TS',
							    list:true,
							    create: false,
							    edit: true
						    },
						    batchPattern: {
						    title: 'Batch Mark'

						    },
						    parentProcessId: {
						    type: 'hidden',
							    defaultValue: item.record.processTemplateId,
						    },
						    canRecover: {
						    title: 'Restorability',
							    edit: true,
							    type: 'combobox',
							    options: { '1': 'Restorable', '0': 'Non-Restorable'},
							    defaultValue: "1"
						    },
						    nextProcessTemplateId: {
								title: 'Next'

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
					    }
				    },
					    function(data) { //opened handler
                                                                                    
					    data.childTable.jtable('load');                                    
					    });                        

				    });                         //Return image to show on the person row
                                                        
				    return $img;                    
			    }                
		    },


			    processTemplateId: {
			    key: true,
				    list: true,
				    create: false,
				    edit: false,
				    title: 'Id'
			    },
				Properties: {
				title: 'Properties Template',
				width: '5%',
				sorting: false,
				edit: false,
				create: false,
				listClass: 'bdre-jtable-button',
				display: function(item) {                         //Create an image that will be used to open child table

				var $img = $('<span class="label label-primary">Show</span>'); //Open child table when user clicks the image

				$img.click(function() {
				$('#Container').jtable('openChildTable',
				$img.closest('tr'), {
				title: ' Properties of ' + item.record.processTemplateId,
				paging: true,
				pageSize: 10,
				actions: {
				listAction: function(postData) {
				return $.Deferred(function($dfd) {
				console.log(item);
				$.ajax({
				url: '/mdrest/propertiestemplate/' + item.record.processTemplateId,
				type: 'GET',
				data: item,
				dataType: 'json',
				success: function(data) {
				$dfd.resolve(data);
				},
				error: function() {
				$dfd.reject();
				}
				}); ;
				});
				},
				deleteAction: function(postData) {
				console.log(postData.processTemplateId);
				return $.Deferred(function($dfd) {
				$.ajax({
				url: '/mdrest/propertiestemplate/' + item.record.processTemplateId + '/' + postData.key,
					type: 'DELETE',
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
				},
				updateAction: function(postData) {
				console.log(postData);
				return $.Deferred(function($dfd) {
				$.ajax({
				url: '/mdrest/propertiestemplate',
					type: 'POST',
					data: postData + '&processTemplateId=' + item.record.processTemplateId,
					dataType: 'json',
					success: function(data) {
					console.log(data);
						$dfd.resolve(data);
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
				url: '/mdrest/propertiestemplate',
					type: 'PUT',
					data: postData + '&processTemplateId=' + item.record.processTemplateId,
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
				fields: {

				processTemplateId: {
				key : true,
				list: false,
				create:false,
				edit: true,
				title: 'Process Template',
				defaultValue: item.record.processTemplateId,
				},
				configGroup: {
				title: 'Config Group',
				defaultValue: item.record.configGroup,
				},
				key: {
				title: 'Key',
				key : true,
				list: true,
				create:true,
				edit:false,
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
			    nextProcessTemplateId: {
					title: 'Next'

					},
			    busDomainId: {
			    title: 'Application',
				    type: 'combobox',
				    options: '/mdrest/busdomain/options/',
				    defaultValue: "1"
			    },
			    processTypeId: {
			    title: 'Type',
				    type: 'combobox',
				    options: '/mdrest/processtype/optionslist',
				    defaultValue: "1",
			    },
			    workflowId: {
			    title: 'Workflow Type',
				    type: 'combobox',
				    options: '/mdrest/workflowtype/optionslist',
				    defaultValue: "1",
			    },
			    Create: {
                			    title: 'Create',
                				    width: '10%',
                				    sorting: false,
                				    create: false,
                				    edit: false,
                				    display: function (item) {      //Create an image that will be used to open child table
                				    var $img1 = $('<span class="label label-primary">Create</span>');                         //Open child table when user clicks the image
                					    $img1.click(function () {
                					    $("#dialog-form").dialog({
                					    resizable: false,
                						    height:'auto',
                						    modal: true,
                						    buttons: {
                						    "Create from template": function() {
                						    processName = document.getElementsByName("processName")[0].value;
                                            description = document.getElementsByName("description")[0].value;
                                            console.log(processName);
                                            console.log(description);

                						    $.ajax({
                						    url: '/mdrest/processtemplate/create/',
                							    type: 'PUT',
                							    data: item+'&busDomainId='+item.record.busDomainId+ '&processTypeId='+item.record.processTypeId+'&processName='+processName+'&canRecover='+item.record.canRecover+'&description='+description+'&processTemplateId='+item.record.processTemplateId,
                							    dataType: 'json',
                							    success: function (data) {
                							    console.log(data);
                							    console.log(item);
                							    alert('Created successfully!');
                							    },
                							    error: function () {
                							    alert('Error posting');
                							    }
                						    });
                							    $(this).dialog("close");
                						    },
                							    Cancel: function() {
                							    $(this).dialog("close");
                							    }
                						    }
                					    });
                					    });
                					    return $img1;
                				    }
                			    },
			Apply: {
				title: 'Apply',
					width: '10%',
					sorting: false,
					create: false,
					edit: false,
					display: function (item) {      //Create an image that will be used to open child table
					var $img1 = $('<span class="label label-primary">Apply</span>');                         //Open child table when user clicks the image
						$img1.click(function () {
						$("#dialog-confirm2").dialog({
						resizable: false,
							height:'auto',
							modal: true,
							buttons: {
							"Apply changes": function() {
							$.ajax({
							url: '/mdrest/processtemplate/apply/',
								type: 'POST',
								data: item+'&busDomainId='+item.record.busDomainId+ '&processTypeId='+item.record.processTypeId+'&processName='+item.record.processName+'&canRecover='+item.record.canRecover+'&description='+item.record.description+'&processTemplateId='+item.record.processTemplateId,
								dataType: 'json',
								success: function (data) {
								alert('Changes applied successfully!');
								console.log(data);
								console.log(item);
								},
								error: function () {
								alert('Error in apply');
								}
							});
								$(this).dialog("close");
							},
								Cancel: function() {
								$(this).dialog("close");
								}
							}
						});
						});
						return $img1;
					}
				}
		    },
		    selectionChanged: function() {
		    //Get all selected rows
		    var $selectedRows = $('#Container').jtable('selectedRows');
			    //    $selectedRows.each(function() {
			    //        var record = $(this).data('record');
			    //        alert('ProcessTemplate id: ' + record.processTemplateId);
			    //    });
		    }
	    });
		    $('#Container').jtable('load');
	    });</script>

	<script>

	    function showProcessTemplatePage(pid){
	    console.log('entered function');
		    console.log(${param.pid == null });
		    location.href = '<c:url value="/pages/processtemplate.page?pid="/>' + pid;
	    }

	</script>
    </head>

    <body>

    <section style="width:100%;text-align:center;">
	<div id="Container"></div>
    </section>
    <div id="input-box-button" >
	<form onsubmit="showProcessTemplatePage(jQuery('#pid').val()); return false;">
	    <div class="input-group">
		<input class="form-control" type="number" name="pid" id="pid" value ="" placeholder=<spring:message code="processtemplate.page.filter_by_processtemplateid_placeholder"/>/>
		<span class="input-group-btn">
		    <button class="btn btn-default  btn-primary" type="submit" onClick="showProcessTemplatePage(jQuery('#pid').val())"><span id="sizing-addon2"><span class="glyphicon glyphicon-arrow-right" aria-hidden="true"></span>&nbsp;</button>
		</span>
	    </div>
	</form>
    </div>
    <div id="dialog-confirm1" title="Are you sure?" style="display:none;">
    <p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span><spring:message code="processtemplate.page.filter_by_new_process_placeholder"/></p>
    </div>
    <div id="dialog-confirm2" title="Are you sure?" style="display:none;">
	<p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span><spring:message code="processtemplate.page.filter_by_edit_process_placeholder"/></p>
	</div>
	  <div id="dialog-form" style="display:none;">
    	<form>
    	<div class="jtable-input-label"><spring:message code="processtemplate.page.process_name"/></div>
		<div class="jtable-input jtable-text-input"><input class="" id="Edit-processName" type="text" name="processName" value=""/>
		</div>

		<div class="jtable-input-label"><spring:message code="processtemplate.page.process_description"/></div>
		<div class="jtable-input jtable-text-input"><input class="" id="Edit-processDescription" type="text" name="description" value=""/>
		</div>
    </form>
     </div>
</body>

</html>