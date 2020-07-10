package it.unibo.citizenDigitalTwin.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import it.unibo.citizenDigitalTwin.data.category.GroupCategory;
import it.unibo.citizenDigitalTwin.data.category.LeafCategory;
import it.unibo.citizenDigitalTwin.db.entity.data.Data;

/**
 * Keep the current state.
 */
public class State {

    private final Map<LeafCategory, Data> state;

    public State() {
        this(new HashMap<>());
    }

    public State(final Collection<Data> dataCollection) {
        this(dataCollection.stream().collect(
                Collectors.toMap(Data::getLeafCategory, Function.identity())
        ));
    }

    private State(final Map<LeafCategory,Data> state) {
        this.state = state;
    }

    /**
     * Get the data relative to a specified leaf category.
     * @param dataCategory the leaf category
     * @return the data if present otherwise Optional.empty()
     */
    public Optional<Data> getData(final LeafCategory dataCategory) {
        return Optional.ofNullable(state.get(dataCategory));
    }

    /**
     * Get the current state
     * @return the current state
     */
    public Map<LeafCategory,Data> getState() {
        return new HashMap<>(state);
    }

    /**
     * Add new data to the current state.
     * @param data the new data
     * @return this
     */
    public State addData(final Data data) {
        final Map<LeafCategory,Data> state = getState();
        state.put(data.getLeafCategory(), data);
        return new State(state);
    }

    /**
     * Add new multiple data to the current state.
     * @param newState the new multiple data
     * @return this
     */
    public State addMultipleData(final Map<LeafCategory,Data> newState) {
        final Map<LeafCategory,Data> state = getState();
        state.putAll(newState);
        return new State(state);
    }

    /**
     * Get the data relative to a specified group category.
     * @param category the group category
     * @return all the data for the category
     */
    public List<Data> getDataFromGroupCategory(final GroupCategory category){
        return state.entrySet().stream()
                .filter(entry -> entry.getKey().getGroupCategory() == category)
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

}