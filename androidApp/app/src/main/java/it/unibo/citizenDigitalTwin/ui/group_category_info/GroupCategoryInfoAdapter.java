package it.unibo.citizenDigitalTwin.ui.group_category_info;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import it.unibo.citizenDigitalTwin.db.entity.data.Data;

class GroupCategoryInfoAdapter extends RecyclerView.Adapter<GroupCategoryInfoAdapter.GroupCategoryInfoHolder> {

    static class GroupCategoryInfoHolder extends RecyclerView.ViewHolder {

        final TextView textView;

        public GroupCategoryInfoHolder(final TextView view) {
            super(view);
            this.textView = view;
        }
    }

    private final List<Data> data;
    private final Context context;

    GroupCategoryInfoAdapter(final Context context, final List<Data> data){
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public GroupCategoryInfoHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        return new GroupCategoryInfoHolder(new TextView(context));
    }

    @Override
    public void onBindViewHolder(GroupCategoryInfoHolder holder, int position) {
        holder.textView.setText(data.get(position).getLeafCategory().getDisplayName(context));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
