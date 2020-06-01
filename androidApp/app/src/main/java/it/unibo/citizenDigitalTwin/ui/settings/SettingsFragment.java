package it.unibo.citizenDigitalTwin.ui.settings;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import it.unibo.citizenDigitalTwin.R;
import it.unibo.citizenDigitalTwin.ui.util.FragmentWithId;

public class SettingsFragment extends FragmentWithId {

    private static final String FRAGMENT_ID = "SETTINGS";

    public static SettingsFragment getInstance(){
        return new SettingsFragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        final TextView textView = root.findViewById(R.id.text_settings);
        textView.setText("This is settings fragment");
        return root;
    }

    @Override
    public String getFragmentId() {
        return FRAGMENT_ID;
    }
}
