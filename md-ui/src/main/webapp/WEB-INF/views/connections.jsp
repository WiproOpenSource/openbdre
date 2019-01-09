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
    <script type="text/javascript">

    var map = new Object();
    var selectedSourceType = '';
    var selectedEmitterType = '';
    var selectedPersistentStoreType = '';
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
<script>
function source()
      {
       console.log("function call is happening in source");
       document.getElementById('createbutton').style.display='none';
       document.getElementById('source-tab').style.display='block';
       document.getElementById('emitter-tab').style.display='none';
       document.getElementById('persistent-stores-tab').style.display='none';
       document.getElementById('saved-connections').style.display='none';
      }

      function emitter()
            {
             console.log("function call is happening in emitter");
             document.getElementById('createbutton').style.display='none';
             document.getElementById('source-tab').style.display='none';
             document.getElementById('emitter-tab').style.display='block';
             document.getElementById('persistent-stores-tab').style.display='none';
             document.getElementById('saved-connections').style.display='none';
            }


       function persistance()
          {
           console.log("function call is happening in persistance");
           document.getElementById('createbutton').style.display='none';
           document.getElementById('source-tab').style.display='none';
           document.getElementById('emitter-tab').style.display='none';
           document.getElementById('persistent-stores-tab').style.display='block';
           document.getElementById('saved-connections').style.display='none';
          }

        function create()
            {
            console.log(window.location.href);
            var str=window.location.href;
            if(str.includes("source")==true)
             source();
             if(str.includes("emitter")==true)
             emitter();
             if(str.includes("persistance")==true)
              persistance();
            }
