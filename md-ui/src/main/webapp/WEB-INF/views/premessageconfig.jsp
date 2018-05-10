<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="security"
	   uri="http://www.springframework.org/security/tags" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link rel="stylesheet" href="../css/jquery.steps.css" />
        <link rel="stylesheet" href="../css/jquery.steps.custom.css" />
        <link href="../css/css/bootstrap.min.css" rel="stylesheet" type="text/css" />
        <link href="../css/jtables-bdre.css" rel="stylesheet" type="text/css" />
        <link href="../css/jquery-ui-1.10.3.custom.css" rel="stylesheet" type="text/css" />
        <link href="../css/bootstrap.custom.css" rel="stylesheet" />
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
        <script src="../js/jquery.min.js"></script>
        <script src="../js/jquery-ui-1.10.3.custom.js"></script>
        <script src="../js/jquery.steps.min.js"></script>
        <script src="../js/jquery.jtable.js" type="text/javascript"></script>
        <script src="../js/bootstrap.js" type="text/javascript"></script>
        <script src="../js/angular.min.js" type="text/javascript"></script>
        <style>
           .body input {
           border:0px !important;
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

                .jtable-bottom-panel,.jtable-no-data-row{
                    display: none;
                }
                #Process{
                float: right;
                bottom:30px;
                right:0;
                }


            #connectionDetails .form-group {
                 width: 100%;
                 /* padding: 0 10% 15px 10%; */
             }


             .form-group label {
                 /* width: 75%; */
                 float: none;
                 text-align: left !important;
             }
				body.container-fluid {
                    padding-left: 0px;
                    padding-right: 0px;
                }


                .list-group.list-group-root {
                    padding: 0;
                    overflow: hidden;
                }

                .list-group.list-group-root .list-group {
                    margin-bottom: 0;
                }

                .list-group.list-group-root .list-group-item {
                    border-radius: 0;
                    border-width: 1px 0 0 0;
                    font-size: 18px;
                }

                .list-group.list-group-root > .list-group-item:first-child {
                    border-top-width: 0;
                }

                nav.navbar.navbar-inverse{
                min-height:60px;
                }

                       </style>





                       <style>
                           	        					html,body {
                                                             height:100%;
                                                             min-height:100%;
                                                             max-height:100%;
                                                           }
                                       					body.container-fluid{
                                                               padding-left: 0px;
                                                               padding-right: 0px;
                                                           }


                                                           				body {
                                                           					overflow: visible;

                                                           				}

                                                           				#footer {
                                                           					background: #f5f5f5;
                                                           					border-top: 1px solid #EDE4BF;
                                                           					bottom: 0;
                                                           					left: 0;
                                                           					position: fixed;
                                                           					right: 0;
                                                           				}

                                                           				.activs, .activs:hover {
                                                           					border-left: 3px solid #f91;
                                                           					font-weight: bold;
                                                           					padding-left: 17px;
                                                           					color: black;
                                                           					margin-left: 0;
                                                           				}

                                                           				.activ, .activ:hover {
                                                           					/* border-left: 3px solid #f91; */
                                                           					font-weight: bold;
                                                           					padding-left: 17px;
                                                           					color: black;
                                                           					margin-left: 0;
                                                           				}
                                                           				.navbar-inverse .navbar-nav>li>a {
                                                                               color: #FFFFFF;
                                                                               font-weight
                                                                           }
                                                                           .navbar.navbar-inverse {
                                                                               height:60px;
                                                                           }

                                                           				#foot {
                                                           					background: #2F4F4F;
                                                           				}

                                                           				.navbar-default .navbar-nav>.open>a, .navbar-default .navbar-nav>.open>a:hover,
                                                           					.navbar-default .navbar-nav>.open>a:focus {
                                                           					background-color: LightSalmon;
                                                           					font-weight: bold;
                                                           				}

                                                           				.level1, .level2, .level3 {
                                                           					font-style: normal;
                                                           				}

                                                           				.B1, .B2 {
                                                           					font-weight: bold;
                                                           					border-left: 3px solid #f91;
                                                           				}

                                                           				.sideheight {
                                                           					height: 63%;
                                                           				}

                                                           				.sideimg {
                                                           					width: 11px;
                                                           					left: 23px;
                                                           					top: 40%;
                                                           					height: 30px;
                                                           					padding: 0;
                                                           				}

                                                           				.left {
                                                           					height: 97%;
                                                           				}

                                                           				.headerbor {
                                                           					border-bottom: 1px solid #EDEDED;
                                                           				}

                                                           				::-webkit-scrollbar {
                                                           					width: 8px;
                                                           				}

                                                           				::-webkit-scrollbar-track {
                                                           					-webkit-border-radius: 5px;
                                                           					border-radius: 5px;
                                                           					background: rgba(0, 0, 0, 0.02);
                                                           				}

                                                           				::-webkit-scrollbar-thumb {
                                                           					-webkit-border-radius: 5px;
                                                           					border-radius: 5px;
                                                           					background: rgba(0, 0, 0, 0.02);
                                                           				}

                                                           				::-webkit-scrollbar-thumb:hover {
                                                           					background: rgba(0, 0, 0, 0.4);
                                                           				}

                                                           				::-webkit-scrollbar-thumb:window-inactive {
                                                           					background: rgba(0, 0, 0, 0.0);
                                                           				}

                                                           				.col-bdre-collapsed {
                                                           					width: 2px;
                                                           					position: relative;
                                                           					min-height: 1px;
                                                           					padding-right: 15px;
                                                           					padding-left: 15px;
                                                           					float: left;
                                                           				}

                                                           				.bdre-full-body {
                                                           					width: 100% !important;
                                                           				}

                                                           				/* HEADER and NAV-BAR*/
                                                           				.input-sm {
                                                           					width: 250px !important;
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

                                                           				.bdretextlogo {
                                                           					color: #FFFFFF;
                                                           					position: relative;
                                                           					font-size: 2em;
                                                           					top: 11px;
                                                           					right: 10px;
                                                           				}

                                                           				.dropdown-toggle {
                                                           					padding-top: 9px !important;
                                                           				}
                                                           				.dropdown-menu {
                                                           				position:initial;
                                                           				}

                               </style>
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



                               					.subprocess-arrow-down {
                               						-ms-transform: rotate(90deg); /* IE 9 */
                               						-webkit-transform: rotate(90deg); /* Chrome, Safari, Opera */
                               						transform: rotate(90deg);
                               					}


                                                     .btn-primary1 {
                                                         background-color: #23C9A4 !important;
                                                         color: #404040 !important;
                                                         border-radius: 4px !important;
                                                         border-color: transparent;
                                                         font-size: 18px;
                                                     }   left: 420px;


                                                   .form-horizontal .form-group {
                                                        margin-right: auto;
                                                        margin-left:  auto;
                                                   }

                                                   .form-group {

                                                       margin-bottom: auto;

                                                   }


                                                   body.container-fluid{
                                                      padding-left: 0px;
                                                      padding-right: 0px;
                                                   }

                                                   body {
                                                       overflow: visible;
                                                   }

                                                   #footer {
                                                       background: #f5f5f5;
                                                       border-top: 1px solid #EDE4BF;
                                                       bottom: 0;
                                                       left: 0;
                                                       position: fixed;
                                                       right: 0;
                                                   }

                                                   .activs, .activs:hover {
                                                       border-left: 3px solid #f91;
                                                       font-weight: bold;
                                                       padding-left: 17px;
                                                       color: black;
                                                       margin-left: 0;
                                                   }

                                                   .activ, .activ:hover {
                                                       /* border-left: 3px solid #f91; */
                                                       font-weight: bold;
                                                       padding-left: 17px;
                                                       color: black;
                                                       margin-left: 0;
                                                   }
                                                   .navbar-inverse .navbar-nav>li>a {
                                                       color: #FFFFFF;
                                                       font-weight
                                                   }
                                                   .navbar.navbar-inverse {
                                                       height:60px;
                                                   }

                                                   #foot {
                                                       background: #2F4F4F;
                                                   }

                                                   .navbar-default .navbar-nav>.open>a, .navbar-default .navbar-nav>.open>a:hover,
                                                       .navbar-default .navbar-nav>.open>a:focus {
                                                       background-color: LightSalmon;
                                                       font-weight: bold;
                                                   }

                                                   .level1, .level2, .level3 {
                                                       font-style: normal;
                                                   }

                                                   .B1, .B2 {
                                                       font-weight: bold;
                                                       border-left: 3px solid #f91;
                                                   }

                                                   .sideheight {
                                                       height: 63%;
                                                   }

                                                   .sideimg {
                                                       width: 11px;
                                                       left: 23px;
                                                       top: 40%;
                                                       height: 30px;
                                                       padding: 0;
                                                   }

                                                   .left {
                                                       height: 97%;
                                                   }

                                                   .headerbor {
                                                       border-bottom: 1px solid #EDEDED;
                                                   }

                                                   ::-webkit-scrollbar {
                                                       width: 8px;
                                                   }

                                                   ::-webkit-scrollbar-track {
                                                       -webkit-border-radius: 5px;
                                                       border-radius: 5px;
                                                       background: rgba(0, 0, 0, 0.02);
                                                   }

                                                   ::-webkit-scrollbar-thumb {
                                                       -webkit-border-radius: 5px;
                                                       border-radius: 5px;
                                                       background: rgba(0, 0, 0, 0.02);
                                                   }

                                                   ::-webkit-scrollbar-thumb:hover {
                                                       background: rgba(0, 0, 0, 0.4);
                                                   }

                                                   ::-webkit-scrollbar-thumb:window-inactive {
                                                       background: rgba(0, 0, 0, 0.0);
                                                   }

                                                   .col-bdre-collapsed {
                                                       width: 2px;
                                                       position: relative;
                                                       min-height: 1px;
                                                       padding-right: 15px;
                                                       padding-left: 15px;
                                                       float: left;
                                                   }

                                                   .bdre-full-body {
                                                       width: 100% !important;
                                                   }

                                                   /* HEADER and NAV-BAR*/
                                                   .input-sm {
                                                       width: 250px !important;
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

                                                   .bdretextlogo {
                                                       color: #FFFFFF;
                                                       position: relative;
                                                       font-size: 2em;
                                                       top: 11px;
                                                       right: 10px;
                                                   }

                                                   .dropdown-toggle {
                                                       padding-top: 9px !important;
                                                   }
                                                   .dropdown-menu {
                                                   position:initial;
                                                   }
                                                   #createConnectionButton{
                                                   text-align:center;
                                                   margin:auto;
                                                   }

                           </style>








		<script >
                function fetchPipelineInfo(pid){
        			location.href = '<c:url value="/pages/lineage.page?pid="/>' + pid;
                }
        		</script >
		<script>
