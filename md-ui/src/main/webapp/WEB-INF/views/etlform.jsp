<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="security"
           uri="http://www.springframework.org/security/tags" %>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>Bigdata Ready Enterprise</title>
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
    <link href="../css/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
    <link href="../css/jtables-bdre.css" rel="stylesheet" type="text/css"/>
    <link href="../css/jquery-ui-1.10.3.custom.css" rel="stylesheet" type="text/css"/>
    <link href="../css/handsontable.full.css" rel="stylesheet" type="text/css"/>

    <!-- Include jTable script file. -->
    <script src="../js/jquery.min.js" type="text/javascript"></script>
    <script src="../js/jquery-ui-1.10.3.custom.js" type="text/javascript"></script>
    <script src="../js/jquery.jtable.js" type="text/javascript"></script>
    <script src="../js/handsontable.full.js" type="text/javascript"></script>


    <script type="text/javascript">

        $(document).ready(function () {

            $('#Container').jtable({
                title: 'Create Data Load Configuration',
                paging: true,
                pageSize: 10,
                sorting: true,
                selecting: true,
                multiselect: false,
                selectOnRowClick: true,
                openChildAsAccordion: true,

                actions: {
                    listAction: function (postData, jtParams) {

                        return $.Deferred(function ($dfd) {
                            $.ajax({
                                url: '/mdrest/etl/main/?page=' + jtParams.jtStartIndex + '&size='+jtParams.jtPageSize,
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
                    <security:authorize access="hasRole('ROLE_ADMIN')">
                    createAction: function (postData) {
                        console.log(postData);
                        return $.Deferred(function ($dfd) {
                            $.ajax({
                                url: '/mdrest/etl/main/',
                                type: 'PUT',
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
                    }, </security:authorize>
                    <security:authorize access="hasRole('ROLE_ADMIN')">
                    updateAction: function (postData) {
                        console.log(postData);
                        return $.Deferred(function ($dfd) {
                            $.ajax({
                                url: '/mdrest/etl/main/',
                                type: 'POST',
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
                    }, </security:authorize>
                    <security:authorize access="hasRole('ROLE_ADMIN')">
                    deleteAction: function (item) {

                        console.log(item);
                        return $.Deferred(function ($dfd) {
                            $.ajax({
                                url: '/mdrest/etl/main/' + item.uuid,
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
                    }</security:authorize>
                },
                fields: {
                    ETLDetails: {
                        title: '',
                        width: '5%',
                        sorting: false,
                        edit: false,
                        create: false,
                        display: function (item) {      //Create an image that will be used to open child table

                            var $img = $('<img src="../css/images/three-bar.png" title="Dataload Job Details info" />');                         //Open child table when user clicks the image

                            $img.click(function () {
                                $('#Container').jtable('openChildTable',
                                        $img.closest('tr'), {
                                            title: ' Job Details for ' + item.record.processName,
                                            actions: {
                                                listAction: function (postData) {

                                                    return $.Deferred(function ($dfd) {
                                                        $.ajax({
                                                            url: '/mdrest/etl/sub/' + item.record.uuid,
                                                            type: 'GET',
                                                            data: postData,
                                                            dataType: 'json',
                                                            success: function (data) {
                                                                console.log(data);
                                                                $dfd.resolve(data);
                                                            },
                                                            error: function () {
                                                                $dfd.reject();
                                                            }
                                                        });
                                                    });
                                                },
                                                <security:authorize access="hasAnyRole('ROLE_ADMIN','ROLE_USER')">
                                                createAction: function (postData) {
                                                    console.log(postData);
                                                    return $.Deferred(function ($dfd) {
                                                        $.ajax({
                                                            url: '/mdrest/etl/sub/',
                                                            type: 'PUT',
                                                            data: postData + '&uuid=' + item.record.uuid + '&busDomainId=' + item.record.busDomainId + '&processName=' + item.record.processName + '&description=' + item.record.description,
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

                                                deleteAction: function (postData) {

                                                    console.log(postData.serialNumber);
                                                    return $.Deferred(function ($dfd) {
                                                        $.ajax({
                                                            url: '/mdrest/etl/sub/' + item.record.uuid + '/' + postData.serialNumber,
                                                            type: 'DELETE',
                                                            data: item + '&uuid=' + item.record.uuid,
                                                            dataType: 'json',
                                                            success: function (data) {
                                                                $dfd.resolve(data);
                                                                $('#Container').jtable('load');
                                                            },
                                                            error: function () {
                                                                $dfd.reject();
                                                            }
                                                        });
                                                    });
                                                }</security:authorize>
                                            },

                                            fields: {
                                                uuid: {
                                                    key: false,
                                                    list: false,
                                                    create: false,
                                                    edit: false,
                                                    title: 'UUID',
                                                    defaultValue: item.record.uuid,
                                                },
                                                busDomainId: {
                                                    list: false,
                                                    create: false,
                                                    edit: false,
                                                    title: 'Bus Domain Id',
                                                    defaultValue: item.record.busDomainId,
                                                },
                                                processName: {
                                                    list: false,
                                                    create: false,
                                                    edit: false,
                                                    title: 'Name',
                                                    defaultValue: item.record.processName,
                                                },
                                                description: {
                                                    list: false,
                                                    create: false,
                                                    edit: false,
                                                    title: 'Description',
                                                    defaultValue: item.record.description,
                                                },
                                                serialNumber: {
                                                    key: true,
                                                    title: 'Serial Number',
                                                    list: false,
                                                    create: false,
                                                    edit: false
                                                },
                                                baseTableName: {
                                                    title: 'Base Table',
                                                    create: false
                                                },
                                                rawTableName: {
                                                    title: 'Raw Table',
                                                    create: false
                                                },
                                                rawViewName: {
                                                    title: 'Raw View',
                                                    create: false
                                                },
                                                baseDBName: {
                                                    title: 'Base DB',
                                                    create: false
                                                },
                                                rawDBName: {
                                                    title: 'Raw DB',
                                                    create: false
                                                },
                                                inputFormat: {
                                                    title: 'Hive Input Format',
                                                        edit: true,
                                                        create: true,
                                                        type: 'combobox',
                                                        options: { '0': 'Delimited', '1': 'XML', '2':'JSON', '3':'Mainframe' },
                                                        defaultValue: "0"
                                                    },
                                                    columnInfo: {
                                                        type: "textarea",
                                                        title: 'Column info',
                                                        create: false,
                                                        list:false
                                                    },
                                                    serdeProperties: {
                                                        title: 'Serde Properties',
                                                        type: "textarea",
                                                        create: false,
                                                        list:false
                                                    },
                                                    tableProperties: {
                                                        title: 'Table Properties',
                                                        type: "textarea",
                                                        create: false,
                                                        list:false
                                                    },
                                                rawPartitionCol: {
                                                    title: 'Raw Partition',
                                                    create: false
                                                },
                                                dropRaw: {
                                                    title: 'Drop Raw',
                                                    type: "radiobutton",
                                                    options: {"1": "true", "0": "false"},
                                                    create: false
                                                },
                                                showHotButton: {

                                                    title: '',
                                                            sorting: false,
                                                            list: true,
                                                            create: false,
                                                            edit: false,
                                                            display: function (data) {
                                                            console.log(data);
                                                                    return '<span title = "Edit Record" onClick="myPopUp(' + data.record.serialNumber + ',\'' + data.record.uuid + '\')"  class="label label-danger">Edit</span>';
                                                            },
                                                    },
						    enqId: {
						    title: 'Enqueing ID',
							    create: true

						    }
					    },
				    },
					    function (data) { //opened handler
					    data.childTable.jtable('load');
					    }
				    );
				    });   //Return image to show on the person row

				    return $img;
			    }
		    },
			    uuid: {
			    key: true,
				    list: true,
				    create: false,
				    edit: false,
				    title: 'UUID'

			    },
			    busDomainId: {
			    title: 'Bus Domain Id',
				    type: 'combobox',
				    options: '/mdrest/busdomain/options',
				    defaultValue: "0"

			    },
			    processName: {
			    title: 'Name'
			    },
			    description: {
			    title: 'Description'
			    },
			    processId: {
			    title: 'process id',
				    create: false,
				    list:false

			    },
			    Publish: {
			    title: 'Publish',
				    width: '10%',
				    sorting: false,
				    create: false,
				    edit: false,
				    display: function (item) {      //Create an image that will be used to open child table
				    var $img1 = $('<span class="label label-danger">Publish</span>');                         //Open child table when user clicks the image
					    $img1.click(function () {
					    $("#dialog-confirm").dialog({
					    resizable: false,
						    height:'auto',
						    modal: true,
						    buttons: {
						    "Yes Publish": function() {
						    $.ajax({
						    url: '/mdrest/etl/publishetl/',
							    type: 'PUT',
							    data: item + '&uuid=' + item.record.uuid+'&busDomainId='+item.record.busDomainId+ '&processName=' + item.record.processName + '&description=' + item.record.description,
							    dataType: 'json',
							    success: function (data) {
							    console.log(data);
							    console.log(item);

							    location.href = '<c:url value="/pages/process.page?pid="/>' + data.Record.processId;
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
			    }
		    }


	    });
		    $('#Container').jtable('load');
	    });    </script>


	<script>
		    var colInfoTable="";
		    var serdePropsTable;
		    var tablePropsTable;
		    var countPopup = 0;

		    function showHot(serialNumber, uuid) {

		    this.serialNumber = serialNumber;
			    this.uuid = uuid;
			    var
			    columnheaders = ['Column Name', 'Data Type', 'Import'],
			    columnheaders1 = ['Serde Properties Key','Serde Properties Value'],
                columnheaders2 = ['Table Properties Key','Table Properties Value'],
			    dataTypes = ['Int', 'BigInt', 'SmallInt', 'Float', 'Double', 'Decimal','Timestamp', 'Date', 'String'],
			    dataColInfo = [ ['', '', 'yes'] ],
                dataSProps = [ ['',''] ],
                dataTProps = [ ['',''] ],

				container = document.getElementById('hoTable');
				tPropsContainer = document.getElementById('hoTableTProps');
				sPropsContainer = document.getElementById('hoTableSProps');
				if (countPopup != 0){
					colInfoTable.destroy(); // invalidate existing hoTableSProps
					serdePropsTable.destroy();
					tablePropsTable.destroy();
                }
                console.log(uuid);
			    $.ajax({
                		    url: '/mdrest/etl/sub/'+uuid+'/'+serialNumber,
                			    type: 'GET',
                			    dataType: 'json',
                			    success: function (hiveTableData) {
                			    console.log(hiveTableData);
                			     $('#Edit-baseTableName').val(hiveTableData.Record.baseTableName);
                			     $('#Edit-rawTableName').val(hiveTableData.Record.rawTableName);
                			     $('#Edit-rawViewName').val(hiveTableData.Record.rawViewName);
                			     $('#Edit-baseDBName').val(hiveTableData.Record.baseDBName);
                			     $('#Edit-rawDBName').val(hiveTableData.Record.rawDBName);
                			     $('#Edit-rawPartitionName').val(hiveTableData.Record.rawPartitionCol);
                			     $('#Edit-dropRaw').val(hiveTableData.Record.dropRaw);
                                 $("#Edit-inputFormat").val(hiveTableData.Record.inputFormat);
                                 $('#Edit-enqId').val(hiveTableData.Record.enqId);
                			    dataColInfoJson = JSON.parse(hiveTableData.Record.columnInfo);
                			    dataSPropsJson= JSON.parse(hiveTableData.Record.serdeProperties);
                			    dataTPropsJson= JSON.parse(hiveTableData.Record.tableProperties);
                			    dataColInfo=dataColInfoJson.data;
								dataSProps=dataSPropsJson.data;
								dataTProps=dataTPropsJson.data;
								colInfoTable.loadData(dataColInfo);
								serdePropsTable.loadData(dataSProps);
								tablePropsTable.loadData(dataTProps);
                			    },
                			    error: function () {
                			    alert('Error in get');
                			    }
                		    });




			    colInfoTable = new Handsontable(container, {
			    colHeaders: columnheaders,
				    columns: [
				    {},
				    {
				    type: 'dropdown',
					    source: dataTypes,
				    },
				    {
				    type: 'checkbox',
					    checkedTemplate: 'yes',
					    uncheckedTemplate: 'no'
				    }
				    ],
				    colWidths: [100, 100, 200],
				    contextMenu: true,
				    copyPaste: true,
				    minSpareRows: 2,
				    minCols: 3,
                    maxCols: 3,
                    contextMenu: ['row_above', 'row_below', 'remove_row'],

			    });
			    serdePropsTable = new Handsontable(sPropsContainer, {
			    colHeaders: columnheaders1,
				    columns: [
				    {},
				    {}
				    ],
				    colWidths: [200, 200],
				    contextMenu: true,
				    copyPaste: true,
				    minSpareRows: 2,
				    minCols: 2,
				    maxCols: 2,
				    contextMenu: ['row_above', 'row_below', 'remove_row']
			    });
			    tablePropsTable = new Handsontable(tPropsContainer, {
			    colHeaders: columnheaders2,
				    columns: [
				    {},
				    {}
				    ],
				    colWidths: [200,200],
				    contextMenu: true,
				    copyPaste: true,
				    minSpareRows: 2,
				    minCols: 2,
                    maxCols: 2,
                    contextMenu: ['row_above', 'row_below', 'remove_row']
			    });

			    countPopup++;

		    }

	    function clickSubmit(serialNumber, uuid) {
	    console.log(serialNumber + ' ::: ' + uuid);

		    baseDbName = document.getElementsByName("baseDbName")[0].value;
		    baseTableName = document.getElementsByName("baseTableName")[0].value;

		    rawDbName = document.getElementsByName("rawDbName")[0].value;
		    rawTableName = document.getElementsByName("rawTableName")[0].value;

		    rawViewName = document.getElementsByName("rawViewName")[0].value;

		    rawPartitionName = document.getElementsByName("rawPartitionName")[0].value;
		    dropRawBoolean = document.getElementsByName("dropRaw")[0].checked;
		    enqId = document.getElementsByName("enqId")[0].value;
		    columnInfo=(JSON.stringify({data: colInfoTable.getData()}));
			serdeProperties=(JSON.stringify({data: serdePropsTable.getData()}));
		    tableProperties=(JSON.stringify({data: tablePropsTable.getData()}));
			inputFormat=document.getElementsByName("inputFormat")[0].value;
		    count = colInfoTable.getData().length;
            console.log(inputFormat + ' ::: ' + inputFormat);

		    $.ajax({
		    url: '/mdrest/etl/sub/',
			    type: 'POST',
			    data:  {
			    serialNumber: serialNumber,
                uuid: uuid,
                baseTableName: baseTableName,
                rawTableName: rawTableName,
                rawViewName: rawViewName,
                baseDBName: baseDbName,
                rawDBName: rawDbName,
                rawPartitionCol: rawPartitionName,
                dropRaw: dropRawBoolean,
                enqId: enqId,
                tableProperties: tableProperties,
                serdeProperties: serdeProperties,
                columnInfo: columnInfo,
                inputFormat: inputFormat
				 			    },
			    dataType: 'json',
			    success: function (data) {
			    $('#Container').jtable('load');
			    },
			    error: function () {
			    alert('Error in post');
			    }
		    });
	    }

	    function myPopUp(serialNumber, uuid) {
	    $('#modalwa').show();
		    dialog = $("#dialog-form").dialog({
	    	autoOpen: true,
		    height: 'auto',
		    width: 500,
		    modal: false,
		    buttons: {
		    Done: function() {
		    clickSubmit(serialNumber, uuid);
			    dialog.dialog("close");
			    form[0].reset();
			    $('#modalwa').hide();
		    },
			    Cancel: function() {
			    dialog.dialog("close");
				    $('#modalwa').hide();
			    }
		    },
		    close: function() {
		    form[0].reset();
			    $('#modalwa').hide();
		    }
	    });
		    showHot(serialNumber, uuid);
		    form = dialog.find("form").on("submit", function(event) {
	    event.preventDefault();
		    clickSubmit(serialNumber, uuid);
		    $('#modalwa').hide();
		    $('#modalwa').hide();
	    });
	    }
	</script>
    </head>

    <body>
    <section style="width:100%;text-align:center;">
	<div id="Container"></div>
    </section>

    <div id="dialog-form" style="display:none;">
	<form>
	    <div class="jtable-input-field-container">
		<div class="jtable-input-label">Base Table</div>
		<div class="jtable-input jtable-text-input"><input class="" id="Edit-baseTableName" type="text" name="baseTableName" value=""/>
		</div>

		<div class="jtable-input-label">Raw Table</div>
		<div class="jtable-input jtable-text-input"><input class="" id="Edit-rawTableName" type="text" name="rawTableName" value=""/>
		</div>

		<div class="jtable-input-label">Raw View</div>
		<div class="jtable-input jtable-text-input"><input class="" id="Edit-rawViewName" type="text" name="rawViewName" value=""/>
		</div>

		<div class="jtable-input-label">Base DB</div>
		<div class="jtable-input jtable-text-input"><input class="" id="Edit-baseDbName" type="text" name="baseDbName" value="base"/>
		</div>

		<div class="jtable-input-label">Raw DB</div>
		<div class="jtable-input jtable-text-input"><input class="" id="Edit-rawDbName" type="text" name="rawDbName" value="raw"/>
		</div>

		<div class="jtable-input-label">Raw Partition</div>
		<div class="jtable-input jtable-text-input"><input class="" id="Edit-rawPartitionName" type="text" name="rawPartitionName" value="batchid"/>
		</div>
		<div class="jtable-input-label">Input Format</div>
		<div> <select id="Edit-inputFormat" name="inputFormat">
		   <option value="0">Delimited</option>
		   <option value="1">XML</option>
		   <option value="2">JSON</option>
		   <option value="3">Mainframe</option>
		   </select>
		</div>
		<div class="jtable-input-label">Drop Raw</div>
		<div class="jtable-input jtable-text-input">
		    <input class="" id="Edit-dropRaw" type="checkbox" name="dropRaw" />
		</div>
		<div class="jtable-input-label">Enqueuer Id</div>
		<div class="jtable-input jtable-text-input"><input class="" id="Edit-enqId" type="text" name="enqId" value=""/>
		</div>
		<div class="jtable-input-label">Columns and Datatypes</div>
		<div class="handsontable" id="hoTable"/>
	    </div>

	    <div class="jtable-input-label">Table Properties</div>
        	<div class="handsontable" id="hoTableTProps"/>
        </div>

	    <div class="jtable-input-label">Serde Properties</div>
        	<div class="handsontable" id="hoTableSProps"/>
        </div>
	    <br/>

	</form>
    </div>
</div>

<div id="modalwa" style="height:100%; width:100%; position:fixed;top: 0;background-color: gray;opacity: .5; display: none">

</div>
<div id="dialog-confirm" title="Are you sure?" style="display:none;">
    <p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>This will create a new data load process using this table configuration.
	This will not replace any existing process already created. Do you want to publish this?</p>
</div>
</body>
</html>