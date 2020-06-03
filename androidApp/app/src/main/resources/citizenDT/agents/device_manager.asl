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
	!observeState;
	.print("Device Manager ready");
	!!checkSensorData.

+!linkToView(CommId) <-
	lookupArtifact("mainUI", MainUI);
	linkArtifacts(MainUI,"deviceManagement",CommId).

-!linkToView(ID) <-
	.wait(100)
	!linkToView(ID).

+!observeState <-
    lookupArtifact("state", S);
    focus(S).

-!observeState <-
    .wait(100);
    !observeState.

+!checkSensorData <-
    .wait(2000);
    .findall(X,sensor(_,X),L);
    !askForData(L);
    !!checkSensorData.

-!checkSensorData <- .print("Error in checkSensorData!"); !!checkSensorData.

+!askForData([]).
+!askForData([H|T]) <-
    sendDataRequest[artifact_name(H)];
    !askForData(T).

+newSensor(DeviceName, SensorName, Channel, LeafCategory, SensorKnowledge) <-
    makeArtifact(SensorName, "it.unibo.citizenDigitalTwin.artifact.ExternalSensorArtifact", [DeviceName, Channel, LeafCategory, SensorKnowledge], Id);
    focus(Id);
    +sensor(DeviceName,SensorName);
    sendDataRequest[artifact_id(Id)];
    println(SensorName).

+data(Data) <- updateStateFromSingleData(Data).

+shutdown <- disconnectBTServices.