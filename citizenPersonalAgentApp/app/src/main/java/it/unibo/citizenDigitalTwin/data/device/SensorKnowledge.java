package it.unibo.citizenDigitalTwin.data.device;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.Optional;

import it.unibo.citizenDigitalTwin.data.Builder;
import it.unibo.citizenDigitalTwin.data.category.LeafCategory;

/**
 * Class that contains all the information needed to communicate with a device's sensor.
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
            throw new IllegalStateException("Leaf category not found: " + leafCategoryIdentifier);
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
        if(Objects.isNull(reqDataMessage) && Objects.isNull(subForDataMessage)){
            throw new IllegalStateException("Missing message for request and subscription");
        }
    }

    /**
     * Get the sensor leaf category.
     * @return the leaf category
     */
    public LeafCategory getLeafCategory() {
        return leafCategory;
    }

    /**
     * Get the sensor data type/identifier.
     * @return the sensor data type/identifier
     */
    public String getSensorDataIdentifier() {
        return sensorDataIdentifier;
    }

    /**
     * Get the unit of measure.
     * @return the unit of measure
     */
    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    /**
     * Get the message to send in order to get the resource value.
     * @return the message to send
     */
    public Optional<String> getReqDataMessage() {
        return Objects.nonNull(reqDataMessage) && !reqDataMessage.isEmpty() ? Optional.of(reqDataMessage) : Optional.empty();
    }

    /**
     * Get the message to send in order to get subscribed to the resource.
     * @return the message to send
     */
    public Optional<String> getSubForDataMessage() {
        return Objects.nonNull(subForDataMessage) && !subForDataMessage.isEmpty() ? Optional.of(subForDataMessage) : Optional.empty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SensorKnowledge that = (SensorKnowledge) o;
        return leafCategory == that.leafCategory &&
                sensorDataIdentifier.equals(that.sensorDataIdentifier) &&
                unitOfMeasure.equals(that.unitOfMeasure) &&
                Objects.equals(reqDataMessage, that.reqDataMessage) &&
                Objects.equals(subForDataMessage, that.subForDataMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leafCategory, sensorDataIdentifier, unitOfMeasure, reqDataMessage, subForDataMessage);
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
