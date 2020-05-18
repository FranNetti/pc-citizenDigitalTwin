// Agent user_interaction in project citizenDT

/* Initial beliefs and rules */

/* Initial goals */

!readyUp.

/* Plans */

+!readyUp <-
	!initializeGui
	!observeState
	.print("User interaction ready")
	.wait(1000)
	pippo.
	
+!initializeGui <-
	makeArtifact("GUI", "citizenDT.gui.UserGUI", [], GuiId)
	focus(GuiId).
	
+!observeState <-
	lookupArtifact("state", S)
	focus(S).
	
-!observeState <-
	.wait(100);
	!observeState.
	
+state(State) <-
	.print("Aggiornamento di stato")
	showNewState(State).
	
+addDeviceFailed(Model) <-
	.print("Failed:")
	.print(Model).

+addDeviceSuccess <-
	.print("Success").


/* ------------------------------------------------------------------------------ */

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }

// uncomment the include below to have an agent compliant with its organisation
//{ include("$moiseJar/asl/org-obedient.asl") }
