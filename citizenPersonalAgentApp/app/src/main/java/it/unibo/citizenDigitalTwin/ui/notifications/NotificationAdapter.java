package it.unibo.citizenDigitalTwin.ui.notifications;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import it.unibo.citizenDigitalTwin.R;
import it.unibo.citizenDigitalTwin.db.entity.notification.DataNotification;
import it.unibo.citizenDigitalTwin.db.entity.notification.MessageNotification;
import it.unibo.citizenDigitalTwin.db.entity.notification.Notification;

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

    interface NotificationAdapterListener {
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

    private final List<Notification> notifications;
    private final NotificationAdapterListener listener;
    private final Context context;

    NotificationAdapter(final Context context, final List<Notification> notifications, final NotificationAdapterListener listener){
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
            } else if(!notification.isRead()){
                notification.setSelected(true);
                listener.onNotificationSelected(notification);
            } else {
                return false;
            }
            this.notifyDataSetChanged();
            return true;
        });

        setBackgroundColor(holder, notification);
        setReadOrNot(holder, notification);
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
        holder.icon.setImageResource(R.drawable.ic_edit_black_24dp);
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

    private void setBackgroundColor(final NotificationHolder holder, final Notification notification){
        int color;
        if(notification.isSelected()){
            color = context.getColor(R.color.selectedItem);
        } else {
            color = Color.WHITE;
        }
        holder.container.setBackgroundColor(color);
    }

    private void setReadOrNot(final NotificationHolder holder, final Notification notification){
        if(!notification.isRead()){
            holder.message.setTypeface(holder.message.getTypeface(), Typeface.BOLD);
            holder.content.setTypeface(holder.content.getTypeface(), Typeface.BOLD);
            holder.sender.setTextColor(Color.BLACK);
            holder.content.setTextColor(Color.BLACK);
            holder.message.setTextColor(Color.BLACK);
        } else {
            final int color = context.getColor(android.R.color.tab_indicator_text);
            holder.message.setTypeface(null, Typeface.NORMAL);
            holder.content.setTypeface(null, Typeface.NORMAL);
            holder.sender.setTextColor(color);
            holder.message.setTextColor(color);
            holder.content.setTextColor(color);
        }
    }


}
