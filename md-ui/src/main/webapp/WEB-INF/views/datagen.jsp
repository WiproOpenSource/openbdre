<%@ taglib prefix="security"
       uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
     pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>

<head>
    <title><spring:message code="common.page.title_bdre_2"/></title>
	<style>
    .alert-info span {
		color: #C85659;
		letter-spacing: 1px;
	}
	.add-more {
		background-color: #389DD0 !important;
		border: none !important;
		padding: 10px !important;
		color: #fff !important;
	}
	.content{
		min-height: 32em !important;
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

    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <script src="../js/jquery.min.js"></script>
    <link href="../css/jquery-ui-1.10.3.custom.css" rel="stylesheet">
    <link href="../css/css/bootstrap.min.css" rel="stylesheet" />
    <link href="../css/bootstrap.custom.css" rel="stylesheet" type="text/css" />
    <script src="../js/jquery-ui-1.10.3.custom.js"></script>
    <script src="../js/jquery.steps.min.js"></script>
    <link rel="stylesheet" href="../css/jquery.steps.css" />
    <link rel="stylesheet" href="../css/jquery.steps.custom.css" />
    <script src="../js/bootstrap.js" type="text/javascript"></script>
    <script src="../js/jquery.jtable.js" type="text/javascript"></script>
    <link href="../css/jtables-bdre.css" rel="stylesheet" type="text/css" />
    

    <script src="../js/angular.min.js" type="text/javascript"></script>
    <script type="text/javascript">
    var map = new Object();
    var map2 = new Object();
    var createJobResult;
    var getGenConfigMap = function(cfgGrp){
        var map = new Object();
        $.ajax({
            type: "GET",
            url: "/mdrest/genconfig/"+cfgGrp+"/?required=2",
            dataType: 'json',
            async: false,
            success: function(data) {
                
                var root = 'Records';
                $.each(data[root], function(i, v) {
                    map[v.key] = v;
                });
                
            },
            error : function(data){
                console.log(data);
            }

        });
    return map;

    };
    
</script>
<script >
        function fetchPipelineInfo(pid){
			location.href = '<c:url value="/pages/lineage.page?pid="/>' + pid;
        }
</script >

<script>
function formIntoMap(typeProp, typeOf) {
    var x = '';
    x = document.getElementById(typeOf);
    var text = "";
    var i;
    for(i = 0; i < x.length; i++) {
        
        map2[typeProp + x.elements[i].name] = x.elements[i].value;
    }
}
</script>

</head>
<body ng-app="myApp" ng-controller="myCtrl" >
	<div class="page-header"><spring:message code="datagen.page.panel_heading"/></div>
	<div class="alert alert-info" role="alert">
			<b style="font-size:24px;"><spring:message code="datagen.page.how_to"/></b><br/>
           <b><spring:message code="datagen.page.b_datatype_format"/></b> <span><spring:message code="datagen.page.b_span"/></span>
           <br>
           <b><spring:message code="datagen.page.b_regex_pattern"/></b><span><spring:message code="datagen.page.b_regex_pattern_span"/></span>
           <br>
           <b><spring:message code="datagen.page.number_format"/></b> <span><spring:message code="datagen.page.number_format_span"/></span>
           <br>
           <b><spring:message code="datagen.page.column_type"/></b> <span><spring:message code="datagen.page.column_type_span"/></span>
    </div>
	


    <div id="datagen">

            <h3><div class="number-circular">1</div><spring:message code="datagen.page.data_types"/></h3>
            <section>
            <form class="form-horizontal pull-none" role="form" id="processFieldsForm1">
                	<!-- btn-group -->
                    <div class="form-group" id="formGroup1" >
                        <div class="col-md-3">
                            <input type="text" class="form-control input-sm" id="fieldName.1" value="" name="fieldName" placeholder=<spring:message code="datagen.page.colname_type_placeholder"/> />
                        </div>
                        <div class="col-md-3">
                            <select class="form-control input-sm" id="generatedType.1" name="generatedType.1">
                                <option ng-repeat="generatedTypes in generatedType.Record" value="{{generatedTypes.defaultVal}}" name="generatedType">{{generatedTypes.description}}</option>
                            </select>
                        </div>
                        <div class="col-md-4">
                            <input type="text" class="form-control input-sm" id="genArg.1" value="" name="genArg.1" placeholder=<spring:message code="datagen.page.generator_argument_placeholder"/> />
                        </div>
                        <button id="remove1" class="btn btn-danger remove-me"><span class="glyphicon glyphicon-trash"></span></button>
                        
                        
                    </div>
                    <!-- /btn-group -->
               <div class="col-md-2" id="deletediv">
                            <button id="b1" class="btn add-more">
                                <span class="glyphicon glyphicon-plus" style="font-size:large"></span>
                            </button>
                </div>
                
            </form>
                
            </section>

            <h3><div class="number-circular">2</div><spring:message code="datagen.page.table_types"/></h3>
            <section>
            <form class="form-horizontal" role="form" id="processFieldsForm2">
                <div id="tableDetails">
                    <div class="alert alert-info" role="alert">
                        <spring:message code="datagen.page.alert_info"/>
                    </div>
                    <!-- btn-group -->
                    <div id="tableFields">
                    	<div class="form-group">
                    	    <label class="control-label col-sm-2" for="numRows"><spring:message code="datagen.page.number_of_records"/></label>
                    	    <div class="col-sm-10">
                    	        <input type="text" class="form-control"  id="numRows" name="numRows" placeholder=<spring:message code="datagen.page.number_of_records_placeholder"/>required>
                    	    </div>
                    	</div>
                    	<div class="form-group">
                    	    <label class="control-label col-sm-2" for="numSplits"><spring:message code="datagen.page.number_of_splits"/></label>
                    	    <div class="col-sm-10">
                    	        <input type="text" class="form-control" id="numSplits" name="numSplits" placeholder=<spring:message code="datagen.page.number_of_splits_placeholder"/>required>
                    	    </div>
                    	</div>
                    	<div class="form-group">
                    	    <label class="control-label col-sm-2" for="separator"><spring:message code="datagen.page.field_separator"/></label>
                    	    <div class="col-sm-10">
                    	        <input type="text" class="form-control" id="separator" name="separator" placeholder=<spring:message code="datagen.page.field_separator_placeholder"/> required>
                    	    </div>
                    	</div>
                    	<div class="form-group">
                    	    <label class="control-label col-sm-2" for="tableName"><spring:message code="datagen.page.table_name"/></label>
                    	    <div class="col-sm-10">
                    	        <input type="text" class="form-control" id="tableName" name="tableName" placeholder=<spring:message code="datagen.page.table_name_placeholder"/>required>
                    	    </div>
                    	</div>

                    </div>
                    <!-- /btn-group -->
                </div>
                
            </form>
            </section>

            <h3><div class="number-circular">3</div><spring:message code="datagen.page.proc_details"/></h3>
                            <section>
                                <form class="form-horizontal" role="form" id="processFieldsForm3">
                                    <div id="processDetails">
                                        <div class="alert alert-info" role="alert">
											<spring:message code="datagen.page.alert-info_2"/>
                                        </div>
                                        <!-- btn-group -->
                                        <div id="processFields">

                                            <div class="form-group">
                                                <label class="control-label col-sm-2" for="processName"><spring:message code="datagen.page.proc_name"/></label>
                                                <div class="col-sm-10">
                                                    <input type="text" class="form-control"  id="processName" name="processName" placeholder=<spring:message code="datagen.page.proc_name_placeholder"/> required>
                                                </div>
                                            </div>
                                            <div class="form-group">
                                                <label class="control-label col-sm-2" for="processDescription"><spring:message code="datagen.page.proc_desc"/></label>
                                                <div class="col-sm-10">
                                                    <input type="text" class="form-control" id="processDescription" name="processDescription" placeholder=<spring:message code="datagen.page.proc_desc_placeholder"/> required>
                                                </div>
                                            </div>
                                            <div class="form-group">
                                                <label class="control-label col-sm-2" for="outputPath"><spring:message code="datagen.page.hdfs_op_path"/></label>
                                                <div class="col-sm-10">
                                                    <input type="text" class="form-control" id="outputPath" name="outputPath" placeholder=<spring:message code="datagen.page.hdfs_op_path_placeholder"/> required>
                                                </div>
                                            </div>
                                            <div class="form-group">
                                                <label class="control-label col-sm-2" for="busDomainId"><spring:message code="datagen.page.bus_domain_id"/></label>
                                                <div class="col-sm-10">
                                                    <select class="form-control" id="busDomainId" name="busDomainId">
                                                        <option ng-repeat="busDomain in busDomains.Options" value="{{busDomain.Value}}" name="busDomainId">{{busDomain.DisplayText}}</option>

                                                    </select>
                                                </div>
                                            </div>
                                        </div>
                                        <!-- /btn-group -->
                                    </div>
                                    </form>
                                    </section>

            <h3><div class="number-circular">4</div><spring:message code="datagen.page.confirm"/></h3>
            <section>
            <div id="createProcess">
                <button ng-click="createJob()" id="createjobs" type="button" class="btn btn-primary btn-lg">Create Job</button>
            </div>
            <div id="Process"></div>
            </section>
                
    </div>
   

        <script type="text/javascript">
    $(document).ready(function(){
    var next = 1;
    $(".add-more").click(function(e){
        e.preventDefault();
        var addto = "#deletediv";
        var addRemove = "#formGroup" + (next);
        next = next + 1;
        var removeBtn = '<button id="remove' + (next) + '" class="btn btn-danger remove-me" ><span class="glyphicon glyphicon-trash" ></span></button></div><div id="field">';
        var newIn = '';
        newIn = newIn +  '<div class="form-group" id="formGroup' + next + '">' ;
        newIn = newIn +  '<div class="col-md-3">' ;
        newIn = newIn +  '<input type="text" class="form-control input-sm" id="fieldName.' + next + '" value="" name="fieldName.' + next + '" placeholder="Column Name : Column Type" />' ;
        newIn = newIn +  '</div>' ;
        newIn = newIn +  '<div class="col-md-3">' ;
        newIn = newIn +  '<select class="form-control input-sm" id="generatedType.' + next + '" name="generatedType.' + next + '">' ;
        newIn = newIn +  getGenTypes() ;
        newIn = newIn +  '</select>' ;
        newIn = newIn +  '</div>' ;
        newIn = newIn +  '<div class="col-md-4">' ;
        newIn = newIn +  '<input type="text" class="form-control input-sm" id="genArg.' + next + '" value="" name="genArg.' + next + '" placeholder="Generator Argument" />' ;
        newIn = newIn +  '</div>' ;
        newIn = newIn + removeBtn;
        newIn = newIn +  '</div>' ;
        
        var newInput = $(newIn);
        var removeButton = $(removeBtn);
        $(addto).before(newInput);
        
        $("#formGroup" + next).attr('data-source',$(addto).attr('data-source'));
        $("#count").val(next);  
        
            $('.remove-me').click(function(e){
                e.preventDefault();
                var fieldNum = this.id.charAt(this.id.length-1);
                var fieldID = "#formGroup" + fieldNum;
                console.log($(this));
                //$(this).remove();
                $(fieldID).remove();
            });
    });

    

    
});
function getGenTypes(){
    var opt='';    
    $.ajax({
            url: '/mdrest/genconfig/testDataGen/',
                type: 'GET',
                dataType: 'json',
                async: false,
                success: function (data) {
                   $.each(data.Record,function(index,record){
                       opt=opt+'<option value="'+record.defaultVal+'" name="generatedType">'+record.description+'</option>' ;
                   });
                },
                error: function () {
                    alert('danger');
                }
          });
    return opt;
}
    </script>
    <script>
        var app = angular.module('myApp', []);
        app.controller('myCtrl', function($scope) {
            $scope.crawlerMap = getGenConfigMap('datagen');
            $scope.generatedType = {};
             $.ajax({
                        url: '/mdrest/genconfig/testDataGen/',
                            type: 'GET',
                            dataType: 'json',
                            async: false,
                            success: function (data) {
                                $scope.generatedType = data;
                                console.log(data);
                            },
                            error: function () {
                                alert('danger');
                            }
                        });
            $scope.busDomains={};
            $.ajax({
                        url: '/mdrest/busdomain/options/',
                            type: 'POST',
                            dataType: 'json',
                            async: false,
                            success: function (data) {
                                $scope.busDomains = data;
                            },
                            error: function () {
                                alert('danger');
                            }
                        });

            $scope.createJob =function (){
                formIntoMap('type_','processFieldsForm1');
            formIntoMap('other_','processFieldsForm2');
            formIntoMap('process_','processFieldsForm3');
                console.log(map2);
            $.ajax({                    
                                        
                                        type: "POST",
                                        url: "/mdrest/datagenproperties/createjobs",
                                        data: jQuery.param(map2),
                                        
                                        success: function(data) {
                                            
                                            if(data.Result == "OK") {
                                                created = 1;
                                                $("#div-dialog-warning").dialog({
                                                    title: "Success",
                                                    resizable: false,
                                                    height: 'auto',
                                                    modal: true,
                                                    buttons: {
                                                        "Ok": function() {
                                                            $(this).dialog("close");
                                                        }
                                                    }
                                                }).html("<p><span class=\"jtable-confirm-message\">Jobs successfully created.</span></p>");
                                                createJobResult = data;
                                                displayProcess(createJobResult);
                                                $('#createProcess').hide();
                                            }
                                            else{
                                                $("#div-dialog-warning").dialog({
                                                    title: "Error",
                                                    resizable: false,
                                                    height: 'auto',
                                                    modal: true,
                                                    buttons: {
                                                        "Ok": function() {
                                                            $(this).dialog("close");
                                                        }
                                                    }
                                                }).html("<p><span class=\"jtable-confirm-message\">"+ data.Message+ "</span></p>");
                                            }
                                            console.log(createJobResult);
                                        }

                                    });
            }
        });
    </script>
    <script type="text/javascript">
    var wizard = null;
    
        $("#datagen").steps({
            headerTag: "h3",
            bodyTag: "section",
            transitionEffect: "slideLeft",
            stepsOrientation: "horizontal",
            enableCancelButton: true,
            
            onFinished: function(event, currentIndex) {
                    if(created == 1) {
                        location.href = '<c:url value="/pages/process.page"/>';
                    } else {
                        $("#div-dialog-warning").dialog({
                            title: "",
                            resizable: false,
                            height: 'auto',
                            modal: true,
                            buttons: {
                                "Ok": function() {
                                    $(this).dialog("close");
                                }
                            }
                        }).html("<p><span class=\"jtable-confirm-message\">Jobs have not been created.</span></p>");
                    }
                },
            onCanceled: function(event) {
			location.href = '<c:url value="/pages/datagen.page"/>';
		      }

        });
    
</script>
<script>
function displayProcess(records) {
    $('#Process').jtable({
        title: 'Data Ingestion Processes',
        paging: false,
        sorting: false,
        create: false,
        edit: true,
        actions: {
            listAction: function() {
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
                display: function(item) { //Create an image that will be used to open child table

                    var $img = $('<span class="label label-primary">Show</span>'); //Open child table when user clicks the image

                    $img.click(function() {
                        $('#Process').jtable('openChildTable',
                            $img.closest('tr'), {
                                title: ' Properties of ' + item.record.processId,
                                paging: false,
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
                                            });;
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
    $('#Process').jtable('load');

}

        </script>
<div id="div-dialog-warning"/>
</body>

</html>
