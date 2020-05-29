package it.unibo.citizenDigitalTwin.artifact;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import cartago.INTERNAL_OPERATION;
import cartago.OPERATION;
import it.unibo.citizenDigitalTwin.R;
import it.unibo.citizenDigitalTwin.data.State;
import it.unibo.citizenDigitalTwin.data.category.LeafCategory;
import it.unibo.citizenDigitalTwin.data.notification.DataNotification;
import it.unibo.citizenDigitalTwin.data.notification.MessageNotification;
import it.unibo.citizenDigitalTwin.data.notification.Notification;
import it.unibo.citizenDigitalTwin.ui.devices.Device;
import it.unibo.citizenDigitalTwin.ui.devices.DevicesFragment;
import it.unibo.citizenDigitalTwin.ui.home.HomeFragment;
import it.unibo.citizenDigitalTwin.ui.notifications.NotificationsFragment;
import it.unibo.citizenDigitalTwin.ui.settings.SettingsFragment;
import it.unibo.citizenDigitalTwin.ui.util.BackHelper;
import it.unibo.citizenDigitalTwin.ui.util.StateView;
import it.unibo.pslab.jaca_android.core.ActivityArtifact;
import it.unibo.pslab.jaca_android.core.JaCaBaseActivity;

public class MainUI extends ActivityArtifact implements Serializable {

    public static class MainActivity extends JaCaBaseActivity {

        @Override
        public void onBackPressed() {
            final BackHelper.BackListener listener = BackHelper.getInstance().getListener();
            if(Objects.isNull(listener) || !listener.onBackClick()) {
                super.onBackPressed();
            }
        }
    }

    private static final String PAGE_SHOWN_PROP = "pageShown";
    public enum PageShown {
        HOME,DEVICES,NOTIFICATIONS,SETTINGS;
    }

    private Fragment currentFragment = null;
    private List<Device> devices;
    private List<Notification> notifications;

    public void init() {
        super.init(MainActivity.class, R.layout.activity_main, true);
        defineObsProperty(PAGE_SHOWN_PROP, "");
        devices = new ArrayList<>();
    }

    @OPERATION
    public void showNewState(final State state){
        execute(() -> {
            if(currentFragment instanceof StateView){
                ((StateView)currentFragment).newData(state);
            }
        });
    }

    @OPERATION
    public void readNotification(final List<Notification> notifications){
        final BottomNavigationView navView = (BottomNavigationView) findUIElement(R.id.nav_view);
        final BadgeDrawable badge = navView.getOrCreateBadge(R.id.navigation_notifications);
        final int notificationToRead = (int)this.notifications.stream().filter(x -> !x.isRead()).count();
        if(notificationToRead > 0) {
            badge.setVisible(true);
            badge.setNumber(notificationToRead);
        } else {
            badge.setVisible(false);
            badge.clearNumber();
        }
    }

    @OPERATION
    public void newSubView(final Fragment fragment, final String identifier){
        this.currentFragment = fragment;
        updateObsProperty(PAGE_SHOWN_PROP, identifier);
    }

