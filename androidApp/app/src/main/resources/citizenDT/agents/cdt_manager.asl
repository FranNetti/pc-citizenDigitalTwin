!start.

+!start <-
	makeArtifact("state","it.unibo.citizenDigitalTwin.artifact.StateManager",[],StateManager);
	focus(StateManager);
	makeArtifact("connection","it.unibo.citizenDigitalTwin.artifact.ConnectionManager",[],ConnectionManager);
	focus(ConnectionManager);
	.print("CDT Manager ready").

+newState(State) <-
	updateState(State).
