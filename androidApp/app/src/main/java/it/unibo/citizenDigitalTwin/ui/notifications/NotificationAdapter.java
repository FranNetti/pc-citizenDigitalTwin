package it.unibo.citizenDigitalTwin.ui.notifications;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import it.unibo.citizenDigitalTwin.R;
import it.unibo.citizenDigitalTwin.data.notification.DataNotification;
import it.unibo.citizenDigitalTwin.data.notification.MessageNotification;
import it.unibo.citizenDigitalTwin.data.notification.Notification;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationHolder> {

    static class NotificationHolder extends RecyclerView.ViewHolder {

        final ConstraintLayout container;
        final ImageView icon;
        final TextView sender;
        final TextView message;
        final TextView content;

        NotificationHolder(final ConstraintLayout view){
            super(view);
            this.container = view;
            this.icon = view.findViewById(R.id.icon);
            this.message = view.findViewById(R.id.message);
            this.sender = view.findViewById(R.id.sender);
            this.content = view.findViewById(R.id.content);
            view.setLongClickable(true);
        }
    }

    private final List<Notification> notifications;
    private final NotificationSelectedListener listener;
    private final Context context;

    NotificationAdapter(final Context context, final List<Notification> notifications, final NotificationSelectedListener listener){
        this.notifications = notifications;
        this.listener = listener;
        this.context = context;
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
        holder.container.setOnLongClickListener(ev -> {
            if(notification.isSelected()){
                notification.setSelected(false);
                if(notifications.parallelStream().noneMatch(Notification::isSelected)){
                    listener.onNoNotificationSelected();
                }
            } else {
                notification.setSelected(true);
                listener.onNotificationSelected(notification);
            }
            this.notifyDataSetChanged();
            return true;
        });

        int color;
        if(notification.isSelected()){
            color = context.getColor(R.color.selectedItem);
        } else {
            color = Color.WHITE;
        }
        holder.container.setBackgroundColor(color);
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
                    .map(x -> x.getDisplayName(context))
                    .reduce((a,b) -> a + ", " + b)
                    .orElse("")
        );
    }

    private void handleMessageNotification(final NotificationHolder holder, final MessageNotification notification){
        holder.icon.setImageResource(R.drawable.ic_mail_black_24dp);
        holder.sender.setText(notification.getSender());
        holder.message.setText(R.string.message_sent);
        holder.content.setText(notification.getMessage());
    }


}
