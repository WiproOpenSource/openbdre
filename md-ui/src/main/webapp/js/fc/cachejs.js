var updatePositionsFromArrangedData = function (dataRecords) {
	$.each(dataRecords, function (i, dataRecord) {
		// console.log(dataRecord);
		// console.log(nodePropertiesCache[i]);
		if(nodePropertiesCache[i]['x']!=null && nodePropertiesCache[i]['y']!=null){
                		nodePropertiesCache[i]['x'].value = dataRecord.xPos;
                		nodePropertiesCache[i]['y'].value = dataRecord.yPos;
                		}
	})
}

var	getKeyWiseJson = function (dataRecords) {
		var keyWiseProperties = {};
		if (dataRecords.length != null) {
			for (var i = 0; i < dataRecords.length; i++) {
				keyWiseProperties[dataRecords[i].key] = dataRecords[i];
			};
		}
		else {
			keyWiseProperties[dataRecords.key] = dataRecords;
		}
		return keyWiseProperties;
	};

var pidWiseDataRecord = function (dataRecords) {
	var dataRecord = {};
	for (var i = 0; i < dataRecords.length; i++) {
		if (dataRecord[dataRecords[i].processId] == null) {
			dataRecord[dataRecords[i].processId] = [dataRecords[i]];
		}
		else {
			dataRecord[dataRecords[i].processId].push(dataRecords[i]);
		}
	}
	return dataRecord;
}

var nodeTypeGenConfigCache = {},
	alterGenConfigCache = function (type, dataRecords) {
		nodeTypeGenConfigCache[type.toString()] = getKeyWiseJson(dataRecords);
	},
	getGenConfigCache = function (type) {
		var returnresult = nodeTypeGenConfigCache[type.toString()]
		if (!returnresult) {
			returnresult = false;
		}
		return returnresult;
	};

var nodePropertiesCache = {},
	alterGroupNPsC = function (dataRecords) {
		var segregatedDataRecord = pidWiseDataRecord(dataRecords);
		$.each(segregatedDataRecord, function(i, dataRecord) {
			alterNPsC(i.toString(), dataRecord);
		})
		// console.log('NPsC initialisation successful');
	},
	alterNPsC = function (arg, dataRecords) {
		if (dataRecords == 'DELETE') {
			if (arg.length == 2) {
				var dataRecord = nodePropertiesCache[arg[0].toString()];
				if (!dataRecord) {
					delete dataRecord[arg[1]];
					nodePropertiesCache[arg[0].toString()] = dataRecord;
				}
			}
			else {
				delete nodePropertiesCache[arg.toString()];
			}
			// console.log('NPsC deleted');
		}
		else {
			if (arg.length == 2) {
				var dataRecord = nodePropertiesCache[arg[0].toString()];
				if (dataRecord != null && dataRecord.length != 0) {
					dataRecord[arg[1]] = getKeyWiseJson(dataRecords)[arg[1]];
					nodePropertiesCache[arg[0].toString()][arg[1]] = getKeyWiseJson(dataRecords)[arg[1]];
				}
				else {
					nodePropertiesCache[arg[0].toString()] = {};
					alterNPsC(arg, dataRecords);
				}
			}
			else {
				nodePropertiesCache[arg.toString()] = getKeyWiseJson(dataRecords);
			}
			// console.log('NPsC altered');
		}
	},
	checkNPsC = function (arg, dataRecords) {
		var result;
		if (nodePropertiesCache[pid.toString()] == dataRecords) {
			result = true;
		}
		else {
			result = false;
		}
		return result;
	},
	getNPsC = function (arg) { //arg can take only pid or pid key pair
		var returnresult;
		if (arg.length == 2) {
			returnresult = nodePropertiesCache[arg[0].toString()][arg[1]];
		}
		else {
			returnresult = nodePropertiesCache[arg.toString()];
		}
		if (!returnresult) {
			returnresult = false;
		}
		return returnresult;
	};

var nodeProcessCache = {},
	alterGroupNPC = function (dataRecords) {
		for (var i = 0; i < dataRecords.length; i++) {
			// nodeProcessCache[dataRecords[i].processId] = dataRecords[i];
			alterNPC(dataRecords[i].processId, dataRecords[i]);
		};
		// console.log('NPC initialisation successful');
	},
	alterNPC = function (pid, dataRecord) {
		if(dataRecord.processId < 0 || pid < 0) {
			pid = -pid;
			dataRecord.processId = - dataRecord.processId;
		}
		if (dataRecord == 'DELETE') {
			delete nodeProcessCache[pid.toString()];
			alterNPsC(pid, 'DELETE');
			// console.log('NPC deleted');
		}
		else {
			nodeProcessCache[pid.toString()] = dataRecord;
			// console.log('NPC altered');
		}
	},
	checkNPC = function (pid, dataRecord) {
		var result;
		if (nodeProcessCache[pid.toString()] == dataRecord) {
			result = true;
		}
		else {
			result = false;
		}
		return result;
	},
	getNPC = function (pid) {
		var returnresult = nodeProcessCache[pid.toString()];
		if (!returnresult) {
			returnresult = false;
		}
		return returnresult;
	};
