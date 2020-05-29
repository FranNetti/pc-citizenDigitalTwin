!readyUp.

+!readyUp <-
	makeArtifact("devices", "it.unibo.citizenDigitalTwin.artifact.DeviceCommunication", [], Communication);
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