    @INTERNAL_OPERATION
    protected void setup() {
        devices = Arrays.asList(
                new Device("Braccialetto", Arrays.asList(LeafCategory.BLOOD_OXIGEN, LeafCategory.HEART_RATE)),
                new Device("Cardiofrequenziometro", Arrays.asList(LeafCategory.HEART_RATE)),
                new Device("Termometro", Arrays.asList(LeafCategory.TEMPERATURE)),
                new Device("Braccialetto", Arrays.asList(LeafCategory.BLOOD_OXIGEN, LeafCategory.HEART_RATE)),
                new Device("Cardiofrequenziometro", Arrays.asList(LeafCategory.HEART_RATE)),
                new Device("Termometro", Arrays.asList(LeafCategory.TEMPERATURE)),
                new Device("Braccialetto", Arrays.asList(LeafCategory.BLOOD_OXIGEN, LeafCategory.HEART_RATE)),
                new Device("Cardiofrequenziometro", Arrays.asList(LeafCategory.HEART_RATE)),
                new Device("Termometro", Arrays.asList(LeafCategory.TEMPERATURE)),
                new Device("Braccialetto", Arrays.asList(LeafCategory.BLOOD_OXIGEN, LeafCategory.HEART_RATE)),
                new Device("Cardiofrequenziometro", Arrays.asList(LeafCategory.HEART_RATE)),
                new Device("Termometro", Arrays.asList(LeafCategory.TEMPERATURE)),
                new Device("Braccialetto", Arrays.asList(LeafCategory.BLOOD_OXIGEN, LeafCategory.HEART_RATE)),
                new Device("Cardiofrequenziometro", Arrays.asList(LeafCategory.HEART_RATE)),
                new Device("Termometro", Arrays.asList(LeafCategory.TEMPERATURE)),
                new Device("Braccialetto", Arrays.asList(LeafCategory.BLOOD_OXIGEN, LeafCategory.HEART_RATE)),
                new Device("Cardiofrequenziometro", Arrays.asList(LeafCategory.HEART_RATE)),
                new Device("Termometro", Arrays.asList(LeafCategory.TEMPERATURE)),
                new Device("Braccialetto", Arrays.asList(LeafCategory.BLOOD_OXIGEN, LeafCategory.HEART_RATE)),
                new Device("Cardiofrequenziometro", Arrays.asList(LeafCategory.HEART_RATE)),
                new Device("Termometro", Arrays.asList(LeafCategory.TEMPERATURE))
        );

        notifications = Arrays.asList(
                new DataNotification("Pippo e Minnie", Arrays.asList(LeafCategory.NAME)),
                new MessageNotification("Cicciolina", "Vienimi a prendere fustacchione"),
                new DataNotification("Paperino", Arrays.asList(LeafCategory.BIRTHDATE)),
                new MessageNotification("Charles Leclerc", "Corriamo insieme!!!"),
                new DataNotification("Pluto", Arrays.asList(LeafCategory.ADDRESS)),
                new MessageNotification("Stefano Righini", "Vieni a recuperare i prodotti della mia terra"),
                new DataNotification("Topolino", Arrays.asList(LeafCategory.SURNAME)),
                new MessageNotification("Dottor Filippone", "Hai il Covid-19 coglione")
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

            final int notificationToRead = (int)notifications.stream().filter(x -> !x.isRead()).count();
            if(notificationToRead > 0) {
                badge.setVisible(true);
                badge.setNumber(notificationToRead);
            } else {
                badge.setVisible(false);
                badge.clearNumber();
            }

            final Toolbar toolbar = (Toolbar) findUIElement(R.id.toolbar);
            ((Activity)getActivityContext()).setActionBar(toolbar);
        });
    }

    private boolean handleNewMenuItemSelected(final int item){
        final FragmentManager fragmentManager = ((Activity)getActivityContext()).getFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        final Fragment fragment;
        final PageShown page;
        setTitle();
        switch (item){
            case R.id.navigation_home:
                fragment = HomeFragment.getInstance(this);
                page = PageShown.HOME;
                break;
            case R.id.navigation_devices:
                fragment = DevicesFragment.getInstance(devices);
                page = PageShown.DEVICES;
                break;
            case R.id.navigation_notifications:
                fragment = NotificationsFragment.getInstance(notifications, this);
                page = PageShown.NOTIFICATIONS;
                break;
            case R.id.navigation_settings:
                fragment = SettingsFragment.getInstance();
                page = PageShown.SETTINGS;
                break;
            default:
                fragment = null;
                page = PageShown.HOME;
                break;
        }
        if(Objects.nonNull(fragment)){
            fragmentManager.popBackStack();
            fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
            fragmentTransaction.commit();
            beginExternalSession();
            newSubView(fragment, page.name());
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

}
