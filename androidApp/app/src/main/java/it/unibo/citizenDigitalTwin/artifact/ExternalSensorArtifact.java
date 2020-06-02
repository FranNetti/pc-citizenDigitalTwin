package it.unibo.citizenDigitalTwin.artifact;

import cartago.OPERATION;
import it.unibo.citizenDigitalTwin.data.category.LeafCategory;
import it.unibo.citizenDigitalTwin.data.device.SensorKnowledge;
import it.unibo.citizenDigitalTwin.data.device.communication.DeviceChannel;
import it.unibo.pslab.jaca_android.core.JaCaArtifact;

public class ExternalSensorArtifact extends JaCaArtifact {

    private static final String PROP_DATA = "data";

    private DeviceChannel channel;
    private SensorKnowledge knowledge;

    void init(final DeviceChannel channel, final LeafCategory leafCategory, final SensorKnowledge knowledge){
        channel.subscribeToIncomingMessages(this, msg -> {
            if(msg.isPresent() && msg.getType().equals(knowledge.getSensorDataIdentifier())){
                beginExternalSession();
                if(hasObsProperty(PROP_DATA)){
                    updateObsProperty(PROP_DATA, leafCategory, msg.getValue(), knowledge.getUnitOfMeasure());
                } else {
                    defineObsProperty(PROP_DATA, leafCategory, msg.getValue(), knowledge.getUnitOfMeasure());
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
