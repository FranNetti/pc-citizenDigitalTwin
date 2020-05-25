package it.unibo.citizenDigitalTwin.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import it.unibo.citizenDigitalTwin.R;
import it.unibo.citizenDigitalTwin.ui.notifications.notification.Notification;

public class NotificationsFragment extends Fragment implements NotificationSelectedListener {

    private NotificationsViewModel notificationsViewModel;
    private TextView emptyNotifications;
    private FloatingActionButton deleteNotificationsBtn;
    private RecyclerView listView;

    private List<Notification> notifications;

    public View onCreateView(@NonNull final LayoutInflater inflater,
                             final ViewGroup container, final Bundle savedInstanceState) {

        notificationsViewModel = new ViewModelProvider(requireActivity()).get(NotificationsViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        emptyNotifications = root.findViewById(R.id.emptyNotifications);
        deleteNotificationsBtn = root.findViewById(R.id.deleteNotificationsButton);

        listView = root.findViewById(R.id.notificationsRecyclerView);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        final DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(listView.getContext(),
                linearLayoutManager.getOrientation());

        listView.addItemDecoration(dividerItemDecoration);
        listView.setHasFixedSize(true);
        listView.setLayoutManager(linearLayoutManager);

        notifications = new ArrayList<>();
        final NotificationAdapter adapter = new NotificationAdapter(getContext(), notifications, this);
        listView.setAdapter(adapter);

        /* handle back command */
        final OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(deleteNotificationsBtn.getVisibility() == View.VISIBLE){
                    deleteNotificationsBtn.setVisibility(View.GONE);
                    deselectNotifications();
                    adapter.notifyDataSetChanged();
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        /* handle delete button click */
        deleteNotificationsBtn.setOnClickListener(ev -> {
            final List<Notification> notificationsRead = notifications.parallelStream()
                    .filter(Notification::isSelected)
                    .collect(Collectors.toList());
            notificationsViewModel.deleteNotifications(notificationsRead);
            deleteNotificationsBtn.setVisibility(View.GONE);
        });

        /* handle notifications updates */
        notificationsViewModel.getNotifications().observe(getViewLifecycleOwner(), currentNotifications -> {
            if(currentNotifications.size() > 0) {
                showNotifications();
                notifications.clear();
                notifications.addAll(currentNotifications);
                adapter.notifyDataSetChanged();
            } else {
                hideNotifications();
            }
        });
        return root;
    }

    @Override
    public void onStop() {
        super.onStop();
        deselectNotifications();
    }

    @Override
    public void onNotificationSelected(final Notification notification) {
        if(deleteNotificationsBtn.getVisibility() == View.GONE){
            deleteNotificationsBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNoNotificationSelected() {
        if(deleteNotificationsBtn.getVisibility() == View.VISIBLE){
            deleteNotificationsBtn.setVisibility(View.GONE);
        }
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

interface NotificationSelectedListener {
    /**
     * A new notification has been selected
     * @param notification the selected notification
     */
    void onNotificationSelected(Notification notification);

    /**
     * Call this method when there are no notification selected
     */
    void onNoNotificationSelected();
}
