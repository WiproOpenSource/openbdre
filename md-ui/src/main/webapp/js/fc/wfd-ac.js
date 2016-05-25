var arrangePositionsAC = function(ajaxURL, type, arg) {
	var returnObject;
	ajaxURL = ajaxURL + arg;
	returnObject = ajaxCall(ajaxURL, 'GET', arg);
	return returnObject;
}

var busdomainOptionsAC = function (ajaxURL, type, arg) {
	var returnObject;
	switch(type) {
		case 'POST':
			ajaxURL = ajaxURL;
			break;
		default: 
			console.log('In AC function defualt block');
	}
	returnObject = ajaxCall(ajaxURL, type, arg, 'optionscall');
	return returnObject;
}

var processtypeOptionslistAC = function (ajaxURL, type, arg) {
	var returnObject;
	switch(type) {
		case 'POST':
			break;
		default:
			console.log('In AC function defualt block');
	}
	returnObject = ajaxCall(ajaxURL, type, arg, 'optionscall');
	return returnObject;
}

var permissiontypeOptionslistAC = function (ajaxURL, type, arg) {
	var returnObject;
	switch(type) {
		case 'POST':
			break;
		default:
			console.log('In AC function defualt block');
	}
	returnObject = ajaxCall(ajaxURL, type, arg, 'optionscall');
	return returnObject;
}

var userrolestypeOptionslistAC = function (ajaxURL, type, arg) {
	var returnObject;
	switch(type) {
		case 'POST':
			break;
		default:
			console.log('In AC function defualt block');
	}
	returnObject = ajaxCall(ajaxURL, type, arg, 'optionscall');
	return returnObject;
}

var workflowtypeOptionslistAC = function (ajaxURL, type, arg) {
	var returnObject;
	switch(type) {
		case 'POST':
			break;
		default:
			console.log('In AC function defualt block');
	}
	returnObject = ajaxCall(ajaxURL, type, arg, 'optionscall');
	return returnObject;
}

var genConfigAC = function (ajaxURL, type, arg) {
	var returnObject;
	switch(type) {
		case 'POST':
			break;
		case 'PUT':
			break;
		case 'DELETE':
			break;
		case 'GET':
			returnObject = getGenConfigCache(arg);
			if (!returnObject) {
				console.log('Did not get genconfigcache');
				ajaxURL = ajaxURL + arg;
				returnObject = ajaxCall(ajaxURL, type);
				alterGenConfigCache(arg, returnObject);
			}
			break;
		default:
			console.log('In AC function defualt block');
	}
	return returnObject;
}

var propertiesCloneAC = function (ajaxURL, type, arg) {
	var returnObject;
	switch(type) {
		case 'PUT':
			ajaxURL = ajaxURL+ '/' +arg;
			break;
		default:
			console.log('In AC function defualt block');
	}
	returnObject = ajaxCall(ajaxURL, type);
	return returnObject;
}

var propertiesAC = function(ajaxURL, type, arg) {
	var returnObject;
	switch(type) {
		case 'POST':
			returnObject = ajaxCall(ajaxURL, type, arg);
			alterNPsC([returnObject.processId, returnObject.key], returnObject);
			break;
		case 'PUT':
			returnObject = ajaxCall(ajaxURL, type, arg);
			if (returnObject) {
				alterNPsC([returnObject.processId, returnObject.key], returnObject);
			}
			else {
				alertBox('warning', 'Duplicate property insertion not allowed');
			}
			break;
		case 'DELETE':
			ajaxURL = ajaxURL + arg[0] + '/' + arg[1]+'/';
			returnObject = ajaxCall(ajaxURL, type);
			break;
		case 'GET':
			if (ajaxURL.search("properties/all") != -1) {
				ajaxURL = ajaxURL + arg;
				returnObject = ajaxCall(ajaxURL, type);
				alterGroupNPsC(returnObject);
			}
			else {
				returnObject = getNPsC(arg);
				if (!returnObject) {
					console.log('get process pid not found, calling ajax');
					if (arg.length == 2) {
						ajaxURL = ajaxURL + arg[0] + '/' + arg[1];
					}
					else {
						ajaxURL = ajaxURL + arg;
					}
					returnObject = ajaxCall(ajaxURL, type);
					alterNPsC(arg, returnObject);
				}
			}
			break;
		default:
			console.log('In AC function defualt block');
	}
	return returnObject;
}

var subprocessAC = function (ajaxURL, type, arg) {
	var returnObject;
	switch(type) {
		case 'POST':
			break;
		case 'PUT':
			ajaxURL = ajaxURL;
			returnObject = ajaxCall(ajaxURL, type, arg);
			alterNPC(returnObject.processId, returnObject);
			break;
		case 'DELETE':
			ajaxURL = ajaxURL + arg;
			returnObject = ajaxCall(ajaxURL, type);
			alterNPC(arg, type);
			break;
		case 'GET':
			ajaxURL = ajaxURL + arg;
			returnObject = ajaxCall(ajaxURL, type);
			alterGroupNPC(returnObject);
			break;
		default:
			console.log('In AC function defualt block');
	}
	return returnObject;
}

