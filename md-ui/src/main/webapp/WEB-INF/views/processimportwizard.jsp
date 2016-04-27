
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="security"
	   uri="http://www.springframework.org/security/tags" %>
	   <%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html >
	<head >
		<meta http-equiv = "Content-Type" content = "text/html; charset=UTF-8" >
	<script>
	  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
	  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
	  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
	  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');
	  //Please replace with your own analytics id
	  ga('create', 'UA-72345517-1', 'auto');
	  ga('send', 'pageview');
	</script>
          <script src="../js/angular.min.js" type="text/javascript"></script>
		<script src = "../js/jquery.min.js" ></script >
		<script src="../js/fc/app.js" type="text/javascript"></script>
		 <!--Ajax calls Code. -->
        <script type="text/javascript" src="../js/fc/wfd-ac.js"></script>

		<link href = "../css/jquery-ui-1.10.3.custom.css" rel = "stylesheet" >
		<link href = "../css/css/bootstrap.min.css" rel = "stylesheet" />
		<link href="../css/bootstrap.custom.css" rel="stylesheet" type="text/css" />
		<script src = "../js/jquery-ui-1.10.3.custom.js" ></script >
		<script src = "../js/jquery.steps.min.js" ></script >
		<link rel = "stylesheet" href = "../css/jquery.steps.css" />
		<link rel="stylesheet" href="../css/jquery.steps.custom.css" />
		<script src = "../js/jquery.fancytree.js" ></script >
		<link rel = "stylesheet" href = "../css/ui.fancytree.css" />
		<script src = "../js/jquery.fancytree.gridnav.js" type = "text/javascript" ></script >
		<script src = "../js/jquery.fancytree.table.js" type = "text/javascript" ></script >
		<script src = "../js/jquery.jtable.js" type = "text/javascript" ></script >
		<link href = "../css/jtables-bdre.css" rel = "stylesheet" type = "text/css" />
		<style>
		#bdre-dataload.wizard > .content{
			margin: 0 !important;
		}
		</style>
		<script >
                function fetchPipelineInfo(pid){
        			location.href = '<c:url value="/pages/lineage.page?pid="/>' + pid;
                }
                        </script >
		<script >
        var wizard=null;
        var finalJson;
		wizard=$(document).ready(function() {
			$("#bdre-dataload").steps({
		    headerTag: "h3",
		    bodyTag: "section",
		    transitionEffect: "slide",
		    onStepChanging: function (event, currentIndex, newIndex)
		    {
		             return true;

		    },
		    onStepChanged: function (event, currentIndex, priorIndex){
		        console.log(currentIndex+" "+priorIndex);
		        if(currentIndex == 1 && priorIndex==0 )
		        {

		        	console.log(finalJson);
		        	displayProcess(finalJson);
		        }
		    },
		    onFinished: function (event, currentIndex) {
                console.log(finalJson.processList[0].processId);
                location.href = '<c:url value="/pages/process.page?pid="/>' + finalJson.processList[0].processId;
                             }
			});

		});

		</script>


              <script>
              function ImportFromJson(){
                  //var jsonText=document.getElementById("jsonTextArea").value;
                        var fileString=uploadedFileName;

              				$.ajax({
                  		    url: "/mdrest/process/import",
                  		    type: "POST",
                  		    data: {'fileString': fileString},
                  		    success: function (getData) {
                  		        if( getData.Result =="OK" ){
                  		            finalJson=getData;
                                    console.log(getData);
                                    $("#div-dialog-warning").dialog({
                                                 title: "",
                                                 resizable: false,
                                                 height: 'auto',
                                                 modal: true,
                                                 buttons: {
                                                     "Ok" : function () {
                                                         $(this).dialog("close");
                                                     }
                                                 }
                                    }).text("Process and Properties inserted successfully");
                                    return false;
                                }
                  		        if(getData.Result =="ERROR"){
                  		            $("#div-dialog-warning").dialog({
                                             title: "",
                                             resizable: false,
                                             height: 'auto',
                                             modal: true,
                                             buttons: {
                                                 "Ok" : function () {
                                                     $(this).dialog("close");
                                                 }
                                             }
                                    }).text("Error in importing"+getData.Message);
                                    return false;

                                }
                            }
                  		});
              }
              </script>
              <script>
                var uploadedFileName ="";
              function uploadZip (subDir,fileId){
             var arg= [subDir,fileId];
               var fd = new FormData();
              		                var fileObj = $("#"+arg[1])[0].files[0];
                                      var fileName=fileObj.name;
                                      fd.append("file", fileObj);
                                      fd.append("name", fileName);
                                      $.ajax({
                                        url: '/mdrest/filehandler/uploadzip/'+arg[0],
                                        type: "POST",
                                        data: fd,
                                        async: false,
                                        enctype: 'multipart/form-data',
                                        processData: false,  // tell jQuery not to process the data
                                        contentType: false,  // tell jQuery not to set contentType
                                        success:function (data) {
                                              uploadedFileName=data.Record.fileName;
                                              console.log( data );
                                              $("#div-dialog-warning").dialog({
                                                              title: "",
                                                              resizable: false,
                                                              height: 'auto',
                                                              modal: true,
                                                              buttons: {
                                                                  "Ok" : function () {
                                                                      $(this).dialog("close");
                                                                  }
                                                              }
                                              }).text("File Uploaded successfully " + uploadedFileName);
                                              return false;
              							},
              						  error: function () {
              							    $("#div-dialog-warning").dialog({
                                                          title: "",
                                                          resizable: false,
                                                          height: 'auto',
                                                          modal: true,
                                                          buttons: {
                                                              "Ok" : function () {
                                                                  $(this).dialog("close");
                                                              }
                                                          }
                                          }).text("File Upload failed ");
                                          return false;
              							}
              						 });

              }



              </script>
              <script>
              function displayProcess (records){
                        console.log(records);
                        $('#ProcessContainer').jtable(
                        {
                        title: 'Imported Processes',
                        paging: false,
                        sorting: false,
                        create: false,
                        edit :false,
                        actions: {
                        listAction: function (postData, jtParams) {
                        return {
                        "Result": "OK",
                        "Records": records.Record.processList,
                        "TotalRecordCount": records.Record.processList.length
                        };
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

                                                                                        var $img = $('<span class="label label-primary">Show</span>'); //Open child table when user clicks the image

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
                                                                                                                        $dfd.resolve(data);
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
                                                                                                                    url: '/mdrest/properties',
                                                                                                                    type: 'POST',
                                                                                                                    data: postData + '&processId=' + item.record.processId,
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
                                                                                                                    url: '/mdrest/properties',
                                                                                                                    type: 'PUT',
                                                                                                                    data: postData + '&processId=' + item.record.processId,
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
                                                                                                list: false,
                                                                                                type: 'hidden'
                                                                                            },
                                                                                            description: {
                                                                                                title: 'Description',
                                                                                            },
                                                                                            batchPattern: {
                                                                                                title: 'Batch Mark',
                                                                                                list: false,
                                                                                                create: false,
                                                                                                edit: true,
                                                                                                type: 'hidden'

                                                                                            },
                                                                                            parentProcessId: {
                                                                                                title: 'Parent',
                                                                                                edit: true,
                                                                                                create: false,
                                                                                                list: false,
                                                                                                type: 'hidden'
                                                                                            },
                                                                                            canRecover: {
                                                                                                title: 'Restorable',
                                                                                                type: 'hidden',
                                                                                                list: false,
                                                                                                edit: true,
                                                                                            },
                                                                                            nextProcessIds: {
                                                                                                title: 'Next',
                                                                                                list: false,
                                                                                                edit: true,
                                                                                                type: 'hidden'

                                                                                            },
                                                                                            enqProcessId: {
                                                                                                title: 'Enqueuer',
                                                                                                list: false,
                                                                                                edit: true,
                                                                                                type: 'hidden',
                                                                                            },
                                                                                            busDomainId: {
                                                                                                title: 'Application',
                                                                                                list: false,
                                                                                                edit: true,
                                                                                                type: 'combobox',
                                                                                                options: '/mdrest/busdomain/options/',
                                                                                            },
                                                                                            processTypeId: {
                                                                                                title: 'Type',
                                                                                                edit: true,
                                                                                                type: 'hidden',
                                                                                                options: '/mdrest/processtype/optionslist'

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
                                                                                            }
                                                                                }
                        });
                        $('#ProcessContainer').jtable('load');

                       }
                       </script>
                       <script>
                       function displayProperties (records){
                             console.log(records);
                             $('#PropertiesContainer').jtable(
                             {
                                 title: 'Imported Properties',
                                 paging: false,
                                 sorting: false,
                                 create: false,
                                 edit :true,
                                 actions: {
                                     listAction: function (postData, jtParams) {
                                                   return {
                                                   "Result": "OK",
                                                   "Records": records.Record.propertiesList,
                                                   "TotalRecordCount": records.Record.propertiesList.length
                                                   };
                                                         }
                                      },

                             fields: {
                            processId:{
                            title: 'process id'},
                            configGroup: {
                            title: 'configGroup'},
                            key: {
                            title: 'key'},
                            value: {
                            title: 'value'},
                            description: {
                            title: 'description'}
                                    }

                                 });
                             $('#PropertiesContainer').jtable('load');

                                }
                       </script>

				<div class="page-header"><spring:message code="processimportwizard.page.zip_file_upload"/></div>
				<div id = "bdre-dataload" ng-controller = "myCtrl" >
				<h3 ><div class="number-circular">1</div><spring:message code="processimportwizard.page.zip_file_upload"/></h3 >
				<section >
					<div class="col-sm-2">
					<input type="file" name="file" class="form-control" id="zip-id" required>
					<div ><br /></div >
                     <button type="button" class="btn btn-sm btn-primary pull-left" onClick="uploadZip('zip','zip-id')">Upload ZIP</button>
					</div >
					<div>
					 <button class = "btn btn-warning  btn-success" type = "button" onClick = "ImportFromJson()" href = "#" >
                                         					Import
                                         				</button >
					</div>
				</section >

                <h3 ><div class="number-circular">2</div><spring:message code="processimportwizard.page.imported_details"/></h3 >
                <section>
                <div id = "ProcessContainer" >
                </div >
                </section>
                <div style = "display:none" id = "div-dialog-warning" >
                				<p ><span class = "ui-icon ui-icon-alert" style = "float:left;" ></span >
                				<div />
                				</p>
                				</div >


				</div>


</head>
</html>