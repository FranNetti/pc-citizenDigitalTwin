package it.unibo.citizenDigitalTwin.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import it.unibo.citizenDigitalTwin.R;
import it.unibo.citizenDigitalTwin.data.category.GroupCategory;

public class GroupCategoryAdapter extends RecyclerView.Adapter<GroupCategoryAdapter.GroupCategoryHolder> {

    static class GroupCategoryHolder extends RecyclerView.ViewHolder {

        final MaterialCardView layout;
        final ImageView categoryIcon;
        final TextView categoryName;

        GroupCategoryHolder(final MaterialCardView layout) {
            super(layout);
            this.layout = layout;
            this.categoryName = layout.findViewById(R.id.notificationMessage);
            categoryIcon = layout.findViewById(R.id.categoryIcon);
        }
    }

    interface GroupCategoryListener {
        void onGroupCategorySelected(View view, GroupCategory category);
    }

    private final List<GroupCategory> categories;
    private final Context context;
    private final GroupCategoryListener listener;

    GroupCategoryAdapter(final Context context, final GroupCategoryListener listener){
        this.context = context;
        this.listener = listener;
        this.categories = Arrays.asList(GroupCategory.values());
    }

    @NonNull
    @Override
    public GroupCategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MaterialCardView layout = (MaterialCardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_category_item, parent, false);
        return new GroupCategoryHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupCategoryHolder holder, int position) {
        final GroupCategory category = this.categories.get(position);
        holder.categoryName.setText(category.getDisplayName(context));
        holder.categoryIcon.setImageDrawable(category.getDisplayIcon(context));
        holder.layout.setOnClickListener(view -> listener.onGroupCategorySelected(view, category));
     }

    @Override
    public int getItemCount() {
        return categories.size();
    }
}
