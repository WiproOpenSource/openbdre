<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	 pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
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

	<link href="../css/pages.css" rel="stylesheet" type="text/css" />
	<!-- Include one of jTable styles. -->
	<link rel="stylesheet" href="../css/css/bootstrap.min.css" />
	<script src="../js/svgutil.js" type="text/javascript"></script>
	<script src="../js/jquery.min.js" type="text/javascript"></script>
	<script language="javascript" type="text/javascript" src="../js/graph/viz.js"></script>
	<script language="javascript" type="text/javascript" src="../js/graph/site.js"></script>
	<script>
	    var graphViz = "";
	    var prefix = "strict digraph{\n" +
		    "ranksep=0.4;" +
		    "ratio=compact;" +
		    "rankdir=LR;" +
		    "graph [splines=true, nodesep=0.25];" +
		    "id=lineagegraph;" +
		    "node[nodesep=0.25,labeljust=left,margin=\".21,.055\",fontsize=.8,fontname=\"verdana\"];" +
		    "\n"
		    ;
	    var postfix = "}"

	    var set = new StringSet();

	    function getIed(ied) {
		//do not reload if the dependency graph for this ied is already rendered.
		if (set.contains(ied)) {
		    return false;
		}
		set.add(ied);
		$.ajax({
		    url: "/mdrest/lineage?ied=" + ied,
		    type: "GET",
		    cache: false,
		    success: function (getData) {
		    if(getData.Result == "OK")
         {
			graphViz = graphViz + getData.Records.dot;
			RefreshGraphviz(prefix + graphViz + postfix);
			}
            else
            {
            console.log("no batch to process getIed(ied)");
            alert("no batch to process");
            }
		    }
		});
	    }
	    function getBid(bid) {
		//do not reload if the dependency graph for this bid is already rendered.
		if (set.contains(bid)) {
		//preventDefault();
		return false;
		}
		set.add(bid);
		$.ajax({
		    url: "/mdrest/lineage/bybatch/" + bid,
		    type: "GET",
		    cache: false,
		    success: function (getData) {
		    if(getData.Result == "OK")
            {
			graphViz = graphViz + getData.Records.dot;
			RefreshGraphviz(prefix + graphViz + postfix);
			}
            else
            {
            console.log("no batch to process getBid(bid)");
            alert("no batch to process");
            }
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
	</style>

	<script>

	  function GotoProcesses() {
                     		location.href = '<c:url value="/pages/process.page" />';
                     	    }
	    var globalPid;
	    var globalIeid;
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
		    globalIeid=undefined;
		    globalPid=undefined;
		};

		// Generate the HTML and add it to the document
		$overlay = $('<div id="overlay"></div>');
		$modal = $('<div id="modal"><button type="button" class="btn btn-primary btn-xs" aria-label="Left Align" onClick="saveSVG(\'execution-details\',1)"><span class="glyphicon glyphicon-save" aria-hidden="true"></span><spring:message code="batchlineagebyinstanceexec.page.button_save"/></button></br></div>');
		$content = $('<div id="content"></div>');
		$close = $('<a id="close" href="#"><spring:message code="batchlineagebyinstanceexec.page.close"/></a>');

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
		    modal.open({content: '<b><spring:message code="batchlineagebyinstanceexec.page.loading"/></b>'});
		    UpdateGraphviz(data);
		    modal.center();
		});
	    }
	    function popDetails(pid,ieid) {
                        		$.get('details/' + pid + '/'+ieid+'.page', function (data) {
                        		    modal.open({content: '<b><spring:message code="batchlineagebyinstanceexec.page.loading"/></b>'});
                        		    UpdateGraphviz(data);
                        		    modal.center();
                        		});
                        		globalPid=pid;
                                globalIeid=ieid;
                        	    }

	    <c:if test="${not empty param.ied}">
	    $(document).ready(function () {
		getIed(${param.ied});
	    });
	    </c:if>

	     var auto = setInterval(    function ()
                                 {      if(globalIeid != undefined && globalPid != undefined){
                                      $.get('details/' + globalPid + '/'+globalIeid+'.page', function (data) {
                                                              		    modal.open({content: '<b><spring:message code="batchlineagebyinstanceexec.page.loading"/></b>'});
                                                              		    UpdateGraphviz(data);
                                                              		    modal.center();
                                                              		});}
                                 }, 5000);
	</script>

    </head>
    <body>
	<br/>
  <button style="margin-left:15px;" type='button' class='btn btn-primary' aria-label='Left Align' onClick='GotoProcesses()'><span aria-hidden='true'></span><spring:message code="lineage.page.button_gotoProcessPage"/></button>

	<button type='button' class='btn btn-primary' aria-label='Left Align' onClick='saveSVG("execution",0)'><span class='glyphicon glyphicon-save' aria-hidden='true'></span>Save</button>
	<c:if test="${empty param.ied}">
	<section>
	    <spring:message code="batchlineagebyinstanceexec.page.instance_exec_id"/><input type="number" name="ied" id="ied" value =""/>
	    <button onClick="resetGraph();
	    	getIed(jQuery('#ied').val())" href="#"><spring:message code="batchlineagebyinstanceexec.page.button_show"/></button>
	</section>
    </c:if>
    <div id="graphviz_svg_div" style="width:100%;text-align:left;">
	<!-- Target for dynamic svg generation -->
    </div>
</body>
</html>