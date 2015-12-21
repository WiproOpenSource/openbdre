
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="security"
	   uri="http://www.springframework.org/security/tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html >
	<head >
		<meta http-equiv = "Content-Type" content = "text/html; charset=UTF-8" >
		<script src = "../js/jquery.min.js" ></script >
		<link href = "../css/jquery-ui-1.10.3.custom.css" rel = "stylesheet" >
		<link href = "../css/css/bootstrap.min.css" rel = "stylesheet" />
		<script src = "../js/jquery-ui-1.10.3.custom.js" ></script >
		<script src = "../js/jquery.steps.min.js" ></script >
		<link rel = "stylesheet" href = "../css/jquery.steps.css" />
		<script src = "../js/jquery.fancytree.js" ></script >
		<link rel = "stylesheet" href = "../css/ui.fancytree.css" />
		<script src = "../js/jquery.fancytree.gridnav.js" type = "text/javascript" ></script >
		<script src = "../js/jquery.fancytree.table.js" type = "text/javascript" ></script >
		<script src = "../js/jquery.jtable.js" type = "text/javascript" ></script >
		<link href = "../css/jtables-bdre.css" rel = "stylesheet" type = "text/css" />
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
		        if(jsonObject == null)
		        {
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
                    }).text("Please Validate JSON first to continue");
                    return false;
		        }
		        var json=document.getElementById("jsonTextArea").value;
		        finalJson=JSON.parse(json).Record;

		        return true;
		    },
		    onStepChanged: function (event, currentIndex, priorIndex){
		        console.log(currentIndex+" "+priorIndex);
		        if(currentIndex == 1 && priorIndex==0 )
		        {

		        	console.log(finalJson);
		        	loadProcessTable(finalJson);
		        	loadPropertiesTable(finalJson);
		        	document.getElementById("bdre-dataload-p-2").style.overflow = "hidden";
		        }
		    },
		    onFinished: function (event, currentIndex) {
                console.log(finalJson.processList[0].processId);
                location.href = '<c:url value="/pages/process.page?pid="/>' + finalJson.processList[0].processId;
                             }
			});

		});

		</script>
 <script type="text/javascript">
         var jsonObject;
         var jsonText;
         function verifyIfJson() {

     jsonText=document.getElementById("jsonTextArea").value;
     try {
             jsonObject=JSON.parse(jsonText).Record;
             if (typeof jsonObject !== "undefined" || typeof jsonObject !== "undefined" )
             alert('Valid Json! Click next');
             else{
             alert('Json does not have Process and Properties data!');
             jsonObject=null;}
              } catch (e) {
             alert('Malformed json!');
              }

           }

        </script>
       <script type="text/javascript">
          function loadProcessTable(finalJson) {
               $('#ProcessTableContainer').jtable({
               title: 'Process List',
               paging: false,
               sorting: false,
               create: false,
               edit :false,
               actions: {
               listAction: function (postData, jtParams) {
                   return {
                   "Result": "OK",
                   "Records": finalJson.processList,
                   "TotalRecordCount": finalJson.processList.length
                   };
               }

               },
               fields: {
                   processId: {
                      key: true,
                      list: true,
                      create: false,
                      edit: false,
                      title: 'Id'
                  },
                  processName: {
                      title: 'Name'
                  },
                  tableAddTS: {
                      title: 'Add TS',
                      create: false,
                      edit: true,

                      type:'hidden'
                  },
                  description: {
                      title: 'Description',
                  },

                  parentProcessId: {
                      title: 'Parent',
                      edit: true,
                      create: false,


                  },
                  canRecover: {
                      title: 'Restorable',


                      edit: true,
                  },
                  nextProcessIds: {
                      title: 'Next',

                         edit: true,


                  },
                  enqProcessId: {
                      title: 'Enqueuer',

                      edit:true,

                  },
                  busDomainId: {
                      title: 'Application',

                      edit:true,
                      type: 'combobox',
                      options: '/mdrest/busdomain/options/',
                  },
                  processTypeId: {
                      title: 'Type',
                      edit: true,

                  },
                  batchPattern: {
                              title: 'Batch Mark',

                              create: false,
                              edit: true,


                                                                                                      },

                   workflowId: {
                   title: 'workflow type'}
                   }
               });

               $('#ProcessTableContainer').jtable('load');
           };
       </script>
         <br />
       <script type="text/javascript">
                 function loadPropertiesTable(finalJson) {
                      $('#PropertiesTableContainer').jtable({
                        title: 'Properties List',
                        paging: false,
                        sorting: false,
                        create: false,
                        edit :false,
                      actions: {
                      listAction: function (postData, jtParams) {
                          return {
                          "Result": "OK",
                          "Records": finalJson.propertiesList,
                          "TotalRecordCount": finalJson.propertiesList.length
                          };
                      }

                      },
                      fields: {
                          processId: {
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

                      $('#PropertiesTableContainer').jtable('load');
                  };
              </script>

              <script>
              function ImportFromJson(){
             var jsonText=document.getElementById("jsonTextArea").value;
              var fileString=JSON.stringify(JSON.parse(jsonText).Record);

              				$.ajax({
                  		    url: "/mdrest/process/import",
                  		    type: "POST",
                  		    data: {'fileString': fileString},
                  		    success: function (getData) {
                  		    if( getData.Result =="OK" ){
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
                                 }).text("Data successfully imported");
                                  console.log(getData);
                                  displayProcess(getData);
                                  displayProperties(getData);
                                         }


                  		    if(getData.Result =="ERROR")
                  		    alert(getData.Message);
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
                        list: true,
                        create: false,

                        title: 'Id'
                        },
                        processName: {
                        title: 'Name'
                        },
                        tableAddTS: {
                        title: 'Add TS',
                        create: false,

                        list: false,
                        type:'hidden'
                        },
                        description: {
                        title: 'Description',
                        },
                        batchPattern: {
                        title: 'Batch Mark',
                        list: false,
                        create: false,

                        type:'hidden'

                        },
                        parentProcessId: {
                        title: 'Parent',

                        create: false,


                        },
                        canRecover: {
                        title: 'Restorable',

                        list: false,

                        },
                        nextProcessIds: {
                        title: 'Next',




                        },
                        enqProcessId: {
                        title: 'Enqueuer',



                        },
                        busDomainId: {
                        title: 'Application',


                        type: 'combobox',
                        options: '/mdrest/busdomain/options/',
                        },
                        processTypeId: {
                        title: 'Type',
                        },

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
				<div id = "bdre-dataload" ng-controller = "myCtrl" >
				<h3 >Json Input</h3 >
				<section >
					<div >
					<textarea  placeholder="Please paste your Json here" rows="13.7" cols="130"  id="jsonTextArea" wrap="soft"></textarea>
					<div ><br /></div >
					<button class = "btn btn-default  btn-success" type = "button" onClick = "verifyIfJson()" href = "#" >
					Validate JSON
					</button >
					</div >
				</section >

                <h3 >Verify table data</h3 >
				<section style = "display: block; overflow: auto;" >
				<div class = "alert alert-success" role = "alert" >Click on next if the data seems okay</div >
				<div id="ProcessTableContainer"> </div>
				<br />
				<div id="PropertiesTableContainer"> </div>
				<br />
				</section >

				<h3 >Import data</h3 >
				<section style = "display: block; overflow: auto;" >
				<div class = "alert alert-success" role = "alert" >Click on the button to import the process and properties data. This may update or delete relevant data in the current environment as per the json.
                </div >
				<button class = "btn btn-warning  btn-success" type = "button" onClick = "ImportFromJson()" href = "#" >
					Import JSON Data
				</button >
				<br />
				</section >
                <h3 >Imported Details</h3 >
                <section>
                <div class = "alert alert-success" role = "alert" >Click on the finish button to view more details about the imported process
                </div >
                <div id = "ProcessContainer" >
                </div >
                <br />
                <br />
                <div id = "PropertiesContainer" >
                </div >
                </section>
				</div>
				<div style = "display:none" id = "div-dialog-warning" >
				<p ><span class = "ui-icon ui-icon-alert" style = "float:left;" ></span >
				<div />
				</p>
				</div >


</head>
</html>