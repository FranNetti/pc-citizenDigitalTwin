package it.unibo.citizenDigitalTwin.artifact;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;
import java.util.Objects;

import androidx.core.app.ActivityCompat;
import cartago.INTERNAL_OPERATION;
import cartago.OPERATION;
import it.unibo.citizenDigitalTwin.R;
import it.unibo.citizenDigitalTwin.data.State;
import it.unibo.citizenDigitalTwin.data.device.ConnectionResult;
import it.unibo.citizenDigitalTwin.db.entity.notification.Notification;
import it.unibo.citizenDigitalTwin.data.device.type.Device;
import it.unibo.citizenDigitalTwin.ui.connect_device.ConnectDeviceFragment;
import it.unibo.citizenDigitalTwin.ui.devices.DevicesFragment;
import it.unibo.citizenDigitalTwin.ui.home.HomeFragment;
import it.unibo.citizenDigitalTwin.ui.notifications.NotificationsFragment;
import it.unibo.citizenDigitalTwin.ui.util.FragmentWithId;
import it.unibo.citizenDigitalTwin.ui.util.BackHelper;
import it.unibo.citizenDigitalTwin.ui.util.StateViewer;
import it.unibo.pslab.jaca_android.core.ActivityArtifact;
import it.unibo.pslab.jaca_android.core.JaCaBaseActivity;

/**
 * Artifact that represents the main view
 */
public class MainUI extends ActivityArtifact {

    public static class MainActivity extends JaCaBaseActivity {

        @Override
        public void onBackPressed() {
            final BackHelper.BackListener listener = BackHelper.getInstance().getListener();
            if(Objects.isNull(listener) || !listener.onBackClick()) {
                super.onBackPressed();
            }
        }
    }

    public static final String MAIN_UI_ACTIVITY_NAME = "mainUI";

    private static final int GPS_REQ_CODE = 12;
    private static final int GPS_BACKGROUND_REQ_CODE = 13;
    private static final String MAIN_UI_TAG = "[MainUI]";
    private static final String PAGE_SHOWN_PROP = "pageShown";
    private static final String MSG_CLOSING = "closing";

    private FragmentWithId currentFragment = null;
    private final MainUIMediator mediator = new MainUIMediator();

    public void init() {
        super.init(MainActivity.class, R.layout.activity_main, true);
        defineObsProperty(PAGE_SHOWN_PROP, "");
        bindOnStopEventToOp("onStop");
    }

    @OPERATION
    public void onStop() {
        signal(MSG_CLOSING);
    }

    @OPERATION
    public void showNewState(final State state){
        execute(() -> {
            if(currentFragment instanceof StateViewer){
                ((StateViewer)currentFragment).newData(state);
            }
        });
    }

    @OPERATION
    public void showConnectedDevices(final List<Device> devices){
        execute(() -> {
            if(currentFragment instanceof DevicesFragment){
                ((DevicesFragment)currentFragment).updateConnectedDevices(devices);
            }
        });
    }

    @OPERATION
    public void showPairedDevices(final List<Device> devices){
        execute(() -> {
            if(currentFragment instanceof ConnectDeviceFragment){
                ((ConnectDeviceFragment)currentFragment).updatePairedDevices(devices);
            }
        });
    }

    @OPERATION
    public void showDiscoveredDevices(final List<Device> devices){
        execute(() -> {
            if(currentFragment instanceof ConnectDeviceFragment){
                ((ConnectDeviceFragment)currentFragment).updateDiscoveredDevices(devices);
            }
        });
    }

    @OPERATION
    public void showResultOfConnectionToDevice(final ConnectionResult result){
        if(result != ConnectionResult.SUCCESS){
            showDialog(result.getMessage(getActivityContext()));
        }
    }

    @OPERATION
    public void showResultOfDisconnectionToDevice(final boolean success){
        if(!success){
            showDialog(getActivityContext().getString(R.string.disconnection_failed));
        }
    }

