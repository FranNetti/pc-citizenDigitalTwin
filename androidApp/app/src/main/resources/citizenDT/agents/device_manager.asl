/*
    -- Knowledge --
    sensor(DeviceName, SensorName)
*/

maxGPSRequestAttempts(3).
GPSRequestAttempts(0).
sensorTimeRequestRate(120000).

@start[atomic]
+activate <-
    makeArtifact("deviceKnowledge", "it.unibo.citizenDigitalTwin.artifact.DeviceKnowledgeArtifact", [], Knowledge);
    focus(Knowledge);
    makeArtifact("bluetooth", "it.unibo.citizenDigitalTwin.artifact.BluetoothArtifact", [], Bluetooth);
    focus(Bluetooth);
    makeArtifact("GPS", "it.unibo.citizenDigitalTwin.artifact.GPSArtifact", [], GPS);
    focus(GPS);
	makeArtifact("devices", "it.unibo.citizenDigitalTwin.artifact.DeviceCommunicationArtifact", [Knowledge, [Bluetooth]], Communication);
	focus(Communication);
	!observeState;
	.print("Device Manager ready");
	!!subscribeForLocationUpdates;
	!!checkSensorData.

+!observeState <-
    lookupArtifact("state", S);
    focus(S).

-!observeState <-
    .wait(100);
    !observeState.

+!subscribeForLocationUpdates : maxGPSRequestAttempts(Y) & GPSRequestAttempts(X) & X < Y <-
    subscribeForLocationUpdates(Result);
    !checkIfLocationPermissionIsGranted(Result).

+!checkIfLocationPermissionIsGranted(true).
+!checkIfLocationPermissionIsGranted(false) <-
    .wait(60000);
    ?GPSRequestAttempts(X);
    -+GPSRequestAttempts(X + 1);
    !!subscribeForLocationUpdates.

+!checkSensorData <-
    ?sensorTimeRequestRate(Time);
    .wait(Time);
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
    lookupArtifact(H,Id);
    unsubscribeForData[artifact_id(Id)];
    disposeArtifact(Id);
    -sensor(DeviceName, H);
    !!unsubscribeFromSensors(DeviceName, T).

+newSensor(DeviceName, SensorName, Channel, SensorKnowledge) <-
    makeArtifact(SensorName, "it.unibo.citizenDigitalTwin.artifact.ExternalSensorArtifact", [DeviceName, Channel, SensorKnowledge], Id);
    focus(Id);
    +sensor(DeviceName,SensorName);
    sendDataRequest[artifact_id(Id)];
    println(SensorName).

+deviceDisconnected(DeviceName) <-
    .findall(X,sensor(DeviceName,X),L);
    !!deleteSensors(DeviceName, L).

+data(Data) <- updateStateFromSingleData(Data).

+deactivate <- disconnectBTServices.