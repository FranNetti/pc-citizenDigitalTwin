package it.unibo.citizenDigitalTwin.data.device;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.Optional;

import it.unibo.citizenDigitalTwin.data.Builder;
import it.unibo.citizenDigitalTwin.data.category.LeafCategory;

/**
 * Class that contains all the information needed to communicate with a device's sensor
 */
public class SensorKnowledge {

    private static final String LEAF_CATEGORY = "leafCategory";
    private static final String DATA_ID = "dataId";
    private static final String UM = "um";
    private static final String REQ_FOR_DATA = "requestMessage";
    private static final String SUB_FOR_DATA = "subMessage";

    private final LeafCategory leafCategory;
    private final String sensorDataIdentifier;
    private final String unitOfMeasure;
    private String reqDataMessage;
    private String subForDataMessage;

    private SensorKnowledge(
            final LeafCategory leafCategory,
            final String sensorDataIdentifier,
            final String unitOfMeasure,
            final String reqDataMessage,
            final String subForDataMessage) {
        this.leafCategory = leafCategory;
        this.sensorDataIdentifier = sensorDataIdentifier;
        this.unitOfMeasure = unitOfMeasure;
        this.reqDataMessage = reqDataMessage;
        this.subForDataMessage = subForDataMessage;
    }

    public SensorKnowledge(final JSONObject object) throws JSONException, IllegalStateException {
        final String leafCategoryIdentifier = object.getString(LEAF_CATEGORY);
        final Optional<LeafCategory> leafCategory = LeafCategory.findByLeafIdentifier(leafCategoryIdentifier);
        if(leafCategory.isPresent()){
            this.leafCategory = leafCategory.get();
        } else {
            throw new IllegalStateException();
        }
        this.sensorDataIdentifier = object.getString(DATA_ID);
        this.unitOfMeasure = object.getString(UM);
        try {
            this.reqDataMessage = object.getString(REQ_FOR_DATA);
        } catch (final JSONException e){
            this.reqDataMessage = null;
        }
        try {
            this.subForDataMessage = object.getString(SUB_FOR_DATA);
        } catch (final JSONException e){
            this.subForDataMessage = null;
        }
    }

    public LeafCategory getLeafCategory() {
        return leafCategory;
    }

    public String getSensorDataIdentifier() {
        return sensorDataIdentifier;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public Optional<String> getReqDataMessage() {
        return Objects.nonNull(reqDataMessage) && !reqDataMessage.isEmpty() ? Optional.of(reqDataMessage) : Optional.empty();
    }

    public Optional<String> getSubForDataMessage() {
        return Objects.nonNull(subForDataMessage) && !subForDataMessage.isEmpty() ? Optional.of(subForDataMessage) : Optional.empty();
    }

    /**
     * Builder for the {@link SensorKnowledge} class
     */
    public static class SensorKnowledgeBuilder implements Builder<SensorKnowledge> {

        private LeafCategory leafCategory;
        private String sensorDataIdentifier;
        private String reqDataMessage;
        private String subForDataMessage;
        private String unitOfMeasure;

        public SensorKnowledgeBuilder leafCategory(final LeafCategory leafCategory){
            this.leafCategory = leafCategory;
            return this;
        }

        public SensorKnowledgeBuilder sensorDataIdentifier(final String sensorDataIdentifier){
            this.sensorDataIdentifier = sensorDataIdentifier;
            return this;
        }

        public SensorKnowledgeBuilder reqDataMessage(final String reqDataMessage){
            this.reqDataMessage = reqDataMessage;
            return this;
        }

        public SensorKnowledgeBuilder subForDataMessage(final String subForDataMessage){
            this.subForDataMessage = subForDataMessage;
            return this;
        }

        public SensorKnowledgeBuilder unitOfMeasure(final String unitOfMeasure){
            this.unitOfMeasure = unitOfMeasure;
            return this;
        }

        @Override
        public SensorKnowledge build() {
            Objects.requireNonNull(sensorDataIdentifier);
            Objects.requireNonNull(unitOfMeasure);
            Objects.requireNonNull(leafCategory);
            return new SensorKnowledge(leafCategory, sensorDataIdentifier, unitOfMeasure, reqDataMessage, subForDataMessage);
        }
    }

}