    @OPERATION
    public void showNotifications(final List<Notification> notifications){
        execute(() -> {
            final BottomNavigationView navView = (BottomNavigationView) findUIElement(R.id.nav_view);
            final BadgeDrawable badge = navView.getOrCreateBadge(R.id.navigation_notifications);
            final int notificationToRead = (int)notifications.stream().filter(x -> !x.isRead()).count();
            if(notificationToRead > 0) {
                badge.setVisible(true);
                badge.setNumber(notificationToRead);
            } else {
                badge.setVisible(false);
                badge.clearNumber();
            }
            if(currentFragment instanceof NotificationsFragment){
                ((NotificationsFragment)currentFragment).updateNotifications(notifications);
            }
        });
    }

    @OPERATION
    public void newSubView(final FragmentWithId fragment){
        this.currentFragment = fragment;
        updateObsProperty(PAGE_SHOWN_PROP, fragment.getFragmentId());
    }

    @INTERNAL_OPERATION
    protected void setup() {
        ActivityCompat.requestPermissions(
                (Activity) getActivityContext(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                GPS_REQ_CODE
        );
        ActivityCompat.requestPermissions(
                (Activity) getActivityContext(),
                new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                GPS_BACKGROUND_REQ_CODE
        );
        initUI();
    }

    private void initUI(){
        execute(() -> {
            final BottomNavigationView navView = (BottomNavigationView) findUIElement(R.id.nav_view);
            navView.setOnNavigationItemSelectedListener(this::handleNewMenuItemSelected);
            handleNewMenuItemSelected(R.id.navigation_home);

            final BadgeDrawable badge = navView.getOrCreateBadge(R.id.navigation_notifications);
            badge.setVisible(false);

            final Toolbar toolbar = (Toolbar) findUIElement(R.id.toolbar);
            ((Activity)getActivityContext()).setActionBar(toolbar);
        });
    }

    private boolean handleNewMenuItemSelected(final int item){
        final FragmentManager fragmentManager = ((Activity)getActivityContext()).getFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        final FragmentWithId fragment;
        setTitle();
        switch (item){
            case R.id.navigation_home:
                fragment = HomeFragment.getInstance(mediator);
                break;
            case R.id.navigation_devices:
                fragment = DevicesFragment.getInstance(mediator);
                break;
            case R.id.navigation_notifications:
                fragment = NotificationsFragment.getInstance(mediator);
                break;
            default:
                fragment = null;
                break;
        }
        if(Objects.nonNull(fragment)){
            fragmentManager.popBackStack();
            fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
            fragmentTransaction.commit();
            beginExternalSession();
            newSubView(fragment);
            endExternalSession(true);
            return true;
        }
        return false;
    }

    private boolean handleNewMenuItemSelected(final MenuItem item){
        return handleNewMenuItemSelected(item.getItemId());
    }

    private void setTitle(){
        final Toolbar toolbar = (Toolbar) findUIElement(R.id.toolbar);
        toolbar.setTitle("");
    }

    private void showDialog(final String message){
        execute(() -> {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext());
            builder.setMessage(message)
                    .setPositiveButton(android.R.string.ok, null);
            final AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

    public class MainUIMediator implements Parcelable {

        protected MainUIMediator() {
        }

        public final Creator<MainUIMediator> CREATOR = new Creator<MainUIMediator>() {
            @Override
            public MainUIMediator createFromParcel(final Parcel in) {
                return new MainUIMediator();
            }

            @Override
            public MainUIMediator[] newArray(final int size) {
                return new MainUIMediator[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {}

        public void newSubView(final FragmentWithId fragment){
            MainUI.this.beginExternalSession();
            MainUI.this.newSubView(fragment);
            MainUI.this.endExternalSession(true);
        }

        public void connectToDevice(final Device device, final String model) {
            MainUI.this.beginExternalSession();
            signal("deviceSelected", device, model);
            MainUI.this.endExternalSession(true);
        }

        public void disconnectFromDevice(final Device device) {
            MainUI.this.beginExternalSession();
            signal("deviceToDisconnect", device);
            MainUI.this.endExternalSession(true);
        }

        public void readNotification(final List<Notification> notifications){
            MainUI.this.beginExternalSession();
            signal("notificationsRead", notifications);
            MainUI.this.endExternalSession(true);
        }
    }
}
