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
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
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
    var datatypeMap = {
    INT: "Int",
    VARCHAR: "String",
    BIGINT: "BigInt",
    TIMESTAMP: "Timestamp",
    BIT: "Boolean",
    SMALLINT: "SmallInt",
    INTEGER: "Int",
    TINYINT: "tinyInt"
};
		function displayProcess (records){
                                $('#Container').jtable(
                                {
                                    title: 'Data Import Processes',
                                    paging: false,
                                    sorting: false,
                                    create: false,
                                    edit :true,
                                    actions: {
                                        listAction: function () {
                                                        return records;
                                                    },
                                     updateAction: function(postData) {

                                        return $.Deferred(function($dfd) {
                                            $.ajax({
                                                url: '/mdrest/process',
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
                                                 Properties: {
                                                                 		    title: 'Properties',
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
                                                                 							    url: '/mdrest/properties/' + item.record.processId + '/' + postData.key,
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
                                                                 					    key : true,
                                                                 						    list: false,
                                                                 						    create:false,
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
                                                     edit: true,
                                                     type:'hidden'

                                                 },
                                                 parentProcessId: {
                                                     title: 'Parent',
                                                     edit: true,
                                                     create: false,
                                                     list: false,
                                                     type:'hidden'
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
                                                        type:'hidden'

                                                 },
                                                 enqProcessId: {
                                                     title: 'Enqueuer',
                                                     list: false,
                                                     edit:true,
                                                     type: 'hidden',
                                                 },
                                                 busDomainId: {
                                                     title: 'Application',
                                                     list: false,
                                                     edit:true,
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
                                $('#Container').jtable('load');

         }




		var wizard=null;
		wizard=$(document).ready(function() {
			$("#bdre-dataload").steps({
		    headerTag: "h3",
		    bodyTag: "section",
		    transitionEffect: "slide",
		    onStepChanging: function (event, currentIndex, newIndex)
		    {
		        if(treeData == null)
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
                    }).text("Please 'Test Connection' first to continue");
		            return false;
		        }
		        return true;
		    },
		    onStepChanged: function (event, currentIndex, priorIndex){
		        console.log(currentIndex+" "+priorIndex);
		        if(currentIndex == 1 && priorIndex==0 && isInit==false)
		        {

		        	loadTableTree();
		        }
		    }
			});

		});
      var isInit=false;
      var createJobResult=null;
      function loadTableTree(){
      $("#wizardform").submit(function() {

      var s = $("#tree1").fancytree("getTree").getSelectedNodes();

      var idPrefix = "selectionIndicator_";
      var list = [];
      var uniqTableKeys=[];
      for(var i=0, keys=Object.keys(s), l=keys.length; i<l; i++) {
      if(s[i].isFolder()){
          continue;
      }
      if(s[i].getParent().isFolder() ){

        $("#"+idPrefix+s[i].getParent().key.replace(".","\\.")).find($('input')).each(function() {
        if( uniqTableKeys.indexOf(this.name) == -1 ){
              uniqTableKeys.push(this.name);
              var myObject = new Object();
              myObject.name=this.name;
              myObject.value=this.value;
              list.push(myObject);
          }
        });
         $("#"+idPrefix+s[i].getParent().key.replace(".","\\.")).find($('select')).each(function() {
                    if( uniqTableKeys.indexOf(this.name) == -1 ){
                    var myObject = new Object();
                    myObject.name=this.name;
                    myObject.value=this.value;
                    list.push(myObject);
                    }
                });
        }
        $("#"+idPrefix+s[i].key.replace(".","\\.")).find($('input')).each(function() {

            var myObject = new Object();
            myObject.name=this.name;
            myObject.value=this.value;
            list.push(myObject);
        });
        $("#"+idPrefix+s[i].key.replace(".","\\.")).find($('select')).each(function() {
            var myObject = new Object();
            myObject.name=this.name;
            myObject.value=this.value;
            list.push(myObject);
        });

      }
      var myObject = new Object();
        myObject.name=$("#dbUser")[0].name;
        myObject.value=$("#dbUser")[0].value;
        list.push(myObject);

        var myObject = new Object();
        myObject.name=$("#dbDriver")[0].name;
        myObject.value=$("#dbDriver")[0].value;
        list.push(myObject);

        var myObject = new Object();
        myObject.name=$("#dbURL")[0].name;
        myObject.value=$("#dbURL")[0].value;
        list.push(myObject);

        var myObject = new Object();
        myObject.name=$("#dbPassword")[0].name;
        myObject.value=$("#dbPassword")[0].value;
        list.push(myObject);

        var myObject = new Object();
        myObject.name=$("#dbHive")[0].name;
        myObject.value=$("#dbHive")[0].value;
        list.push(myObject);

         var myObject = new Object();
            myObject.name=$("#busDomainId")[0].name;
            myObject.value=$("#busDomainId")[0].value;
            list.push(myObject);




        $.ajax({
          type: "POST",
          url: "/mdrest/dataimport/createjobs",
          data: jQuery.param(list),
          success: function(data)
          {
            if( data.Result =="OK" ){
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
                    }).text("Jobs successfully created.");
                    createJobResult=data;
                    displayProcess(createJobResult);
            }
            console.log(createJobResult);

          }
        });
      // return false to prevent submission of this form
      return false;
    });

	$("#tree1").fancytree({
    checkbox: true,
    source: treeData,
    activeVisible: true,
    generateIds: true, // Generate id attributes like <span id='fancytree-id-KEY'>
    idPrefix: "selectionIndicator_", // Used to generate node id´s like <span id='fancytree-id-<key>'>.
    selectMode: 3,
	titlesTabbable: false,     // Add all node titles to TAB chain
    quicksearch: true,        // Jump to nodes when pressing first character
    // extensions: ["edit", "table", "gridnav"],
    extensions: ["table", "gridnav"],
    table: {
      indentation: 20,
      nodeColumnIdx: 1,
      checkboxColumnIdx: 0
    },
    gridnav: {
      autofocusInput: false,
      handleCursorKeys: true
    },
    lazyLoad: function(event, data) {
    console.log(data);
    data.result = {url: "/mdrest/dataimport/tables/"+data.node.key};
    },
    renderColumns: function(event, data) {
      var node = data.node,
        $select = $("<select name='hiveDataType_" + data.node.key +"'/>"),
        $ingestSelect = $("<select name='ingestOnly_" + data.node.key +"'/>"),
        $incrementSelect = $("<select name='incrementType_" + data.node.key +"'/>"),
        $tdList = $(node.tr).find(">td");
      // (Index #0 is rendered by fancytree by adding the checkbox)
      //$tdList.eq(1).text(node.getIndexHier()).addClass("alignRight");
      // Index #2 is rendered by fancytree, but we make the title cell
      // span the remaining columns if it is a folder:
      if( node.isFolder() ) {
        $tdList.eq(3)
          .prop("colspan", 2);

          $tdList.eq(3).html("<input type='input' name='destTableName_" + data.node.key +"' class='form-control' value='" + data.node.title + "'/>"+
                "<input type='hidden' name='srcTableName_" + data.node.key +"' value='" + data.node.title + "'/>"+
                "<input type='hidden' name='primaryKeyColumn_" + data.node.key +"' value='" + data.node.data.primarykey + "'/>");

                  $("<option />", {text: "Ingest Only", value: "true"}).appendTo($ingestSelect);
                  $("<option />", {text: "Ingest and HiveLoad", value: "false"}).appendTo($ingestSelect);
                  $ingestSelect.addClass("form-control");
                  $tdList.eq(4).html($ingestSelect);

                  $("<option />", {text: "None", value: "None"}).appendTo($incrementSelect);
                  $("<option />", {text: "Append Rows", value: "AppendRows"}).appendTo($incrementSelect);
                  $("<option />", {text: "Last Moified", value: "DateLastModified "}).appendTo($incrementSelect);
                  $incrementSelect.addClass("form-control");
                  $tdList.eq(5).html($incrementSelect);

      }else{
            $tdList.eq(2).html(data.node.data.dtype+
                "<input type='hidden' name='srcColumnDType_" + data.node.key +"' value='" + data.node.data.dtype + "'/>"+
                "<input type='hidden' name='srcColumnName_" + data.node.key +"' value='" + data.node.title + "'/>"+
                 "<input type='hidden' name='srcColumnIndex_" + data.node.key +"' value='" + data.node.data.columnId + "'/>"


            );
            $tdList.eq(3).html("<input type='input' name='destColumnName_" + data.node.key +"' class='form-control' value='" + data.node.title + "'/>");
            $("<option />", {text: "Int", value: "Int"}).appendTo($select);
                  $("<option />", {text: "BigInt", value: "BigInt"}).appendTo($select);
                  $("<option />", {text: "SmallInt", value: "SmallInt"}).appendTo($select);
                  $("<option />", {text: "Float", value: "Float"}).appendTo($select);
                  $("<option />", {text: "Double", value: "Double"}).appendTo($select);
                  $("<option />", {text: "Decimal", value: "Decimal"}).appendTo($select);
                  $("<option />", {text: "Timestamp", value: "Timestamp"}).appendTo($select);
                  $("<option />", {text: "Date", value: "Date"}).appendTo($select);
                  $("<option />", {text: "String", value: "String"}).appendTo($select);
                  $select.addClass("form-control");
                  $tdList.eq(4).html($select);
                  $select.val(datatypeMap[data.node.data.dtype]);
      }


    }


  }).on("nodeCommand", function(event, data){
    // Custom event handler that is triggered by keydown-handler and
    // context menu:
    var refNode, moveMode,
      tree = $(this).fancytree("getTree"),
      node = tree.getActiveNode();
  });

isInit=true;
		}


		</script >

		<script type = "text/javascript" >
		  var treeData;

          function verifyConnection()
          {
                  var verificationUrl="/mdrest/dataimport/tables?" +
                  $("#dbUser")[0].name+"="+encodeURIComponent($("#dbUser")[0].value) +
                  "&"+$("#dbURL")[0].name+"="+encodeURIComponent($("#dbURL")[0].value)  +
                  "&"+$("#dbPassword")[0].name+"="+encodeURIComponent($("#dbPassword")[0].value)  +
                  "&"+$("#dbDriver")[0].name+"="+encodeURIComponent($("#dbDriver")[0].value) ;

                   $.ajax({
                          type: "GET",
                          url: verificationUrl,
                          dataType: 'json',
                          success: function(items)
                          {
                            console.log(items);
                            if(items.Result=="ERROR"){
                                $("#div-dialog-warning").dialog({
                                    title: "Test Connection Failed",
                                    resizable: false,
                                    height: 'auto',
                                    modal: true,
                                    buttons: {
                                        "Ok" : function () {
                                            $(this).dialog("close");
                                        }
                                    }
                                }).text(items.Message);
                                }
                            else if(items.Result=="OK"){
                            treeData=items.Record;
                                $("#div-dialog-warning").dialog({
                                    title: "Success",
                                    resizable: false,
                                    height: 'auto',
                                    modal: true,
                                    buttons: {
                                        "Ok" : function () {
                                            $(this).dialog("close");
                                        }
                                    }
                                }).text("Test Connection Successful !");

                            }
                          }
                  });


          }

		</script >

	</head >
	<body >
		<form action = "#" method = "POST" id = "wizardform" >
			<br />
			<div id = "bdre-dataload" ng-controller = "myCtrl" >
				<h3 >Database</h3 >
				<section >
					<div >
					<fmt:bundle basename="db">


						<label for = "dbURL" >Database URL</label >
						<input id = "dbURL" onchange = "treeData=null;" name = "common_dbURL" type = "text" class = "form-control" value = "<fmt:message key='hibernate.connection.url' />" />
						<label for = "dbUser" >Database User</label >
						<input id = "dbUser" onchange = "treeData=null;" name = "common_dbUser" type = "text" class = "form-control" value = "<fmt:message key='hibernate.connection.username' />" />
						<label for = "dbPassword" >Database Password</label >
						<input id = "dbPassword" onchange = "treeData=null;" name = "common_dbPassword" type = "password" class = "form-control" value = "<fmt:message key='hibernate.connection.password' />" />
						<label for = "dbDriver" >Database Driver</label >
						<input id = "dbDriver" onchange = "treeData=null;" name = "common_dbDriver" type = "text" class = "form-control" value = "<fmt:message key='hibernate.connection.driver_class' />" />
						<div ><br /></div >
						<button class = "btn btn-default  btn-success" type = "button" onClick = "verifyConnection()" href = "#" >
							Test Connection
						</button >
					</div >
                    </fmt:bundle>
				</section >
				<h3 >Table and Columns</h3 >
				<section style = "display: block; overflow: scroll;" >
					<table id = "tree0" class = "table-striped" width = "290px" >
						<thead >
						<tr >
							<th ><label for = "dbHive" >Hive DB</label ></th >
						</tr >
						</thead >
						<tbody >
						<tr >
							<td >
								<input id = "dbHive" name = "common_dbHive" type = "text" class = "form-control" size = "180" value = "base" />
							</td >
						</tr >
						</tbody >
					</table >


					<br />
					<table id = "tree1" class = "table table-striped" >
						<colgroup >
							<col max-width = "30px" >
							<col width = "200px" >
							<col width = "100px" >
							<col >
							<col >
							<col max-width = "30px">
						</colgroup >
						<thead >
						<tr >
							<th ></th >
							<th >Entity</th >
							<th >Datatype</th >
							<th >Hive Column Name</th >
							<th >Hive Datatype</th >
							<th> Options</th>
							<th> Increment Type</th>
						</tr >
						</thead >
						<tbody >
						</tbody >
					</table >


				</section >

				<h3 >Submission</h3 >

				<section >
					<table id = "tree0" class = "table-striped" width = "290px" >

                                <th ><label for = "busDomainId" > Business Domain Id</label ></th >
                            </tr >
                            </thead >
                            <tbody >
                            <tr >
                                <td >
                                    <input id = "busDomainId" name = "common_busDomainId" type = "text" class = "form-control" size = "180" value = "1" />
                                </td >
                            </tr >
                            </tbody >
					</table >
					<p >Pressing 'Create Job' will make the system build following job workflows</p >

					<div class = "list-group" >
                    <span href = "#" class = "list-group-item" >
                        <span class = "glyphicon glyphicon-export" ></span >Data Extraction Workflows to ingest the data from selected RDBMS tables to Hadoop
                    </span >
                    <span href = "#" class = "list-group-item" >
                        <span class = "glyphicon glyphicon-import" ></span >A Data Loading Workflow to load the ingested data into Hive table in ORC format.
                    </span >

					</div >

					<div class = "alert alert-success" role = "alert" >Create Jobs will be connected automatically so
						upon completion of Data Extraction Workflow, Data Loading Workflow is enqueued automatically.
					</div >

					<input type = "submit" class = "btn btn-warning" value = "Create Data import Jobs" >
				</section >

				<h3 >Confirm</h3 >
				<section >
					<div id = "Container" >
					</div >
				</section >
			</div >
		</form >
		<div style = "display:none" id = "div-dialog-warning" >
			<p ><span class = "ui-icon ui-icon-alert" style = "float:left;" ></span >

			<div />
			</p>
		</div >

	</body >
</html >