package it.unibo.citizenDigitalTwin.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import it.unibo.citizenDigitalTwin.data.category.GroupCategory;
import it.unibo.citizenDigitalTwin.data.category.LeafCategory;
import it.unibo.citizenDigitalTwin.db.entity.data.Data;

public class State {

    private final Map<LeafCategory, Data> state;

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

    public List<Data> getDataFromGroupCategory(final GroupCategory category){
        return state.entrySet().stream()
                .filter(entry -> entry.getKey().getGroupCategory() == category)
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

}