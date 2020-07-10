package it.unibo.citizenDigitalTwin.artifact;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import java.util.Objects;

import cartago.INTERNAL_OPERATION;
import cartago.OPERATION;
import it.unibo.citizenDigitalTwin.R;
import it.unibo.pslab.jaca_android.core.ActivityArtifact;
import it.unibo.pslab.jaca_android.core.JaCaBaseActivity;

/**
 * The artifact that represents the Login Activity.
 */
public class LoginUIArtifact extends ActivityArtifact {

    public static class LoginActivity extends JaCaBaseActivity {}

    private static final int PASSWORD_MIN_LENGTH = 4;

    public void init() {
        super.init(LoginActivity.class, R.layout.activity_login, true);
    }

    /**
     * Show a new login failed error message.
     * @param message the login error message
     */
    @OPERATION
    public void showLoginFailed(final String message){
       execute(() -> {
            final ProgressBar loadingProgressBar = (ProgressBar) findUIElement(R.id.loading);
            loadingProgressBar.setVisibility(View.GONE);
            final AlertDialog.Builder builder = new AlertDialog.Builder((Activity)getActivityContext());
            builder.setMessage(message)
                    .setTitle(R.string.login_failed_title)
                    .setPositiveButton(android.R.string.ok, null);
            final AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

    @INTERNAL_OPERATION
    protected void setup() {
        initUI();
    }

    private void initUI(){
        execute(() -> {
            final EditText usernameEditText = (EditText) findUIElement(R.id.username);
            final EditText passwordEditText = (EditText) findUIElement(R.id.password);
            final Button loginButton = (Button) findUIElement(R.id.login);
            final ProgressBar loadingProgressBar = (ProgressBar) findUIElement(R.id.loading);
            passwordEditText.setOnEditorActionListener((v,actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    signalLogin(loadingProgressBar, usernameEditText, passwordEditText);
                }
                return false;
            });
            loginButton.setOnClickListener(v -> signalLogin(loadingProgressBar, usernameEditText, passwordEditText));
        });
    }

    private void signalLogin(final ProgressBar progressBar, final EditText username, final EditText password){
        final String email = username.getText().toString();
        final String pwd = password.getText().toString();

        if(!isEmailValid(email)){
            username.setError(getActivityContext().getString(R.string.invalid_username));
            username.requestFocus();
        } else if (!isPasswordValid(pwd)){
            password.setError(getActivityContext().getString(R.string.invalid_password));
            password.requestFocus();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            LoginUIArtifact.this.beginExternalSession();
            signal("loginButtonClicked", email, pwd);
            LoginUIArtifact.this.endExternalSession(true);
        }
    }

    private boolean isEmailValid(final String username) {
        if (Objects.isNull(username)) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return false;
        }
    }

    private boolean isPasswordValid(final String password) {
        return Objects.nonNull(password) && password.trim().length() > PASSWORD_MIN_LENGTH;
    }

}
