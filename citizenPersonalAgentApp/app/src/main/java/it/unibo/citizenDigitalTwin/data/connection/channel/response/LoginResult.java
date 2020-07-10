package it.unibo.citizenDigitalTwin.data.connection.channel.response;

import android.content.Context;

import java.net.HttpURLConnection;
import java.util.Optional;

import it.unibo.citizenDigitalTwin.R;

/**
 * Class that contains a response regarding the login procedure.
 */
public class LoginResult extends Response {

    /**
     * Create a successful login result response.
     * @param uri the user identifier
     * @return a new instance of LoginResult
     */
    public static LoginResult loginSuccessful(final String uri){
        return new LoginResult(uri, LOGIN_SUCCESS);
    }

    /**
     * Create a failed login result response.
     * @param failCode the error code
     * @return a new instance of LoginResult
     */
    public static LoginResult loginFailed(final int failCode){
        return new LoginResult(null, failCode);
    }

    public static final int LOGIN_SUCCESS = HttpURLConnection.HTTP_CREATED;
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

    /**
     * Get the current locale error message if present.
     * @param context the context of the application
     * @return the error message
     */
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

    /**
     * Get the uri if present
     * @return the user identifier
     */
    public Optional<String> getUri() {
        return Optional.ofNullable(uri);
    }
}
