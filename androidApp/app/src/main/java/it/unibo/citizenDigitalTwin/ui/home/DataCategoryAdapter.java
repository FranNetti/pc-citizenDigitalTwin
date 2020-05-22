package it.unibo.citizenDigitalTwin.ui.home;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import it.unibo.citizenDigitalTwin.R;

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

    private final List<String> categories;

    public DataCategoryAdapter(final List<String> categories){
        this.categories = categories;
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
        final String dataCategory = this.categories.get(position);
        holder.dataCategoryName.setText(dataCategory);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }
}
