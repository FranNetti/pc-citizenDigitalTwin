package it.unibo.citizenDigitalTwin.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import it.unibo.citizenDigitalTwin.R;
import it.unibo.citizenDigitalTwin.ui.notifications.notification.Notification;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    private ConstraintLayout emptyNotifications;
    private ScrollView scrollView;

    public View onCreateView(@NonNull final LayoutInflater inflater,
                             final ViewGroup container, final Bundle savedInstanceState) {

        notificationsViewModel = new ViewModelProvider(requireActivity()).get(NotificationsViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        emptyNotifications = root.findViewById(R.id.emptyNotifications);
        scrollView = root.findViewById(R.id.scrollView);

        final RecyclerView listView = root.findViewById(R.id.notificationsRecyclerView);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        final DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(listView.getContext(),
                linearLayoutManager.getOrientation());

        listView.addItemDecoration(dividerItemDecoration);
        listView.setHasFixedSize(true);
        listView.setLayoutManager(linearLayoutManager);

        final List<Notification> notifications = new ArrayList<>();
        final NotificationAdapter adapter = new NotificationAdapter(notifications);
        listView.setAdapter(adapter);

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

    private void hideNotifications(){
        setRightVisualization(false);
    }

    private void showNotifications(){
        setRightVisualization(true);
    }

    private void setRightVisualization(final boolean visibleNotifications){
        emptyNotifications.setVisibility(visibleNotifications ? View.GONE : View.VISIBLE);
        scrollView.setVisibility(visibleNotifications ? View.VISIBLE : View.GONE);
    }


}
