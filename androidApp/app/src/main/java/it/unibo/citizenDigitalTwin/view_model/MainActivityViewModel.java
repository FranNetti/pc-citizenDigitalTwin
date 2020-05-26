package it.unibo.citizenDigitalTwin.view_model;

import androidx.lifecycle.ViewModel;

public class MainActivityViewModel extends ViewModel {

    public final HomeViewModel home = new HomeViewModel();
    public final DevicesViewModel devices = new DevicesViewModel();
    public final NotificationsViewModel notifications = new NotificationsViewModel();
    public final SettingsViewModel settings = new SettingsViewModel();

}
