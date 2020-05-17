// CArtAgO artifact code for project citizenDT

package citizenDT.gui;

import cartago.*;
import cartago.tools.GUIArtifact;
import citizenDT.device.type.BluetoothDevice;
import citizenDT.state.State;

@ARTIFACT_INFO(
		outports = {
				@OUTPORT(name = "deviceManagement")
		}
)
public class UserGUI extends GUIArtifact {
	
	private UserFrame gui;
	
	protected void init() {
		this.gui = new UserFrame(this);
	}

	@LINK
	void updateState(final State state) {
		gui.updateInfo(state);
	}
	
	@OPERATION
	void pippo() {
		try {
			execLinkedOp("deviceManagement", "addDevice", new BluetoothDevice("pippone"), "pappone");
		} catch (OperationException e) {
			e.printStackTrace();
		}
	}
}

