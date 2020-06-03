package it.unibo.citizenDigitalTwin.data.connection;

public enum CommunicationStandard {

    DEFAULT_VALUE_IDENTIFIER("value"),
    DEFAULT_UNIT_OF_MEASURE_IDENTIFIER("um"),
    LATITUDE("lat"),
    LONGITUDE("lng");

    private final String message;

    CommunicationStandard(final String message){
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
