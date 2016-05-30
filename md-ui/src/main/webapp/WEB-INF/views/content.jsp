<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
    <%@ taglib prefix="security"
	   uri="http://www.springframework.org/security/tags" %>
	   <%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
        <html id="ng-app">

        <head>
            <title><spring:message code="common.page.title_bdre_2"/></title>
	<script>
	  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
	  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
	  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
	  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');
	  //Please replace with your own analytics id
	  ga('create', 'UA-72345517-1', 'auto');
	  ga('send', 'pageview');
	</script>

            <meta name="viewport" content="width=device-width, initial-scale=1">
            <link rel="stylesheet" href="../css/css/bootstrap.min.css" />
            <link rel="stylesheet" href="../css/bootstrap.custom.css" />
            <link rel="stylesheet" href="../css/submenu.css" />
           	<script src="../js/jquery.min.js"></script>
            <script src="../js/bootstrap.js"></script>
            <script src="../js/angular.min.js"></script>
            
            <style>
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
				
				#foot {
					background: #f5f5f5;
				}
				
				.navbar-default .navbar-nav>.open>a, .navbar-default .navbar-nav>.open>a:hover,
					.navbar-default .navbar-nav>.open>a:focus {
					background-color: #FAFAFA;
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
					background: #1ca7f7 no-repeat center;
					background-image: url("../css/images/user_icon.png");
					background-size: 65% 65%;
				}
				
				.bdretextlogo {
					color: #1ca7f7;
					position: relative;
					font-size: 3em;
					top: 11px;
					right: 10px;
				}
				
				.dropdown-toggle {
					padding-top: 9px !important;
				}
			</style>
		</head>

        <body class="container-fluid" ng-app="myApp" ng-controller="myCtrl">
            <nav class="navbar navbar-inverse">
                <div class="container-fluid">
                    <!-- Brand and toggle get grouped for better mobile display -->
                    <div class="navbar-header">
                        <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
                            <span class="sr-only">Toggle navigation</span>
                            <span class="icon-bar"></span>
                            <span class="icon-bar"></span>
                            <span class="icon-bar"></span>
                        </button>

                        <a class="navbar-brand" href="#">
							<img alt="<spring:message code="common.page.title_bdre_1"/>" class="img-responsive logo" src="../css/images/bdre_logo.png" style="width:55px;">
                        </a>
						<span class="bdretextlogo"><spring:message code="content.page.app_abbrevation"/></span>
                    </div>

                    <!-- Collect the nav links, forms, and other content for toggling -->
                    <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
                        <ul class="nav navbar-nav navbar-nav-position">
                            <li ng-repeat="item in menu" ng-class="{dropdown:item.children.length!=0,activ:item.active}">
                                <a href="#" ng-hide="item.children.length==0" class="dropdown-toggle text-muted " data-toggle="dropdown" role="button" aria-expanded="false">{{item.label}} <span class="glyphicon glyphicon-chevron-down"></span></a>
                                <a href="#" ng-show="item.children.length==0" class="text-muted level1" ng-click="openlink($event,item.url)">{{item.label}}</a>
                                <ul class="dropdown-menu" ng-hide="item.children.length==0" role="menu">
                                    <li ng-repeat="x in item.children" ng-class="{'dropdown-submenu':x.children.length!=0,'node':x.children.length==0}" ng-click="reset($event.currentTarget,this.item)">
                                        <a href="#" class="level2" ng-click="openlink($event,x.url)" ng-show="x.children.length==0">{{x.label}}</a>
                                        <a href="#" tabindex="-1" ng-hide="x.children.length==0">{{x.label}}</a>
                                        <ul class="dropdown-menu" ng-hide="x.children.length==0">
                                            <li ng-repeat="y in x.children" url="{{y.url}}" ng-click="reset($event.currentTarget,this.item)" ng-class="{'node':x.children.length==0}">
                                                <a href="#" class="level3" ng-click="openlink($event,y.url)">{{y.label}}</a>
                                            </li>
                                        </ul>
                                    </li>
                                </ul>
                            </li>
                        </ul>
                        
                        <ul class="nav navbar-nav navbar-right" >
                            <li class="dropdown user-icon-style"><a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false"><span class="usericon"></span></a>
                                <ul class="dropdown-menu" role="menu">
                                    <li><a href="/auth/bdre/security/logout">Logout <security:authentication property="principal.username"/></a></li>
                                </ul>
                            </li>
                        </ul>
                        <form class="navbar-form navbar-left" role="search" style="float: right !important">
                            <div class="btn-group">
                                <input type="text" class="form-control input-sm dropdown-toggle input-sm-search" placeholder="" ng-model="searchText" id="srch" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                <span class="glyphicon glyphicon-search form-control-feedback"></span>
                                <ul class="dropdown-menu" ng-show="searchText">
                                    <li ng-repeat="m in linearMenu  | filter:searchText">
                                        <a href="#" ng-click="openlink($event, m.url)">{{m.label}}</a>
                                    </li>
                                </ul>
                            </div>
                        </form>
                    </div>
                    <!-- /.navbar-collapse -->
                </div>
                <!-- /.container-fluid -->
            </nav>

            <div>
                <iframe id="dframe" src="welcome.page" scrolling="yes" style="overflow-y:visible;width:100%; height: 800px; border: none;"></iframe>
            </div>

            <!--
	<div id="foot" class="row">
	    div id="footer" class="footer navbar-fixed-bottom"
		<p class="text-center"><ul id="listf" class="list-inline"><li ><a class="btn btn-xs" href="">Terms of use</a> </li><li> <a class="btn btn-xs" href="">Liceelnse</a> </li><li> <a class="btn btn-xs" href="">About BDRE</a> </li><li> <a class="btn btn-xs" href="">Help</a> </li><li><a class="btn btn-xs" href="">Site Map</a> </li><li> <a  class="btn btn-xs" href="">About Wipro</a></li>
		    </p>
	    </div>
	</div>
	 -->
            <script>
                var app = angular.module("myApp", []);
                app.controller("myCtrl", function ($scope) {
                            $scope.left = 0;
                            $scope.down = 1;
                            $scope.linearMenu = [];
                            $scope.createLinearMenu = function (arg) { //arg structure would be [{},{}...]
                                if (arg.length == null) {
                                    $scope.createLinearMenu([arg]);
                                } else {
                                    for (var i = 0; i < arg.length; i++) {
                                        if (arg[i].children.length == 0) {
                                            console.log(arg[i].label);
                                            $scope.linearMenu.push({
                                                label: arg[i].label,
                                                url: arg[i].url
                                            })
                                        } else {
                                            for (var j = 0; j < arg[i].children.length; j++) {
                                                $scope.createLinearMenu(arg[i].children[j]);
                                            }
                                        }
                                    }
                                }
                            }
                            $scope.menu = [{
                                    label: "About",
                                    collapse: "1",
                                    active: "1",
                                    url: "welcome.page",
                                    children: []
				}, {
                                    label: "Metadata Management",
                                    collapse: "1",
                                    children: [{
                                            label: "Master",
                                            collapse: "1",
                                            children: [{
                                                label: "Batch Status",
                                                collapse: "1",
                                                url: "batchstatus.page",
                                                children: []
							}, {
                                                label: "App Type",
                                                collapse: "1",
                                                url: "processtype.page",
                                                children: []
							}, {
                                                label: "Execution Status",
                                                collapse: "1",
                                                url: "execstatus.page",
                                                children: []
							}, {
                                                label: "Deployment Status",
                                                collapse: "1",
                                                url: "deploystatus.page",
                                                children: []
							},
							{
                                                label: "App Deployment Status",
                                                collapse: "1",
                                                url: "adqstatus.page",
                                                children: []
                            },{
                                                label: "Workflow Type",
                                                collapse: "1",
                                                url: "workflowtype.page",
                                                children: []
							}]
						}, {
                                            label: "Job Definitions",
                                            collapse: "1",
                                            children: [{
                                                    label: "Processes",
                                                    collapse: "1",
                                                    url: "process.page",
                                                    children: []
								},
                                                {
                                                    label: "Workflow Creator",
                                                    collapse: "1",
                                                    url: "wfdesigner.page",
                                                    children: []
								}, {
                                                    label: "Process Domains",
                                                    collapse: "1",
                                                    url: "busdomain.page",
                                                    children: []
								}, {
                                                    label: "Servers",
                                                    collapse: "1",
                                                    url: "servers.page",
                                                    children: []
								}, {
                                                    label: "Process Logs",
                                                    collapse: "1",
                                                    url: "processlog.page",
                                                    children: []
								}, {
                                                    label: "Properties",
                                                    collapse: "1",
                                                    url: "properties.page",
                                                    children: []
								}
							]
						},
                                        {
                                            label: "Run Control",

                                            collapse: "1",
                                            children: [{
                                                    label: "Batches",
                                                    collapse: "1",
                                                    url: "batch.page",
                                                    children: []
								}, {
                                                    label: "Files Batches",
                                                    collapse: "1",
                                                    url: "file.page",
                                                    children: []
								}, {
                                                    label: "Queued Batches",
                                                    collapse: "1",
                                                    url: "bcq.page",
                                                    children: []
								}, {
                                                    label: "Instance Execution",
                                                    collapse: "1",
                                                    url: "instanceexec.page",
                                                    children: []
								}, {
                                                    label: "Processed Batches",
                                                    collapse: "1",
                                                    url: "acq.page",
                                                    children: []
								}
							]
						}
					]
				}, {
                                    label: "Job Management",
                                    collapse: "1",
                                    children: [
                                        {
                                            label: "Process Deployment",
                                            collapse: "1",
                                            url: "pdq.page",
                                            children: []
						}, {
                                       label: "Table Column Lineage",
                                       collapse: "1",
                                       url: "tablecolumnlineage.page",
                                       children: []
                        },
                                                                              {
                                            label: "App Deployment",
                                            collapse: "1",
                                            url: "adq.page",
                                            children: []
                                                                            },{
                                            label: "Job Import Wizard",
                                            collapse: "1",
                                            url: "processimportwizard.page",
                                            children: []
                                                         					}, {
                                            label: "Process Template",
                                            collapse: "1",
                                            url: "processtemplate.page",
                                            children: []
                                                         					},{
                                              label: "App Store",
                                              collapse: "1",
                                              url: "appstore.page",
                                              children: []
                                                                            }
					]
				}, {
                                    label: "Data Ingestion",
                                    collapse: "1",
                                    children: [{
                                            label: "Load File in Hive",
                                            collapse: "1",
                                            url: "dataload.page",
                                            children: []
					}, {
                                            label: "Import from RDBMS",
                                            collapse: "1",
                                            url: "dataimportwizard.page",
                                            children: []
					},
                                        {
                                            label: "Web Crawl and Ingest",
                                            collapse: "1",
                                            url: "crawler.page",
                                            children: []
					}, {
                                               label: "Monitor Directory & Ingest",
                                               collapse: "1",
                                               url: "filemonitor.page",
                                               children: []
                    },  {
                                            label: "Generate Bulk Data",
                                            collapse: "1",
                                            url: "datagen.page",
                                            children: []
					}, {
                                            label: "New DQ Job",
                                            collapse: "1",
                                            url: "dqprocess.page",
                                            children: []
					}, {
                                            label: "Ingest from Streams",
                                            collapse: "1",
                                            url: "flumepropertieswizard.page",
                                            children: []
	                }, {
                                             label: "Analytics App",
                                             collapse: "1",
                                             url: "analyticsui.page",
                                             children: []
                    },{
                                               label: "Analytics UI",
                                               collapse: "1",
                                               url: "/aui/pages/menu.page",
                                               children: []
                      },{
                                              label: "Hive Table Migration",
                                              collapse: "1",
                                              url: "hivetablemigration.page",
                                              children: []
                     },]
                                },

                                    <security:authorize access = "hasRole('ROLE_ADMIN')"> {
                                        label: "Administration",
                                        collapse: "1",
                                        children: [{
                                                label: "Security",
                                                collapse: "1",
                                                url: "users.page",
                                                children: []
					}, {
                                                label: "Sessions",
                                                collapse: "1",
                                                url: "sessions.page",
                                                children: []
					}, {
                                                label: "Settings",
                                                collapse: "1",
                                                url: "settings.page",
                                                children: []
					}
					]
                                    },
                                    </security:authorize>
                                    ];
            $scope.createLinearMenu($scope.menu); //For creating linear menu
			$scope.openlink = function (event, url) {
                                    $(".activ").removeClass("activ");
                                    $(".B1").removeClass("B1");
                                    $(".B2").removeClass("B2");
                                    console.log($(event.target).parent().parent().parent(), $(event.target).hasClass("level1"));
                                    if ($(event.target).hasClass("level1")) {
                                        console.log("level1");
                                        $(event.target).addClass("activ");
                                    } else if ($(event.target).hasClass("level2")) {
                                        console.log("level2");
                                        $(event.target).parent().parent().parent().addClass("activ");
                                        $(event.target).addClass("B1");
                                        console.log($(event.target));
                                    } else if ($(event.target).hasClass("level3")) {
                                        console.log("level3");
                                        $(event.target).addClass("B2");
                                        var elem = $(event.target).parent().parent().parent();
                                        elem.addClass("B1");
                                        elem.parent().parent().addClass("activ");
                                    }
                                    if (url != "") {
                                        $("#dframe").attr('src', url);
                                        console.log(url, url != "");
                                    } else
                                        alert("TBD")
			};
			$scope.reset = function (target, index) {
                                    //console.log(target,index);
                                    if (index.children.length == 0) {
                                        $(".activ").removeClass("activ");
                                        $(target).addClass("activ");
                                        if (index.url != "") {
                                            var n = index.url.indexOf("/");
                                            if (n == 0) {
                                                document.location = index.url;
                                            } else {
                                                $("#dframe").attr('src', index.url);
                                                console.log(index.url, index.url != "");
                                            }
                                        } else
                                            alert("TBD")
                                    }
			};
                            });
            </script>

        </body>

        </html>