var jsonObj = {"Result":"OK","Records":[],"Message":null,"TotalRecordCount":0,"Record":[]};
var map = new Object();
var createJobResult;
var requiredProperties;
var sourceFlag;
var created = 0;


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


		<script type="text/javascript">
function addDataToJson(properties) {
	console.log(properties);
	var id = properties.id;
	console.log(id);
}
		</script>
			<script type="text/javascript">
        		function copyForm() {
        		    var newform2=$('#rawTableColumn').clone(); //Clone form 1
                    newform2.filter('form').prop('id', 'baseTableColumn'); //Update formID
                    newform2.children("#rawDescription").children("#rawFormGroup1").attr('id', 'baseFormGroup1');
                    newform2.children("#rawDescription").children("#rawFormGroup1").children('#rawColName.1').attr('id', 'baseColName.1');
                    newform2.children("#rawDescription").children("#rawFormGroup1").children('#rawDataType.1').attr('id', 'baseDataType.1');
                    newform2.children("#rawDescription").children("#rawFormGroup1").children('#rawRemove1').attr('id', 'baseRemove1');
                    newform2.children("#rawDeleteDiv").attr('id', 'baseDeleteDiv');
                    newform2.children("#baseDeleteDiv").children("#rawButton1").attr('id', 'baseButton1');
                    $('#baseTableColumn').replaceWith(newform2);
        		}
        		</script>

		</script>
		<script>
		var insert=1;
     var wizard = null;
     var finalJson;
     wizard = $(document).ready(function() {

	$("#bdre-data-load").steps({
		headerTag: "h3",
		bodyTag: "section",
		transitionEffect: "slide",
		enableCancelButton: true,
		onStepChanging: function(event, currentIndex, newIndex) {
			console.log(currentIndex + 'current ' + newIndex );
			if(currentIndex == 0 && newIndex == 1) {
			console.log(document.getElementById('fileFormat').elements[1].value);

			console.log(document.getElementById('fileFormat'));
           if((document.getElementById('fileformat').value=="Json" || document.getElementById('fileformat').value=="XML") && insert==1 && document.getElementById('isDefaultTemplate').value=="No"){
           var content1="";
           content1=content1+'<div class="form-group">';
           content1=content1+'<label for = "fileUpload" >File Upload</label >';
           content1=content1+'<input name = "regFile" id = "regFile" type = "file" class = "form-control" style="opacity: 100; position: inherit;" /></div>';
           content1=content1+'<div class="form-group">';
            content1=content1+'<div class="clearfix"></div>';
            var format = document.getElementById('fileformat').value;
           content1=content1+'<button class = "btn btn-default  btn-success" style="margin-top: 30px;background: lightsteelblue;" type = "button" onClick = "uploadFile(\''+format+'\')" href = "#" >Upload File</button >';
           $('#bdre-data-load').steps('insert', 1, { title: "File Upload", content: content1 });
           insert=insert+1;
           }

           if(insert==2 && document.getElementById('fileformat').value !="Json" && document.getElementById('fileformat').value !="XML" )
           {
           $('#bdre-data-load').steps('remove',1);
           insert=insert-1;
           }
			}
			return true;
		},
		onStepChanged: function(event, currentIndex, priorIndex) {
			        console.log(currentIndex + " " + priorIndex);
			        if(insert==1 && priorIndex==0 && currentIndex==1)
                    $('#rawTableColumnDetails').jtable('load');
                    if(insert==2 && priorIndex==1 && currentIndex==2)
                    $('#rawTableColumnDetails').jtable('load');
		},
		onFinished: function(event, currentIndex) {
		                         formIntoMap('fileformat_', 'fileFormat');
                                 jtableIntoMap('rawtablecolumn_', 'rawTableColumnDetails');
                                 console.log("$scope.connectionName is "+con_name);
                                 map["fileformat_connectionName"]=con_name;
                                 console.log(map);
        						$.ajax({
        							type: "POST",
        							url: "/mdrest/message/createjobs",
        							data: jQuery.param(map),
        							success: function(data) {
        								if(data.Result == "OK") {
        									created = 1;
        									$("#div-dialog-warning").dialog({
        										title: "",
        										resizable: false,
        										height: 'auto',
        										modal: true,
        										buttons: {
        											"Ok": function() {
        											    $('#Container').jtable('load');
        												$(this).dialog("close");
        												location.href = '<c:url value="/pages/premessageconfig.page"/>';
        											}
        										}
        									}).html('<p><span class="jtable-confirm-message">Message successfully created </span></p>');
        								}

        							else{
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
                                    }).html('<p><span class="jtable-confirm-message">Message is not created</span></p>');

        								}
        							}

        						});
                            return false;
		},
		onCanceled: function(event) {
			location.href = '<c:url value="/pages/premessageconfig.page"/>';
		}
	});
});

		</script>
		<script type="text/javascript">
            $(document).ready(function(){
            var next = 1;
            $(".add-more").click(function(e){
                e.preventDefault();
                var addto = "#rawDeleteDiv";
                var addRemove = "#rawFormGroup" + (next);
                next = next + 1;
                var removeBtn = '<button id="rawRemove' + (next) + '" class="btn btn-danger remove-me" ><span class="glyphicon glyphicon-trash" ></span></button></div><div id="field">';
                var newIn = '';
                newIn = newIn +  '<div class="form-group" id="rawFormGroup' + next + '">' ;
                newIn = newIn +  '<div class="col-md-3">' ;
                newIn = newIn +  '<input type="text" class="form-control input-sm" id="rawColName.' + next + '" value="" name="rawColName.' + next + '" placeholder="Column Name" />' ;
                newIn = newIn +  '</div>' ;
                newIn = newIn +  '<div class="col-md-3">' ;
                newIn = newIn +  '<input type="text" class="form-control input-sm" id=rawDataType.' + next + '" value="" name="rawDataType.' + next + '" placeholder="Data Type" />' ;
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
                        var fieldID = "#rawFormGroup" + fieldNum;
                        console.log($(this));
                        //$(this).remove();
                        $(fieldID).remove();
                    });
            });
        });
        </script>
        	<script type="text/javascript">
                    $(document).ready(function(){
                    var next = 1;
                    $(".add-more").click(function(e){
                        e.preventDefault();
                        var addto = "#baseDeleteDiv";
                        var addRemove = "#baseFormGroup" + (next);
                        next = next + 1;
                        var removeBtn = '<button id="rawRemove' + (next) + '" class="btn btn-danger remove-me" ><span class="glyphicon glyphicon-trash" ></span></button></div><div id="field">';
                        var newIn = '';
                        newIn = newIn +  '<div class="form-group" id="baseFormGroup' + next + '">' ;
                        newIn = newIn +  '<div class="col-md-3">' ;
                        newIn = newIn +  '<input type="text" class="form-control input-sm" id="baseColName.' + next + '" value="" name="baseColName.' + next + '" placeholder="Column Name" />' ;
                        newIn = newIn +  '</div>' ;
                        newIn = newIn +  '<div class="col-md-3">' ;
                        newIn = newIn +  '<input type="text" class="form-control input-sm" id=baseDataType.' + next + '" value="" name="baseDataType.' + next + '" placeholder="Data Type" />' ;
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
                                var fieldID = "#baseFormGroup" + fieldNum;
                                console.log($(this));
                                //$(this).remove();
                                $(fieldID).remove();
                            });
                    });
                });
                </script>

                          <script>
                          var restWrapper=new Object();
                                   var uploadedFileName ="";
                                    function uploadFile(msgformat){
                                   var arg= ["regFile"];
                                     var fd = new FormData();
                                    var fileObj = $("#"+arg[0])[0].files[0];
                                    var fileName=fileObj.name;
                                    fd.append("file", fileObj);
                                    fd.append("name", fileName);
                                    console.log("message format : "+msgformat);
                                    $.ajax({
                                      url: '/mdrest/filehandler/uploadFile/'+msgformat,
                                      type: "POST",
                                      data: fd,
                                      async: false,
                                      enctype: 'multipart/form-data',
                                      processData: false,  // tell jQuery not to process the data
                                      contentType: false,  // tell jQuery not to set contentType
                                      success:function (data) {
                                      console.log("Printing data");
                                      console.log( data );
                                      if(data.Result == "OK"){
                                      uploadedFileName=data.Record.fileName;
                                      restWrapper=data.Record.restWrapper;
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
                                      }).html('<p><span class="jtable-confirm-message"><spring:message code="processimportwizard.page.upload_success"/>'+' ' + uploadedFileName + '</span></p>');
                                      }
                                      else
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
                                      }).html('<p><span class="jtable-confirm-message"><spring:message code="processimportwizard.page.upload_error"/></span></p>');
                                      }
                                      return false;
                                  },
                                error: function () {
                                console.log("Inside error function");
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
                                  }).html('<p><span class="jtable-confirm-message"><spring:message code="processimportwizard.page.upload_error"/></span></p>');
                                  return false;
                                  }
                               });

                              }
                </script>
        <script type="text/javascript">
                    $(document).ready(function(){
                    var next = 1;
                    $(".add-more").click(function(e){
                        e.preventDefault();
                        var addto = "#serdePropDiv";
                        var addRemove = "#formGroupSerde" + (next);
                        next = next + 1;
                        var removeBtn = '<button id="removeserde' + (next) + '" class="btn btn-danger remove-me" ><span class="glyphicon glyphicon-trash" ></span></button></div><div id="field">';
                        var newIn = '';
                        newIn = newIn +  '<div class="form-group" id="formGroupSerde' + next + '">' ;
                        newIn = newIn +  '<div class="col-md-3">' ;
                        newIn = newIn +  '<input type="text" class="form-control input-sm" id="serdePropKey.' + next + '" value="" name="serdePropKey.' + next + '" placeholder="Serde Key" />' ;
                        newIn = newIn +  '</div>' ;
                        newIn = newIn +  '<div class="col-md-3">' ;
                        newIn = newIn +  '<input type="text" class="form-control input-sm" id="serdePropValue.' + next + '" value="" name="serdePropValue.' + next + '" placeholder="Serde Property" />' ;
                        newIn = newIn +  '</div>' ;
                        newIn = newIn + removeBtn;
                        newIn = newIn +  '</div>' ;

                        var newInput = $(newIn);
                        var removeButton = $(removeBtn);
                        $(addto).before(newInput);

                        $("#formGroupSerde" + next).attr('data-source',$(addto).attr('data-source'));
                        $("#count").val(next);

                            $('.remove-me').click(function(e){
                                e.preventDefault();
                                var fieldNum = this.id.charAt(this.id.length-1);
                                var fieldID = "#formGroupSerde" + fieldNum;
                                console.log($(this));
                                //$(this).remove();
                                $(fieldID).remove();
                            });
                    });
                });
                </script>
                      <script type="text/javascript">
                                    $(document).ready(function(){
                                    var next = 1;
                                    $(".add-more").click(function(e){
                                        e.preventDefault();
                                        var addto = "#tablePropDiv";
                                        var addRemove = "#formGroupTable" + (next);
                                        next = next + 1;
                                        var removeBtn = '<button id="removetable' + (next) + '" class="btn btn-danger remove-me" ><span class="glyphicon glyphicon-trash" ></span></button></div><div id="field">';
                                        var newIn = '';
                                        newIn = newIn +  '<div class="form-group" id="formGroupTable' + next + '">' ;
                                        newIn = newIn +  '<div class="col-md-3">' ;
                                        newIn = newIn +  '<input type="text" class="form-control input-sm" id="tablePropKey.' + next + '" value="" name="tablePropKey.' + next + '" placeholder="Table Prop Key" />' ;
                                        newIn = newIn +  '</div>' ;
                                        newIn = newIn +  '<div class="col-md-3">' ;
                                        newIn = newIn +  '<input type="text" class="form-control input-sm" id="tablePropValue.' + next + '" value="" name="tablePropValue.' + next + '" placeholder="Table Property" />' ;
                                        newIn = newIn +  '</div>' ;
                                        newIn = newIn + removeBtn;
                                        newIn = newIn +  '</div>' ;

                                        var newInput = $(newIn);
                                        var removeButton = $(removeBtn);
                                        $(addto).before(newInput);

                                        $("#formGroupTable" + next).attr('data-source',$(addto).attr('data-source'));
                                        $("#count").val(next);

                                            $('.remove-me').click(function(e){
                                                e.preventDefault();
                                                var fieldNum = this.id.charAt(this.id.length-1);
                                                var fieldID = "#formGroupTable" + fieldNum;
                                                console.log($(this));
                                                //$(this).remove();
                                                $(fieldID).remove();
                                            });
                                    });
                                });
                                </script>
		<script>
                var app = angular.module('myApp', []);
                app.controller('myCtrl', function($scope) {
                    $scope.fileformats= getGenConfigMap('file_format');
                    $scope.messageTypes={'ApacheLog':'ApacheLog','RouterLogs':'RouterLogs','Custom':'Custom'};
                    console.log($scope.fileformats);
                    $scope.formatMap=null;
                    $scope.busDomains = {};
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

                    $scope.workflowTypes = {};
                    $.ajax({
                    url: '/mdrest/workflowtype/optionslist',
                        type: 'POST',
                        dataType: 'json',
                        async: false,
                        success: function (data) {
                            $scope.workflowTypes = data;
                        },
                        error: function () {
                            alert('danger');
                        }
                    });
                });
        </script>
     <script>
      function changeme()
      {
         var filetype = document.getElementById('fileformat').value;
         console.log("filetype is "+filetype);
       console.log("function call is happening ");
       if(filetype == 'Delimited'){
       jQuery('#dilimiteddiv label').text("Delimiter");
       document.getElementById('dilimiteddiv').style.display='block';

       }
       else if(filetype == 'Regex'){
       jQuery('#dilimiteddiv label').text("Regex Pattern");
       document.getElementById('dilimiteddiv').style.display='block';
       }
       else
       {
       document.getElementById('dilimiteddiv').style.display='none';
       }
      }


      function changeMessageformat()
      {
        var messageType = document.getElementById('messageType').value;
      if(messageType=="ApacheLog")
      {
               document.getElementById('fileformat').value="Regex";
               jQuery('#dilimiteddiv label').text("Regex Pattern");
               document.getElementById('dilimiteddiv').style.display='block';
               document.getElementById('delimiter').value=".*";
      }
      }
     </script>
  <script type="text/javascript">
    		    $(document).ready(function () {
    	    $('#Container').jtable({
    	    title: 'Message List',
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
    			    }
    		    },
    		    fields: {
    		    messagename: {
    		        key : true,
    			    list: true,
    			    create:true,
    			    edit: false,
    			    title: 'Message Name'
    		    },
    		     format:{
                    key : true,
                    list: true,
                    create:true,
                    edit: false,
                    title: 'File Format'
                },

                  connectionName: {
                    key : true,
                    list: true,
                    create:true,
                    edit: false,
                    title: 'Connection Name'
                },

    			    Properties: {
                    title: 'Schema',
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
                                    title: ' <spring:message code="process.page.title_properties_of"/>'+' ' + item.record.messagename,
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
                                        }
                                    },
                                    fields: {

                                        columnName: {
                                            key: true,
                                            list: true,
                                            create: false,
                                            edit: true,
                                            title: 'Column',
                                            defaultValue: item.record.columnName,
                                        },
                                        dataType: {
                                                key: true,
                                                list: true,
                                                create: false,
                                                edit: true,
                                                title: 'Type',
                                                defaultValue: item.record.dataType,
                                            }

                                    }
                                },
                                function(data) { //opened handler

                                    data.childTable.jtable('load');
                                });
                        }); //Return image to show on the person row

                        return $img;
                    }
                }
    		    }
    	    });
    		    $('#Container').jtable('load');
    	    });
    	</script>



	</head>
