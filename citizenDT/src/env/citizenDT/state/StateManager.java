// CArtAgO artifact code for project citizenDT

package citizenDT.state;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import cartago.*;
import citizenDT.common.Data;
import citizenDT.common.LeafCategory;

public class StateManager extends Artifact {
	
	private static final String PROP_STATE = "state";
	
	void init() {
		defineObsProperty(PROP_STATE, new State());
	}

	@OPERATION
	void updateState(final List<Data> stateDatas) {
		final ObsProperty stateProp = getObsProperty(PROP_STATE);
		State state = (State)stateProp.getValue();
		
		final Map<LeafCategory,Data> newState = stateDatas.stream()
				.collect(Collectors.toMap(Data::getDataCategory, Function.identity()));
		
		state = state.addMultipleData(newState);
		stateProp.updateValue(state);
	}

}

