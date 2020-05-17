// Agent device_manager in project citizenDT

/* Initial beliefs and rules */

//sensor(Sensor,Device).
//sensor(X,Device).

/* Initial goals */

!readyUp.

/* Plans */

+!readyUp <- 
	makeArtifact("Communication", "citizenDT.device.DeviceCommunication", [], Communication)
	focus(Communication)
	!linkToView(Communication)
	.print("Device Manager ready").

+!linkToView(CommId) <-
	lookupArtifact("GUI", GUI)
	linkArtifacts(GUI,"deviceManagement",CommId).
	
-!linkToView(ID) <-
	.wait(100)
	!linkToView(ID).
	
+addDeviceFailed(Model) <-
	.send(user_interaction,tell,addDeviceFailed(Model)).

+newDevice(_) <-
	.send(user_interaction,tell,addDeviceSuccess).

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }

// uncomment the include below to have an agent compliant with its organisation
//{ include("$moiseJar/asl/org-obedient.asl") }