<body>
<script type="text/javascript">
             var workspace="";
             function findWorkspace() {
                 var location=window.location.href;
                 console.log(window.location.href);
                 var res = location.split("/");
                 for (var i in res) {
                  if(res[i].includes("mdui")==true)
                     {
                      workspace=res[i];
                      console.log(workspace);
                     }
                 }
                 if(workspace!="mdui")
                 $('#logout').append(" from "+workspace.substring(5,workspace.length));
             }
             window.onload = findWorkspace;
             </script>
 <div ng-app="app" id="preMessageDetails" ng-controller="myCtrl">

<div class='col-md-3'>
 <form  role="form" id="connectionDetails">
  <div class="form-group">
    <label for="connectionName">Connection Configuration</label>
    <div id="connectionDropdown">
        <select class="form-control" id="connectionName" name="connectionName"  ng-change="change()" ng-model="connectionName" ng-options = "val.Value as val.Value for (file, val) in connectionsList" >
            <option  value="">Select the option</option>
        </select>
    </div>
</div>


<div class="form-group" id="topic" ng-show="IsVisible">
    <label   for="connectionName">Topic</label>
    <div >
        <select class="form-control" id="topicName" name="topicName"  ng-change="showPopup()" ng-model="topicName" ng-options = "val.Value as val.Value for (file, val) in topicList" >
            <option  value="">Select the option</option>
        </select>
    </div>
