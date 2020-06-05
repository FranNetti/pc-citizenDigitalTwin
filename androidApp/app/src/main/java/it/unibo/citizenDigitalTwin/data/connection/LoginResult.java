package it.unibo.citizenDigitalTwin.data.connection;

import android.content.Context;

import java.util.Optional;

import it.unibo.citizenDigitalTwin.R;

public class LoginResult {

    public static LoginResult loginSuccessful(final String uri){
        return new LoginResult(true, uri, LOGIN_SUCCESS);
    }

    public static LoginResult loginFailed(final int failCode){
        return new LoginResult(false, null, failCode);
    }

    private static final int LOGIN_SUCCESS = 201;

    private final boolean isSuccessful;
    private final String uri;
    private final int failCode;

    private LoginResult(final boolean success, final String uri, final int failCode){
        this.isSuccessful = success;
        this.uri = uri;
        this.failCode = failCode;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public Optional<String> getFailMessage(final Context context) {
        if(isSuccessful){
            return Optional.empty();
        } else {
            final int message;
            switch (failCode){
                case 400: message = R.string.login_failed_400; break;
                case 401: message = R.string.login_failed_401; break;
                case 500: message = R.string.login_failed_500; break;
                default: message = R.string.login_failed_unknown; break;
            }
            return Optional.of(context.getString(message) + (message == R.string.login_failed_unknown ? failCode : ""));
        }
    }

    public Optional<String> getUri() {
        return Optional.ofNullable(uri);
    }
}
