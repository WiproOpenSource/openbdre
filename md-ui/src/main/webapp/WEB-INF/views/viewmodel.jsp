<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="security"
	   uri="http://www.springframework.org/security/tags" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

                    <meta name="viewport" content="width=device-width, initial-scale=1">
                    <link rel="stylesheet" href="../css/css/bootstrap.min.css" />
                    <link rel="stylesheet" href="../css/bootstrap.custom.css" />
                    <!--<link rel="stylesheet" href="../css/css/materialstyle.css" />-->
                    <link rel="stylesheet" href="../css/submenu.css" />
                    <script src="../js/angular.min.js"></script>
                    <link href="../StreamAnalytix_files/materialdesignicons.min.css" media="all" rel="stylesheet" type="text/css">
                    <link href="../StreamAnalytix_files/bootstrap.min.css" rel="stylesheet">
                    <link href="../StreamAnalytix_files/bootstrap-material-design.min.css" rel="stylesheet">
                    <link href="../StreamAnalytix_files/ripples.min.css" rel="stylesheet">
                    <link href="../StreamAnalytix_files/sax-fonts.css" class="include" rel="stylesheet" type="text/css">
                    <link href="../StreamAnalytix_files/toastr.min.css" rel="stylesheet">
                    <link href="../StreamAnalytix_files/datatables.min.css" rel="stylesheet">
                    <link href="../StreamAnalytix_files/theme.css" rel="stylesheet" type="text/css">
                    <link href="../StreamAnalytix_files/style.css" rel="stylesheet" type="text/css">
                    <link href="../StreamAnalytix_files/select2.4.0.css" rel="stylesheet">
                    <link href="../StreamAnalytix_files/select2-bootstrap.css" rel="stylesheet">

                        <!-- Favicon-->
                        <link rel="icon" href="favicon.ico" type="image/x-icon">

                        <!-- Google Fonts -->
                        <link href="https://fonts.googleapis.com/css?family=Roboto:400,700&subset=latin,cyrillic-ext" rel="stylesheet" type="text/css">
                        <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet" type="text/css">


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
             .dropdown-menu {
                        position:initial;
                        }

                        .dropdown-toggle {
                            padding-top: 9px !important;
                        }

            .usericon {
                    display: block;
                    width: 30px;
                    height: 30px;
                    border-radius: 80px;
                    background: #FFF no-repeat center;
                    background-image: url("../css/images/user_icon.png");
                    background-size: 65% 65%;
                }

        .label-icons {
                    margin: 0 auto;
                    width: 15px;
                    height: 15px;
                    background-size: 100% !important;
                    display: block;
                    background-repeat: no-repeat !important;
                    background-position: center !important;
                }
                .label-properties {
                    background: url('../css/images/subprocess-rarrow.png') no-repeat center;
                }


        </style>

        <script src="../js/jquery.steps.min.js" type="text/javascript"></script>
		<script src="../js/jquery.min.js" type="text/javascript" ></script>
		<script src="../js/bootstrap.js" type="text/javascript"></script>
        <script src="../js/jquery-ui-1.10.3.custom.js" type="text/javascript"></script>
        <script src="../js/jquery.jtable.js" type="text/javascript"></script>
        <script src="../js/angular.min.js" type="text/javascript"></script>

		<link href="../css/jtables-bdre.css" rel="stylesheet" type="text/css" />
		<link href="../css/jquery-ui-1.10.3.custom.css" rel="stylesheet">
		<link href="../css/css/bootstrap.min.css" rel="stylesheet" />
		<link rel="stylesheet" href="../css/jquery.steps.css" />
        <link rel="stylesheet" href="../css/jquery.steps.custom.css" />
        <link href="../css/bootstrap.custom.css" rel="stylesheet" />


