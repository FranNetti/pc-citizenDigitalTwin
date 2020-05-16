package citizenDT.state;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import citizenDT.common.Data;
import citizenDT.common.LeafCategory;

public class State {
	
	private final Map<LeafCategory,Data> state;
	
	public State() {
		this(new HashMap<>());
	}
	
	private State(final Map<LeafCategory,Data> state) {
		this.state = state;
	}
	
	public Optional<Data> getData(final LeafCategory dataCategory) {
		return Optional.ofNullable(state.get(dataCategory));
	}
	
	public Map<LeafCategory,Data> getState() {
		return new HashMap<>(state);
	}
	
	public State addData(final LeafCategory dataCategory, final Data data) {
		final Map<LeafCategory,Data> state = getState();
		state.put(dataCategory, data);
		return new State(state);
	}
	
	public State addMultipleData(final Map<LeafCategory,Data> newState) {
		final Map<LeafCategory,Data> state = getState();
		state.putAll(newState);
		return new State(state);
	}
	
}
