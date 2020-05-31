package it.unibo.citizenDigitalTwin.ui.notifications;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import it.unibo.citizenDigitalTwin.artifact.MainUI;
import it.unibo.citizenDigitalTwin.R;
import it.unibo.citizenDigitalTwin.data.notification.Notification;
import it.unibo.citizenDigitalTwin.ui.util.BackHelper;

public class NotificationsFragment extends Fragment implements NotificationAdapter.NotificationAdapterListener, BackHelper.BackListener {

    private static final String NOTIFICATIONS = "notifications";
    private static final String ARTIFACT = "artifact";

    public static NotificationsFragment getInstance(final List<Notification> notifications, final MainUI.MainUIMediator artifact){
        final NotificationsFragment fragment = new NotificationsFragment();
        final Bundle bundle = new Bundle();
        if(Objects.nonNull(notifications)){
            bundle.putSerializable(NOTIFICATIONS, new ArrayList<>(notifications));
        }  else {
            bundle.putSerializable(NOTIFICATIONS, new ArrayList<>());
        }
        if(Objects.nonNull(artifact)) {
            bundle.putParcelable(ARTIFACT, artifact);
        }
        fragment.setArguments(bundle);
        return fragment;
    }

    private TextView emptyNotifications;
    private FloatingActionButton readNotificationsBtn;
    private RecyclerView listView;
    private NotificationAdapter adapter;

    private List<Notification> notifications;
    private MainUI.MainUIMediator mainUIArtifact;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Objects.nonNull(getArguments())){
            notifications = (List<Notification>)getArguments().getSerializable(NOTIFICATIONS);
            mainUIArtifact = getArguments().getParcelable(ARTIFACT);
        }
    }

    public View onCreateView(@NonNull final LayoutInflater inflater,
                             final ViewGroup container, final Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        emptyNotifications = root.findViewById(R.id.emptyNotifications);
        readNotificationsBtn = root.findViewById(R.id.readNotificationsButton);

        listView = root.findViewById(R.id.notificationsRecyclerView);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        final DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(listView.getContext(),
                linearLayoutManager.getOrientation());

        listView.addItemDecoration(dividerItemDecoration);
        listView.setLayoutManager(linearLayoutManager);

        adapter = new NotificationAdapter(getContext(), notifications, this);
        listView.setAdapter(adapter);

        /* handle delete button click */
        readNotificationsBtn.setOnClickListener(ev -> {
            final List<Notification> readNot = new ArrayList<>();
            for(final Notification notification : notifications){
                if(notification.isSelected()) {
                    notification.setRead(true);
                    notification.setSelected(false);
                    readNot.add(notification);
                }
            }
            if(Objects.nonNull(mainUIArtifact)) {
                mainUIArtifact.readNotification(readNot);
            }
            readNotificationsBtn.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
        });

        /* handle notifications updates */
        if(notifications.size() > 0) {
            showNotifications();
        } else {
            hideNotifications();
        }
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        BackHelper.getInstance().setListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        deselectNotifications();
        BackHelper.getInstance().clearListener();
    }

    @Override
    public void onNotificationSelected(final Notification notification) {
        if(readNotificationsBtn.getVisibility() == View.GONE){
            readNotificationsBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNoNotificationSelected() {
        if(readNotificationsBtn.getVisibility() == View.VISIBLE){
            readNotificationsBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onBackClick(){
        if(readNotificationsBtn.getVisibility() == View.VISIBLE){
            readNotificationsBtn.setVisibility(View.GONE);
            deselectNotifications();
            adapter.notifyDataSetChanged();
            return true;
        }
        return false;
    }

    public void newNotification(final Notification notification){
        notifications.add(notification);
        adapter.notifyDataSetChanged();
    }

    private void deselectNotifications(){
        notifications.parallelStream().filter(Notification::isSelected).forEach(not -> not.setSelected(false));
    }

    private void hideNotifications(){
        setRightVisualization(false);
    }

    private void showNotifications(){
        setRightVisualization(true);
    }

    private void setRightVisualization(final boolean visibleNotifications){
        emptyNotifications.setVisibility(visibleNotifications ? View.GONE : View.VISIBLE);
        listView.setVisibility(visibleNotifications ? View.VISIBLE : View.GONE);
    }
}
