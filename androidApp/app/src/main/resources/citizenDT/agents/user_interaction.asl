!readyUp.

+!readyUp <-
    !initializeGui;
    .print("User interaction ready");
    !!observeState.

+!initializeGui <-
    makeArtifact("mainUI", "it.unibo.citizenDigitalTwin.artifact.MainUI",[],MainUI);
    focus(MainUI).

+!observeState <-
    lookupArtifact("state", S)
    focus(S).

-!observeState <-
    .wait(100);
    !!observeState.

+ui_ready [artifact_name(Id,MainUI)] <-
    println("MainUI ready.").

/* Handle state change */
+state(State) <- showNewState(State).
/* ------------------ */

/* Handle user that changes page, acquiring the information needed */
+pageShown("HOME") [artifact_name(Id,MainUI)] <-
    ?state(State);
    showNewState(State).

+pageShown("DEVICES") [artifact_name(Id,MainUI)] <-
    .print("Devices").

+pageShown("NOTIFICATIONS") [artifact_name(Id,MainUI)] <-
    .print("Notifications").

+pageShown("SETTINGS") [artifact_name(Id,MainUI)] <-
    .print("Settings").
/* -------------------------------------------------------------- */
