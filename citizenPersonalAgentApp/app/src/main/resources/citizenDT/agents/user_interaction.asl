!readyUp.

+!readyUp <-
    !initializeLogin;
    .print("User interaction ready").

+!initializeLogin <-
    makeArtifact("loginUI", "it.unibo.citizenDigitalTwin.artifact.LoginUIArtifact",[],LoginUI);
    focus(LoginUI).

+!initializeMainUI <-
    makeArtifact("mainUI", "it.unibo.citizenDigitalTwin.artifact.MainUIArtifact",[],MainUI);
    focus(MainUI);
    lookupArtifact("loginUI",LoginUI);
    disposeArtifact(LoginUI).

+ui_ready [artifact_name(Id,loginUI)] <-
    .send(cdt_manager, tell, activate);
    !!observeState.

+ui_ready [artifact_name(Id,mainUI)] <-
    +mainViewReady;
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
    focus(B);
    lookupArtifact("notifications", N);
    focus(N).

-!observeDevices <-
    .wait(100);
    !!observeDevices.

/* Handle user login */
+loginButtonClicked(Username, Password) <- .send(cdt_manager, achieve, login(Username, Password)).
+logged(_) <- !!initializeMainUI.
+loginFailed(Message) <- showLoginFailed(Message).

/* Handle state change */
+state(State): mainViewReady <- showNewState(State).

/* Handle discovered devices change */
+discoveredDevices(Devices): mainViewReady <- showDiscoveredDevices(Devices).

/* Handle connected devices change */
+connectedDevices(Devices): mainViewReady <- showConnectedDevices(Devices).

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
+notifications(Notifications): mainViewReady <- showNotifications(Notifications).

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

+closing <-
    .send(cdt_manager, tell, deactivate);
    .send(device_manager, tell, deactivate).