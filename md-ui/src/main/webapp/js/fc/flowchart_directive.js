//
// Flowchart module.
//
angular.module('flowChart', ['dragging'] )

//
// Directive that generates the rendered chart from the data model.
//
.directive('flowChart', function() {
  return {
  	restrict: 'E',
  	templateUrl: "../js/fc/flowchart_template.html",
  	replace: true,
  	scope: {
  		chart: "=chart",
  	},

  	//
  	// Controller for the flowchart directive.
  	// Having a separate controller is better for unit testing, otherwise
  	// it is painful to unit test a directive without instantiating the DOM 
  	// (which is possible, just not ideal).
  	//
  	controller: 'FlowChartController',
  };
})

//
// Directive that allows the chart to be edited as json in a textarea.
//
.directive('chartJsonEdit', function () {
	return {
		restrict: 'A',
		scope: {
			viewModel: "="
		},
		link: function (scope, elem, attr) {

			//
			// Serialize the data model as json and update the textarea.
			//
			var updateJson = function () {
				if (scope.viewModel) {
					var json = JSON.stringify(scope.viewModel.data, null, 4);
					$(elem).val(json);
				}
			};

			//
			// First up, set the initial value of the textarea.
			//
			updateJson();

			//
			// Watch for changes in the data model and update the textarea whenever necessary.
			//
			scope.$watch("viewModel.data", updateJson, true);

			//
			// Handle the change event from the textarea and update the data model
			// from the modified json.
			//
			$(elem).bind("input propertychange", function () {
				var json = $(elem).val();
				var dataModel = JSON.parse(json);
				scope.viewModel = new flowchart.ChartViewModel(dataModel);

				scope.$digest();
			});
		}
	}

})