</div>
</form>
</div>
 <div class='col-md-8'>
	<div id="bdre-data-load">
			<h3>Message details</h3>
            			<section>
            <form class="form-horizontal" role="form" id="fileFormat">


                    <!-- btn-group -->
                    <div id="rawTablDetailsDB">

                    <div class="form-group" >
                    <label class="control-label col-sm-2" for="topicNameInForm">Topic Name</label>
                    <div class="col-sm-10">
                        <input type="text" class="form-control"  id="topicNameInForm" name="topicName" ng-model="topicName" readonly>
                    </div>
                </div>


                    <div class="form-group" >
                        <label class="control-label col-sm-2" for="messageName">Message Name</label>
                        <div class="col-sm-10">
                            <input type="text" class="form-control"  id="messageName" name="messageName" placeholder="message name" value="" required>
                        </div>
                    </div>


                    <div class="form-group">
                   <label class="control-label col-sm-2" for="isDefaultTemplate">Use Default Message</label>
                      <div class="col-sm-10">
                      <select class="form-control" id="isDefaultTemplate" name="isDefaultTemplate" onchange="isDefault()">
                        <option  value="No" selected>No</option>
                        <option  value="Yes">Yes</option>
                        </select>
                      </div>
                    </div>



                  <div class="form-group" style="display:none;" id="defaultMessage">
                    <label class="control-label col-sm-2"  for="fileformat" >Message Template</label>
                    <div class="col-sm-10">
                        <select class="form-control" id="messageType" name="messageType" onchange="changeMessageformat()" ng-model="messageType" ng-options = "file as val for (file, val) in messageTypes" >
                            <option  value="">Select the option</option>
                        </select>
                    </div>
                </div>

                   <div class="form-group">
                     <label class="control-label col-sm-2"  for="fileformat">Message Format</label>
                     <div class="col-sm-10">
                         <select class="form-control" id="fileformat" name="fileformat" onchange="changeme()" ng-model="fileformat1" ng-options = "file as val.value for (file, val) in fileformats" >
                             <option  value="">Select the option</option>
                         </select>
                      </div>
                  </div>

                  <div class="form-group" id="dilimiteddiv" style="display:none;" >
                    <label class="control-label col-sm-2" for="delimiter">Delimiter</label>
                    <div class="col-sm-10">
                        <input type="text" class="form-control"  id="delimiter" name="delimiter" value="" required>
                    </div>
                </div>
                      <div class="form-group" id="persistanceId">
                      <label class="control-label col-sm-2" for="persistanceId">Persistance Id</label>
                      <div class="col-sm-10">
                      <select class="form-control" id="ispersistanceId" name="persistanceId">
                      <option  value="No" selected>No</option>
                      <option  value="UUID">UUID</option>
                      </select>
                      </div>
                    </div>


                      <div class="form-group" id="indexId">
                          <label class="control-label col-sm-2" for="indexId">Index Id</label>
                          <div class="col-sm-10">
                              <select class="form-control" id="isindexId" name="indexId">
                              <option  value="No" selected>No</option>
                              <option  value="UUID">UUID</option>
                              </select>
                          </div>
                      </div>


                    <div class="clearfix"></div>
                    </div>

                    <!-- /btn-group -->

                </form>
            			</section>



			<h3>Message Schema</h3>
			<section>
			    <div id="rawTableColumnDetails"></div>
			    </section>
		</div>
        </div>
		<div style="display:none" id="div-dialog-warning">
			<p><span class="ui-icon ui-icon-alert" style="float:left;"></span></p>
		</div>
