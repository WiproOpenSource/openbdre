<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
    <%@ page language="java" contentType="text/html; charset=ISO-8859-1"
        pageEncoding="ISO-8859-1"%>
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

                <link href="../css/pages.css" rel="stylesheet" type="text/css" />
                <!-- Include one of jTable styles. -->
                <link rel="stylesheet" href="../css/css/bootstrap.min.css" />
                <script src="../js/svgutil.js" type="text/javascript"></script>
                <script src="../js/jquery.min.js" type="text/javascript"></script>
                <script language="javascript" type="text/javascript" src="../js/graph/viz.js"></script>
                <script language="javascript" type="text/javascript" src="../js/graph/site.js"></script>
            </head>
            <body>
                <form class="form-horizontal" role="form" id="processFieldsForm2">
                    <div id="crawlerDetails">
                        <div class="form-group">
                            <label class="control-label col-sm-3" for="urlsToSearch">Regex Pattern to search:</label>
                            <div class="col-sm-9">
                                <input type="text" class="form-control" name="urlsToSearch" placeholder="Enter Regex Pattern to search" value="skdjf">
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label col-sm-3" for="urlsToSearch">Regex Pattern to search:</label>
                            <div class="col-sm-9">
                                <input type="text" class="form-control" name="urlsToSearch" placeholder="Enter Regex Pattern to search" value="laksgd">
                            </div>
                        </div>
                    </div>
                </form>
            </body>
        </html>