</script>
</head>
<body ng-app="myApp" ng-controller="myCtrl">

 <button type="button" class=" btn-primary1" id="createbutton" style="margin-left:88.9%;margin-bottom: 5px;"onclick="create()">Create Connection</button>
   <div id="tabs" style="background:transparent" width="1000px">
   </div>
     <div id="source-tab" style="display:none;">
                <section >
                   <div id="sourceConnectionFields" style="padding-left: 15%;">
                        <label  class="control-label col-sm-3" style="padding-left:0px;">Source Configuration Type</label>
                        <div id="dropdownSource" class="btn-group"  >
                            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="false" aria-expanded="true" id="srcDropdown">
                                <span>Select Source</span><span class="caret"></span>
                            </button>
                            <ul class="dropdown-menu" aria-labelledby="srcDropdown">
                                <li>
                                    <a href="#"></a>
                                </li>
                            </ul>
                         </div>
                      </div>
                      </section>
                      </div>
                    <div style="background: white;margin-left: 15%;margin-right: 15%;">
                    <form class="form-horizontal" role="form" id="sourceConnectionForm">

                    </form>
                    <div id="button_sourceConnectionForm">
                     </div>
                    <div class="clearfix"></div>
                    </div>







      <div id="emitter-tab" style="display:none;">
             <section >

                <div id="emitterConnectionFields" style="padding-left: 15%;">
                <label style="padding-left:0px;" class="control-label col-sm-3">Emitter Configuration Type</label>
                <div id="dropdownEmitter" class="btn-group" >
                    <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="false" aria-expanded="true" id="emitterDropdown">
                        <span>Select Emitter</span><span class="caret"></span>
                    </button>
                    <ul class="dropdown-menu" aria-labelledby="emitterDropdown">
                        <li>
                            <a href="#"></a>
                        </li>
                    </ul>
                 </div>
              </div>
              </section>
              </div>


            <div style="background: white;margin-left: 15%;margin-right: 15%;">
            <form class="form-horizontal" role="form" id="emitterConnectionForm">

             </form>
             <div id="button_emitterConnectionFields">
              </div>
             <div class="clearfix"></div>
             </div>



         <div id="persistent-stores-tab" style="display:none;">
               <section >


           <div id="persistentStoresConnectionDetails" style="padding-left: 15%;">
                <label style="padding-left: 0px;" class="control-label col-sm-3">PersistentStore Configuration Type</label>
                <div id="dropdownPersistentStores" class="btn-group" >
                    <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="false" aria-expanded="true" id="persistentStoresDropdown">
                        <span>Select Persistent Store</span><span class="caret"></span>
                    </button>
                    <ul class="dropdown-menu" aria-labelledby="persistentStoresDropdown">
                        <li>
                            <a href="#"></a>
                        </li>
                    </ul>
                 </div>
              </div>
              </section>
              </div>


                       <div style="background: white;margin-left: 15%;margin-right: 15%;">
                       <form class="form-horizontal" role="form" id="persistentStoresConnectionForm">

           </form>
            <div id="button_persistentStoresConnectionForm">
             </div>
            <div class="clearfix"></div>
            </div>



      <div id="saved-connections">
        <section style="width:100%;text-align:center;">
        <div id="Container"></div>
        </section>
       </div>



     <script>

     $( function() {
       $( "#tabs" ).tabs();
     });


     function createConnectionFunction(connectionType){
                                     console.log("inside create connection function")
                                     formIntoMap(connectionType+'_', connectionType+'ConnectionForm');
                                     if(connectionType=="source"){
                                         map['type_source'] = "source_"+selectedSourceType;
                                     }
                                     if(connectionType=="emitter"){
                                         map['type_emitter'] = "emitter_"+selectedEmitterType;
                                     }
                                     if(connectionType=="persistentStores"){
                                         map['type_persistentStores'] = "persistentStore_"+selectedPersistentStoreType;
                                     }
                                     $.ajax({
                                         type: "POST",
                                         url: "/mdrest/connections/createconnection",
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

                                                 if(connectionType=="source"){
                                                      location.href = '<c:url value="/pages/connections.page?type=source"/>';
                                                  }
                                                  if(connectionType=="emitter"){
                                                      location.href = '<c:url value="/pages/connections.page?type=emitter"/>';
                                                  }
                                                  if(connectionType=="persistentStores"){
                                                      location.href = '<c:url value="/pages/connections.page?type=persistance"/>';
                                                  }

                                                             $(this).dialog("close");
                                                         }
                                                     }
                                                 }).html('<p><span class=\"jtable-confirm-message\">Connection saved</span></p>');

                                             }

                                         }

                                     });
                                 }
     </script>

	<script>
            $('#dropdownSource').on('show.bs.dropdown', function() {
            		$.ajax({
            			type: "GET",
            			url: "/mdrest/genconfig/Source_Connection_Type/?required=2",
            			dataType: 'json',
            			success: function(data) {
            				var root = 'Records';
            				var ul = $('#srcDropdown').parent().find($("ul"));
            				$(ul).empty();
            				$.each(data[root], function(i, v) {
            					$(ul).append('<li><a href="#">' + v.value + '</a></li>');
            					var li = $(ul).children()[i];
            					$(li).data(v);
            				});
            			},
            		});

            	});

                $('#dropdownEmitter').on('show.bs.dropdown', function() {
                        $.ajax({
                            type: "GET",
                            url: "/mdrest/genconfig/Emitter_Connection_Type/?required=2",
                            dataType: 'json',
                            success: function(data) {
                                var root = 'Records';
                                var ul = $('#emitterDropdown').parent().find($("ul"));
                                $(ul).empty();
                                $.each(data[root], function(i, v) {
                                    $(ul).append('<li><a href="#">' + v.value + '</a></li>');
                                    var li = $(ul).children()[i];
                                    $(li).data(v);
                                });
                            },
                        });

                    });

                    $('#dropdownPersistentStores').on('show.bs.dropdown', function() {
                            $.ajax({
                                type: "GET",
                                url: "/mdrest/genconfig/PersistentStores_Connection_Type/?required=2",
                                dataType: 'json',
                                success: function(data) {
                                    var root = 'Records';
                                    var ul = $('#persistentStoresDropdown').parent().find($("ul"));
                                    $(ul).empty();
                                    $.each(data[root], function(i, v) {
                                        $(ul).append('<li><a href="#">' + v.value + '</a></li>');
                                        var li = $(ul).children()[i];
                                        $(li).data(v);
                                    });
                                },
                            });

                        });

          	$('#dropdownSource').on('click', 'a', function() {

          		var car = $(this).parent();
          		console.log($(car));
          		var cardata = $(car).data();
          		console.log($(cardata));
          		$('#srcDropdown').html(cardata.value + '<span class="caret"></span>');

          		selectedSourceType = cardata.key;
          		buildForm(selectedSourceType + "_Source_Connection", 'sourceConnectionForm');
          		console.log(selectedSourceType);
          	});

            $('#dropdownEmitter').on('click', 'a', function() {

                var car = $(this).parent();
                console.log($(car));
                var cardata = $(car).data();
                console.log($(cardata));
                $('#emitterDropdown').html(cardata.value + '<span class="caret"></span>');

                selectedEmitterType = cardata.key;
                buildForm(selectedEmitterType + "_Emitter_Connection", 'emitterConnectionForm');
                console.log(selectedEmitterType);
            });

            $('#dropdownPersistentStores').on('click', 'a', function() {

                var car = $(this).parent();
                console.log($(car));
                var cardata = $(car).data();
                console.log($(cardata));
                $('#persistentStoresDropdown').html(cardata.value + '<span class="caret"></span>');

                selectedPersistentStoreType = cardata.key;
                buildForm(selectedPersistentStoreType + "_PersistentStores_Connection", 'persistentStoresConnectionForm');
                console.log(selectedPersistentStoreType);
            });



    	    $('#Container').jtable({
    	    title: 'Connections List',
    		    paging: true,
    		    pageSize: 10,
    		    sorting: true,
    		    actions: {
    		    listAction: function (postData, jtParams) {
    		    var type;
                    console.log(window.location.href);
                    var str=window.location.href;
                    if(str.includes("source")==true)
                     type = "source";
                     if(str.includes("emitter")==true)
                     type = "emitter";
                     if(str.includes("persistance")==true)
                      type = "persistentStore";

    		    console.log(postData);
    			    return $.Deferred(function ($dfd) {
    			    $.ajax({
    			    url: '/mdrest/connections/listbytype/'+ type +'?page=' + jtParams.jtStartIndex + '&size='+jtParams.jtPageSize,
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
                            url: '/mdrest/connections/' + item.connectionName,
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

			 	Properties: {
					width: '1%',
					sorting: false,
					edit: false,
					create: false,
					title: 'Properties',
					listClass: 'bdre-jtable-button',
					display: function(item) {
                    var $img = $('<img class="subprocess-arrow" src="../css/images/subprocess-rarrow.png" title=<spring:message code="process.page.img_sub_process_info"/> />');//Open child table when user clicks the image
						$img.click(function() {
							$('.subprocess-arrow').removeClass('subprocess-arrow-down');
							$(this).addClass('subprocess-arrow-down');

                            $('#Container').jtable('openChildTable',
                                $img.closest('tr'), {
                                    title: 'Properties_of'+' ' + item.record.connectionName,
                                    paging: true,
                                    pageSize: 10,
                                    actions: {
                                        listAction: function(postData,jtParams) {
                                            return $.Deferred(function($dfd) {
                                                console.log(item);
                                                $.ajax({
                                                    url: '/mdrest/connections/list/' + item.record.connectionName+'?page=' + jtParams.jtStartIndex + '&size='+jtParams.jtPageSize,
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


                                         updateAction: function(postData,jtRecordKey) {
                                                        console.log(postData);
                                                       return $.Deferred(function($dfd) {
                                                        $.ajax({
                                                           url: '/mdrest/connections/update/'+item.record.connectionName,
                                                           type: 'POST',
                                                           data: postData,
                                                           dataType: 'json',
                                                            success: function(data) {
                                                           if(data.Result == "OK") {

                                                               $dfd.resolve(data);

                                                           }
                                                           }
                                                         ,
                                                           error: function() {
                                                               $dfd.reject();
                                                           }
                                                       });
                                                       });
                                                     },



                                                          deleteAction: function(postData) {
                                                           console.log(postData);
                                                         return $.Deferred(function($dfd) {
                                                               $.ajax({
                                                                url: '/mdrest/connections/' + item.record.connectionName + '/' + postData.propKey + '/',
                                                              type: 'DELETE',
                                                                 data: item,
                                                                 dataType: 'json',
                                                                 success: function(data) {
                                                                     if(data.Result == "OK") {

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
                                               var type;
                                               console.log(window.location.href);
                                                var str=window.location.href;
                                                   if(str.includes("source")==true)
                                                     type = "source";
                                                  if(str.includes("emitter")==true)
                                                        type = "emitter";
                                                    if(str.includes("persistance")==true)
                                                    type = "persistentStore";

                                                         console.log(postData);
                                                       return $.Deferred(function($dfd) {
                                                              $.ajax({
                                                          url: '/mdrest/connections/insert/' + item.record.connectionName + '/' + type + '/',
                                                                type: 'PUT',
                                                               data: postData,
                                                                  dataType: 'json',
                                                              success: function(data) {
                                                                   if(data.Result == "OK") {

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

                                        propKey: {
                                            key: true,
                                            list: true,
                                            create: true,
                                            edit: true,
                                            title: 'Property Key',
                                            defaultValue: item.record.propKey,
                                        },
                                        propValue: {
                                                list: true,
                                                create: true,
                                                edit: true,
                                                title: 'Property Value',
                                                defaultValue: item.record.propValue,
                                            }

                                    }
                                },
                                function(data) { //opened handler

                                    data.childTable.jtable('load');
                                });
                        }); //Return image to show on the person row

                        return $img;
                    }
                },

                 connectionType: {
                    width: '5%',
                      key : true,
                      list: true,
                      create:false,
                      edit: false,
                      title: 'Connection Type'
                  },
                connectionName: {
                    width: '5%',
                    key : true,
                    list: true,
                    create:false,
                    edit: false,
                    title: 'Connection Name'
                }

    		    }
    	    });
    		    $('#Container').jtable('load');

    </script>

    <script>
        function formIntoMap(typeProp, typeOf) {
            var x = '';
            x = document.getElementById(typeOf);
            console.log(x);
            var text = "";
            var i;
            for(i = 0; i < x.length; i++) {

                if(x.elements[i].name.endsWith("ConnectionName")){
                    map["connectionName"] = x.elements[i].value;
                }
                else{
                    map[typeProp + x.elements[i].name] = x.elements[i].value;
                    console.log(map[typeProp + x.elements[i].name]);
                    console.log(x.elements[i].value);
                    }
            }

        }

   </script>

    <script>
        function buildForm(typeOf, typeDiv) {
            console.log('inside the build form function');
            var connectionType = typeDiv.replace("ConnectionForm", "");
            $.ajax({
                type: "GET",
                url: "/mdrest/genconfig/" + typeOf + "/?required=1",
                dataType: 'json',
                success: function(data) {
                    var root = 'Records';
                    var div = document.getElementById(typeDiv);

                    console.log(data[root]);
                    $('.buildForm').remove();
                    var key="";
                    var value="";
                       console
                       if(typeOf.includes("Source")==true)
                          {
                          key="sourceConnectionName";
                          value="Source Configuration Name";
                          }
                         else if(typeOf.includes("Emitter")==true)
                          {
                          key="emitterConnectionName";
                          value="Emitter Configuration Name";
                          }
                          else
                          {
                          key="persistentStoresConnectionName";
                          value="PersistentStore Configuration Name";
                          }


                      var inputHTML = '';
                      inputHTML = inputHTML + '<div class="form-group buildForm">';
                      inputHTML = inputHTML +  '<label class="control-label col-sm-2 for="' + key + '">' + value + '</label>';
                      inputHTML = inputHTML + '<div class="col-sm-10"> <input name="' + key +'" type="' + "text" + '" class="form-control" id="' + key + '"></div>';
                      inputHTML = inputHTML + '</div>';
                     $('#'+typeDiv).append(inputHTML);
                    $.each(data[root], function(i, v) {
                         inputHTML = '';
                         inputHTML = inputHTML + '<div class="form-group buildForm">';
                            inputHTML = inputHTML +  '<label class="control-label col-sm-2 for="' + v.key + '">' + v.value + '</label>';
                            inputHTML = inputHTML + '<div class="col-sm-10"> <input name="' + v.key + '" value="' + v.defaultVal + '" type="' + v.type + '" class="form-control" id="' + v.key + '"></div>';
                            inputHTML = inputHTML + '</div>';
                           $('#'+typeDiv).append(inputHTML);
                      });
                       $('#'+typeDiv).append('<div class="clearfix"></div>');
                      var buttonHTML = '';
                      buttonHTML = buttonHTML + '<div class="actions text-center buildForm">';
                      buttonHTML = buttonHTML + '<button onclick="createConnectionFunction(\''+connectionType+ '\');" type="button" id="createConnectionButton" class="btn btn-primary">&nbsp;Create Connection</button> </div>';
                     $('#button_'+typeDiv).append(buttonHTML);
                    console.log(div);
                }
            });
            return true;
        }

         </script>





<div id="div-dialog-warning"/>
</div>
</body>

</html>

