/*
    -- Knowledge --
    sensor(DeviceName, SensorName)
*/

@start[atomic]
+activate <-
    makeArtifact("bluetooth", "it.unibo.citizenDigitalTwin.artifact.BluetoothArtifact", [], Bluetooth);
    focus(Bluetooth);
	makeArtifact("devices", "it.unibo.citizenDigitalTwin.artifact.DeviceCommunication", [Bluetooth], Communication);
	focus(Communication);
	!linkToView(Communication);
	.print("Device Manager ready");
	!!checkSensorData.

+!linkToView(CommId) <-
	lookupArtifact("mainUI", MainUI);
	linkArtifacts(MainUI,"deviceManagement",CommId).

-!linkToView(ID) <-
	.wait(100)
	!linkToView(ID).

+!checkSensorData <-
    .wait(2000);
    .findall(X,sensor(_,X),L);
    !askForData(L);
    .findall(data(X,Y,Z),data(X,Y,Z),L2);
    !updateState(L2);
    !!checkSensorData.

-!checkSensorData <- .print("Error in checkSensorData!"); !!checkSensorData.

+!askForData([]).
+!askForData([H|T]) <-
    sendDataRequest[artifact_name(H)];
    !askForData(T).

+!updateState([]).
+!updateState([data(LeafCategory,Value,UnitOfMeasure)|T]) <-
    .print(LeafCategory);
    .print(Value);
    .print(UnitOfMeasure);
    .print("-------------------");
    !updateState(T).

+newSensor(DeviceName, SensorName, Channel, LeafCategory, SensorKnowledge) <-
    makeArtifact(SensorName, "it.unibo.citizenDigitalTwin.artifact.ExternalSensorArtifact", [Channel, LeafCategory, SensorKnowledge], Id);
    focus(Id);
    +sensor(DeviceName,SensorName);
    sendDataRequest[artifact_id(Id)];
    println(SensorName).

+shutdown <- disconnectBTServices.