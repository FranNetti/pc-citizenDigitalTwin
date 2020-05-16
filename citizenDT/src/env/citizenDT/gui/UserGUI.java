// CArtAgO artifact code for project citizenDT

package citizenDT.gui;

import cartago.*;
import cartago.tools.GUIArtifact;
import citizenDT.state.State;

public class UserGUI extends GUIArtifact {
	
	private UserFrame gui;
	
	protected void init() {
		this.gui = new UserFrame(this);
	}

	@OPERATION
	void updateState(final State state) {
		gui.updateInfo(state);
	}
}

