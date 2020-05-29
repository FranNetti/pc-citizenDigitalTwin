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
    focus(S);
    !!observeDevices.

-!observeState <-
    .wait(100);
    !!observeState.

+!observeDevices <-
    lookupArtifact("devices", D);
    focus(D).

-!observeDevices <-
    .wait(100);
    !!observeDevices.

+ui_ready [artifact_name(Id,MainUI)] <-
    +viewReady
    println("MainUI ready.").

/* Handle state change */
+state(State) <-
    .count(viewReady, N);
    N == 1;
    showNewState(State).
/* ------------------ */

/* Handle connected devices change */
+connectedDevices(Devices) <-
    .count(viewReady, N);
    N == 1;
    showConnectedDevices(Devices).
/* ------------------ */

/* Handle user that changes page, acquiring the information needed */
+pageShown("HOME") [artifact_name(Id,MainUI)] <-
    ?state(State);
    showNewState(State).

+pageShown("DEVICES") [artifact_name(Id,MainUI)] <-
    ?connectedDevices(Devices);
    showConnectedDevices(Devices).

+pageShown("NOTIFICATIONS") [artifact_name(Id,MainUI)] <-
    .print("Notifications").

+pageShown("SETTINGS") [artifact_name(Id,MainUI)] <-
    .print("Settings").
/* -------------------------------------------------------------- */