</div>
 </div>

    <script>
    $( "#advancedsection" ).click(function() {
       console.log("in advancedsection");
        $( "#advanced" ).toggle();
    });

    </script>


<script>
                var con_name="";
                var app = angular.module('app', []);
                   app.controller('myCtrl', function($scope) {
                    $scope.fileformats= getGenConfigMap('file_format');
                    $scope.messageTypes={'ApacheLog':'ApacheLog','RouterLogs':'RouterLogs'};
                    console.log($scope.fileformats);
                    $scope.formatMap=null;
                    $scope.busDomains = {};
                    $scope.connectionsList={};
                    $scope.IsVisible=false;
                    $scope.topicList={};
                   $.ajax({
                       url: '/mdrest/connections/optionslist/source',
                           type: 'POST',
                           dataType: 'json',
                           async: false,
                           success: function (data) {
                               $scope.connectionsList = data.Options;
                           },
                           error: function () {
                               alert('danger');
                           }
                       });

                    $scope.change=function()
                    {
                    console.log("function change is being called");
                    console.log("value of connectionName is "+$scope.connectionName);
                    con_name=$scope.connectionName;
                    console.log(con_name);
                    $.ajax({
                       url: '/mdrest/connections/'+$scope.connectionName+"/"+"topicName",
                           type: 'GET',
                           dataType: 'json',
                           async: false,
                           success: function (data) {
                                console.log("topic list is "+data.Options);
                                $scope.topicList = data.Options;
                           },
                           error: function () {
                               alert('danger');
                           }
                       });

                    $scope.IsVisible=true;
                    }


                     $scope.showPopup=function()
                        {
                        console.log("value of topicName is "+$scope.topicName);
                        $('#topicNameInForm').val($scope.topicName);
                        $('#connectionNameInform').val($scope.connectionName);
                        }
                });


        </script>

