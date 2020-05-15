// CArtAgO artifact code for project citizenDT

package citizenDT.gui;

import java.util.Map;

import cartago.*;
import cartago.tools.GUIArtifact;
import citizenDT.state.LeafCategory;

public class UserGUI extends GUIArtifact {
	
	private UserFrame gui;
	
	protected void init() {
		this.gui = new UserFrame(this);
	}

	@OPERATION
	void updateState(final Map<LeafCategory, String> state) {
		gui.updateInfo(state);
	}
}

