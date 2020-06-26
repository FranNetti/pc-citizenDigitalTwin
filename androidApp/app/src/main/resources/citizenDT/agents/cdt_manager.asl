maxRefreshTokenAttempts(3).
refreshTokenAttempts(0).
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
    makeArtifact("configurations", "it.unibo.citizenDigitalTwin.artifact.ConfigurationsArtifact",[],Configurations);
    focus(Configurations);
	makeArtifact("state","it.unibo.citizenDigitalTwin.artifact.StateManagerArtifact",[],StateManager);
	focus(StateManager);
	?citizenService(CitizenServiceAddress)
	?authenticationService(AuthenticationServiceAddress)
	makeArtifact("connection","it.unibo.citizenDigitalTwin.artifact.ConnectionManagerArtifact",[CitizenServiceAddress,AuthenticationServiceAddress],ConnectionManager);
	focus(ConnectionManager);
	.print("CDT Manager ready").

/* New state from ConnectionManager (Digital State) */
+newState(NewData) <-
	updateState(NewData);
	createNotifications(NewData).

+token(_,Ttl) <-
    -+refreshTokenAttempts(0);
    !!refreshToken(Ttl).

+logged(CitizenId) <-
    getDigitalState(CitizenId).

+refreshTokenFailed : maxRefreshTokenAttempts(M) & refreshTokenAttempts(X) & X < M <-
    -+refreshTokenAttempts(X+1);
    refreshToken.

+refreshTokenFailed <-
    !!login.

+newGeneratedData(Data) <-
    ?logged(CitizenId);
    updateDigitalState(CitizenId,Data).

+deactivate : logged(_) <-
    ?logged(CitizenId);
    stopConnectionToDigitalState(CitizenId).