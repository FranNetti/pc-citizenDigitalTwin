package it.unibo.citizenDigitalTwin.view_model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import it.unibo.citizenDigitalTwin.data.State;

public class HomeViewModel {

    private MutableLiveData<State> state;

    public HomeViewModel() {
        state = new MutableLiveData<>();
    }

    public void setState(final State state){
        this.state.setValue(state);
    }

    public LiveData<State> getState() {
        return state;
    }
}