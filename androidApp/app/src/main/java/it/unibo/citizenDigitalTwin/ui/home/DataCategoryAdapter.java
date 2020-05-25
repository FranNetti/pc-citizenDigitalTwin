package it.unibo.citizenDigitalTwin.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import it.unibo.citizenDigitalTwin.R;
import it.unibo.citizenDigitalTwin.commons.GroupCategory;

public class DataCategoryAdapter extends RecyclerView.Adapter<DataCategoryAdapter.DataCategoryHolder> {

    static class DataCategoryHolder extends RecyclerView.ViewHolder {

        final ImageView dataCategoryIcon;
        final TextView dataCategoryName;

        DataCategoryHolder(final MaterialCardView layout) {
            super(layout);
            this.dataCategoryName = layout.findViewById(R.id.notificationMessage);
            dataCategoryIcon = layout.findViewById(R.id.categoryIcon);
        }
    }

    private final List<GroupCategory> categories;
    private final Context context;

    DataCategoryAdapter(final Context context){
        this.context = context;
        this.categories = Arrays.asList(GroupCategory.values());
    }

    @NonNull
    @Override
    public DataCategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MaterialCardView layout = (MaterialCardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_category_item, parent, false);
        return new DataCategoryHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull DataCategoryHolder holder, int position) {
        final GroupCategory dataCategory = this.categories.get(position);
        holder.dataCategoryName.setText(dataCategory.getDisplayName(context));
        holder.dataCategoryIcon.setImageDrawable(dataCategory.getDisplayIcon(context));
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }
}
