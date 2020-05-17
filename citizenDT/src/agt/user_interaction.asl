// Agent user_interaction in project citizenDT

/* Initial beliefs and rules */

/* Initial goals */

!initializeGui.

/* Plans */

+!initializeGui <-
	makeArtifact("GUI", "citizenDT.gui.UserGUI", [], GuiId)
	focus(GuiId)
	!observeState(GuiId)
	.print("User interaction ready")
	.wait(1000)
	pippo.
	
+!observeState(GuiId) <-
	lookupArtifact("state", S);
	//+stateManager(S)
	//focus(S).
	linkArtifacts(S,"statusUpdate",GuiId).
	
-!observeState(ID) <-
	.wait(100);
	!observeState(ID).
	
/*+state(State) <-
	.print("Aggiornamento di stato")
	stateManager(Id)
	updateState(State)[artifact_id(Id)].*/
	
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
