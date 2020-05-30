@start[atomic]
+activate <-
	makeArtifact("devices", "it.unibo.citizenDigitalTwin.artifact.DeviceCommunication", [], Communication);
	focus(Communication);
	!linkToView(Communication);
	makeArtifact("bluetooth", "it.unibo.citizenDigitalTwin.artifact.BluetoothArtifact", [], Bluetooth);
    focus(Bluetooth);
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