//
// Controller for the flowchart directive.
// Having a separate controller is better for unit testing, otherwise
// it is painful to unit test a directive without instantiating the DOM 
// (which is possible, just not ideal).
//
.controller('FlowChartController', ['$scope', 'dragging', '$element', function FlowChartController ($scope, dragging, $element) {

	var controller = this;

	//
	// Reference to the document and jQuery, can be overridden for testting.
	//
	this.document = document;

	//
	// Wrap jQuery so it can easily be  mocked for testing.
	//
	this.jQuery = function (element) {
		return $(element);
	}

	//
	// Init data-model variables.
	//
	$scope.draggingConnection = false;
	$scope.connectorSize = 10;
	$scope.dragSelecting = false;
	/* Can use this to test the drag selection rect.
	$scope.dragSelectionRect = {
		x: 0,
		y: 0,
		width: 0,
		height: 0,
	};
	*/

	//
	// Reference to the connection, connector or node that the mouse is currently over.
	//
	$scope.mouseOverConnector = null;
	$scope.mouseOverConnection = null;
	$scope.mouseOverNode = null;

	//
	// The class for connections and connectors.
	//
	this.connectionClass = 'connection';
	this.connectorClass = 'connector';
	this.nodeClass = 'node';

	//
	// Search up the HTML element tree for an element the requested class.
	//
	this.searchUp = function (element, parentClass) {

		//
		// Reached the root.
		//
		if (element == null || element.length == 0) {
			return null;
		}

		// 
		// Check if the element has the class that identifies it as a connector.
		//
		if (hasClassSVG(element, parentClass)) {
			//
			// Found the connector element.
			//
			return element;
		}

		//
		// Recursively search parent elements.
		//
		return this.searchUp(element.parent(), parentClass);
	};

	//
	// Hit test and retreive node and connector that was hit at the specified coordinates.
	//
	this.hitTest = function (clientX, clientY) {

		//
		// Retreive the element the mouse is currently over.
		//
		return this.document.elementFromPoint(clientX, clientY);
	};

	//
	// Hit test and retreive node and connector that was hit at the specified coordinates.
	//
	this.checkForHit = function (mouseOverElement, whichClass) {

		//
		// Find the parent element, if any, that is a connector.
		//
		var hoverElement = this.searchUp(this.jQuery(mouseOverElement), whichClass);
		if (!hoverElement) {
			return null;
		}

		return hoverElement.scope();
	};

	//
	// Translate the coordinates so they are relative to the svg element.
	//
	this.translateCoordinates = function(x, y) {
		var svg_elem =  $element.get(0);
		var matrix = svg_elem.getScreenCTM();
		var point = svg_elem.createSVGPoint();
		point.x = x;
		point.y = y;
		return point.matrixTransform(matrix.inverse());
	};

	//
	// Called on mouse down in the chart.
	//
	var oldX=0;
	var oldY=0;
	$scope.mouseDown = function (evt) {

		$scope.chart.deselectAll();

		dragging.startDrag(evt, {

			//
			// Commence dragging... setup variables to display the drag selection rect.
			//
			dragStarted: function (x, y) {

				oldX=x;
				oldY=y;

			},

			//
			// Update the drag selection rect while dragging continues.
			//
			dragging: function (x, y) {
				leftStr=evt.target.style.left;
				left=leftStr.substring(0,leftStr.length-2);

				topStr=evt.target.style.top;

                topy=topStr.substring(0,topStr.length-2);
				// console.log("topStr="+topStr+ "; top="+topy);
				ultimateLeft=parseInt(left) + (x - oldX);
				ultimateTop=parseInt(topy) + (y - oldY);
			    evt.target.style.left=ultimateLeft+'px';
			    evt.target.style.top=ultimateTop+'px';

				//evt.target.style.width=evt.target.style.width+(oldX-x);
				oldX=x;
				oldY=y;
			},

			//
			// Dragging has ended... select all that are within the drag selection rect.
			//
			dragEnded: function () {


			},
		});
	};

	//
	// Called for each mouse move on the svg element.
	//
	$scope.mouseMove = function (evt) {

		//
		// Clear out all cached mouse over elements.
		//
		$scope.mouseOverConnection = null;
		$scope.mouseOverConnector = null;
		$scope.mouseOverNode = null;

		var mouseOverElement = controller.hitTest(evt.clientX, evt.clientY);
		if (mouseOverElement == null) {
			// Mouse isn't over anything, just clear all.
			return;
		}

		if (!$scope.draggingConnection) { // Only allow 'connection mouse over' when not dragging out a connection.

			// Figure out if the mouse is over a connection.
			var scope = controller.checkForHit(mouseOverElement, controller.connectionClass);
			$scope.mouseOverConnection = (scope && scope.connection) ? scope.connection : null;
			if ($scope.mouseOverConnection) {
				// Don't attempt to mouse over anything else.
				return;
			}
		}

		// Figure out if the mouse is over a connector.
		var scope = controller.checkForHit(mouseOverElement, controller.connectorClass);
		$scope.mouseOverConnector = (scope && scope.connector) ? scope.connector : null;
		if ($scope.mouseOverConnector) {
			// Don't attempt to mouse over anything else.
			return;
		}

		// Figure out if the mouse is over a node.
		var scope = controller.checkForHit(mouseOverElement, controller.nodeClass);
		$scope.mouseOverNode = (scope && scope.node) ? scope.node : null;		
	};

	//
	// Handle mousedown on a node.
	//
	$scope.nodeMouseDown = function (evt, node) {
		var chart = $scope.chart;
		var lastMouseCoords;
		var xpos=0;
		var ypos=0;
		dragging.startDrag(evt, {

			//
			// Node dragging has commenced.
			//
			dragStarted: function (x, y) {

				lastMouseCoords = controller.translateCoordinates(x, y);

				//
				// If nothing is selected when dragging starts, 
				// at least select the node we are dragging.
				//
				if (!node.selected()) {
					chart.deselectAll();
					node.select();
				}
			},
			
			//
			// Dragging selected nodes... update their x,y coordinates.
			//
			dragging: function (x, y) {

				var curCoords = controller.translateCoordinates(x, y);
				var deltaX = curCoords.x - lastMouseCoords.x;
				var deltaY = curCoords.y - lastMouseCoords.y;
				xpos= node.data.x+deltaX;
				ypos= node.data.y+deltaY;;
				if(ypos<=0){
					ypos=0;
					deltaY=-node.data.y;
				}
				if(xpos<=0){
					xpos=0;
					deltaX=-node.data.x;
				}
				chart.updateSelectedNodesLocation(deltaX, deltaY);

				lastMouseCoords = curCoords;
			},
			dragEnded: function () {
				var pid=node.data.pid;
				if (pid < 0) {
					pid = -pid;
				}
				var dataRecord = propertiesAC('/mdrest/properties/', 'DELETE', [pid, 'x']);
				if (dataRecord) {
					var dataRecord1 = propertiesAC('/mdrest/properties', 'PUT', 'configGroup=position&key=x&value='+xpos+'&description=xposition&processId='+pid);
					if (dataRecord1) {

					}
					else {
						alertBox('warning','Could not update x position');
					}
				}
				else {

				}

				var dataRecord = propertiesAC('/mdrest/properties/', 'DELETE', [pid, 'y']);
				if (dataRecord) {
					var dataRecord1 = propertiesAC('/mdrest/properties', 'PUT', 'configGroup=position&key=y&value='+ypos+'&description=yposition&processId='+pid);
					if (dataRecord1) {

					}
					else {
						alertBox('warning','Could not update y position');
					}
				}
				else {

				}
			},
			//
			// The node wasn't dragged... it was clicked.
			//
			clicked: function () {


				chart.handleNodeClicked(node, evt.ctrlKey);

			},

		});
	};

	//
	// Handle mousedown on a connection.
	//
	$scope.connectionMouseDown = function (evt, connection) {
		var chart = $scope.chart;
		chart.handleConnectionMouseDown(connection, evt.ctrlKey);

		// Don't let the chart handle the mouse down.
		evt.stopPropagation();
		evt.preventDefault();
	};

	//
	// Handle mousedown on an input connector.
	//
	$scope.connectorMouseDown = function (evt, node, connector, connectorIndex, isInputConnector) {

		//
		// Initiate dragging out of a connection.
		//
		dragging.startDrag(evt, {

			//
			// Called when the mouse has moved greater than the threshold distance
			// and dragging has commenced.
			//
			dragStarted: function (x, y) {
				if(isInputConnector)
				{
					return;
				}
				var curCoords = controller.translateCoordinates(x, y);				

				$scope.draggingConnection = true;
				console.log($scope.draggingConnection);
				$scope.dragPoint1 = flowchart.computeConnectorPos(node, connectorIndex, isInputConnector);
				$scope.dragPoint2 = {
					x: curCoords.x,
					y: curCoords.y
				};
				$scope.dragTangent1 = flowchart.computeConnectionSourceTangent($scope.dragPoint1, $scope.dragPoint2);
				$scope.dragTangent2 = flowchart.computeConnectionDestTangent($scope.dragPoint1, $scope.dragPoint2);
			},

			//
			// Called on mousemove while dragging out a connection.
			//
			dragging: function (x, y, evt) {
				if(isInputConnector)
				{
					return;
				}
				var startCoords = controller.translateCoordinates(x, y);				
				$scope.dragPoint1 = flowchart.computeConnectorPos(node, connectorIndex, isInputConnector);
				$scope.dragPoint2 = {
					x: startCoords.x,
					y: startCoords.y
				};
				$scope.dragTangent1 = flowchart.computeConnectionSourceTangent($scope.dragPoint1, $scope.dragPoint2);
				$scope.dragTangent2 = flowchart.computeConnectionDestTangent($scope.dragPoint1, $scope.dragPoint2);
			},

			//
			// Clean up when dragging has finished.
			//
			dragEnded: function () {
				if(isInputConnector)
				{
					return;
				}
				if ($scope.mouseOverConnector && 
					$scope.mouseOverConnector != connector && $scope.mouseOverConnector._parentNode.outputConnectors[0] != $scope.mouseOverConnector && $scope.mouseOverConnector._parentNode.outputConnectors[0] != connector) {

					//
					// Dragging has ended...
					// The mouse is over a valid connector...
					// Create a new connection.
					//
					$scope.chart.createNewConnection(connector, $scope.mouseOverConnector);
					 var sourcePid=connector.parentNode().data.pid;
                     var destPid=$scope.mouseOverConnector.parentNode().data.pid;
                     //-parentId is start node
                     if(sourcePid<0)sourcePid=-sourcePid;
                     var dataRecord = processAC('/mdrest/process/', 'GET', sourcePid);
                     if (dataRecord) {
                     	if(dataRecord.nextProcessIds=='0'){
                     		dataRecord.nextProcessIds=destPid;
                     	}
                     	else{
                     		dataRecord.nextProcessIds = dataRecord.nextProcessIds + "," + destPid;
                     	}
                     	//TS fields cause exception due to time format
                     	delete dataRecord["addTS"];
                     	delete dataRecord["editTS"];
                     	var jsonToPost = dataRecord;
                     	var dataRecord1 = processAC('/mdrest/process/', 'POST', $.param(jsonToPost));
                     	if (dataRecord1) {
                     		alertBox('info','Next process updated');
                     	}
                     	else {
                     		alertBox('danger','Next process not updated');
                     	}
                     }
                     else {
                     	alertBox('danger','Next process not updated');
                     }
				}

				$scope.draggingConnection = false;
				delete $scope.dragPoint1;
				delete $scope.dragTangent1;
				delete $scope.dragPoint2;
				delete $scope.dragTangent2;
			},

		});
	};
}])
;
