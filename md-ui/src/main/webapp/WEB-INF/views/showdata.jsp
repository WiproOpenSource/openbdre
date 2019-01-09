<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	 pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
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
	<link href="../css/highlight.css" rel="stylesheet" type="text/css" />
	<link href="../css/css/bootstrap.min.css" rel="stylesheet" type="text/css">
	<link href="../css/bootstrap.custom.css" rel="stylesheet" type="text/css" />

	<!-- Include one of jTable styles. -->

	<script src="../js/svgutil.js" type="text/javascript"></script>
	<script src="../js/jquery.min.js" type="text/javascript"></script>
	<script language="javascript" type="text/javascript" src="../js/graph/viz.js"></script>
	<script language="javascript" type="text/javascript" src="../js/graph/site.js"></script>
	<script language="javascript" type="text/javascript" src="../js/highlight.pack.js"></script>

	<script>
	    var graphViz = "";
	    var prefix = "strict digraph{\n" +
		    "ratio=auto;" +
		    "rankdir=LR;" +
		    "graph [splines=true, nodesep=1];" +
		    "id=pipelinegraph;" +
		    "node[labeljust=center,margin=\".55,.055\",fontsize=.8,fontname=\"verdana\"];" +
		    "\n"
		    ;
	    var postfix = "}"

	    var set = new StringSet();

	    function getPid(pid) {
		//do not reload if the dependency graph for this pid is already rendered.
		if (set.contains(pid)) {
		    return false;
		}
		set.add(pid);
		$.ajax({
		    url: "/mdrest/prodep?pid=" + pid,
		    type: "GET",
		    cache: false,
		    success: function (getData) {
			graphViz = graphViz + getData.Records.dot;
			RefreshGraphviz(prefix + graphViz + postfix);
		    }
		});
	    }
	    function RefreshGraphviz(data) {
		var svg_div = jQuery('#graphviz_svg_div');
		svg_div.html("");
		// Generate the Visualization of the Graph into "svg".
		var svg = Viz(data, "svg");

		svg_div.html("<br/>" + svg);

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

	    function showXML(xmlContent) {
		var svg_div = jQuery('#content').text(xmlContent);

	    }
	</script>
	<style>

	    .hljs-string{
	          color: green;
              font-weight: bold;
            }
         .hljs-keyword{
            color:blue;
            font-weight: bold;
            }
           .hljs-title
           {
           color: black;
           font-weight: bold;
           }
        .hljs-section,
        .hljs-selector-class{
              color: yellow;
            }
        .hljs-template-variable,
        .hljs-deletion {
          color: blue;
        }


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
        body{
        background:#fff !important ;
        }

	    #modal {
		position:absolute;
		background:url(tint20.png) 0 0 repeat;
		background:rgba(0,0,0,0.2);
		border-radius:8px;
		overflow: hidden !important;
		}
		#content {
		border-radius:8px;
		background:#fff;
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
	    #input-box-button {
		width: 370px;
		padding: 5px;
		border: 1px solid #e4e4e4;
		border-radius: 10px;
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
			width: settings.width || '95%',
			height: settings.height || 'auto',
			overflow: 'scroll',
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
		$modal = $('<div id="modal"><button type="button" class="btn btn-primary btn-xs" aria-label="Left Align" onClick="saveSVG(\'process-details\',1)"><span class="glyphicon glyphicon-save" aria-hidden="true"></span>Save</button></br></div>');
		$content = $('<pre id="content"></pre>');
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
		    console.log("data is"+data);
		    if(data=="not allowed")
		     alert("ACCESS DENIED");
		     else{
		    modal.open({content: "<b>Loading</b>"});
		    UpdateGraphviz(data);
		    modal.center();
		    }
		});
	    }

	    function popModalXml(pid) {
		$.get('workflowxml/' + pid + '.page', function (data) {
		 if(data=="not allowed")
        		     alert("ACCESS DENIED");
        		     else{
		    modal.open({content: "<b>Loading</b>"});
		    showXML(data);
		    $('#content').each(function (i, block) {
			hljs.highlightBlock(block);
			console.log(block);
		    });
		    modal.center();
		    }
		});
	    }

	     function popModalDag(pid) {
        		$.get('airflowdag/' + pid + '.page', function (data) {
        		 if(data=="not allowed")
                		     alert("ACCESS DENIED");
                		     else{
        		    modal.open({content: "<b>Loading</b>"});
        		    showXML(data);
        		    $('#content').each(function (i, block) {
        			hljs.highlightBlock(block);
        			console.log(block);
        		    });
        		    modal.center();
        		    }
        		});
        	    }

         function GotoProcesses() {
                		location.href = '<c:url value="/pages/process.page" />';
                	    }

	    function GotoProcess(pid) {
		location.href = '<c:url value="/pages/process.page?pid="/>' + pid;
	    }



	    <c:if test="${not empty param.pid}">
	    $(document).ready(function () {
		getPid(${param.pid});
	    });
	    </c:if>
	</script>

    </head>
    <body>
	<br/>
	    <button style="margin-left:15px;" type='button' class='btn btn-primary' aria-label='Left Align' onClick='GotoProcesses()'><span aria-hidden='true'></span><spring:message code="lineage.page.button_gotoProcessPage"/></button>

		<button type='button' class='btn btn-primary' aria-label='Left Align' onClick='saveSVG("pipeline",0)'><span class='glyphicon glyphicon-save' aria-hidden='true'></span><spring:message code="lineage.page.button_save"/></button>

         	<c:if test="${empty param.pid}">

	    <div id="input-box-button" >
		<form>
		    <div class="input-group">
			<input class="form-control" type="number" name="pid" id="pid" value ="" placeholder=<spring:message code="lineage.page.parent_process_id_placeholder"/>/>
			<!-- <button  class="btn btn-default btn-lg btn-primary"><span id="sizing-addon2"><span class="glyphicon glyphicon-arrow-right" aria-hidden="true"></span> Show Lineage </button> -->
			<span class="input-group-btn">
			    <button class="btn btn-default  btn-primary" type="button" onClick="resetGraph();
				    getPid(jQuery('#pid').val())" href="#"><span id="sizing-addon2"><span class="glyphicon glyphicon-arrow-right" aria-hidden="true"></span>&nbsp;</button>
			</span>
		    </div>
		</form>
	    </div>
	</c:if>

	<div id="graphviz_svg_div" style="width:100%;text-align:center;">
	    <!-- Target for dynamic svg generation -->
	</div>
    </body>
</html>