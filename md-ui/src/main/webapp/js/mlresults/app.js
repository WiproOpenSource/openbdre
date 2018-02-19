	var app = angular.module("uigridApp", ["ui.grid","ui.grid.pagination","ui.grid.exporter"]);
        var ml_output=[];
        var ml_coefficients=[];
        var ml_intercept;
        var ml_algo;
        var slopeCoefficientMap = {};
        var parsedParamArray = {};
        var classificationArray = [];
        var dataArray = [];
        var kMeansClusterMap ={};
        var predictionEncodeMap ={};
        var linearEquation = "";
        //var intercept;
        //var mLModel;
        app.controller("uigridCtrl", function ($scope) {
        //console.log(window.location.href);
	$scope.str = window.location.href.split("=")[1];
	$scope.users={};
	$scope.srenv="localhost:10000";
	var childProcessId="";
	$scope.predictionName = [];
	$scope.mapLogisticPredictionFlag = false;
	   $.ajax({
                url: "/mdrest/ml/data/" + $scope.srenv + '/' + "default" + '/' + $scope.str,
                type: 'GET',
                dataType: 'json',
                async: false,
                success: function (data) {
                    //console.log(data);
                    $scope.users= data.Records;
                    ml_output = data.Records;
                    //localStorage.setItem("ml_output",JSON.stringify(data.Records));


                },
                error: function () {
                    alert('error in getting grid data');
                }
         });

        $scope.gridOptions = {
                enableFiltering: true,
                enableGridMenu: true,
                exporterMenuPdf: false,
                exporterMenuExcel: false,
                exporterCsvFilename: 'ml-output.csv',
                exporterCsvLinkElement: angular.element(document.querySelectorAll(".custom-csv-link-location")),
                paginationPageSizes: [25, 50, 75],
                paginationPageSize: 25,
                onRegisterApi: function (gridApi) {
                    $scope.grid1Api = gridApi;
                }
            };
        $scope.gridOptions.data = $scope.users;

         $.ajax({
                url: "/mdrest/subprocess/"+$scope.str,
                type: 'GET',
                dataType: 'json',
                async: false,
                success: function (data) {
                    //console.log(data.Record[0].processId);
                    childProcessId = data.Record[0].processId;
                    fetchMLAttributes(childProcessId);
                },
                error: function () {
                    alert('error in getting sub-process id');
                }
         });

         function fetchMLAttributes(childProcessId) {
             $.ajax({
                 url: "/mdrest/properties/" + childProcessId + "/ml",
                 type: 'GET',
                 dataType: 'json',
                 async: false,
                 success: function (data) {
                     //console.log(data);
                     prepareMLProperties(data.Records);
                     initSelectOptions();

                 },
                 error: function () {
                     alert('error in getting ML properties for the selected ML model');
                 }
             });
         }


        function prepareMLProperties(record){
                var mlProp = {};
                var keyTemp="";
                var valueTemp="";
                record=JSON.stringify(record);
                $.each(JSON.parse(record), function(idx, obj) {
                    $.each(obj, function(k, v) {
                        if(k === "key"){
                            keyTemp = v;
                        }else if(k === "value"){
                            valueTemp = v;
                        }


                    });
                    mlProp[keyTemp]=valueTemp;

                });
                //localStorage.setItem("ml-algo",mlProp["ml-algo"]);
                ml_algo = mlProp["ml-algo"];
                if(mlProp["ml-algo"] === "LinearRegression"){
                    //console.log("success - rr")
                    //var temp = extractLinearCoefficients(mlProp["coefficients"]);
                    ml_coefficients = extractLinearCoefficients(mlProp["coefficients"]);
                    //localStorage.setItem("ml-coefficients",temp);
                    //localStorage.setItem("ml-intercept",mlProp["intercept"]);
                    ml_intercept = mlProp["intercept"];
                }
                //console.log(mlProp);
                if(mlProp["ml-algo"] === "LogisticRegression"){
                    $scope.mapLogisticPredictionFlag = true;
                }
         }

         function extractLinearCoefficients(coefficients) {
             var finalArray = [];
             var temp = coefficients.split(",");
             $.each(temp, function (index, value) {
                 var temp2 = value.split(":");
                 finalArray.push(parseFloat(temp2[1]));
             });
             return finalArray;
         }

            function initSelectOptions() {
                //Variable declaration
                var modelAttributes = [];
                parsedParamArray = {};
                var bestFitYArray = [];
                var isLinearRegression = false; //flag for testing to switch b/w linear and logistic regression graph
                dataArray = [];
                classificationArray = [];
                slopeCoefficientMap = {};
                var slopeCoefficient = "";
                var lastAttribute = "";
                var mLModel = ml_algo;//localStorage.getItem("ml-algo"); //"Linear";//"Logistic"//"KMeans"//"Linear";
                var temp;
                kMeansClusterMap = {"0": "versicolor", "1": "setosa", "2": "virginica"};
                var optionElemX='';
                var optionElemY='';

                var xAxis = "";
                var yAxis = "";
                var plotType = "";

                var slopeArray = ml_coefficients;//JSON.parse("[" + localStorage.getItem("ml-coefficients") + "]");
                //[-1.96586443,-0.23710717,0.06238536,-0.15547643];//Get the slope data from MySQL database
                //console.log(slopeArray);
                intercept = parseFloat(ml_intercept);//[454.01394354];//Get the slope data from MySQL database

                //console.log(intercept);
                //console.log(ml_algo);

                //Get the data from the json file and later to be integrated with backend system
                //if(mLModel ==='LinearRegression'){
                //temp = localStorage.getItem("ml_output");//data1.linear;
                //json = JSON.parse(temp);
                json = ml_output;
                /*}else if(mLModel ==='LogisticRegression'){
                    json = data2.logistic;//data4.logistic;
                }else if(mLModel ==='KMeans'){
                    json = data3.kmeans;//data5.logistic;
                }*/

                //Get the attribute names/feature names of the model from the first object
                if (json.length > 0) {
                    temp = json[0];
                    var temp2 = Object.keys(temp);
                    $.each(temp2, function (index, value) { //ignore rawprediction and probability attributes
                        if (value !== 'rawprediction' && value !== 'probability') {
                            modelAttributes.push(value);
                        }
                    });
                }
                if (mLModel !== 'LinearRegression') {
                    lastAttribute = modelAttributes.pop();//This pops out the classification attribute
                }
                //console.log(modelAttributes);
                //console.log(lastAttribute);

                if(mLModel === 'LinearRegression'){
                    //Map the slope co-efficient to its corresponding attributes
                    $.each(modelAttributes , function (index, value){
                        slopeCoefficientMap[value]= slopeArray[index];
                    });
                    //console.log(slopeCoefficientMap);
                    json = JSON.stringify(json);//needed to stringify before parsing

                    //Parse the json response and push the attribute values to its corresponding properties
                    for(var t=0;t<modelAttributes.length;t++){
                        var attribute=modelAttributes[t];
                        parsedParamArray.attribute =[];
                        temp = [];
                        $.each(JSON.parse(json), function(idx, obj) {
                            $.each(obj, function(k, v) {
                                if(k === attribute){
                                    temp.push(v);
                                }

                            });
                        });
                        parsedParamArray[attribute] = temp;

                    }
                }else{
                    //Group the result based on the class
                    result = json.reduce(function (r, a) {
                        r[a[lastAttribute]] = r[a[lastAttribute]] || [];
                        r[a[lastAttribute]].push(a);
                        return r;
                    }, Object.create(null));

                    //console.log(result);

                    $.each(result , function (key, value){
                        //alert(value);
                        classificationArray.push(key);
                        predictionEncodeMap[key] = key; //Initializing the prediction Map as default
                        parsedParamArray[key] = {};
                        //createParsedParamArray(value);

                        //Parse the json response and push the attribute values to its corresponding properties
                        for(var t=0;t<modelAttributes.length;t++){
                            var attribute=modelAttributes[t];
                            parsedParamArray[key][attribute] =[];
                            temp = [];
                            //value = JSON.stringify(value);//needed to stringify before parsing
                            //$.each(value, function(idx, obj) {
                            for(var m=0;m<value.length;m++){
                                var obj=value[m];
                                $.each(obj, function(k, v) {
                                    if(k === attribute){
                                        temp.push(v);
                                    }

                                });
                                //});
                            }
                            parsedParamArray[key][attribute] = temp;

                        }

                    });
                }
                //console.log(classificationArray);
                //console.log(predictionEncodeMap);
                $scope.classificationElements = classificationArray;

                $.each(modelAttributes, function(index, attribute) {
                    if(mLModel === 'LinearRegression'){
                        if(attribute.toLowerCase().indexOf("prediction") < 0){
                            optionElemX+='<option>'+attribute+'</option>';
                        }else{
                            optionElemY+='<option>'+attribute+'</option>';
                        }
                    }else{
                        optionElemX+='<option>'+attribute+'</option>';
                        optionElemY+='<option>'+attribute+'</option>';
                    }

                });
                $("#xAxis").html(optionElemX).selectpicker('refresh');
                $("#yAxis").html(optionElemY).selectpicker('refresh');

            }



            $scope.plotGraph = function(){

                if(ml_algo === 'LinearRegression'){
                    prepareLinearGraphAttributes();
                }else if(ml_algo === 'LogisticRegression'){
                    //decode the prediction values
                    $scope.decodePredictionValues();
                    //alert($scope.predictionName[0]);
                    prepareLogisticGraphAttributes();
                }else if(ml_algo ==='KMeans'){
                    prepareLogisticGraphAttributes();
                }

            }

            $scope.decodePredictionValues = function(){

                $.each(classificationArray, function( index, value ) {
                    if($scope.predictionName[index] !== undefined) {
                        predictionEncodeMap[value] = $scope.predictionName[index];
                    }
                })

            }








        });



    /*function plotGraph($scope){

        if(ml_algo === 'LinearRegression'){
            prepareLinearGraphAttributes();
        }else if(ml_algo === 'LogisticRegression'){
            //decode the prediction values
            decodePredictionValues();
            alert($scope.predictionName[0]);
            prepareLogisticGraphAttributes();
        }else if(ml_algo ==='KMeans'){
            prepareLogisticGraphAttributes();
        }

    }*/

    function decodePredictionValues(){
        //alert($scope.predictionName[0]);
    }

    function prepareLinearGraphAttributes(){
        xAxis = $("#xAxis").val();
        yAxis = $("#yAxis").val();
        plotType = $("#plotType").val();
        slopeCoefficient = slopeCoefficientMap[xAxis];
        xAxisArray = parsedParamArray[xAxis];
        yAxisArray = parsedParamArray[yAxis];
        linearEquation = "";
        computeBestFitValues(xAxisArray,yAxisArray,xAxis);
        drawLinearGraph(xAxisArray,yAxisArray,plotType,xAxis,linearEquation);
    }

    function drawLinearGraph(xAxisArray,yAxisArray,plotType,xAxis){

        var trace = {
            x: xAxisArray,
            y: yAxisArray,
            mode: 'markers',
            type: plotType,
            name: 'Linear Regression' ,
            marker: { size: 3 }
        };

        var bestFit = {
            x: xAxisArray,
            y: bestFitYArray,
            mode: 'line',
            type: plotType,
            name: 'BestFit : '+linearEquation,
            marker: { size: 3 }
        };


        var data = [trace,bestFit];

        var layout = {

            title:'Relationship between '+xAxis+' & '+yAxis,
            xaxis: {
                title: xAxis,
                showgrid: false,
                zeroline: false
            },
            yaxis: {
                title: yAxis,
                showline:false
            },
            width: 1000,
            height: 500

        };

        Plotly.newPlot('modelGraph', data, layout);

    }


    function computeBestFitValues(xAxisArray,xAxis){
        bestFitYArray=[];
        //Compute xAvg and yAvg
        var xAvg = 0;
        var yAvg = 0;
        var temp = 0;
        var xMinusXAvg = 0;
        var yMinusyAvg = 0;
        var productMean = 0;
        var squaredMean = 0;
        var calculatedSlope = 0;
        var calculatedYIntercept = 0;
        //https://www.varsitytutors.com/hotmath/hotmath_help/topics/line-of-best-fit

        $.each(xAxisArray, function( index, value ) {
        	temp += value;

        });
        xAvg = temp/(xAxisArray.length);
        temp = 0;
        $.each(yAxisArray, function( index, value ) {
        	temp += value;

        });
        yAvg = temp/(yAxisArray.length);

        //Compute slope
        for(i=0;i<xAxisArray.length;i++){
        	xMinusXAvg = xAxisArray[i] - xAvg;
        	yMinusXAvg = yAxisArray[i] - yAvg;
        	productMean = productMean + (xMinusXAvg * yMinusXAvg);
        	squaredMean = squaredMean + (xMinusXAvg * xMinusXAvg);
        }
        calculatedSlope = (productMean/squaredMean);
        calculatedYIntercept = yAvg - (calculatedSlope * xAvg);
        //alert("xAvg : "+xAvg+" yAvg : "+yAvg+" productMean : "+productMean+" squareMean : "+squaredMean+" slope : "+calculatedSlope+" ::: y intercept "+calculatedYIntercept + ":: sample length - "+xAxisArray.length);
        temp = 0;
        $.each(xAxisArray, function( index, value ) {
        	temp = (value * calculatedSlope) + calculatedYIntercept;
        	bestFitYArray.push(temp);
        });
        linearEquation= "Y = ("+calculatedSlope+") X + "+calculatedYIntercept;

    }

    function prepareLogisticGraphAttributes(){
        //xAxis = "Sepal Width (cm)";
        //yAxis = "Petal Width (cm)";
        //xAxis = $("#xAxis").val();
        //yAxis = $("#yAxis").val();
        //plotType="scatter";

        $.each(classificationArray , function (index, value){
            //alert(value);
            xAxis = $("#xAxis").val();//"Sepal Width (cm)";
            yAxis = $("#yAxis").val();//"Petal Width (cm)";
            plotType=$("#plotType").val();
            if(ml_algo === 'LogisticRegression'){
                var decodedPredictionValue = predictionEncodeMap[value];
                temp = createTraceObject(parsedParamArray,value,xAxis,yAxis,plotType,decodedPredictionValue);
            }else if(ml_algo ==='KMeans'){
                //value = kMeansClusterMap[value];
                temp = createTraceObject(parsedParamArray,value,xAxis,yAxis,plotType);
            }

            dataArray[index]=temp;

        });

        drawLogisticRegressionGraph(parsedParamArray,xAxis,yAxis,plotType);
    }

    function createTraceObject(parsedParamArray,className,xAxis,yAxis,plotType,decodedPredictionValue){
        if(ml_algo === 'LogisticRegression'){
            var temp ={x: parsedParamArray[className][xAxis], y: parsedParamArray[className][yAxis], mode: 'markers', type: plotType, name: decodedPredictionValue, marker: { size: 12 }};
        }else{
            var clusterName = kMeansClusterMap[className];
            var temp ={x: parsedParamArray[className][xAxis], y: parsedParamArray[className][yAxis], mode: 'markers', type: plotType, name: clusterName, marker: { size: 12 }};

        }
        //dataArray.push(temp);
        return temp;
    }


    function drawLogisticRegressionGraph(parsedParamArray,xAxis,yAxis,plotType){
        var data = dataArray;
        var layout = {

            title:'Relationship between '+xAxis+' & '+yAxis,
            xaxis: {
                title: xAxis,
                showgrid: false,
                zeroline: false
            },
            yaxis: {
                title: yAxis,
                showline:false
            },
            width: 1000,
            height: 500

        };

        Plotly.newPlot('modelGraph', data, layout);

    }
