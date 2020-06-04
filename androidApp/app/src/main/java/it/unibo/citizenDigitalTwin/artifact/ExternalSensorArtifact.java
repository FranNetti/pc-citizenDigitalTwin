package it.unibo.citizenDigitalTwin.artifact;

import cartago.OPERATION;
import it.unibo.citizenDigitalTwin.data.category.LeafCategory;
import it.unibo.citizenDigitalTwin.data.connection.CommunicationStandard;
import it.unibo.citizenDigitalTwin.data.device.SensorKnowledge;
import it.unibo.citizenDigitalTwin.data.device.communication.DeviceChannel;
import it.unibo.citizenDigitalTwin.db.entity.Feeder;
import it.unibo.citizenDigitalTwin.db.entity.data.Data;
import it.unibo.citizenDigitalTwin.db.entity.data.DataBuilder;
import it.unibo.pslab.jaca_android.core.JaCaArtifact;

/**
 * Artifact that represents external sensor from which it is possible to gather data
 */
public class ExternalSensorArtifact extends JaCaArtifact {

    private static final String PROP_DATA = "data";

    private DeviceChannel channel;
    private SensorKnowledge knowledge;

    void init(final String deviceName, final DeviceChannel channel, final LeafCategory leafCategory, final SensorKnowledge knowledge){
        final Feeder feeder = new Feeder("", deviceName);

        channel.subscribeToIncomingMessages(this, msg -> {
            if(msg.isPresent() && msg.getType().equals(knowledge.getSensorDataIdentifier())){
                final Data newData = new DataBuilder()
                        .feeder(feeder)
                        .leafCategory(leafCategory)
                        .addInformation(
                                CommunicationStandard.DEFAULT_VALUE_IDENTIFIER,
                                msg.getValue()
                        )
                        .addInformation(
                                CommunicationStandard.DEFAULT_UNIT_OF_MEASURE_IDENTIFIER,
                                knowledge.getUnitOfMeasure()
                        )
                        .build();
                beginExternalSession();
                if(hasObsProperty(PROP_DATA)){
                    updateObsProperty(PROP_DATA, newData);
                } else {
                    defineObsProperty(PROP_DATA, newData);
                }
                endExternalSession(true);
            }
        });
        this.channel = channel;
        this.knowledge = knowledge;
    }

    @OPERATION
    public void sendDataRequest(){
        knowledge.getReqDataMessage().ifPresent(channel::askForData);
    }

    @OPERATION
    public void subscribeForData(){
        knowledge.getSubForDataMessage().ifPresent(channel::askForData);
    }

    @OPERATION
    public void unsubscribeForData(){
        channel.unsubscribeFromIncomingMessages(this);
    }

}
