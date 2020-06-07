package it.unibo.citizenDigitalTwin.data.connection.channel.response;

import java.net.HttpURLConnection;
import java.util.Optional;

abstract class Response {
    private final int code;

    Response(final int code){
        this.code = code;
    }

    public boolean isSuccessful(){
        return this.code == HttpURLConnection.HTTP_OK;
    }

    public int getCode() {
        return code;
    }

    public abstract Optional<String> getErrorMessage();
}
