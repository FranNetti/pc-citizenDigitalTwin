// CArtAgO artifact code for project citizenDT

package citizenDT.gui;

import cartago.*;
import cartago.tools.GUIArtifact;
import citizenDT.device.type.Device;
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

	@OPERATION
	void showNewState(final State state) {
		gui.updateInfo(state);
	}
	
	@OPERATION
	void addDevice(final Device device, final String model) {
		execInternalOp("sendAddDeviceRequest", device, model);
	}
	
	@OPERATION
	void removeDevice(final Device device) {
		execInternalOp("sendRemoveDeviceRequest", device);
	}
	
	@INTERNAL_OPERATION
	void sendAddDeviceRequest(final Device device, final String model) {
		try {
			OpFeedbackParam<Boolean> operationResult = new OpFeedbackParam<Boolean>();
			execLinkedOp("deviceManagement", "addDevice",device, model, operationResult);
			if(operationResult.get()) {
				gui.deviceAdded();
			}
		} catch (OperationException e) {
			e.printStackTrace();
		}
	}
	
	@INTERNAL_OPERATION
	void sendRemoveDeviceRequest(final Device device) {
		try {
			OpFeedbackParam<Boolean> operationResult = new OpFeedbackParam<Boolean>();
			execLinkedOp("deviceManagement", "removeDevice",device, operationResult);
			if(operationResult.get()) {
				gui.deviceRemoved();
			}
		} catch (OperationException e) {
			e.printStackTrace();
		}
	}
}

