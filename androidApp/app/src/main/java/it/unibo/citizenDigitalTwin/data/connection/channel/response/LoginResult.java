package it.unibo.citizenDigitalTwin.data.connection.channel.response;

import android.content.Context;

import java.net.HttpURLConnection;
import java.util.Optional;

import it.unibo.citizenDigitalTwin.R;

public class LoginResult extends Response {

    public static LoginResult loginSuccessful(final String uri){
        return new LoginResult(uri, LOGIN_SUCCESS);
    }

    public static LoginResult loginFailed(final int failCode){
        return new LoginResult(null, failCode);
    }

    public static final int LOGIN_SUCCESS = 201;
    public static final int APPLICATION_ERROR = 600;
    public static final int MALFORMED_RECEIVED_DATA = 601;

    private final String uri;

    private LoginResult(final String uri, final int failCode){
        super(failCode);
        this.uri = uri;
    }

    @Override
    public boolean isSuccessful() {
        return getCode() == LOGIN_SUCCESS;
    }

    @Override
    public Optional<String> getErrorMessage() {
        return Optional.empty();
    }

    public Optional<String> getErrorMessage(final Context context) {
        if(isSuccessful()){
            return Optional.empty();
        } else {
            final int message;
            switch (getCode()){
                case HttpURLConnection.HTTP_BAD_REQUEST:
                    message = R.string.login_failed_400; break;
                case HttpURLConnection.HTTP_UNAUTHORIZED:
                    message = R.string.login_failed_401; break;
                case HttpURLConnection.HTTP_INTERNAL_ERROR:
                    message = R.string.login_failed_500; break;
                default:
                    message = R.string.login_failed_unknown; break;
            }
            return Optional.of(context.getString(message) + (message == R.string.login_failed_unknown ? getCode() : ""));
        }
    }

    public Optional<String> getUri() {
        return Optional.ofNullable(uri);
    }
}
