!readyUp.

+!readyUp <-
    !initializeLogin;
    .print("User interaction ready").

+!initializeLogin <-
    makeArtifact("loginUI", "it.unibo.citizenDigitalTwin.artifact.LoginUI",[],LoginUI);
    focus(LoginUI).

+!initializeGui <-
    makeArtifact("mainUI", "it.unibo.citizenDigitalTwin.artifact.MainUI",[],MainUI);
    focus(MainUI);
    lookupArtifact("loginUI",LoginUI);
    disposeArtifact(LoginUI).

+ui_ready [artifact_name(Id,LoginUI)] <-
    .send(cdt_manager, tell, activate);
    !!observeState.

+ui_ready [artifact_name(Id,MainUI)] <-
    +viewReady;
    .send(device_manager, tell, activate);
    println("MainUI ready.");
    !!observeDevices.

+!observeState <-
    lookupArtifact("state", S);
    focus(S).

-!observeState <-
    .wait(100);
    !!observeState.

+!observeDevices <-
    lookupArtifact("devices", D);
    focus(D);
    lookupArtifact("bluetooth", B);
    focus(B).

-!observeDevices <-
    .wait(100);
    !!observeDevices.

/* Handle user login */
+loginButtonClicked(Username, Password) <- .send(cdt_manager, achieve, login(Username, Password)).

+loginFailed(Message) <- showLoginFailed(Message).

/* Handle state change */
+state(State): viewReady <- showNewState(State).

/* Handle discovered devices change */
+discoveredDevices(Devices): viewReady <- showDiscoveredDevices(Devices).

/* Handle connected devices change */
+connectedDevices(Devices): viewReady <- showConnectedDevices(Devices).

/* Handle when user selects a device to connect to */
+deviceSelected(Device, Model) <-
    connectToDevice(Device, Model, Result);
    showResultOfConnectionToDevice(Result).

/* Handle when user wants to disconnect from a device */
+deviceToDisconnect(Device) <-
    disconnectFromDevice(Device, Result);
    showResultOfDisconnectionToDevice(Result).

/* Handle bluetooth changes */
+bluetoothState(_): pageShown("DEVICES") <-
    ?connectedDevices(Devices);
    showConnectedDevices(Devices).

/* Handle notifications changes */
+notifications(Notifications): viewReady <- showNotifications(Notifications).

/* Handle when notifications are read */
+notificationsRead(Notifications) <- setNotificationsRead(Notifications).

/* Handle user that changes page, acquiring the information needed */
+pageShown("HOME") [artifact_name(Id,MainUI)] <-
    ?state(State);
    showNewState(State).

+pageShown("DEVICES") [artifact_name(Id,MainUI)]: bluetoothState("OFF") <- askToTurnOnBluetooth.

+pageShown("DEVICES") [artifact_name(Id,MainUI)] <-
    ?connectedDevices(Devices);
    showConnectedDevices(Devices).

+pageShown("CONNECT_DEVICE") [artifact_name(Id,MainUI)] <-
    ?pairedDevices(Devices);
    showPairedDevices(Devices);
    scanForDevices.

+pageShown("NOTIFICATIONS") [artifact_name(Id,MainUI)] <-
    ?notifications(Notifications);
    showNotifications(Notifications).

+pageShown(X) [artifact_name(Id,MainUI)] <- .print(X).