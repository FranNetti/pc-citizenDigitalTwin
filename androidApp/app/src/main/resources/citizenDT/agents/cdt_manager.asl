!start.

+!start <-
	makeArtifact("state","it.unibo.citizenDigitalTwin.artifact.StateManager",[],StateManager);
	focus(StateManager);
	//makeArtifact("connection","citizenDT.connection.ConnectionManager",[],ConnectionManager)
	//focus(ConnectionManager)
	.print("CDT Manager ready").

+newState(State) <-
	updateState(State).
