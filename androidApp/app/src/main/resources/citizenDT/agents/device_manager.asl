/*
    -- Knowledge --
    sensor(DeviceName, SensorName)
*/

gpsRequest(0).

@start[atomic]
+activate <-
    makeArtifact("bluetooth", "it.unibo.citizenDigitalTwin.artifact.BluetoothArtifact", [], Bluetooth);
    focus(Bluetooth);
    makeArtifact("GPS", "it.unibo.citizenDigitalTwin.artifact.GPSArtifact", [], GPS);
    focus(GPS);
	makeArtifact("devices", "it.unibo.citizenDigitalTwin.artifact.DeviceCommunication", [Bluetooth], Communication);
	focus(Communication);
	!linkToView(Communication);
	!observeState;
	.print("Device Manager ready");
	!!subscribeForLocationUpdates;
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

+!subscribeForLocationUpdates : gpsRequest(X) & X < 3 <-
    subscribeForLocationUpdates(Result);
    !checkIfLocationPermissionIsGranted(Result).

+!checkIfLocationPermissionIsGranted(true).
+!checkIfLocationPermissionIsGranted(false) <-
    .wait(60000);
    ?gpsRequest(X);
    -+gpsRequest(X + 1);
    !!subscribeForLocationUpdates.

+!checkSensorData <-
    .wait(30000);
    .findall(X,sensor(_,X),L);
    !askForData(L);
    !!checkSensorData.

-!checkSensorData <- .print("Error in checkSensorData!"); !!checkSensorData.

+!askForData([]).
+!askForData([H|T]) <-
    sendDataRequest[artifact_name(H)];
    !askForData(T).

+!deleteSensors(DeviceName, L) <- !unsubscribeFromSensors(DeviceName, L).

+!unsubscribeFromSensors(DeviceName, []).
+!unsubscribeFromSensors(DeviceName, [H|T]) <-
    unsubscribeForData[artifact_name(H)];
    lookupArtifact(H,Id);
    disposeArtifact(Id);
    -sensor(DeviceName, H);
    !!unsubscribeFromSensors(DeviceName, T).

+newSensor(DeviceName, SensorName, Channel, LeafCategory, SensorKnowledge) <-
    makeArtifact(SensorName, "it.unibo.citizenDigitalTwin.artifact.ExternalSensorArtifact", [DeviceName, Channel, LeafCategory, SensorKnowledge], Id);
    focus(Id);
    +sensor(DeviceName,SensorName);
    sendDataRequest[artifact_id(Id)];
    println(SensorName).

+deviceDisconnected(DeviceName) <-
    .findall(X,sensor(DeviceName,X),L);
    !!deleteSensors(DeviceName, L).

+data(Data) <- updateStateFromSingleData(Data).

+shutdown <- disconnectBTServices.