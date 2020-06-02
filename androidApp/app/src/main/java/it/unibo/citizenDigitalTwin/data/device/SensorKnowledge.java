package it.unibo.citizenDigitalTwin.data.device;

import java.util.Objects;
import java.util.Optional;

import it.unibo.citizenDigitalTwin.data.Builder;

public class SensorKnowledge {

    private final String sensorDataIdentifier;
    private final String unitOfMeasure;
    private final String reqDataMessage;
    private final String subForDataMessage;

    private SensorKnowledge(final String sensorDataIdentifier, final String unitOfMeasure, final String reqDataMessage, final String subForDataMessage){
        this.sensorDataIdentifier = sensorDataIdentifier;
        this.unitOfMeasure = unitOfMeasure;
        this.reqDataMessage = reqDataMessage;
        this.subForDataMessage = subForDataMessage;
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


    public static class SensorKnowledgeBuilder implements Builder<SensorKnowledge> {

        private String sensorDataIdentifier;
        private String reqDataMessage;
        private String subForDataMessage;
        private String unitOfMeasure;

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
            return new SensorKnowledge(sensorDataIdentifier, unitOfMeasure, reqDataMessage, subForDataMessage);
        }
    }

}
