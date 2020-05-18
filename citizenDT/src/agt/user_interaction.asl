// Agent user_interaction in project citizenDT

/* Initial beliefs and rules */

/* Initial goals */

!readyUp.

/* Plans */

+!readyUp <-
	!initializeGui
	!observeState
	.print("User interaction ready").
	
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
	showNewState(State).


/* ------------------------------------------------------------------------------ */

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }

// uncomment the include below to have an agent compliant with its organisation
//{ include("$moiseJar/asl/org-obedient.asl") }