<script type="text/javascript">
     		    $(document).ready(function () {
     	    $('#Container').jtable({
     	    title: 'Model List',
     		    paging: true,
     		    pageSize: 10,
     		    sorting: true,
     		    actions: {
     		    listAction: function (postData, jtParams) {
     		    console.log(postData);
     			    return $.Deferred(function ($dfd) {
     			    $.ajax({
     			    url: '/mdrest/message?page=' + jtParams.jtStartIndex + '&size='+jtParams.jtPageSize,
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


     		    deleteAction: function(item) {
                console.log(item);
                    return $.Deferred(function($dfd) {
                    $.ajax({
                    url: '/mdrest/message/' + item.messagename,
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
            },



     		    fields: {
     		    modelName: {
     		        key : true,
     			    list: true,
     			    create:false,
     			    edit: false,
     			    title: 'Model Name'
     		    },
     		     modelType:{
                     list: true,
                     create:false,
                     edit: true,
                     title: 'Model Type'
                 },

                   messageName: {
                     list: true,
                     create:false,
                     edit: true,
                     title: 'Message Name'
                 },

                 properties: {
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
                                     title: ' <spring:message code="process.page.title_properties_of"/>'+' ' + item.record.modelName,
                                     paging: true,
                                     pageSize: 10,
                                     actions: {
                                         listAction: function(postData,jtParams) {
                                             return $.Deferred(function($dfd) {
                                                 console.log(item);
                                                 $.ajax({
                                                     url: '/mdrest/message/' + item.record.messagename+'?page=' + jtParams.jtStartIndex + '&size='+jtParams.jtPageSize,
                                                     type: 'GET',
                                                     data: item,
                                                     dataType: 'json',
                                                     success: function(data) {
                                                        if(data.Result == "OK") {

                                                            $dfd.resolve(data);

                                                        }
                                                        else
                                                        {
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
                                     console.log(postData.messagename);
                                        return $.Deferred(function($dfd) {
                                            $.ajax({
                                                url: '/mdrest/message/' + item.record.messagename + '/' + postData.columnName + '/',
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

                                     updateAction: function(postData, jtRecordKey) {
                                     console.log(postData);
                                        return $.Deferred(function($dfd) {
                                            $.ajax({
                                                url: '/mdrest/message/'+item.record.messagename,
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
                                     },

                                     createAction: function(postData) {
                                     console.log(postData);
                                        return $.Deferred(function($dfd) {
                                            $.ajax({
                                                url: '/mdrest/message/'+ item.record.messagename+'/',
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

             trainModel: {                    
                width: '5%',
                sorting: false,
                edit: false,
                create: false,
                title: 'Train',
                display: function(data) {
                    var $img2 = $('<span title=kill class="label-icons label-execute" ></span>');
                    $img2.click(function() {
                        console.log(data);
                        $("#train-dialog-confirm").dialog({
                            resizable: false,
                            height: 'auto',
                            modal: true,
                            buttons: {
                                Cancel: function() {
                                    $(this).dialog("close");
                                },
                                'Yes Kill': function() {
                                    $(this).dialog("close");
                                    return $.Deferred(function($dfd) {
                                        var processData = jQuery.param(data.record);
                                        console.log(processData);
                                        $.ajax({
                                            url: '/mdrest/process/kill/',
                                            type: 'POST',
                                            data: processData,
                                            dataType: 'json',
                                            success: function(data) {
                                                if(data.Result == "OK") {
                                                    console.log(data);
                                                    $("#kill-result").dialog({
                                                        resizable: false,
                                                        height: 'auto',
                                                        modal: true,
                                                        buttons: {

                                                            "OK": function() {
                                                                $('div#Container').jtable('load');
                                                                $(this).dialog("close");
                                                            }
                                                        }
                                                    }).html('<p><span class="jtable-confirm-message"><spring:message code="process.page.title_process"/>'+' <b>' +data.Record.processId +'</b> '+' successfully killed</span></p>');
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
                                                    }).html('<p><span class="jtable-confirm-message">Attempt to kill failed</span></p>');
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
            },
          });
          $('#Container').jtable('load');
        });

      </script>
  </head>

  <body>
  <div class='col-md-12' id="modelDetails" style="padding-left:0px;padding-right:0px">
      <section style="width:100%;text-align:center;">
  	    <div id="Container"></div>
      </section>
  	<div style="display:none" id="div-dialog-warning">
  			<p><span class="ui-icon ui-icon-alert" style="float:left;"></span></p>
  	</div>

   </div>

   <div id="train-dialog-confirm" style="display: none;">
       <p>
           <span class="ui-icon-alert"></span>
           <span class="dialog-title-custom"><spring:message code="process.page.span_sure"/></span>
           <span class="jtable-confirm-message">This will train the model.
           </span>
       </p>
   </div>
  	</body>

  </html>





