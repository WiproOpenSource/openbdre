<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
    <%@ page language="java" contentType="text/html; charset=ISO-8859-1"
        pageEncoding="ISO-8859-1"%>
        <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
        <html>
            <title><spring:message code="tablecolumnlineage.page.table_column_lineage_bdre"/></title>
            <head>
                <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
                <title><spring:message code="common.page.title_bdre_1"/></title>
                <script>
                    (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
                        (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
                        m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
                                            })(window,document,'script','//www.google-analytics.com/analytics.js','ga');
                    //Please replace with your own analytics id
                    ga('create', 'UA-72345517-1', 'auto');
                    ga('send', 'pageview');
                </script>

                <link href="../css/css/bootstrap.min.css" rel="stylesheet" type="text/css" />
                <link href="../css/jtables-bdre.css" rel="stylesheet" type="text/css" />
                <link href="../css/jquery-ui-1.10.3.custom.css" rel="stylesheet" type="text/css" />
                <link href="../css/bootstrap.custom.css" rel="stylesheet" type="text/css" />

                <!-- Include jTable script file. -->
                <script src="../js/jquery.min.js" type="text/javascript"></script>
                <script src="../js/jquery-ui-1.10.3.custom.js" type="text/javascript"></script>
                <script src="../js/jquery.jtable.js" type="text/javascript"></script>
                <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
                <script src="../js/jquery.steps.min.js"></script>
                <link rel="stylesheet" href="../css/jquery.steps.css" />
                <link rel="stylesheet" href="../css/jquery.steps.custom.css" />
                <script src="../js/bootstrap.js" type="text/javascript"></script>
                <script src="../js/angular.min.js" type="text/javascript"></script>
                <script src="../js/svgutil.js" type="text/javascript"></script>
                <script language="javascript" type="text/javascript" src="../js/graph/viz.js"></script>
                <script language="javascript" type="text/javascript" src="../js/graph/site.js"></script>
                <script type="text/javascript">
                    var graphViz = "";
                    var prefix = "strict digraph{\n" +
                        "ranksep=0.4;" +
                        "ratio=compact;" +
                        "rankdir=LR;" +
                        "graph [splines=true, nodesep=0.25, dpi=50];" +
                        "id=lineagegraph;" +
                        "node[nodesep=0.25,labeljust=left,margin=\".21,.055\",fontsize=10,fontname=\"verdana\"];" +
                        "\n"
                    ;
                    var postfix = "}"

                    var set = new StringSet();

                    function getTableName(tableName, colName) {
                        //do not reload if the dependency graph for this tableName is already rendered.
                        if (set.contains(tableName) && colName == "") {
                            return false;
                        }
                        set.add(tableName);
                        $.ajax({
                            url: "/mdrest/tabcollineage?tableName=" + tableName + "&colName=" + colName,
                            type: "GET",
                            cache: false,
                            success: function (getData) {
                                if(getData.Result == "OK") {
                                    console.log(getData);
                                    graphViz = graphViz + getData.Records.dot;
                                    RefreshGraphviz(prefix + graphViz + postfix);
                                    console.log("Called refreshGraphViz");
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
                                    }).html(getData.Message);
                                }
                            },
                             error : function() { $("#div-dialog-warning").dialog({

                                 resizable: false,
                                 height: 'auto',
                                 modal: true,
                                 buttons: {
                                     "Ok": function() {
                                         $(this).dialog("close");
                                     }
                                 }
                             }).html("You have entered or selected wrong Table Name or Column Name");

                            }
                        });
                    }

                    function RefreshGraphviz(data) {
                        var svg_div = jQuery('#graphviz_svg_div');
                        svg_div.html("");
                        // Generate the Visualization of the Graph into "svg".
                        var svg = Viz(data, "svg");
                        svg_div.html("<br/>"+
                                     svg);
                    }

                    function StringSet() {
                        var setObj = {}, val = {};

                        this.add = function (str) {
                            setObj[str] = val;
                        };

                        this.contains = function (str) {
                            return setObj[str] === val;
                        };

                        this.remove = function (str) {
                            delete setObj[str];
                        };

                        this.values = function () {
                            var values = [];
                            for (var i in setObj) {
                                if (setObj[i] === val) {
                                    values.push(i);
                                }
                            }
                            return values;
                        };

                        this.removeAll = function () {

                            setObj = {}, val = {};
                        };
                    }
                    function resetGraph() {
                        graphViz = "";
                        set.removeAll();
                    }
                </script>
                <style>
                    * {
                        margin:0;
                        padding:0;
                    }

                    #overlay {
                        position:fixed;
                        top:0;
                        left:0;
                        width:100%;
                        height:100%;
                        background:#000;
                        opacity:0.5;
                        filter:alpha(opacity=50);
                    }

                    #modal {
                        position:absolute;
                        background:url(tint20.png) 0 0 repeat;
                        background:rgba(0,0,0,0.2);
                        border-radius:14px;
                        padding:8px;
                    }

                    #content {
                        border-radius:8px;
                        background:#fff;
                        padding:20px;
                    }

                    #close {
                        position:absolute;
                        background:url('../css/metro/close.png') 14 14 no-repeat;
                        width:24px;
                        height:27px;
                        display:block;
                        text-indent:-9999px;
                        top:-7px;
                        right:-7px;
                    }
                    .panel-primary{
                    padding-top: 0% !important;
                    }
                </style>

                <script>
                    var modal = (function () {
                        var
                        method = {},
                            $overlay,
                            $modal,
                            $content,
                            $close;

                        // Center the modal in the viewport
                        method.center = function () {
                            var top, left;

                            top = Math.max($(window).height() - $modal.outerHeight(), 0) / 2;
                            left = Math.max($(window).width() - $modal.outerWidth(), 0) / 2;

                            $modal.css({
                                top: top + $(window).scrollTop(),
                                left: left + $(window).scrollLeft()
                            });
                        };

                        // Open the modal
                        method.open = function (settings) {
                            $content.empty().append(settings.content);

                            $modal.css({
                                width: settings.width || 'auto',
                                height: settings.height || 'auto',
                                'max-width': $(window).width() * .75,
                                overflow: 'visible',
                            });

                            method.center();
                            $(window).bind('resize.modal', method.center);
                            $modal.show();
                            $overlay.show();
                        };

                        // Close the modal
                        method.close = function () {
                            $modal.hide();
                            $overlay.hide();
                            $content.empty();
                            $(window).unbind('resize.modal');
                        };

                        // Generate the HTML and add it to the document
                        $overlay = $('<div id="overlay"></div>');
                        $modal = $('<div id="modal"><button type="button" class="btn btn-primary btn-xs" aria-label="Left Align" onClick="saveSVG(\'execution-details\',1)"><span class="glyphicon glyphicon-save" aria-hidden="true"></span>Save</button></br></div>');
                        $content = $('<div id="content"></div>');
                        $close = $('<a id="close" href="#">close</a>');

                        $modal.hide();
                        $overlay.hide();
                        $modal.append($content, $close);

                        $(document).ready(function () {
                            $('body').append($overlay, $modal);
                        });

                        $overlay.click(function (e) {
                            e.preventDefault();
                            method.close();
                        });

                        return method;
                    }());

                    // Wait until the DOM has loaded before querying the document

                    function popModal(pid) {
                        $.get('workflow/' + pid + '.page', function (data) {
                            modal.open({content: "<b>Loading</b>"});
                            UpdateGraphviz(data);
                            modal.center();
                        });
                    }
                    function popDetails(pid,ieid) {
                        $.get('details/' + pid + '/'+ieid+'.page', function (data) {
                            modal.open({content: "<b>Loading</b>"});
                            UpdateGraphviz(data);
                            modal.center();
                        });
                    }

                    <c:if test="${not empty param.tableName}">
                        $(document).ready(function () {
                            getTableName(${param.tableName},${param.colName});
                        });
                    </c:if>
                </script>

            </head>
            <body>
                <br/>
				<div class="page-header"><spring:message code="tablecolumnlineage.page.table_column_lineage"/></div>
                <div class="row">&nbsp;</div>
                <div class="row">
                    <div class="col-md-2"> </div>
                    <div class="col-md-10 divEncloseHeading" id="divEncloseHeading">
                        <c:if test="${empty param.tableName}">
                            <div class="col-md-10" id="divEncloseHeading">
                                <div class="panel panel-primary">
                                    <div class="panel-heading">
										<div class="text-right">
                                    <%-- <div class="panel-heading"><spring:message code="tablecolumnlineage.page.table_column_lineage"/> --%>
											<button type='button' class='btn btn-default' aria-label='Left Align' onClick='saveSVG("execution",0)'><span class='glyphicon glyphicon-save-file' aria-hidden='true'></span> Save </button>
                                        </div>
                                    </div>

                                    <div class="panel-body">
                                            <div class="row">
                                                 <div class="col-xs-5 form-group">
													<label><spring:message code="tablecolumnlineage.page.table_name"/></label>
                                                    <input type="text" class="form-control" name="tableName" id="tableName" value =""/>
												</div>
                                                <div class="col-xs-5 form-group">
                                                    <label><spring:message code="tablecolumnlineage.page.column_name"/></label>
													<input type="text" class="form-control" name="colName" id="colName" value =""/>
                                                </div>
											</div>
											
                                    </div>
                                    <div class="text-right actions">
											    <button class="btn btn-primary" onClick="resetGraph(); getTableName(jQuery('#tableName').val(), jQuery('#colName').val())" href="#">Show Lineage </button>
											</div>
                                </div>
                            </div>
                        </c:if>
                    </div>
                </div>
                <div id="graphviz_svg_div" style="width:100%;text-align:left;">
                    <!-- Target for dynamic svg generation -->
                </div>

                <div id="div-dialog-warning" title="Process Not Found" style="display:none;">
                    <p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span><spring:message code="tablecolumnlineage.page.process_not_found"/></p>
                </div>
            </body>
        </html>