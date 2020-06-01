@start[atomic]
+activate <-
    makeArtifact("bluetooth", "it.unibo.citizenDigitalTwin.artifact.BluetoothArtifact", [], Bluetooth);
    focus(Bluetooth);
	makeArtifact("devices", "it.unibo.citizenDigitalTwin.artifact.DeviceCommunication", [Bluetooth], Communication);
	focus(Communication);
	!linkToView(Communication);
	.print("Device Manager ready").

+!linkToView(CommId) <-
	lookupArtifact("mainUI", MainUI);
	linkArtifacts(MainUI,"deviceManagement",CommId).

-!linkToView(ID) <-
	.wait(100)
	!linkToView(ID).

+newDevice(Device) <-
    .print("New device connected! ");
    println(Device).

+shutdown <- disconnectBTServices.