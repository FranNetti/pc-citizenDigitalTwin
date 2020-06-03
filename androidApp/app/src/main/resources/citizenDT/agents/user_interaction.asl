!readyUp.

+!readyUp <-
    !initializeGui;
    .print("User interaction ready");
    !!observeState.

+!initializeGui <-
    makeArtifact("mainUI", "it.unibo.citizenDigitalTwin.artifact.MainUI",[],MainUI);
    focus(MainUI).

+!observeState <-
    lookupArtifact("state", S);
    focus(S).

-!observeState <-
    .wait(100);
    !!observeState.

+ui_ready [artifact_name(Id,MainUI)] <-
    +viewReady;
    .send(device_manager, tell, activate);
    .send(cdt_manager, tell, activate);
    println("MainUI ready.");
    !!observeDevices.

+!observeDevices <-
    lookupArtifact("devices", D);
    focus(D);
    lookupArtifact("bluetooth", B);
    focus(B).

-!observeDevices <-
    .wait(100);
    !!observeDevices.

/* Handle state change */
+state(State): viewReady <- showNewState(State).

/* Handle discovered devices change */
+discoveredDevices(Devices): viewReady <- showDiscoveredDevices(Devices).

/* Handle connected devices change */
+connectedDevices(Devices): viewReady <- showConnectedDevices(Devices).

/* Handle when user select a device to connect to */
+deviceSelected(Device, Model) <-
    connectToDevice(Device, Model, Result);
    showResultOfConnectionToDevice(Result).

/* Handle bluetooth changes */
+bluetoothState(X): pageShown("DEVICES") <-
    ?connectedDevices(Devices);
    showConnectedDevices(Devices).

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
    .print("Notifications").

+pageShown("SETTINGS") [artifact_name(Id,MainUI)] <-
    .print("Settings").

+pageShown(X) [artifact_name(Id,MainUI)] <- .print(X).