<script>

function isDefault()
{
console.log("value is  "+document.getElementById('isDefaultTemplate').value);
if(document.getElementById('isDefaultTemplate').value == "Yes")
document.getElementById('defaultMessage').style.display='block';
else{
document.getElementById('defaultMessage').style.display='none';
document.getElementById('messageType').value="";
}
}

function showAdvanced()
{
document.getElementById('advanced').style.display='none';
document.getElementById('persistanceId').style.display='block';
document.getElementById('indexId').style.display='block';
}

</script>
		<script type="text/javascript">
	$(document).ready(function () {
	$('#rawTableColumnDetails').jtable({
		title: 'Message column details',
		paging: false,
		sorting: false,
		create: false,
		edit: false,
		actions: {
			listAction: function(postData, jtParams) {
			var messageType = document.getElementById("messageType").value;
             console.log("message type is "+messageType);
                if(messageType=="")
                   messageType="NOTHING";
                return $.Deferred(function ($dfd) {
                if(document.getElementById('isDefaultTemplate').value=="Yes" || (document.getElementById('fileformat').value !="Json" && document.getElementById('fileformat').value !="XML")){
                $.ajax({
                        type: "POST",
                        url: "/mdrest/genconfig/"+messageType+"/?required=2",
                        dataType: 'json',
                        async: false,
                        success: function(data) {
                                   console.log(data);
                                    if(data.Result == "OK") {
                                      $dfd.resolve(data);
                                  }
                                  else
                                  {
                                   $dfd.resolve(data);
                                  }

                        },
                        error : function(data){
                            console.log(data);
                        }

                    });
                    }
                    else
                    {

                    console.log(restWrapper);
                    $dfd.resolve(restWrapper);

                    }




                });
			},
			createAction: function(postData) {
                console.log(postData);
                var serialnumber = 1;
                var rawSplitedPostData = postData.split("&");
                var rawJSONedPostData = '{';
                rawJSONedPostData += '"serialNumber":"';
                rawJSONedPostData += serialnumber;
                serialnumber += 1;
                rawJSONedPostData += '"';
                rawJSONedPostData += ',';
                for (i=0; i < rawSplitedPostData.length ; i++)
                {
                    console.log("data is " + rawSplitedPostData[i]);
                    rawJSONedPostData += '"';
                    rawJSONedPostData += rawSplitedPostData[i].split("=")[0];
                    rawJSONedPostData += '"';
                    rawJSONedPostData += ":";
                    rawJSONedPostData += '"';
                    rawJSONedPostData += rawSplitedPostData[i].split("=")[1];
                    rawJSONedPostData += '"';
                    rawJSONedPostData += ',';
                    console.log("json is" + rawJSONedPostData);
                }
                var rawLastIndex = rawJSONedPostData.lastIndexOf(",");
                rawJSONedPostData = rawJSONedPostData.substring(0,rawLastIndex);
                rawJSONedPostData +=  "}";
                console.log(rawJSONedPostData);


               var rawReturnObj='{"Result":"OK","Record":' + rawJSONedPostData + '}';
               var rawJSONedReturn = $.parseJSON(rawReturnObj);

               return $.Deferred(function($dfd) {
                                console.log(rawJSONedReturn);
                                $dfd.resolve(rawJSONedReturn);
                            });

				},

			updateAction: function(postData) {

				return $.Deferred(function($dfd) {
					console.log(postData);
					$dfd.resolve(jsonObj);
				});
			},
			deleteAction: function(item) {
				console.log(item.key);
				return $.Deferred(function($dfd) {
					$dfd.resolve(jsonObj);
				});
			}

		},
		fields: {
		    serialNumber:{
		        key : true,
		        list:false,
		        create : false,
		        edit:false
		    },

			columnName: {
				title: '<spring:message code="dataload.page.title_col_name"/>',
				width: '50%',
				edit: true,
				create:true
			},
			dataType: {

				create: true,
				title: 'Data Type',
				edit: true,
				options:{ 'String':'String',
                          'Integer':'Integer',
                          'Long':'Long',
                          'Short':'Short',
                          'Byte':'Byte',
                          'Float':'Float',
                          'Double':'Double',
                          'Decimal':'Decimal',
                          'Boolean':'Boolean',
                          'Decimal':'Decimal',
                          'Binary' : 'Binary',
                          'Date':'Date',
                          'TimeStamp':'TimeStamp'
                          }
			}
		}

	});





});

		</script>



		<script>
