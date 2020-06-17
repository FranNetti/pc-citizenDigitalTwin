maxAttempts(3).
attempts(0).
credentials("","").
citizenService("192.168.1.9:8080").
authenticationService("192.168.1.9:8081").

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
	?citizenService(CitizenServiceAddress)
	?authenticationService(AuthenticationServiceAddress)
	makeArtifact("connection","it.unibo.citizenDigitalTwin.artifact.ConnectionManager",[CitizenServiceAddress,AuthenticationServiceAddress],ConnectionManager);
	focus(ConnectionManager);
	.print("CDT Manager ready").

+newState(NewData) <-
	updateState(NewData);
	createNotifications(NewData).

+token(_,Ttl) <-
    -+attempts(0);
    !!refreshToken(Ttl).

+logged(CitizenId) <-
    getDigitalState(CitizenId).

+refreshTokenFailed : maxAttempts(M) & attempts(X) & X < M <-
    -+attempts(X+1);
    refreshToken.

+refreshTokenFailed <-
    !!login.

+newGeneratedData(Data) <-
    ?logged(CitizenId);
    updateDigitalState(CitizenId,Data).

+deactivate : logged(_) <-
    ?logged(CitizenId);
    onClosing(CitizenId).