var processAC = function (ajaxURL, type, arg) {
	var returnObject;
	switch(type) {
		case 'POST':
			ajaxURL = ajaxURL;
			returnObject = ajaxCall(ajaxURL, type, arg);
			alterNPC(returnObject.processId, returnObject);
			break;
		case 'PUT':
			ajaxURL = ajaxURL;
			returnObject = ajaxCall(ajaxURL, type, arg);
			alterNPC(returnObject.processId, returnObject);
			break;
		case 'DELETE':
			ajaxURL = ajaxURL + arg;
			returnObject = ajaxCall(ajaxURL, type);
			alterNPC(arg, type);
			break;
		case 'GET': //arg will be having pid for this case
			returnObject = getNPC(arg);
			if(returnObject.processId < 0) {
				returnObject.processId = - returnObject.processId;
				console.log('got negative pid,changed to positive');
				ajaxURL = ajaxURL + arg;
				returnObject = ajaxCall(ajaxURL, type);
				alterNPC(arg, returnObject);
			}
			if(!returnObject) {
				console.log('get process pid not found, calling ajax');
				ajaxURL = ajaxURL + arg;
				returnObject = ajaxCall(ajaxURL, type);
				alterNPC(arg, returnObject);
			}
			break;
		default:
			console.log('In AC function defualt block');
	}
	return returnObject;
}

var fileHandlerAC = function (ajaxURL, type, arg) {
	var returnObject;
	switch(type) {
		case 'POST':
						if(ajaxURL=='/mdrest/filehandler/check/'){
						ajaxURL = ajaxURL + arg[0]+'/'+arg[1]+'?file='+arg[2];
                        returnObject = ajaxCall(ajaxURL, type, arg);
						}
						else{
		                var fd = new FormData();
		                var fileObj = $("#"+arg[2])[0].files[0];
                        var fileName=fileObj.name;
                        fd.append("file", fileObj);
                        fd.append("name", fileName);
                        $.ajax({
                          url: ajaxURL + arg[0]+'/'+arg[1],
                          type: "POST",
                          data: fd,
                          async: false,
                          enctype: 'multipart/form-data',
                          processData: false,  // tell jQuery not to process the data
                          contentType: false,  // tell jQuery not to set contentType
                          success:function (data) {
                                console.log( data );
                                returnObject=data;
							},
						  error: function () {
							    returnObject=false;
							}
						 });
						 }
			return returnObject;
            break;
		case 'PUT':
			break;
		case 'DELETE':
		    if(arg.length==3){
		    	ajaxURL = ajaxURL + arg[0]+'/'+arg[1]+'/'+arg[2];
			    returnObject = ajaxCall(ajaxURL, type, arg);
		    }
		    else{
		    ajaxURL = ajaxURL + arg[0]+'/'+arg[1];
			returnObject = ajaxCall(ajaxURL, type, arg);
		    }
			return returnObject;
			break;
		case 'GET':
		    if(arg[1]=="lib"){
 				ajaxURL = ajaxURL + Math.abs(arg[0])+'/'+arg[1];
				returnObject = ajaxCall(ajaxURL, type, arg);
		    }
		    else{
            ajaxURL = ajaxURL + arg[0]+'/'+arg[1];
            $.ajax({
					url: '/mdrest/process/export/' + item.record.processId,
					type: 'GET',
					data: item + '&processId=' + item.record.processId + '&busDomainId=' + item.record.busDomainId + '&processTypeId=' + item.record.processTypeId + '&processName=' + item.record.processName + '&canRecover=' + item.record.canRecover + '&description=' + item.record.description + '&processTemplateId=0' + '&NextProcessIds=0',
					dataType: 'json',
					success: function(data) {
						if (data.Result == "OK") {
							console.log(window.location.protocol);
							var url = (window.location.protocol + "//" + window.location.host + "/mdrest/process/export/" + item.record.processId);
							window.location.href = url;
						}
						if (data.Result == "ERROR")
							alert(data.Message);
					},
					error: function() {
						alert('Error getting json');
					}
		         });
            }
			return returnObject;
			break;
		default:
			console.log('In AC function defualt block');
	}
	return returnObject;
}

var processExportCache,
	processExportAC = function (ajaxURL, processId) {
		var returnObject;
		if (processExportCache != null) {
			returnObject = processExportCache;
		}
		else {
			returnObject = ajaxCall(ajaxURL+processId, 'GET');
			// processExportCache = returnObject;
		}
	return returnObject;
}

var ajaxCall = function (ajaxURL, type, putData, optionscall) {
	var returnObject;
	$.ajax({
	    url: ajaxURL,
	    data: putData,
	    type: type,
	    async: false,
	    dataType: 'json',
	    success: function(data) {
	    	if (recordObjectError(data)) {
	    		if (optionscall) {
	    			returnObject = data.Options;
	    			if (returnObject == null) {
	    				returnObject = 'null';
	    			}
	    		}
	    		else {
	    			returnObject = data.Record;
	    			if (returnObject == null) {
	    				returnObject = 'null'
	    			}
	    		}
	    	}
	    	else {
	    		returnObject = false;
	    	}
	    },
	    error: function() {
	    	returnObject = false;
	    }
	});
	return returnObject;
}

var recordObjectError = function (data) {
	if (data.Result == 'ERROR') {
		console.log('Record Object Error');
		return false;
	}
	else {
		return true;
	}
}
