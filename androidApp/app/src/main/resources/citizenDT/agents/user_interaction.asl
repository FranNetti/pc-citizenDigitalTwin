!init.

+!init
  <-  makeArtifact("mainUI", "it.unibo.citizenDigitalTwin.MainUI",[],MainUI);
      focus(MainUI).

+ui_ready [artifact_name(Id,MainUI)]
  <- println("MainUI ready.").