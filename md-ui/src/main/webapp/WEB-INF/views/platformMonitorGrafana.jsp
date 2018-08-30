       <html id="ng-app">

        <head>
            <title>Wi-ProActive</title>
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
html {
max-width:100%;
overflow-x: hidden;
}
body {
   margin: 0;
   overflow: hidden;
}

#iframe1 {
    position:absolute;
    left: 0px;
    width: 100%;
    top: 0px;
    height: 100%;
    z-index: 0;
    overflow-x: hidden;
}

#logout{
    position:fixed;
    top : 0px;
    right: 20px;
    z-index: 1;
}

.btn-default {
    background-color: #f5f5f5;
}

.btn {
    font-size: 11px;
    font-weight: 700;
    padding: 4px 18px;
    height: 25px;
}


</style>
</head>
<body>

<div >
    <iframe id="iframe1" src="http://ec2-52-204-70-138.compute-1.amazonaws.com:3000" width="1000" height="200"></iframe>


  </div>
</body>

        </html>
