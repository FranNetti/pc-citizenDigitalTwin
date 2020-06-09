maxAttempts(3).
attempts(0).
credentials("","").

+!login(Username,Password) <-
    -+credentials(Username,Password);
    doLogin(Username, Password, LoginResult, _);
    checkIfLogged(LoginResult).

+!refreshToken(Ttl) <-
    .wait(Ttl);
    refreshToken.

+!login <-
    ?credentials(Username,Password);
    doLogin(Username, Password, _, Logged);
    !!logged(Logged).

+!logged(true).

+!logged(false) <- !!login.

@start[atomic]
+activate <-
	makeArtifact("state","it.unibo.citizenDigitalTwin.artifact.StateManager",[],StateManager);
	focus(StateManager);
	makeArtifact("connection","it.unibo.citizenDigitalTwin.artifact.ConnectionManager",["localhost:8080","localhost:8081"],ConnectionManager);
	focus(ConnectionManager);
	.print("CDT Manager ready").

+newState(State) <-
	updateState(State).

+token(_,Ttl) <-
    -+attempts(0);
    !!refreshToken(Ttl).

+refreshTokenFailed : maxAttempts(M) & attempts(X) & X < M <-
    -+attempts(X+1);
    refreshToken.

+refreshTokenFailed <-
    !!login.

+newGeneratedData(Data) <-
    ?logged(CitizenId);
    updateDigitalState(CitizenId,Data).