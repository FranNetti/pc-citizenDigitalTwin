package it.unibo.citizenDigitalTwin;

import android.os.Bundle;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Arrays;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import it.unibo.citizenDigitalTwin.data.category.LeafCategory;
import it.unibo.citizenDigitalTwin.data.notification.DataNotification;
import it.unibo.citizenDigitalTwin.data.notification.MessageNotification;
import it.unibo.citizenDigitalTwin.data.notification.Notification;
import it.unibo.citizenDigitalTwin.view_model.MainActivityViewModel;

public class MainActivity extends AppCompatActivity {

    private MainActivityViewModel mainActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,
                R.id.navigation_devices,
                R.id.navigation_notifications,
                R.id.navigation_settings
        ).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        final BadgeDrawable badge = navView.getOrCreateBadge(R.id.navigation_notifications);
        badge.setVisible(false);

        mainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        mainActivityViewModel.notifications.getNotifications().observe(this, notifications -> {
            final int notificationToRead = (int)notifications.stream().filter(x -> !x.isRead()).count();
            if(notificationToRead > 0) {
                badge.setVisible(true);
                badge.setNumber(notificationToRead);
            } else {
                badge.setVisible(false);
                badge.clearNumber();
            }
        });

        mainActivityViewModel.notifications.addNotifications(
                Arrays.asList(
                        new DataNotification("Pippo e Minnie", Arrays.asList(LeafCategory.NAME)),
                        new MessageNotification("Cicciolina", "Vienimi a prendere fustacchione"),
                        new DataNotification("Paperino", Arrays.asList(LeafCategory.BIRTHDATE)),
                        new MessageNotification("Charles Leclerc", "Corriamo insieme!!!"),
                        new DataNotification("Pluto", Arrays.asList(LeafCategory.ADDRESS)),
                        new MessageNotification("Stefano Righini", "Vieni a recuperare i prodotti della mia terra"),
                        new DataNotification("Topolino", Arrays.asList(LeafCategory.SURNAME)),
                        new MessageNotification("Dottor Filippone", "Hai il Covid-19 coglione")
                )
        );
    }

    public void newNotification(final Notification notification) {
        mainActivityViewModel.notifications.addNotification(notification);
    }

}
