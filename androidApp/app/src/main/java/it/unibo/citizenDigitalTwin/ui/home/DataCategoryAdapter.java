package it.unibo.citizenDigitalTwin.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import it.unibo.citizenDigitalTwin.R;
import it.unibo.citizenDigitalTwin.commons.GroupCategory;

public class DataCategoryAdapter extends RecyclerView.Adapter<DataCategoryAdapter.DataCategoryHolder> {

    static class DataCategoryHolder extends RecyclerView.ViewHolder {

        final ImageView dataCategoryIcon;
        final TextView dataCategoryName;

        DataCategoryHolder(final ConstraintLayout layout) {
            super(layout);
            this.dataCategoryName = (TextView) layout.getViewById(R.id.notificationMessage);
            dataCategoryIcon = (ImageView) layout.getViewById(R.id.categoryIcon);
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
        ConstraintLayout layout = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.data_category_item, parent, false);
        return new DataCategoryHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull DataCategoryHolder holder, int position) {
        final String dataCategory = this.categories.get(position).getDisplayName(context);
        holder.dataCategoryName.setText(dataCategory);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }
}