function buildForm(fileformat) {
	console.log('inside the function');

	$.ajax({
		type: "GET",
		url: "/mdrest/genconfig/" + fileformat + "/?required=2",
		dataType: 'json',
		success: function(data) {
			var root = 'Records';
			var div = document.getElementById('fileFormatDetails');
			var formHTML = '';
			formHTML = formHTML + '<div class="alert alert-info" role="alert"><spring:message code="dataload.page.form_alert_msg"/></div>';
			formHTML = formHTML + '<div id="Serde, OutPut and Input Format">';
			formHTML = formHTML + '<form class="form-horizontal" role="form" id = "formatFields">';



			console.log(data[root].length);
			if (data[root].length == 0){

			        formHTML = formHTML + '<div class="form-group"> <label class="control-label col-sm-3" for="inputFormat">Input Format:</label>';
                    formHTML = formHTML + '<div class="col-sm-9">';
                    formHTML = formHTML + '<input name="inputFormat" value="" placeholder="input format to be used" type="text" class="form-control" id="inputFormat"></div>';
                    formHTML = formHTML + '</div>';
                    formHTML = formHTML + '<div class="form-group"> <label class="control-label col-sm-3" for="outputFormat">Output Format:</label>';
                    formHTML = formHTML + '<div class="col-sm-9">';
                    formHTML = formHTML + '<input name="outputFormat" value="" placeholder="output format to be used" type="text" class="form-control" id="outputFormat"></div>';
                    formHTML = formHTML + '</div>';
                    formHTML = formHTML + '<div class="form-group"> <label class="control-label col-sm-3" for="serdeClass">Serde Class:</label>';
                    formHTML = formHTML + '<div class="col-sm-9">';
                    formHTML = formHTML + '<input name="serdeClass" value="" placeholder="serde class to be used" type="text" class="form-control" id="serdeClass"></div>';
                    formHTML = formHTML + '</div>';

			}else{
			$.each(data[root], function(i, v) {
				formHTML = formHTML + '<div class="form-group"> <label class="control-label col-sm-3" for="' + v.key + '">' + v.value +':</label>';
				formHTML = formHTML + '<div class="col-sm-9">';
				formHTML = formHTML + '<input name="' + v.key + '" value="' + v.defaultVal + '" placeholder="' + v.description + '" type="' + v.type + '" class="form-control" id="' + v.key + '"></div>';
				formHTML = formHTML + '</div>';
			});
			}
			formHTML = formHTML + '</form>';
		}
	});
	return true;
}

		</script>

		<script>
