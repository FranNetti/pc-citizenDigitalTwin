package it.unibo.citizenDigitalTwin.ui.notifications;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import it.unibo.citizenDigitalTwin.R;
import it.unibo.citizenDigitalTwin.commons.LeafCategory;
import it.unibo.citizenDigitalTwin.ui.notifications.notification.DataNotification;
import it.unibo.citizenDigitalTwin.ui.notifications.notification.MessageNotification;
import it.unibo.citizenDigitalTwin.ui.notifications.notification.Notification;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationHolder> {

    static class NotificationHolder extends RecyclerView.ViewHolder {

        final ImageView icon;
        final TextView sender;
        final TextView message;
        final TextView content;

        NotificationHolder(final ConstraintLayout view){
            super(view);
            this.icon = view.findViewById(R.id.icon);
            this.message = view.findViewById(R.id.message);
            this.sender = view.findViewById(R.id.sender);
            this.content = view.findViewById(R.id.content);
        }
    }

    private final List<Notification> notifications;

    public NotificationAdapter(final List<Notification> notifications){
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public NotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final ConstraintLayout layout = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_item, parent, false);
        return new NotificationHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationHolder holder, int position) {
        final Notification notification = notifications.get(position);
        switch (notification.getType()){
            case DATA: handleDataNotification(holder, (DataNotification)notification); break;
            case MESSAGE: handleMessageNotification(holder, (MessageNotification)notification); break;
            default: break;
        }
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    private void handleDataNotification(final NotificationHolder holder, final DataNotification notification){
        holder.icon.setImageResource(R.drawable.ic_add_box_black_24dp);
        holder.sender.setText(notification.getSender());
        holder.message.setText(R.string.data_change);
        holder.content.setText(
                notification.getChangedCategories()
                    .stream()
                    .map(LeafCategory::getDisplayName)
                    .reduce((a,b) -> a + ", " + b)
                    .orElseGet(() -> "")
        );
    }

    private void handleMessageNotification(final NotificationHolder holder, final MessageNotification notification){
        holder.icon.setImageResource(R.drawable.ic_mail_black_24dp);
        holder.sender.setText(notification.getSender());
        holder.message.setText(R.string.message_sent);
        holder.content.setText(notification.getMessage());
    }


}
