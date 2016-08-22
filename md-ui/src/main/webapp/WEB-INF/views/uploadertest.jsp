<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<html>
<head>
    <title><spring:message code="uploadertest.page.title"/></title>
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

    	<!-- Include one of jTable styles. -->

    	<link href="../css/css/bootstrap.min.css" rel="stylesheet" type="text/css" />
    	<link href="../css/jtables-bdre.css" rel="stylesheet" type="text/css" />
    	<link href="../css/jquery-ui-1.10.3.custom.css" rel="stylesheet" type="text/css" />

    	<!-- Include jTable script file. -->
    	<script src="../js/jquery.min.js" type="text/javascript"></script>
    	<script src="../js/jquery-ui-1.10.3.custom.js" type="text/javascript"></script>
    	<script src="../js/jquery.jtable.js" type="text/javascript"></script>
    <script type="text/javascript">
        function uoloadFile(parentProcessId,subDir) {
            var fd = new FormData();
            var fileObj = jQuery('#file')[0].files[0];
            var fileName=fileObj.name;
            fd.append("file", fileObj);
            fd.append("name", fileName);
            $.ajax({
              url: "/mdrest/filehandler/upload/"+parentProcessId+"/"+subDir,
              type: "POST",
              data: fd,
              enctype: 'multipart/form-data',
              processData: false,  // tell jQuery not to process the data
              contentType: false   // tell jQuery not to set contentType
            }).done(function( data ) {

                console.log( data );
                alert('<spring:message code="uploadertest.page.alert_part_1"/>'+' '+subDir + '/'+fileName + ' '+'<spring:message code="uploadertest.page.alert_part_2"/>');
            });
            return false;
        }
    </script>
</head>

<body>
<p class="alert alert-danger"><spring:message code="uploadertest.page.file_alert"/></p>

    <form method="post" id="fileinfo" name="fileinfo" onsubmit="return submitForm();">
    <div class="form-group">
        <label><spring:message code="uploadertest.page.select_file"/></label><br>
        <input type="file" id="file" name="file" required class="form-control"/>
     </div>
        <input type="button" onClick="uoloadFile(100,'hql');" value="Upload" class="btn btn-primary"/>
    </form>
    <div id="output"></div>

</body>
</html>