function testNullValues(typeOf) {
	var x = '';
	console.log('type Of ' + typeOf);
	x = document.getElementById(typeOf);
	console.log(x.length);
	var text = "";
	sourceFlag = 0;
	var i;
	for(i = 0; i < x.length; i++) {
		console.log('value for element is ' + x.elements[i].value);
		if(x.elements[i].value == '' || x.elements[i].value == null) {
			sourceFlag = 1;
		}
	}
}



		</script>

		<script>
function jtableIntoMap(typeProp, typeDiv) {
	var div = '';
	div = document.getElementById(typeDiv);
	$('div .jtable-data-row').each(function() {
		console.log(this);
		$(this).addClass('jtable-row-selected');
		$(this).addClass('ui-state-highlight');
	});

	var $selectedRows = $(div).jtable('selectedRows');
	$selectedRows.each(function() {
		var record = $(this).data('record');
		var keys = typeProp + record.columnName;
		console.log(keys);
		map[keys] = record.dataType;
		console.log(map);
	});
	$('.jtable-row-selected').removeClass('jtable-row-selected');
}

		</script>
				<script>
        function jtableIntoMapForBase(typeDiv) {
        	var div = '';
        	div = document.getElementById(typeDiv);
        	$('div .jtable-data-row').each(function() {
        		console.log(this);
        		$(this).addClass('jtable-row-selected');
        		$(this).addClass('ui-state-highlight');
        	});

        	var $selectedRows = $(div).jtable('selectedRows');
        	$selectedRows.each(function() {
        		var record = $(this).data('record');
        		console.log(record.columnName);
        		map["transform_"+record.columnName] = record.transformations;
        		map["stagedatatype_"+record.columnName] = record.dataType;
        		map["baseaction_"+record.columnName] = record.dataType;
        		map["partition_"+record.columnName] = record.partition;
        		console.log(map);
        	});
        	$('.jtable-row-selected').removeClass('jtable-row-selected');
        }

        		</script>

		<script>
function formIntoMap(typeProp, typeOf) {
	var x = '';
	x = document.getElementById(typeOf);
	var text = "";
	var i;
	for(i = 0; i < x.length; i++) {
		map[typeProp + x.elements[i].name] = x.elements[i].value;
	}
}

		</script>

        <script>
        $(document).ready(function(){
            $( "#enqueueId" ).click(function() {
              console.log("enqueid is clicked");
               $("#filePath").prop("disabled", true);
                $("#enqueueId").prop("disabled", false);
                $('#filePath').val("null");
            });

            $( "#enqueueId1" ).click(function() {
                  console.log("enqueid1 is clicked");
                    $("#enqueueId").prop("disabled", false);

                });

            $( "#filePath" ).click(function() {
                console.log("filePath is clicked");
               $("#enqueueId").prop("disabled", true);
               $("#filePath").prop("disabled", false);
               $('#enqueueId').val("null");
            });

            $( "#filePath1" ).click(function() {
                console.log("filePath is clicked");
               $("#filePath").prop("disabled", false);
            });
          });
           </script>

</div>
	</body>

</html>
