// CArtAgO artifact code for project citizenDT

package citizenDT.gui;

import java.util.HashMap;
import java.util.Map;

import cartago.*;
import citizenDT.state.LeafCategory;

@ARTIFACT_INFO(
		outports = {
				@OUTPORT(name = "statusUpdate")
		}
)
public class StateTest extends Artifact {
	
	private static final String PROP_STATE = "state";
	
	void init(final String name, final String surname) {
		final Map<LeafCategory, String> state = new HashMap<>();
		state.put(LeafCategory.NAME, name);
		state.put(LeafCategory.SURNAME, surname);
		defineObsProperty(PROP_STATE, state);
	}

	@OPERATION
	void updateState(final String name, final String surname) {
		final ObsProperty prop = getObsProperty(PROP_STATE);
		Map<LeafCategory, String> state = (Map<LeafCategory, String>)prop.getValue();

		state.put(LeafCategory.NAME, "Francesco");
		state.put(LeafCategory.SURNAME, "Grandinetti");
		state.put(LeafCategory.BIRTHDATE, "27 aprile 1996");
		state.put(LeafCategory.ADDRESS, "Via zaccherini 25, Imola (BO)");
		prop.updateValue(state);
		try {
			execLinkedOp("statusUpdate","updateState", state);
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}
}

