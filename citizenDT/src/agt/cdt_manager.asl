// Agent cdt_manager in project citizenDT

/* Initial beliefs and rules */

/* Initial goals */

!start.

/* Plans */

+!start : true <- 
	makeArtifact("state","citizenDT.state.StateManager",[],StateManager)
	focus(StateManager)
	makeArtifact("connection","citizenDT.connection.ConnectionManager",[],ConnectionManager)
	focus(ConnectionManager)
	.print("CDT Manager ready").
	
+newState(State) <-
	updateState(State).

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }

// uncomment the include below to have an agent compliant with its organisation
//{ include("$moiseJar/asl/org-obedient.asl") }
