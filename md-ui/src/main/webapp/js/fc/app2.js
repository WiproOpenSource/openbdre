//
// Define the 'app' module.
//

 function closeBroadcast()
 {
 document.getElementById('broadcast').style.display = "none";
 }

angular.module('app', ['flowChart', ])
    //
    // Simple service to create a prompt.
    //
    .factory('prompt', function() {
        /* Uncomment the following to test that the prompt service is working as expected.
        return function () {
        	return "Test!"
        }
        */
        // Return the browsers prompt function.
        return prompt;
    })
    //
    // Application controller.
    //
    .controller('AppCtrl', ['$scope', 'prompt', function AppCtrl($scope, prompt) {
        //
        // Code for the delete key.
        //
        var deleteKeyCode = 46;
        //
        // Code for control key.
        //
        var ctrlKeyCode = 17;
        //
        // Set to true when the ctrl key is down.
        //
        var ctrlDown = false;
        //
        // Code for A key.
        //
        var aKeyCode = 65;
        //
        // Code for esc key.
        //
        var escKeyCode = 27;
        //
        // Selects the next node id.
        //
        var nextNodeID = 10;
        //
        // Setup the data-model for the chart.
        //
        var chartDataModel = {};

        $scope.processTypes={};
        $scope.source_processTypes={};
        $scope.operator_processTypes={};
        $scope.emitter_processTypes={};
        $scope.persistentStore_processTypes={};
        $scope.chartViewModel={};
        $scope.newMessagesList = {};

        //
        // Event handler for key-down on the flowchart.
        //
        $scope.processDetails={a:0};

        $scope.parentPidRecord = 0;

        $scope.keyDown = function(evt) {
            if (evt.keyCode === ctrlKeyCode) {
                ctrlDown = true;
                evt.stopPropagation();
                evt.preventDefault();
            }
        };

        //
        // Event handler for key-up on the flowchart.
        //
        $scope.keyUp = function(evt) {
            if (evt.keyCode === deleteKeyCode) {
                //
                // Delete key.
                //
                $scope.confirmDialog('Do you really want to delete the selected element?', 'deleteSelected');
            }
            if (evt.keyCode == aKeyCode && ctrlDown) {
                //
                // Ctrl + A
                //
                $scope.chartViewModel.selectAll();
            }
            if (evt.keyCode == escKeyCode) {
                // Escape.
                $scope.chartViewModel.deselectAll();
            }
            if (evt.keyCode === ctrlKeyCode) {
                ctrlDown = false;
                evt.stopPropagation();
                evt.preventDefault();
            }
        };
        var parentPid;
        var busDomainId;
        var parentType;
        var parentName;
        var parentDesc;
        var hot;
        $scope.init = function(pid) {
            loadProgressBar(2);
            $scope.parentPidRecord = pid;
            var dataRecord = processExportAC('/mdrest/process/export/', pid);
            if (dataRecord) {
                parentPid = dataRecord.processList[0].processId;
                busDomainId = dataRecord.processList[0].busDomainId;
                parentType = dataRecord.processList[0].processTypeId;
                parentName = 'parent';
                parentDesc = 'parent desc';

                alterGroupNPC(dataRecord.processList);
                alertBox('success', 'Welcome to Workflow Creator page');
            }
            else {
                alertBox('danger', 'Error Initialising Process Page');
            }

            jQuery.post('/mdrest/processtype/options_emitter/'+parentType,function(data){$scope.emitter_processTypes=data});
            jQuery.post('/mdrest/processtype/options_persistentStore/'+parentType,function(data){$scope.persistentStore_processTypes=data});
            jQuery.post('/mdrest/processtype/options/'+parentType,function(data){$scope.processTypes=data});
            jQuery.post('/mdrest/processtype/options_source/'+parentType,function(data){$scope.source_processTypes=data});
            jQuery.post('/mdrest/processtype/options_operator/'+parentType,function(data){$scope.operator_processTypes=data});
            var messagesOptionslist = workflowtypeOptionslistAC('/mdrest/message/optionslist',  'POST', '');
                if (messagesOptionslist) {
                    $scope.newMessagesList = messagesOptionslist;
                    console.log('info -- no messages options listed');
                }
                else {
                    console.log('messagesOptionslist not loaded');
                }

            //     var connectionsOptionslist = workflowtypeOptionslistAC('/mdrest/connections/optionslist',  'POST', '');
            //     if (connectionsOptionslist) {
            //         $scope.connectionsList = connectionsOptionslist;
            //         console.log('info -- no connections options listed');
            //     }
            //     else {
            //         console.log('connectionsOptionslist not loaded');
             //    }

                    var emitterconnections = workflowtypeOptionslistAC('/mdrest/connections/optionslist/emitter',  'POST', '');
                      if (emitterconnections) {
                          $scope.emitterConnectionsList = emitterconnections;
                          console.log('info -- no connections options listed');
                      }
                      else {
                         console.log('emitterconnectionslist not loaded');
                      }

                      var persistentStoreconnections = workflowtypeOptionslistAC('/mdrest/connections/optionslist/persistentStore',  'POST', '');
                        if (persistentStoreconnections) {
                            $scope.persistentStoreConnectionsList = persistentStoreconnections;
                            console.log('info -- no connections options listed');
                        }
                        else {
                           console.log('persistentStoreconnectionslist not loaded');
                        }


                   var hbaseConnections = workflowtypeOptionslistAC('/mdrest/connections/optionslist/persistentStore_hbase',  'POST', '');
                      if (hbaseConnections) {
                          $scope.hbaseConnectionsList = hbaseConnections;
                          console.log('info -- no connections options listed');
                      }
                      else {
                         console.log('hbaseConnectionsList not loaded');
                      }




            //
            // Setup the data-model for the chart.
            //
            chartDataModel = {};
            //
            // Create the view-model for the chart and attach to the scope.
            //
            $scope.chartViewModel = new flowchart.ChartViewModel(chartDataModel);
            propertiesAC('/mdrest/properties/all/', 'GET', $scope.parentPidRecord);
            $scope.initProps();
            loadProgressBar(100);
        }

        $scope.initProps = function() {
                var nodeMap = {},
                    dataRecord = processAC('/mdrest/process/', 'GET', parentPid);

                if (dataRecord) {
                    dataRecord.processName = "End";
                   // nodeMap[dataRecord.processId] = $scope.restoreNode(dataRecord);
                }
                else {
                    alertBox('warning','Error occured');
                }
                //Resstore sub processes
                var subprocessRecord = subprocessAC('/mdrest/subprocess/', 'GET', parentPid);
                if (subprocessRecord) {
                    $.each(subprocessRecord, function(i, val) {
                        nodeMap[val.processId] = $scope.restoreNode(val);
                    });
                    $.each(subprocessRecord, function(i, val) {
                        var srcNode = nodeMap[val.processId];
                        var srcConnector = srcNode.outputConnectors[0];
                        var destPids = val.nextProcessIds.split(',');
                        $.each(destPids, function(j, destPid) {
                            if (destPid != parentPid) {
                                var destNode = nodeMap[destPid];
                                var destConnector = destNode.inputConnectors[0];
                                $scope.chartViewModel.createNewConnection(srcConnector, destConnector);
                            }
                        });
                    });
                }
                else {
                    alertBox('warning','Error occured');
                }

                var dataRecord1 = processAC('/mdrest/process/', 'GET', parentPid);
                if (dataRecord1) {
                    dataRecord1.processId = -parentPid;
                    dataRecord1.processName = "Start";
                  //  var startNode = $scope.restoreNode(dataRecord1);
                    //handle start node
                  //  var startNodeConnector = startNode.outputConnectors[0];
                    var firstNodePids = dataRecord1.nextProcessIds.split(',');
                    jQuery.each(firstNodePids, function(j, firstNodePid) {

                        if (firstNodePid != 0) {
                            var firstNode = nodeMap[firstNodePid];
                            var firstNodeConnector = firstNode.inputConnectors[0];
                           // $scope.chartViewModel.createNewConnection(startNodeConnector, firstNodeConnector);
                        }
                    });
                }
                else {
                    alertBox('warning','Error occured');
                }
            }
            //Restore node
        $scope.restoreNode = function(process) {
            if (!process) {
                return;
            }
            var processType= processTypeAC('/mdrest/processtype/', process.processTypeId);

            var processTypeName = processType.processTypeName;
            var index = processTypeName.indexOf("_");
            var nodeType = processTypeName.substr(0,index);
            var nodename = processTypeName.substr(index+1, processTypeName.length);

            //Get x y position
            var xpos = 0;
            var ypos = 0;
            var pid = process.processId;

            if (process.processId <0) {
                pid = - process.processId;
            }
            var dataRecord = propertiesAC('/mdrest/properties/', 'GET', pid);
            if (dataRecord) {
                jQuery.each(dataRecord, function(i, props) {
                if(props!=null){
                    if (props.key == 'x') {
                        xpos = parseInt(props.value);
                    } else if (props.key == 'y') {
                        ypos = parseInt(props.value);
                    }
                }
                });
                if (process.processId < 0) {
                    xpos = 10;
                }
            }
            else {
                alertBox('warning', 'Error occured');
            }
            //
            // Template for a new node.
            //
            var newNodeDataModel = {
                name: process.processName,
                id: process.processId,
                x: xpos,
                y: ypos,
                type: process.processTypeId,
                pid: process.processId,
                busDomainId: process.busDomainId,
                parent: process.parentProcessId,
                properties: [
                    ['', '']
                ],
                inputConnectors: [{
                    name: ""
                }],
                outputConnectors: [{
                    name: ""
                }],
            };
            //Start and end node are of single connector.
            if (process.processId < 0) {
                delete newNodeDataModel["inputConnectors"];
            } else if (process.processId == parentPid) {
                delete newNodeDataModel["outputConnectors"];
            }

             if(nodeType=="source"){
              delete newNodeDataModel["inputConnectors"];
              }

            if(nodeType=="persistentStore"){
              delete newNodeDataModel["outputConnectors"];
              }
            var nodedm = $scope.chartViewModel.addNode(newNodeDataModel);
            return nodedm;
        };
        //
        // Add a new node to the chart.
        //
        $scope.addNewNode = function(nodeTypeId) {
            if (!nodeTypeId) {
                return;
            }
            console.log("nodeTypeId "+nodeTypeId);

            var processType= processTypeAC('/mdrest/processtype/', nodeTypeId);

            var processTypeName = processType.processTypeName;
            var index = processTypeName.indexOf("_");
            var nodeType = processTypeName.substr(0,index);
            var nodename = processTypeName.substr(index+1, processTypeName.length);

            if(parentType==41)
            {
            //
                        //create new process
                        console.log("Inside parenttype 41");
                        var postData = '&processName=' +
                                nodename + '&description=A+Child+of+' +
                                parentPid + '&batchPattern=&parentProcessId=' +
                                parentPid + '&canRecover=1&nextProcessIds='+parentPid+'&enqProcessId=0&busDomainId=' +
                                busDomainId + '&processTypeId=' +
                                nodeTypeId + '&workflowId=0&processTemplateId=';
            }
           else
           {
            //
            //create new process
            var postData = '&processName=Child+of+' +
                    parentPid + '&description=A+Child+of+' +
                    parentPid + '&batchPattern=&parentProcessId=' +
                    parentPid + '&canRecover=1&nextProcessIds='+parentPid+'&enqProcessId=0&busDomainId=' +
                    busDomainId + '&processTypeId=' +
                    nodeTypeId + '&workflowId=0&processTemplateId=';
               }
            var subprocessRecord = subprocessAC('/mdrest/subprocess', 'PUT', postData);


            if (subprocessRecord) {

           // Template for a new node.

              var newNodeDataModel = {
                  name: subprocessRecord.processName,
                  id: subprocessRecord.processId,
                  x: 0,
                  y: 0,
                  type: nodeTypeId,
                  operator: nodeType,
                  pid: subprocessRecord.processId,
                  busDomainId: busDomainId,
                  parent: parentPid,
                  properties: [
                      ['', '']
                  ],
                  inputConnectors: [{
                      name: ""
                  }],
                  outputConnectors: [{
                      name: ""
                  }],
              };
            if(nodeType=="source"){
              delete newNodeDataModel["inputConnectors"];
              }

            if(nodeType=="persistentStore"){
              delete newNodeDataModel["outputConnectors"];
              }
              $scope.chartViewModel.addNode(newNodeDataModel);

              alertBox('info', 'New node with id '+ subprocessRecord.processId +' created');


                //adding position properties
                var putDataX = "&configGroup=position"+"&key=x"+"&value=0"+"&description=xposition"+"&processId="+subprocessRecord.processId;

                var dataRecordX = propertiesAC('/mdrest/properties/', 'PUT', putDataX);
                            if (dataRecordX) {
                                $.get('/mdrest/properties/'+subprocessRecord.processId, function(getdata) {
                                    $scope.chartViewModel.selectedProcessProps = getdata.Record;
                                    $scope.configKeyValue = getdata.Record;

                                });
                            }else{
                                alertBox('warning', 'position Y insertion failed');
                            }
                var putDataY = "&configGroup=position"+"&key=y"+"&value=0"+"&description=yposition"+"&processId="+subprocessRecord.processId;

                var dataRecordY = propertiesAC('/mdrest/properties/', 'PUT', putDataY);
                            if (dataRecordY) {
                                $.get('/mdrest/properties/'+subprocessRecord.processId, function(getdata) {
                                    $scope.chartViewModel.selectedProcessProps = getdata.Record;
                                    $scope.configKeyValue = getdata.Record;

                                });
                            }else{
                             alertBox('warning', 'position Y insertion failed');
                            }


            }
            else {
                alertBox('warning', 'New node creation failed');
            }
        };

//
// On clicking Name Descirption update button
//
$scope.updateProcessDetails = function() {
   var newPName = document.getElementById('process.name').value;
   var newPDesc= document.getElementById('process.description').value;
    //$scope.chartViewModel.selectedProcess.description = document.getElementById('process.description').value;

    var postJson=$scope.chartViewModel.selectedProcess;
    postJson.processName=newPName;
    postJson.description=newPDesc;
    delete postJson["addTS"];
    delete postJson["editTS"];
    var postData= $.param(postJson);
    // console.log(postData);
    var dataRecord = processAC('/mdrest/process', 'POST', postData);
    if (dataRecord) {
        $scope.chartViewModel.selectedProcess=dataRecord;
        var selectedNodes = $scope.chartViewModel.getSelectedNodes();
            jQuery.each(selectedNodes, function(i, selectedNode) {
                selectedNode.data.name=newPName;
            });
        alertBox('info', 'Process details updated');
    }
    else {
        alertBox('warning', 'Process details not updated');
    }
}

//
// On clicking Name Descirption update button
//

$scope.insertSourceProp=function(processId){
var map=new Object();

var value6=document.getElementById("messageName").value;
map["messageName"]=value6;
console.log("property6 is "+value6);


                       $.ajax({
							type: "POST",
							url: "/mdrest/properties/"+processId,
							data: jQuery.param(map),
							success: function(data) {
								if(data.Result == "OK") {
								    var modal = document.getElementById('myModal');
                                    modal.style.display = "none";
									alertBox("info","kafka properties added");
								}
								else
								alertBox("warning","Error occured");

							}

						});


}

var filterFinal ="";
function filterFormIntoText(typeOf) {
    var map = new Object();
    var x = '';
    x = document.getElementById(typeOf);
    console.log(x);
    var text = "";

        var column=x.elements[0].value;
        var operator=x.elements[1].value;
        var filterValue=x.elements[2].value;
        text= text+"NONE"+"::"+column+"::"+operator+"::"+filterValue+",";
          var i;
          for(i = 4; i < x.length-2; i=i+5) {
          var logicalOp=x.elements[i].value;
          var column=x.elements[i+1].value;
          var operator=x.elements[i+2].value;
          var filterValue=x.elements[i+3].value;
         console.log(logicalOp+" "+column+" "+operator+" "+filterValue);
         console.log(text);
          text= text+logicalOp+"::"+column+"::"+operator+"::"+filterValue+",";
    }

    console.log(text);
    filterFinal=text;
}


$scope.insertFilterProp=function(processId){
console.log("processId is "+processId);
var mapFilter=new Object();
filterFormIntoText('processFieldsForm3');
console.log(filterFinal);
mapFilter["filterData"]=filterFinal.substr(0, filterFinal.length-1);
console.log(mapFilter);
$.ajax({
            type: "POST",
            url: "/mdrest/properties/addFilterProperties/"+processId,
            data:jQuery.param(mapFilter),
            success: function(data) {
                if(data.Result == "OK") {
                   var modal = document.getElementById('myModal');
                    modal.style.display = "none";
                    alertBox("info","Filter properties added");
                }
                else
                alertBox("warning","Error occured");

            }

        });


}
$scope.insertHiveProp=function(processId){
var value1 = $('#hiveColumn').val();
console.log(value1);
 var modal = document.getElementById('myModal');
 modal.style.display = "none";
}

$scope.saveDuplicationProperties=function(processId){
var map=new Object();
var duplicationType=document.getElementById("deDuplicationType").value;
 if(duplicationType=="WindowDeduplication"){
 var windowColumn=document.getElementById("windowDeDuplicationColumn").value;
 var windowDuration=document.getElementById("windowDuration").value;
 console.log(windowColumn+"  "+windowDuration);
 map["windowDeDuplicationColumn"]=windowColumn;
 map["windowDuration"]=windowDuration;
 }

 else
 {
 var hbaseColumn=document.getElementById("hbaseDeDuplicationColumn").value;
 var hbaseConnection=document.getElementById("hbaseConnectionName").value;
 var hbaseTableName=document.getElementById("hbaseTableName").value;
 console.log(hbaseColumn+" "+hbaseConnection+" "+hbaseTableName);
 map["hbaseDeDuplicationColumn"]=hbaseColumn;
 map["hbaseConnectionName"]=hbaseConnection;
 map["hbaseTableName"]=hbaseTableName;
 }

$.ajax({
            type: "POST",
            url: "/mdrest/properties/addDeDuplicationProperties/"+processId,
            data: jQuery.param(map),
            success: function(data) {
                if(data.Result == "OK") {
                    var modal = document.getElementById('myModal');
                     modal.style.display = "none";
                     alertBox("info","DeDuplication properties added");
                }
                else
                alertBox("warning","Error occured");

            }

        });



}



var aggregationFinal ="";
function formIntoText(typeOf) {
    var map = new Object();
    var x = '';
    x = document.getElementById(typeOf);
    console.log(x);
    var text = "";
    var i;
    for(i = 0; i < x.length-2; i=i+3) {
          var column=x.elements[i].value;
          var aggregation=x.elements[i+1].value;
          text= text+column+":::"+aggregation+",";
    }

    console.log(text);
    aggregationFinal=text;
}

var broadcastFinal ="";
function broadcastformIntoText(typeOf) {
    var map = new Object();
    var x = '';
    x = document.getElementById(typeOf);
    console.log(x);
    var text = "";
    var i;
    for(i = 0; i < x.length-2; i=i+6) {
          var connectionName=x.elements[i].value;
          var tableName=x.elements[i+1].value;
          var columnFamily=x.elements[i+2].value;
          var columnName=x.elements[i+3].value;
          var broadcastIdentifier=x.elements[i+4].value;
          if(broadcastIdentifier=="")
          broadcastIdentifier=connectionName+tableName+columnFamily+columnName;
          text= text+connectionName+":::"+tableName+":::"+columnFamily+":::"+columnName+":::"+broadcastIdentifier+",";
    }

    console.log(text);
    broadcastFinal=text;
}


$scope.insertBroadcastProp=function(){
var url_string=window.location.href;
console.log("url is "+url_string);
var url = new URL(url_string);
var c = url.searchParams.get("processId");
console.log(c);
var map=new Object();
broadcastformIntoText('processFieldsForm2');
console.log(broadcastFinal);
map["connectionName:tableName:columnFamily:columnName:broadcastIdentifier"]=broadcastFinal.substr(0, broadcastFinal.length-2);

$.ajax({
            type: "POST",
            url: "/mdrest/properties/addBroadcastProperties/"+c,
            data: jQuery.param(map),
            success: function(data) {
                if(data.Result == "OK") {
                     document.getElementById('broadcast').style.display = "none";
                     alertBox("info","Broadcast properties added");
                }
                else
                alertBox("warning","Error occured");

            }

        });


}


$scope.insertAggProp=function(processId){
var map=new Object();
formIntoText('processFieldsForm1');
console.log(aggregationFinal);
map["column:aggType"]=aggregationFinal.substr(0, aggregationFinal.length-2);
    $.ajax({
            type: "POST",
            url: "/mdrest/properties/"+processId,
            data: jQuery.param(map),
            success: function(data) {
                if(data.Result == "OK") {
                     var modal = document.getElementById('myModal');
                     modal.style.display = "none";
                    alertBox("info","Aggregation properties added");
                }
                else
                alertBox("warning","Error occured");

            }

        });
}









$scope.insertJoinProperties=function(processId){
var joinPrevProcessId=$('#joinTable').val();
var columnName=$('#joinColumn').val();
var outputColumns=$('#joinColumns').val().toString();
console.log("join processId "+processId+"  "+joinPrevProcessId+" "+columnName+" "+outputColumns);
var map=new Object();
map["join-column"]=columnName;
map["join-type"] =$('#join-type').val();
map["outputColumns"]=outputColumns;
 $.ajax({
            type: "POST",
            url: "/mdrest/properties/"+joinPrevProcessId,
            data: jQuery.param(map),
            success: function(data) {
                if(data.Result == "OK") {
                  var modal = document.getElementById('myModal');
                  modal.style.display = "none";
                    alertBox("info","Sort properties added");
                }
                else
                alertBox("warning","Error occured");

            }

        });
}


$scope.insertSortProp=function(processId){
var value1=document.getElementById("sortcolumn").value;
var value2=document.getElementById("sortorder").value;
console.log("values are "+value1+" "+value2);
console.log("processId is "+processId);
var map=new Object();
map["column"]=value1;
map["order"]=value2;
    $.ajax({
            type: "POST",
            url: "/mdrest/properties/"+processId,
            data: jQuery.param(map),
            success: function(data) {
                if(data.Result == "OK") {
                  var modal = document.getElementById('myModal');
                  modal.style.display = "none";
                    alertBox("info","Sort properties added");
                }
                else
                alertBox("warning","Error occured");

            }

        });
}

$scope.insertTakeProp=function(processId){
var value1=document.getElementById("number").value;
console.log("values are "+value1);
console.log("processId is "+processId);
var map=new Object();
map["number-elements"]=value1;
    $.ajax({
            type: "POST",
            url: "/mdrest/properties/"+processId,
            data: jQuery.param(map),
            success: function(data) {
                if(data.Result == "OK") {
                  var modal = document.getElementById('myModal');
                  modal.style.display = "none";
                    alertBox("info","Take properties added");
                }
                else
                alertBox("warning","Error occured");

            }

        });
}

$scope.insertPersistProp=function(processId){
var value1=document.getElementById("storageLevel").value;
console.log("values are "+value1);
console.log("processId is "+processId);
var map=new Object();
map["storage-level"]=value1;
    $.ajax({
            type: "POST",
            url: "/mdrest/properties/"+processId,
            data: jQuery.param(map),
            success: function(data) {
                if(data.Result == "OK") {
                  var modal = document.getElementById('myModal');
                  modal.style.display = "none";
                    alertBox("info","Persist properties added");
                }
                else
                alertBox("warning","Error occured");

            }

        });
}

$scope.insertRepartitionProp=function(processId){
var value1=document.getElementById("numPartitions").value;
console.log("values are "+value1);
console.log("processId is "+processId);
var map=new Object();
map["num-partitions"]=value1;
    $.ajax({
            type: "POST",
            url: "/mdrest/properties/"+processId,
            data: jQuery.param(map),
            success: function(data) {
                if(data.Result == "OK") {
                  var modal = document.getElementById('myModal');
                  modal.style.display = "none";
                    alertBox("info","Repartition properties added");
                }
                else
                alertBox("warning","Error occured");

            }

        });
}

$scope.insertWindowProp=function(processId){
var value1=document.getElementById("windowType").value;
var value2=document.getElementById("windowDuration").value;
var value3=document.getElementById("slideDuration").value;
console.log("values are "+value1+" "+value2+" "+value3);
console.log("processId is "+processId);
var map=new Object();
map["window-type"]=value1;
map["window-duration"]=value2;
map["slide-duration"]=value3;
    $.ajax({
            type: "POST",
            url: "/mdrest/properties/"+processId,
            data: jQuery.param(map),
            success: function(data) {
                if(data.Result == "OK") {
                   var modal = document.getElementById('myModal');
                    modal.style.display = "none";
                    alertBox("info","Window properties added");
                }
                else
                alertBox("warning","Error occured");

            }

        });
}

$scope.insertMapToPairProp=function(processId){
console.log(document.getElementById("keyFields"));
var value2=document.getElementById("keyFields").value;
console.log("values are  "+value2);
console.log("processId is "+processId);
var map=new Object();
map["keyFields"]=value2;
    $.ajax({
            type: "POST",
            url: "/mdrest/properties/"+processId,
            data: jQuery.param(map),
            success: function(data) {
                if(data.Result == "OK") {
                   var modal = document.getElementById('myModal');
                    modal.style.display = "none";
                    alertBox("info","MapToPair properties added");
                }
                else
                alertBox("warning","Error occured");

            }

        });
}


$scope.insertMapProp=function(parentProcessId,processId){
var value1=document.getElementById("mapper").value;
var value2=document.getElementById("executorPlugin").value;
$scope.uploadJar(parentProcessId,processId,'mapJar');
console.log("values are "+value1+" "+value2);
console.log("processId is "+processId);
var map=new Object();
map["mapper"]=value1;
map["executor-plugin"]=value2;
$scope.uploadJar(processId,'lib','mapJar');

    $.ajax({
            type: "POST",
            url: "/mdrest/properties/"+processId,
            data: jQuery.param(map),
            success: function(data) {
                if(data.Result == "OK") {
                   var modal = document.getElementById('myModal');
                    modal.style.display = "none";
                    alertBox("info","Map properties added");
                }
                else
                alertBox("warning","Error occured");

            }

        });



}

$scope.insertFlatMapProp=function(parentProcessId,processId){
var value1=document.getElementById("mapper").value;
var value2=document.getElementById("executorPlugin").value;
$scope.uploadJar(parentProcessId,processId,'flatmapJar');

console.log("values are "+value1+" "+value2);
console.log("processId is "+processId);
var map=new Object();
map["mapper"]=value1;
map["executor-plugin"]=value2;
    $.ajax({
            type: "POST",
            url: "/mdrest/properties/"+processId,
            data: jQuery.param(map),
            success: function(data) {
                if(data.Result == "OK") {
                   var modal = document.getElementById('myModal');
                    modal.style.display = "none";
                    alertBox("info","FlatMap properties added");
                }
                else
                alertBox("warning","Error occured");

            }

        });
}

$scope.insertReduceProp=function(parentProcessId,processId){
var value1=document.getElementById("operator").value;
var value2=document.getElementById("executorPlugin").value;
var value3=document.getElementById("windowDuration").value;
var value4=document.getElementById("slideDuration").value;
$scope.uploadJar(parentProcessId,processId,'reduceJar');


console.log("values are "+value1+" "+value2);
console.log("processId is "+processId);
var map=new Object();
map["operator"]=value1;
map["executor-plugin"]=value2;
map["window-duration"]=value3;
map["slide-duration"]=value4;

    $.ajax({
            type: "POST",
            url: "/mdrest/properties/"+processId,
            data: jQuery.param(map),
            success: function(data) {
                if(data.Result == "OK") {
                   var modal = document.getElementById('myModal');
                    modal.style.display = "none";
                    alertBox("info","Reduce properties added");
                }
                else
                alertBox("warning","Error occured");

            }

        });
}

$scope.insertReduceByKeyProp=function(parentProcessId,processId){
var value1=document.getElementById("operator").value;
var value2=document.getElementById("executorPlugin").value;
var value3=document.getElementById("windowDuration").value;
var value4=document.getElementById("slideDuration").value;
$scope.uploadJar(parentProcessId,processId,'reducebykeyJar');


console.log("values are "+value1+" "+value2);
console.log("processId is "+processId);
var map=new Object();
map["operator"]=value1;
map["executor-plugin"]=value2;
map["window-duration"]=value3;
map["slide-duration"]=value4;

    $.ajax({
            type: "POST",
            url: "/mdrest/properties/"+processId,
            data: jQuery.param(map),
            success: function(data) {
                if(data.Result == "OK") {
                   var modal = document.getElementById('myModal');
                    modal.style.display = "none";
                    alertBox("info","ReduceByKey properties added");
                }
                else
                alertBox("warning","Error occured");

            }

        });
}

$scope.insertGroupByKeyProp=function(processId){
var value1=document.getElementById("operator").value;
var value3=document.getElementById("windowDuration").value;
var value4=document.getElementById("slideDuration").value;

console.log("values are "+value1+" "+value2);
console.log("processId is "+processId);
var map=new Object();
map["operator"]=value1;
map["window-duration"]=value3;
map["slide-duration"]=value4;

    $.ajax({
            type: "POST",
            url: "/mdrest/properties/"+processId,
            data: jQuery.param(map),
            success: function(data) {
                if(data.Result == "OK") {
                   var modal = document.getElementById('myModal');
                    modal.style.display = "none";
                    alertBox("info","GroupByKey properties added");
                }
                else
                alertBox("warning","Error occured");

            }

        });
}



$scope.insertEmitterProp=function(processId){
var value1=document.getElementById("emitterConnectionName").value;
console.log("values are "+value1);
console.log("processId is "+processId);
var map=new Object();
map["connectionName"]=value1;
    $.ajax({
            type: "POST",
            url: "/mdrest/properties/"+processId,
            data: jQuery.param(map),
            success: function(data) {
                if(data.Result == "OK") {
                    var modal = document.getElementById('myModal');
                    modal.style.display = "none";
                    alertBox("info","Emitter properties added");
                }
                else
                alertBox("warning","Error occured");

            }

        });
}

$scope.insertPersistentStoreProp=function(processId){
var value1=document.getElementById("persistentStoreConnectionName").value;
console.log("values are "+value1);
console.log("processId is "+processId);
var map=new Object();
map["connectionName"]=value1;
    $.ajax({
            type: "POST",
            url: "/mdrest/properties/"+processId,
            data: jQuery.param(map),
            success: function(data) {
                if(data.Result == "OK") {
                   var modal = document.getElementById('myModal');
                    modal.style.display = "none";
                    alertBox("info","PersistentStore properties added");
                }
                else
                alertBox("warning","Error occured");

            }

        });
}



$scope.fetchMessageColumns=function(processId){

     var dataRecord = messagesAC('/mdrest/sparkstreaming/getmessagecolumns/'+processId, 'POST', [$scope.chartViewModel.getSelectedNodes()[0].data.pid]);
    if (dataRecord) {
        $scope.messageColumnNames = dataRecord;
    } else {
        alertBox('danger', 'Error has occured');
    }
}



$scope.insertProp = function(cfgDetails) {
    var cfg = cfgDetails.key,
        key = $('#'+cfgDetails.key+'-propkey').val(),
        val = $('#'+cfgDetails.key+'-propval').val();
    var selectedNode = $scope.chartViewModel.getSelectedNodes()[0];
    var desc = selectedNode.data.description;
    var proppid = selectedNode.data.pid;
    var putData = "configGroup="+cfg+"&key="+key+"&value="+val+"&description="+desc+"&processId="+proppid;

    var dataRecord = propertiesAC('/mdrest/properties/', 'PUT', putData);
    if (dataRecord) {
        $.get('/mdrest/properties/'+proppid, function(getdata) {
            $scope.chartViewModel.selectedProcessProps = getdata.Record;
        });
        $scope.getKeyValue(cfgDetails);
        alertBox('info', 'New property added');
    }
    else {
        alertBox('warning', 'Duplicate key not allowed');
    }
}

//
// On clicking upload button
//

$scope.uploadFile = function(processId,parentProcessId,subDir,cg) {

        var args = [parentProcessId,subDir,cg+'-propval'];
        var fileArgs=[parentProcessId,subDir,$("#"+args[2])[0].files[0].name];
        var fileData=fileHandlerAC('/mdrest/filehandler/check/','POST',fileArgs);
        var dataRecord = fileHandlerAC('/mdrest/filehandler/upload/', 'POST', args);

    if (dataRecord ) {
         //After file load added following to properties table: '+subDir + '/'+fileName + ' against a random generated key.');

         if(cg=='extraFiles'){
         function generatePropKey()
         {
             var text = "FileId-";
             var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

             for( var i=0; i < 5; i++ )
                 text += possible.charAt(Math.floor(Math.random() * possible.length));

             return text;
         }
         var putData = "configGroup="+cg+"&key="+generatePropKey()+"&value="+subDir+'/'+dataRecord.Record.fileName+"&description="+'File path'+"&processId="+processId;

         console.log("file exists="+fileData.fileExists);
         if(fileData.fileExists==false){
         cfgDetails = propertiesAC('/mdrest/properties/', 'PUT', putData);
                  $.get('/mdrest/properties/'+processId, function(getdata) {
                  $scope.chartViewModel.selectedProcessProps = getdata.Record;
                  });
                 $scope.getKeyValue(cfgDetails);
                 alertBox('info', 'File uploaded :'+dataRecord.Record.fileName+','+'size:'+(dataRecord.Record.fileSize/1024).toFixed(2)+'KB'+' and new property added');
         }
         }

       else if(cg=='mapper'){
             console.log(cg);
             var property = propertiesAC('/mdrest/properties/', 'DELETE', [processId,'mapperPath']);
             var putData = "configGroup="+cg+"&key="+'mapperPath'+"&value="+subDir+'/'+dataRecord.Record.fileName+"&description="+'File path'+"&processId="+processId;
             cfgDetails = propertiesAC('/mdrest/properties/', 'PUT', putData);
            $.get('/mdrest/properties/'+processId, function(getdata) {
                $scope.chartViewModel.selectedProcessProps = getdata.Record;
            });
            $scope.getKeyValue(cfgDetails);
            alertBox('info', 'File uploaded :'+dataRecord.Record.fileName+','+'size:'+(dataRecord.Record.fileSize/1024).toFixed(2)+'KB'+' and new property added');
            }
       else if(cg=='reducer'){
             console.log(cg);
             var property = propertiesAC('/mdrest/properties/', 'DELETE', [processId,'reducerPath']);
             var putData = "configGroup="+cg+"&key="+'reducerPath'+"&value="+subDir+'/'+dataRecord.Record.fileName+"&description="+'File path'+"&processId="+processId;
             cfgDetails = propertiesAC('/mdrest/properties/', 'PUT', putData);
            $.get('/mdrest/properties/'+processId, function(getdata) {
                $scope.chartViewModel.selectedProcessProps = getdata.Record;
            });
            $scope.getKeyValue(cfgDetails);
            alertBox('info', 'File uploaded :'+dataRecord.Record.fileName+','+'size:'+(dataRecord.Record.fileSize/1024).toFixed(2)+'KB'+' and new property added');
            }
     else {
             console.log(cg);
             var property = propertiesAC('/mdrest/properties/', 'DELETE', [processId,'scriptPath']);
             var putData = "configGroup="+cg+"&key="+'scriptPath'+"&value="+subDir+'/'+dataRecord.Record.fileName+"&description="+'File path'+"&processId="+processId;
             cfgDetails = propertiesAC('/mdrest/properties/', 'PUT', putData);
            $.get('/mdrest/properties/'+processId, function(getdata) {
                $scope.chartViewModel.selectedProcessProps = getdata.Record;
            });
            $scope.getKeyValue(cfgDetails);
            alertBox('info', 'File uploaded :'+dataRecord.Record.fileName+','+'size:'+(dataRecord.Record.fileSize/1024).toFixed(2)+'KB'+' and new property added');
            }
    }
    else {
        alertBox('warning', 'File upload failed');
    }
}

$scope.uploadJar = function(parentProcessId,subDir,fileId) {

    var args = [parentProcessId,subDir,fileId];
    console.log(args);
    var dataRecord = fileHandlerAC('/mdrest/filehandler/upload/', 'POST', args);

    if (dataRecord ) {

        $scope.getJarList();
        alertBox('info', 'Jar uploaded : '+ dataRecord.Record.fileName+','+'size:'+(dataRecord.Record.fileSize/1024).toFixed(2)+'KB');

    }
    else {
        alertBox('warning', dataRecord.Record.fileName+' upload failed');
    }
}


$scope.getFile = function(parentProcessId,cfgKVP) {
    var args = [parentProcessId,cfgKVP.value];
    var dataRecord = fileHandlerAC('/mdrest/filehandler/upload/', 'GET', args);

    if (dataRecord ) {
        alertBox('info', 'File downloaded');
    }
    else {
        alertBox('warning', 'File download failed');
    }
}

$scope.deleteFile = function(parentProcessId,cfgDetails,cfgKVP) {

    var args = [parentProcessId,cfgKVP.value];
    var dataRecord = fileHandlerAC('/mdrest/filehandler/upload/', 'DELETE', args);

    if (dataRecord ) {
         //After file delete remove following from properties table: '+subDir + '/'+fileName + ' against scriptPath key.');
         var property = propertiesAC('/mdrest/properties/', 'DELETE', [cfgKVP.processId,cfgKVP.key]);
         if (property) {
        $.get('/mdrest/properties/'+ cfgKVP.processId, function(getdata) {
            $scope.chartViewModel.selectedProcessProps = getdata.Record;
        });
        $scope.getKeyValue(cfgDetails);
        alertBox('info', 'File and its property deleted');
        }
    }
    else {
        alertBox('warning', 'File delete failed');
    }
}
$scope.changeme=function(){

var messageTypeOptionslist = messagesAC('/mdrest/sparkstreaming/getmessagecolumns/'+$('#joinTable').val(), 'POST', []);
    if (messageTypeOptionslist) {
        $scope.messageColumnListChart = messageTypeOptionslist;
        console.log('info -- messageColumnListChart options listed');
    }
    else {
        console.log('messageColumnListChart not loaded');
    }
}


$scope.deleteJar = function(parentProcessId,subDir,fileName) {

    var args = [parentProcessId,subDir,fileName];
    var dataRecord = fileHandlerAC('/mdrest/filehandler/upload/', 'DELETE', args);

    if (dataRecord ) {
        $scope.getJarList();
        alertBox('info', 'Jar:'+fileName+' deleted');
        }
    else {
        alertBox('warning', 'Jar delete failed');
    }
}

$scope.deleteProp = function (cfgDetails, cfgKVP) {
    var proppid = $scope.chartViewModel.getSelectedNodes()[0].data.pid,
        dataRecord = propertiesAC('/mdrest/properties/', 'DELETE', [proppid, cfgKVP.key]);
    if (dataRecord) {
        $.get('/mdrest/properties/'+proppid, function(getdata) {
            $scope.chartViewModel.selectedProcessProps = getdata.Record;
        });
        $scope.getKeyValue(cfgDetails);
        alertBox('info', 'Property deleted');
    }
    else {
        alertBox('warning', 'Property not deleted');
    }
}

$scope.updateProp = function (cfgDetails, cfgKVP) {
    var cfgGrp = cfgDetails.key,
        key = cfgKVP.key,
        val = cfgKVP.value;
    var selectedNode = $scope.chartViewModel.getSelectedNodes()[0];
    var desc = selectedNode.data.description;
    var proppid = selectedNode.data.pid;
    var postData = "configGroup="+cfgGrp+"&key="+key+"&value="+val+"&description="+desc+"&processId="+proppid;

    var dataRecord = propertiesAC('/mdrest/properties/', 'POST', postData);
    if (dataRecord) {
        $.get('/mdrest/properties/'+proppid, function(getdata) {
            $scope.chartViewModel.selectedProcessProps = getdata.Record;
        })
        alertBox('info', 'Property updated');
    }
    else {
        alertBox('warning', 'Property not updated');
    }
}

$scope.arrangePositions = function() {
    loadProgressBar(10);
    var dataRecord;
    setTimeout(function() {
        loadProgressBar(30);
        dataRecord = arrangePositionsAC('/mdrest/arrangepositions/', 'GET', $scope.parentPidRecord);
        if (dataRecord) {
            updatePositionsFromArrangedData(dataRecord);
            $scope.init($scope.parentPidRecord);
            location.reload(true);
        }
        else {
            console.log('error');
        }
        loadProgressBar(100);
    }, 200);
}

$scope.duplicateSelected = function () {
    var selectedNodeData = $scope.chartViewModel.getSelectedNodes()[0].data;
    if (selectedNodeData.pid <0 || selectedNodeData.pid == parentPid) {
        alertBox('warning',"You can't duplicate start or end nodes");
    }
    else{
        duplicatePropRecord = propertiesCloneAC('/mdrest/process/clone','PUT', selectedNodeData.pid);
        if (duplicatePropRecord) {
            var newNodeDataModel = {
                name: duplicatePropRecord.processName,
                id: duplicatePropRecord.processId,
                x: selectedNodeData.x + 20,
                y: selectedNodeData.y + 20,
                type: duplicatePropRecord.processTypeId,
                pid: duplicatePropRecord.processId,
                busDomainId: busDomainId,
                parent: parentPid,
                properties: [
                    ['', '']
                ],
                inputConnectors: [{
                    name: ""
                }],
                outputConnectors: [{
                    name: ""
                }],
            };
            $scope.chartViewModel.addNode(newNodeDataModel);
            alertBox('info','Node duplicated');
        }
        else {
            alertBox('warning','Error unable to duplicate');
        }
    }
}

$scope.configKeyValue = {};
$scope.getKeyValue = function (val) {
    var temp = {},
        dataRecord = propertiesAC('/mdrest/properties/', 'GET', [$scope.chartViewModel.getSelectedNodes()[0].data.pid, val.key]);
    if (dataRecord) {
        $scope.configKeyValue = dataRecord;
    } else {
        alertBox('danger', 'Error has occured');
    }
}

//fetching jar names
$scope.jarList = {};
$scope.getJarList = function() {
    var temp = {},
        dataRecord = fileHandlerAC('/mdrest/filehandler/upload/', 'GET', [$scope.chartViewModel.getSelectedNodes()[0].data.pid, 'lib']);
    if (dataRecord) {
        $scope.jarList = dataRecord;
    }
    else {
        alertBox('danger','Error has occured');
    }
}


$scope.exportSVG = function() {
}

//
// Initialise New Process Page
//
$scope.newPageBusDomain = {};
$scope.newPageProcessType = {};
$scope.newPagePermissionType={};
$scope.newPageUserRoles={};
$scope.newPageWorkflowType = {};
$scope.messageColumnListChart={};
$scope.operators = ["equals","is null","is not null","not equals", "contains","doesnot contains","begins with","ends with","greater than","lesser than"];
$scope.aggregations = ["sum","max","min","count","mean"];
$scope.intialiseNewProcessPage =function() {

    var busdomainOptions = busdomainOptionsAC('/mdrest/busdomain/options/', 'POST', '');
    if (busdomainOptions) {
        $scope.newPageBusDomain = busdomainOptions;
    }
    else {
        console.log('busdomainOptions not loaded');
    }
    var processtypeOptionslist = processtypeOptionslistAC('/mdrest/processtype/optionslist', 'POST', '');
    if (processtypeOptionslist) {
        $scope.newPageProcessType = processtypeOptionslist;
        console.log('info -- Process Type Options Listed');
    }
    else {
        console.log('processtypeOptionlist not loaded');
    }
    var permissiontypeOptionslist = permissiontypeOptionslistAC('/mdrest/process/options/', 'POST', '');
        if (permissiontypeOptionslist) {
            $scope.newPagePermissionType = permissiontypeOptionslist;
            console.log('info -- PermissiontypeOptionlist Type Options Listed');
        }
        else {
            console.log('PermissiontypeOptionlist not loaded');
        }

        var userrolestypeOptionslist = userrolestypeOptionslistAC('/mdrest/userroles/options/', 'POST', '');
                if (permissiontypeOptionslist) {
                    $scope.newPageUserRoles = userrolestypeOptionslist;
                    console.log('info -- UserRoles Type Options Listed');
                }
                else {
                    console.log('UserRoles not loaded');
                }

    var workflowtypeOptionslist = workflowtypeOptionslistAC('/mdrest/workflowtype/optionslist',  'POST', '');
    if (workflowtypeOptionslist) {
        $scope.newPageWorkflowType = workflowtypeOptionslist;
        console.log('info -- Workflow type options listed');
    }
    else {
        console.log('workflowtypeOptionlist not loaded');
    }
}





//
// Create first process function
//
$scope.createFirstProcess = function() {

    var postData = {
        'processName': $('#processname').val(),
        'description': $('#description').val(),
        'canRecover': '0',
        'nextProcessIds': '0',
        'enqProcessId': '0',
        'busDomainId': $('#domain').val(),
        'ownerRoleId':$('#ownerRoleId').val(),
         'permissionTypeByUserAccessId': $('#permissionTypeByUserAccessId').val(),
         'permissionTypeByGroupAccessId': $('#permissionTypeByGroupAccessId').val(),
         'permissionTypeByOthersAccessId': $('#permissionTypeByOthersAccessId').val(),
        'processTypeId': '41',
        'processTemplateId': '',
        'workflowId': '2'
    };
    postData = $.param(postData),
    dataRecord = processAC('/mdrest/process', 'PUT', postData);
    if (dataRecord) {
        if(dataRecord.processTypeId==41)
        location.href='/mdui/pages/wfdesigner2.page?processId='+ dataRecord.processId;
        else
        location.href='/mdui/pages/wfdesigner.page?processId='+ dataRecord.processId;
        console.log('info', 'Parent process created');
    }
    else {
        console.log('Parent process not created');
    }
}

$scope.confirmDialog = function (message, callBackFunctionName){
    var callBackFunction;

    switch(callBackFunctionName) {
        case 'deleteSelected':
            callBackFunction = $scope.deleteSelected;
            break;
        default:
            callBackFunction = console.log;
    }

    $('<div></div>').appendTo('body')
        .html('<div><h6>'+message+'?</h6></div>')
        .dialog({
            modal: true, title: 'Delete message', zIndex: 10000, autoOpen: true,
            width: 'auto', resizable: false,
            buttons: {
                Yes: function () {
                    // $(obj).removeAttr('onclick');
                    // $(obj).parents('.Parent').remove();
                    $(this).dialog("close");
                    callBackFunction(true);
                },
                No: function () {
                    $(this).dialog("close");
                }
            },
            close: function (event, ui) {
                $(this).remove();
            }
    });
}
            //
            // Delete selected nodes and connections.
            //
        $scope.deleteSelected = function(ansreally) {
            if (ansreally) {
                //Selected nodes
                var selectedNodes = $scope.chartViewModel.getSelectedNodes();
                jQuery.each(selectedNodes, function(i, selectedNode) {
                    if (selectedNode.data.pid <0 || selectedNode.data.pid == parentPid) {
                        selectedNode.deselect();
                        alertBox('warning',"You can't delete start or end nodes");
                        return true;
                    } else {
                        var dataRecord = processAC('/mdrest/process/', 'DELETE', selectedNode.data.pid);
                        if (dataRecord) {
                            alertBox('warning','Node with id '+selectedNode.data.pid+' deleted');
                        }
                        else {
                            alertBox('danger','Error unable to delete');
                        }
                    }
                });
                //Selected connections
                var selectedConnections = $scope.chartViewModel.getSelectedConnections();
                jQuery.each(selectedConnections, function(i, selectedConnection) {
                    var srcPid = selectedConnection.source.parentNode().data.pid;
                    var destPid = selectedConnection.dest.parentNode().data.pid;
                    if(srcPid<0)srcPid=-srcPid;
                    var nextIds = [];
                    var dataRecord1 = processAC('/mdrest/process/', 'GET', srcPid);
                    if (dataRecord1) {
                        var nextPidsArr = dataRecord1.nextProcessIds.split(',');
                        jQuery.each(nextPidsArr, function(i, nextProcessId) {
                            if (nextProcessId != destPid) {
                                nextIds.push(nextProcessId);
                            }
                        });
                        console.log("parentPid "+parentPid);
                        console.log("nextids "+nextIds);
                        var index = nextIds.indexOf(parentPid);
                        console.log("index "+index);
                        nextIds.splice(index, 1);
                        console.log("modified nextids "+nextIds);

                        //if empty place 0
                        if (nextIds.length == 0) nextIds.push(0);
                        dataRecord1.nextProcessIds = nextIds.join(',');
                        //TS fields cause exception due to time format
                        delete dataRecord1["addTS"];
                        delete dataRecord1["editTS"];
                        var jsonToPost = dataRecord1;
                        var dataRecord2 = processAC('/mdrest/process/', 'POST', $.param(jsonToPost));
                        if (dataRecord2) {
                            alertBox('info','Next process updated');
                        }
                        else {
                            alertBox('warning','Error occured');
                        }
                    }
                    else {
                        alertBox('warning','Error occured');
                    }
                });
                $scope.chartViewModel.deleteSelected();
            }
        };

        $scope.isFileId = function(key){

                   var patt = new RegExp("FileId-");
                   var res = patt.test(key);
                   return res;
        };
    }]);